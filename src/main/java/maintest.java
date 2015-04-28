
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.RepaintManager;
import javax.swing.SwingConstants;

import org.apache.batik.dom.*;
import org.apache.batik.svggen.SVGGraphics2D;
import org.jgraph.JGraph;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.JGraphLayout;
import com.jgraph.layout.hierarchical.JGraphHierarchicalLayout;

import models.connections.GraphLayoutConnection;
import models.graphbased.AttributeMap;
import models.graphbased.ViewSpecificAttributeMap;
import models.graphbased.directed.bpmn.BPMNDiagram;
import models.jgraph.CustomGraphModel;
import models.jgraph.CustomJGraph;
import models.jgraph.visualization.CustomJGraphPanel;
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
				
				File out = new File("test.jpg"); // Replace with your output stream
				Color bg = null; // Use this to make the background transparent
				 // Use this to use the graph background
				CustomJGraph jgraph = newjgraph(graph);
				
				
				CustomJGraphPanel panel = new CustomJGraphPanel(jgraph);
				
				Object[] cells = jgraph.getRoots();
				int inset = 1;
			     Rectangle2D bounds = jgraph.toScreen(jgraph.getCellBounds(cells));
			     
			     if (bounds != null) {
			           // Constructs the svg generator used for painting the graph to
			           DOMImplementation domImpl = GenericDOMImplementation
			                     .getDOMImplementation();
			           Document document = domImpl.createDocument(null, "svg", null);
			           SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
			           svgGenerator.translate(-bounds.getX() + inset, -bounds.getY()+ inset);
			           // Paints the graph to the svg generator with no double
			           // buffering enabled to make sure we get a vector image.
			           RepaintManager currentManager = RepaintManager
			                     .currentManager(panel);
			           currentManager.setDoubleBufferingEnabled(false);
			           panel.paint(svgGenerator);
			           // Writes the graph to the specified file as an SVG stream
			           OutputStream oos = new FileOutputStream(new File("test.svg"));
			           Writer writer = new OutputStreamWriter(oos, "UTF-8");
			           svgGenerator.stream(writer, false);
			           currentManager.setDoubleBufferingEnabled(true);
			     }
				BufferedImage img = jgraph.getImage(null, 1);
				ImageIO.write(img, "JPG", out);
			
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static CustomJGraph newjgraph(BPMNDiagram graph) {
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
		
		return jgraph;
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