import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import models.graphbased.directed.bpmn.BPMNDiagram;
import models.graphbased.directed.petrinet.Petrinet;
import models.graphbased.directed.petrinet.PetrinetGraph;
import models.semantics.petrinet.Marking;
import models.utils.Pair;
import petrinet.dot.DotImporting;
import petrinet.pnml.importing.PnmlImportNet;
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

				SimplePanel sp = new SimplePanel();
				
				File file = fc.getSelectedFile();
				Petrinet pn = null;
				Marking marking = null;
				if(file.getAbsolutePath().contains(".bpmn")){
					Bpmn bpmn = new Bpmn(file);


					BPMNDiagram BPMNdiagram = 	bpmn.BpmnextractDiagram().iterator().next();
					sp.view(BPMNdiagram);
					BpmnToPetriNet btpn = new BpmnToPetriNet(BPMNdiagram);
					pn = (Petrinet) btpn.getPetriNet();
					marking = btpn.getMarking();

				}else{
					if(file.getAbsolutePath().contains(".pnml")){
						PnmlImportNet ipnml = new PnmlImportNet();
						 Pair<PetrinetGraph, Marking> resulti = ipnml.importFromStream(new FileInputStream(file), file.getName(), file.length());
						
						 pn = (Petrinet) resulti.getFirst();
						 marking = resulti.getSecond();
					}


				}


				String pepfile = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("."))+".ll_net";
				Util.toFile(pepfile,pn.toPEP(marking));

				//./cunf ../../../tmp/testbpmn/test1.pep -f dot  -o  testre.dot

				String pathcunf  = "/Users/isiu/cunf/bin/";
				String output = pepfile+".unfold.dot";
				String result = Util.executeCommand(pathcunf+"cunf -f dot -o "+output+"  "+pepfile);

				System.out.println(result);

				DotImporting doti = new DotImporting(new File(output));

				Petrinet pnunfold = doti.importPetriNet();
				sp.view(pn);
				sp.view(pnunfold);
				

			}
		} catch (Exception e) {

			e.printStackTrace();
		}


	}




}
