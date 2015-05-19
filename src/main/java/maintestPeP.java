import java.io.File;


import javax.swing.JFileChooser;
import javax.swing.JFrame;

import models.graphbased.directed.petrinet.Petrinet;

import petrinet.analysis.WorkflowNetUtils;
import petrinet.behavioralanalysis.woflan.Woflan;
import petrinet.behavioralanalysis.woflan.WoflanDiagnosis;
import petrinet.pep.PePImporting;


import framework.util.SimplePanel;
import framework.util.Util;



public class maintestPeP {

	public static void main(String[] args) {


		try {

			JFileChooser fc = new JFileChooser();
			fc.setDialogType(JFileChooser.OPEN_DIALOG);		
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

			int returnVal = fc.showOpenDialog(new JFrame("Load File"));

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();


					PePImporting pepi = new PePImporting(file);
				
					Petrinet pn = pepi.importPetriNet();
					boolean result = WorkflowNetUtils.isValidWFNet(pn);


					Woflan wolf = new Woflan();
					WoflanDiagnosis result2 = wolf.diagnose(pn);
					System.out.println();
					
				//	Util.toFile(file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("."))+".ll_net",pn.toPEP());
					
					Util.toFile(file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("."))+".html", result2.toHTMLString(false));
					
					/*String dot = graph.toDOT();
					Util.toFile("./esempi/bp.dot", dot);
					String  dot2png = "dot -q -Tpng ./esempi/bp.dot -o ./esempi/bp.png \n";
					System.out.println(dot2png);*/

					String dot = pn.toDOT();
					Util.toFile("./esempi/pn.dot", dot);
					String  dot2png = "dot -q -Tpng ./esempi/pn.dot -o ./esempi/pn.png \n";
					System.out.println(dot2png);

					
					SimplePanel sp = new SimplePanel();
					sp.view(pn);
					
					/*File pnml = new File(file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("."))+".pnml");

					PnmlExportNetToPNML pex = new PnmlExportNetToPNML();
					Marking m = wolf.getInitialMarking();
					if(m==null){
						m = new Marking();
					}
					pex.exportPetriNetToPNMLFile(pn, pnml,m,new GraphLayoutConnection(pn) );*/

				}
			
			

		} catch (Exception e) {

			e.printStackTrace();
		}


	}


	

	
	
}
