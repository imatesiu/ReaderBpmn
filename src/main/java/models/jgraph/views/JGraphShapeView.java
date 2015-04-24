package models.jgraph.views;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.VertexView;
import models.utils.Cleanable;
import models.graphbased.AttributeMap;
import models.graphbased.ViewSpecificAttributeMap;
import models.graphbased.directed.DirectedGraphNode;
import models.jgraph.elements.CustomGraphCell;
import models.jgraph.renderers.CustomGroupShapeRenderer;
import models.jgraph.renderers.CustomShapeRenderer;
import models.shapes.Rectangle;
import models.shapes.Shape;

public class JGraphShapeView extends VertexView implements Cleanable {

	public final static Shape RECTANGLE = new Rectangle();

	private static final long serialVersionUID = 660416141359812229L;
	private static CustomGroupShapeRenderer renderer;
	private DirectedGraphNode node;
	private Shape shape;

	private final boolean isPIP;

	private final ViewSpecificAttributeMap viewSpecificAttributes;

	@SuppressWarnings("unchecked")
	public JGraphShapeView(CustomGraphCell cell, boolean isPIP, ViewSpecificAttributeMap viewSpecificAttributes) {
		super(cell);
		this.isPIP = isPIP;
		this.viewSpecificAttributes = viewSpecificAttributes;
		node = cell.getNode();
		shape = node.getAttributeMap().get(AttributeMap.SHAPE, RECTANGLE);

		GraphConstants.setMoveable(getAttributes(), node.getAttributeMap().get(AttributeMap.MOVEABLE, true));

		groupBounds = null;
		childViews = new ArrayList();
		for (Object child : cell.getChildren()) {
			if (child instanceof CustomGraphCell) {
				CustomGraphCell c2 = (CustomGraphCell) child;
				if (c2.getView() != null) {
					childViews.add(c2.getView());
				}
			}
		}
		setCachedBounds(GraphConstants.getBounds(cell.getAttributes()));

	}

	@Override
	public CustomShapeRenderer getRenderer() {
		if (renderer == null) {
			renderer = new CustomGroupShapeRenderer();
		}
		return renderer;
	}

	@Override
	public Point2D getPerimeterPoint(EdgeView edge, Point2D source, Point2D p) {
		if (p == null) {
			p = getCenterPoint(this);
		}
		return shape.getPerimeterPoint(this.getBounds(), source, p);
	}

	public DirectedGraphNode getNode() {
		return node;
	}

	public ViewSpecificAttributeMap getViewSpecificAttributeMap() {
		return viewSpecificAttributes;
	}

	public void cleanUp() {
		viewSpecificAttributes.clearViewSpecific(node);
		setCell(null);
		node = null;
		shape = null;
		if (renderer != null) {
			getRenderer().cleanUp();
			renderer = null;
		}
	}

	public boolean isPIP() {
		return isPIP;
	}

	@Override
	public Rectangle2D getBounds() {
		Dimension d = (Dimension) viewSpecificAttributes.get(node, AttributeMap.SIZE);
		Rectangle2D b = super.getBounds();
		if (d != null && (d.getWidth() != b.getWidth() || d.getHeight() != b.getHeight())) {
			return new Rectangle2D.Double(b.getX(), b.getY(), d.getWidth(), d.getHeight());
		} else {
			return b;
		}
	}
}
