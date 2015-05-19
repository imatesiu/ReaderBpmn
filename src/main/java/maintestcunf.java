import java.io.File;
import java.util.Collection;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import models.graphbased.directed.bpmn.BPMNDiagram;
import models.graphbased.directed.petrinet.Petrinet;
import models.semantics.petrinet.Marking;
import petrinet.dot.DotImporting;
import plugins.bpmn.Bpmn;
import plugins.bpmn.trasform.BpmnToPetriNet;
import framework.util.SimplePanel;
import framework.util.Util;


public class maintestcunf {
	public static void main(String[] args) {
		try {

			JFileChooser fc = new JFileChooser();
			fc.setDialogType(JFileChooser.OPEN_DIALOG);		
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

			int returnVal = fc.showOpenDialog(new JFrame("Load File"));

			if (returnVal == JFileChooser.APPROVE_OPTION) {


				File file = fc.getSelectedFile();
				Bpmn bpmn = new Bpmn(file);
				

				BPMNDiagram BPMNdiagram = 	bpmn.BpmnextractDiagram().iterator().next();
				BpmnToPetriNet btpn = new BpmnToPetriNet(BPMNdiagram);
				Petrinet pn = (Petrinet) btpn.getPetriNet();
				Marking marking = btpn.getMarking();
				String pepfile = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("."))+".ll_net";
				Util.toFile(pepfile,pn.toPEP(marking));
				
				//./cunf ../../../tmp/testbpmn/test1.pep -f dot  -o  testre.dot
				
				String pathcunf  = "/Users/isiu/cunf/bin/";
				String output = pepfile+".unfold.dot";
				String result = Util.executeCommand(pathcunf+"cunf -f dot -o "+output+"  "+pepfile);
				
				System.out.println(result);
				
				DotImporting doti = new DotImporting(new File(output));
				
				Petrinet pnunfold = doti.importPetriNet();
				SimplePanel sp = new SimplePanel();
				sp.view(pnunfold);
				sp.view(BPMNdiagram);

			}
		} catch (Exception e) {

			e.printStackTrace();
		}


	}




}
