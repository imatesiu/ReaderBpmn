



import java.io.File;
import java.util.Collection;

import framework.util.SimplePanel;
import framework.util.Util;
import models.graphbased.directed.bpmn.BPMNDiagram;
import models.graphbased.directed.petrinet.Petrinet;
import petrinet.analysis.WorkflowNetUtils;
import petrinet.behavioralanalysis.woflan.Woflan;
import plugins.bpmn.Bpmn;
import plugins.bpmn.trasform.BpmnToPetriNet;

public class main {

	public static void main(String[] args) {
		
		if(args.length<0 || args[0]==null){
			System.out.println("Non del file non trovato");
		}else{
			try {

				
				File file = new File(args[0]);
				Bpmn bpmn = new Bpmn(file);

				Collection<BPMNDiagram> BPMNdiagrams = 	bpmn.BpmnextractDiagram();
				for(BPMNDiagram graph : BPMNdiagrams){
					BpmnToPetriNet btpn = new BpmnToPetriNet(graph);
					Petrinet pn = (Petrinet) btpn.getPetriNet();
					boolean result = WorkflowNetUtils.isValidWFNet(pn);
					

					Woflan wolf = new Woflan();
					wolf.diagnose(pn);
					
					SimplePanel sp = new SimplePanel();
					sp.view(graph);
					sp.view(pn);
					

					String dot = graph.toDOT();
					Util.toFile("./esempi/bp.dot", dot);
					String  dot2png = "dot -q -Tpng ./esempi/bp.dot -o ./esempi/bp.png \n";
					System.out.println(dot2png);
					
					 dot = pn.toDOT();
					Util.toFile("./esempi/pn.dot", dot);
					  dot2png = "dot -q -Tpng ./esempi/pn.dot -o ./esempi/pn.png \n";
					System.out.println(dot2png);

					
					
				}

			} catch (Exception e) {
				
				e.printStackTrace();
			}
		}

	}
	
	

	

}
