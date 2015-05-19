

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.SwingConstants;

import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.JGraphLayout;
import com.jgraph.layout.hierarchical.JGraphHierarchicalLayout;

import framework.util.SimplePanel;
import framework.util.Util;
import framework.util.ui.scalableview.ScalableViewPanel;
import framework.util.ui.scalableview.interaction.ExportInteractionPanel;
import framework.util.ui.scalableview.interaction.ZoomInteractionPanel;
import models.graphbased.AttributeMap;
import models.graphbased.ViewSpecificAttributeMap;
import models.graphbased.directed.DirectedGraph;
import models.graphbased.directed.DirectedGraphEdge;
import models.graphbased.directed.DirectedGraphNode;
import models.graphbased.directed.bpmn.BPMNDiagram;
import models.graphbased.directed.petrinet.Petrinet;
import models.graphbased.directed.petrinet.PetrinetGraph;
import models.jgraph.CustomGraphModel;
import models.jgraph.CustomJGraph;
import models.jgraph.visualization.CustomJGraphPanel;
import models.semantics.petrinet.Marking;
import models.connections.*;
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
