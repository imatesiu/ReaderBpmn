

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingConstants;

import org.jgraph.JGraph;

import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.JGraphLayout;
import com.jgraph.layout.hierarchical.JGraphHierarchicalLayout;

import framework.connections.Connection;
import framework.connections.ConnectionCannotBeObtained;
import framework.connections.ConnectionID;
import framework.connections.ConnectionManager;
import framework.util.ui.scalableview.ScalableViewPanel;
import framework.util.ui.scalableview.interaction.ExportInteractionPanel;
import framework.util.ui.scalableview.interaction.ZoomInteractionPanel;
import models.graphbased.AttributeMap;
import models.graphbased.ViewSpecificAttributeMap;
import models.graphbased.directed.DirectedGraph;
import models.graphbased.directed.bpmn.BPMNDiagram;
import models.jgraph.CustomGraphModel;
import models.jgraph.CustomJGraph;
import models.jgraph.visualization.CustomJGraphPanel;
import models.connections.*;
import plugins.bpmn.Bpmn;

public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub


		try {

			//File file = new File("esempi/AB_CC_Compatto.2.bpmn");
			File file = new File("esempi/cc2.bpmn");
			Bpmn bpmn = new Bpmn(file);

			Collection<BPMNDiagram> BPMNdiagrams = 	bpmn.BpmnextractDiagram();
			for(BPMNDiagram graph : BPMNdiagrams){

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
				BufferedImage bi = jgraph.getImage(null, 0);
				ImageIO.write(bi, "JPG", new File("test.jpg"));

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
