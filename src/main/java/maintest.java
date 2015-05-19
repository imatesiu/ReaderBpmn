

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

 



import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
 




import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.JGraphLayout;
import com.jgraph.layout.hierarchical.JGraphHierarchicalLayout;

import framework.util.ui.scalableview.ScalableViewPanel;
import framework.util.ui.scalableview.interaction.ExportInteractionPanel;
import framework.util.ui.scalableview.interaction.ZoomInteractionPanel;
import models.connections.GraphLayoutConnection;
import models.graphbased.AttributeMap;
import models.graphbased.ViewSpecificAttributeMap;
import models.graphbased.directed.DirectedGraph;
import models.graphbased.directed.DirectedGraphEdge;
import models.graphbased.directed.DirectedGraphNode;
import models.graphbased.directed.bpmn.BPMNDiagram;
import models.graphbased.directed.petrinet.Petrinet;
import models.jgraph.CustomGraphModel;
import models.jgraph.CustomJGraph;
import models.jgraph.visualization.CustomJGraphPanel;
import models.semantics.petrinet.Marking;
import petrinet.analysis.WorkflowNetUtils;
import petrinet.behavioralanalysis.woflan.Woflan;
import petrinet.behavioralanalysis.woflan.WoflanDiagnosis;
import petrinet.pnml.exporting.PnmlExportNetToPNML;
import plugins.bpmn.Bpmn;
import plugins.bpmn.trasform.BpmnToPetriNet;





public class maintest {
	public static void main(String[] args) {


		try {

			JFileChooser fc = new JFileChooser();
			fc.setDialogType(JFileChooser.OPEN_DIALOG);		
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

			int returnVal = fc.showOpenDialog(new JFrame("Load File"));

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();



				Bpmn bpmn = new Bpmn(file);

				Collection<BPMNDiagram> BPMNdiagrams = 	bpmn.BpmnextractDiagram();
				for(BPMNDiagram graph : BPMNdiagrams){
					BpmnToPetriNet btpn = new BpmnToPetriNet(graph);
					Petrinet pn = (Petrinet) btpn.getPetriNet();
					boolean result = WorkflowNetUtils.isValidWFNet(pn);


					Woflan wolf = new Woflan();
					WoflanDiagnosis result2 = wolf.diagnose(pn);
					System.out.println();
					
					toFile(file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("."))+".ll_net",pn.toPEP());
					
					toFile(file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("."))+".html", result2.toHTMLString(false));
					
					String dot = graph.toDOT();
					toFile("./esempi/bp.dot", dot);
					String  dot2png = "dot -q -Tpng ./esempi/bp.dot -o ./esempi/bp.png \n";
					System.out.println(dot2png);

					dot = pn.toDOT();
					toFile("./esempi/pn.dot", dot);
					dot2png = "dot -q -Tpng ./esempi/pn.dot -o ./esempi/pn.png \n";
					System.out.println(dot2png);

					view(pn);
					view(graph);
					
					
					File pnml = new File(file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("."))+".pnml");

					PnmlExportNetToPNML pex = new PnmlExportNetToPNML();
					Marking m = wolf.getInitialMarking();
					if(m==null){
						m = new Marking();
					}
					pex.exportPetriNetToPNMLFile(pn, pnml,m,new GraphLayoutConnection(pn) );

				}
			}

		} catch (Exception e) {

			e.printStackTrace();
		}


	}

	protected static void view(DirectedGraph<? extends DirectedGraphNode, ? extends DirectedGraphEdge<? extends DirectedGraphNode, ? extends DirectedGraphNode>> graph) {
		//BPMNDiagram graph = BPMNdiagrams.iterator().next();
		CustomGraphModel model = new CustomGraphModel(graph);
		ViewSpecificAttributeMap map =new ViewSpecificAttributeMap();
		GraphLayoutConnection layoutConnection =  new GraphLayoutConnection(graph);
		CustomJGraph jgraph = new CustomJGraph(model,map,layoutConnection);
		jgraph.repositionToOrigin();
		JGraphLayout layout = getLayout(map.get(graph, AttributeMap.PREF_ORIENTATION, SwingConstants.SOUTH));

		if (!layoutConnection.isLayedOut()) {

			JGraphFacade facade = new JGraphFacade(jgraph);

			facade.setOrdered(false);
			facade.setEdgePromotion(true);
			facade.setIgnoresCellsInGroups(false);
			facade.setIgnoresHiddenCells(false);
			facade.setIgnoresUnconnectedCells(false);
			facade.setDirected(true);
			facade.resetControlPoints();
			if (layout instanceof JGraphHierarchicalLayout) {
				facade.run((JGraphHierarchicalLayout) layout, true);
			} else {
				facade.run(layout, true);
			}

			Map<?, ?> nested = facade.createNestedMap(true, true);

			jgraph.getGraphLayoutCache().edit(nested);
			//				jgraph.repositionToOrigin();
			layoutConnection.setLayedOut(true);

		}

		jgraph.setUpdateLayout(layout);
		CustomJGraphPanel panel = new CustomJGraphPanel(jgraph);
		panel.addViewInteractionPanel(new ZoomInteractionPanel(panel, ScalableViewPanel.MAX_ZOOM), SwingConstants.WEST);
		panel.addViewInteractionPanel(new ExportInteractionPanel(panel), SwingConstants.SOUTH);


		JFrame f = new JFrame();

		// get the screen size as a java dimension
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		// get 2/3 of the height, and 2/3 of the width
		int height = screenSize.height * 2 / 3;
		int width = screenSize.width * 2 / 3;

		// set the jframe height and width
		f.setPreferredSize(new Dimension(width, height));
		f.setLayout(new BorderLayout());
		f.add(panel, BorderLayout.CENTER);

		f.setSize(640, 480);
		f.pack();
		f.setVisible(true);
		//BufferedImage bi = jgraph.getImage(null, 0);
		//ImageIO.write(bi, "JPG", new File("test.jpg"));
		
	}
	protected static JGraphLayout getLayout(int orientation) {
		JGraphHierarchicalLayout layout = new JGraphHierarchicalLayout();
		layout.setDeterministic(false);
		layout.setCompactLayout(false);
		layout.setFineTuning(true);
		layout.setParallelEdgeSpacing(15);
		layout.setFixRoots(false);

		layout.setOrientation(orientation);

		return layout;
	}

	public static void toFile(String fileName, String content) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
			out.write(content);
			out.close();
		}
		catch (IOException e) {
			System.err.println(e.getMessage());		
		}
	}

}