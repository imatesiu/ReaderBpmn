
import java.io.File;
import java.util.Collection;


import models.graphbased.directed.bpmn.BPMNDiagram;
import plugins.bpmn.Bpmn;





public class maintest {
	public static void main(String[] args) {
		try {

			//File file = new File("esempi/AB_CC_Compatto.2.bpmn");
			File file = new File("esempi/cc2.bpmn");
			Bpmn bpmn = new Bpmn(file);

			Collection<BPMNDiagram> BPMNdiagrams = 	bpmn.BpmnextractDiagram();
			for(BPMNDiagram graph : BPMNdiagrams){

				//BPMNDiagram graph = BPMNdiagrams.iterator().next();
				
				System.out.print(graph.getLabel());
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}