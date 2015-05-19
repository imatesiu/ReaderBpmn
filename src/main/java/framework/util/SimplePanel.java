package framework.util;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.SwingConstants;

import models.connections.GraphLayoutConnection;
import models.graphbased.AttributeMap;
import models.graphbased.ViewSpecificAttributeMap;
import models.graphbased.directed.DirectedGraph;
import models.graphbased.directed.DirectedGraphEdge;
import models.graphbased.directed.DirectedGraphNode;
import models.jgraph.CustomGraphModel;
import models.jgraph.CustomJGraph;
import models.jgraph.visualization.CustomJGraphPanel;

import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.JGraphLayout;
import com.jgraph.layout.hierarchical.JGraphHierarchicalLayout;

import framework.util.ui.scalableview.ScalableViewPanel;
import framework.util.ui.scalableview.interaction.ExportInteractionPanel;
import framework.util.ui.scalableview.interaction.ZoomInteractionPanel;

public class SimplePanel {
	
	public SimplePanel() {
		
	}
	
	public void view(DirectedGraph<? extends DirectedGraphNode, ? extends DirectedGraphEdge<? extends DirectedGraphNode, ? extends DirectedGraphNode>> graph) {
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
