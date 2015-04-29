package models.jgraph.renderers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import models.graphbased.Expandable;
import models.graphbased.directed.DirectedGraphNode;
import models.jgraph.views.JGraphShapeView;


public class CustomGroupShapeRenderer extends CustomShapeRenderer {

	private static final long serialVersionUID = 1L;

	protected boolean isGroup = false;

	
	protected Color rectColor = Color.white, graphForeground = Color.black;

	public static Rectangle rect = new Rectangle(0, 0, 20, 20);
	
	

	
	public void paint(Graphics g) {
		super.paint(g);
		if (isGroup) {
			g.setColor(rectColor);
			g.fill3DRect(rect.x, rect.y, rect.width, rect.height, true);
			g.setColor(graphForeground);
			g.drawRect(rect.x, rect.y, rect.width, rect.height);
			g.drawLine(rect.x + 1, rect.y + rect.height / 2, rect.x + rect.width - 2, rect.y
					+ rect.height / 2);
			if (view.isLeaf()) {
				g.drawLine(rect.x + rect.width / 2, rect.y + 1, rect.x + rect.width / 2, rect.y
						+ rect.height - 2);
			}
		}
	}

	public Component getRendererComponent(JGraph graph, CellView view, boolean sel, boolean focus, boolean preview) {
		rectColor = graph.getHandleColor();
		graphForeground = graph.getForeground();
		DirectedGraphNode node = ((JGraphShapeView) view).getNode();
		isGroup = (node instanceof Expandable); //DefaultGraphModel.isGroup(graph.getModel(), view.getCell());
		return super.getRendererComponent(graph, view, sel, focus, preview);
	}
	
	public boolean inHitRegion(Point2D pt) {
		return rect.contains(pt.getX(), pt.getY());
	}

}
