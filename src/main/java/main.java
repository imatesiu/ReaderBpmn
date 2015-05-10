

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.SwingConstants;

import org.jbpt.algo.graph.DirectedGraphAlgorithms;
import org.jbpt.bp.RelSet;
import org.jbpt.bp.construct.RelSetCreatorUnfolding;
import org.jbpt.petri.Flow;
import org.jbpt.petri.Node;
import org.jbpt.petri.NetSystem;
import org.jbpt.petri.PetriNet;
import org.jbpt.petri.unfolding.CompletePrefixUnfolding;
import org.jbpt.petri.unfolding.CompletePrefixUnfoldingSetup;
import org.jbpt.petri.unfolding.Event;
import org.jbpt.petri.unfolding.OccurrenceNet;
import org.jbpt.petri.unfolding.order.AdequateOrderType;
import org.jbpt.utils.IOUtils;

import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.JGraphLayout;
import com.jgraph.layout.hierarchical.JGraphHierarchicalLayout;

import framework.util.ui.scalableview.ScalableViewPanel;
import framework.util.ui.scalableview.interaction.ExportInteractionPanel;
import framework.util.ui.scalableview.interaction.ZoomInteractionPanel;
import models.graphbased.AttributeMap;
import models.graphbased.ViewSpecificAttributeMap;
import models.graphbased.directed.bpmn.BPMNDiagram;
import models.jgraph.CustomGraphModel;
import models.jgraph.CustomJGraph;
import models.jgraph.visualization.CustomJGraphPanel;
import models.connections.*;
import plugins.bpmn.Bpmn;

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
					
				BPMNtoNetSystem fpn = new	BPMNtoNetSystem(graph);
				PetriNet pn = fpn.getPN();
				NetSystem sys = new NetSystem(pn);
				
				sys.loadNaturalMarking();
				
				boolean serializeModels=true;
				String name="testpn";
				if (serializeModels) IOUtils.toFile("./"+name+"/original.dot", sys.toDOT());
				String dot2png="";
				if (serializeModels) dot2png += "dot -q -Tpng ./"+name+"/original.dot -o ./"+name+"/original.png\n";
				System.out.println(dot2png);
				
				// check if net is cyclic
				DirectedGraphAlgorithms<Flow,Node> dga = new DirectedGraphAlgorithms<>();
				String result = dga.isCyclic(sys) ? "cyclic\n" : "acyclic\n"; 
				
				CompletePrefixUnfoldingSetup setup = new CompletePrefixUnfoldingSetup();
				setup.ADEQUATE_ORDER = AdequateOrderType.ESPARZA_FOR_ARBITRARY_SYSTEMS;
				
				CompletePrefixUnfolding unfolding = new CompletePrefixUnfolding(sys,setup);
				OccurrenceNet occurrenceNet = (OccurrenceNet) unfolding.getOccurrenceNet();
				List<Event> log = unfolding.getLog();
				if (serializeModels) IOUtils.toFile("./"+name+"/occurrenceNet.dot", occurrenceNet.toDOT());
				dot2png="";
				if (serializeModels) dot2png += "dot -q -Tpng ./"+name+"/occurrenceNet.dot -o ./"+name+"/occurrenceNet.png\n";
				System.out.println(dot2png);
				
				
				System.out.println(result);
				
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

			} catch (Exception e) {
				
				e.printStackTrace();
			}
		}

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


}
