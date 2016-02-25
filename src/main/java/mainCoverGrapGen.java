import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import models.graphbased.directed.bpmn.BPMNDiagram;
import models.graphbased.directed.petrinet.Petrinet;
import models.graphbased.directed.petrinet.PetrinetGraph;
import models.graphbased.directed.petrinet.PetrinetNode;
import models.graphbased.directed.petrinet.elements.Place;
import models.graphbased.directed.transitionsystem.CoverabilityGraph;
import models.graphbased.directed.transitionsystem.Transition;
import models.semantics.petrinet.Marking;
import models.utils.Pair;
import petrinet.analysis.CoverabilitySet;
import petrinet.behavioralanalysis.CGGenerator;
import petrinet.dot.DotImporting;
import petrinet.pnml.importing.PnmlImportNet;
import plugin.sim.DepthFirstSearchLearnPad;
import plugins.bpmn.Bpmn;
import plugins.bpmn.trasform.BpmnToPetriNet;
import framework.util.SimplePanel;
import framework.util.Util;


public class mainCoverGrapGen {
	public static void main(String[] args) {
		try {

			JFileChooser fc = new JFileChooser("/Users/isiu/github/learnpadworkspace/ReaderBpmn/esempi");
			fc.setDialogType(JFileChooser.OPEN_DIALOG);		
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

			int returnVal = fc.showOpenDialog(new JFrame("Load File"));

			if (returnVal == JFileChooser.APPROVE_OPTION) {

				SimplePanel sp = new SimplePanel();

				File file = fc.getSelectedFile();
				Petrinet pn = null;
				Marking marking = null;
				BpmnToPetriNet btpn = null;
				if(file.getAbsolutePath().contains(".bpmn")){
					Bpmn bpmn = new Bpmn(file);

					Collection<BPMNDiagram> cb= bpmn.BpmnextractDiagram();
					BPMNDiagram BPMNdiagram = 	bpmn.BpmnextractDiagram().iterator().next();
					sp.view(BPMNdiagram);
					btpn = new BpmnToPetriNet(BPMNdiagram);
					pn = (Petrinet) btpn.getPetriNet();
					marking = btpn.getMarking();



				}else{
					if(file.getAbsolutePath().contains(".pnml")){
						PnmlImportNet ipnml = new PnmlImportNet();
						Pair<PetrinetGraph, Marking> resulti = ipnml.importFromStream(new FileInputStream(file), file.getName(), file.length());

						pn = (Petrinet) resulti.getFirst();
						marking = resulti.getSecond();
						if(marking.isEmpty()){
							Set<PetrinetNode> nodes = pn.getNodes();
							for (PetrinetNode petrinetNode : nodes) {
								int inedges =	petrinetNode.getGraph().getInEdges(petrinetNode).size();
								if(inedges==0){
									if(petrinetNode instanceof Place){
										marking = new Marking();
										marking.add((Place)petrinetNode,1);
										btpn = new BpmnToPetriNet(null);
									}
								}
							}
						}


					}


				}

				sp.view(pn);
				CGGenerator cg = new CGGenerator();
				Object[] result = cg.petriNetToCoverabilityGraph(pn,marking);
				CoverabilityGraph covGraph = (CoverabilityGraph) result[0];
				CoverabilitySet covSet = (CoverabilitySet) result[1];
				System.out.println(result);



				sp.view(covGraph);
				Set<Transition> v = covGraph.getEdges();

				DepthFirstSearchLearnPad dpslp =new DepthFirstSearchLearnPad(covGraph, btpn.getMap());

				System.out.println(dpslp.toStringCT());
				System.out.println(dpslp);

			}
		} catch (Exception e) {

			e.printStackTrace();
		}


	}
}
