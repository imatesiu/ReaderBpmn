package models.jgraph.views;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.jgraph.graph.EdgeView;
import models.utils.Cleanable;
import models.graphbased.ViewSpecificAttributeMap;
import models.graphbased.directed.DirectedGraphEdge;
import models.jgraph.elements.CustomGraphEdge;
import models.jgraph.renderers.CustomEdgeRenderer;

public class JGraphEdgeView extends EdgeView implements Cleanable {

	private static final long serialVersionUID = -2874236692967529775L;
	private static CustomEdgeRenderer renderer;
	private DirectedGraphEdge<?, ?> edge;
	private final boolean isPIP;
	private final ViewSpecificAttributeMap viewSpecificAttributes;

	@SuppressWarnings("unchecked")
	public JGraphEdgeView(CustomGraphEdge cell, boolean isPIP, ViewSpecificAttributeMap viewSpecificAttributes) {
		super(cell);
		this.isPIP = isPIP;
		this.viewSpecificAttributes = viewSpecificAttributes;
		edge = cell.getEdge();
		points = new ArrayList(2);
		points.add(cell.getSource().getView());
		points.addAll(cell.getInternalPoints());
		points.add(cell.getTarget().getView());

		groupBounds = null;
	}

	public void setPoints(List<Point2D> list) {
		points = list;
	}

	public ViewSpecificAttributeMap getViewSpecificAttributeMap() {
		return viewSpecificAttributes;
	}

	@Override
	public CustomEdgeRenderer getRenderer() {
		if (renderer == null) {
			renderer = new CustomEdgeRenderer();
		}
		return renderer;
	}

	public void cleanUp() {
		edge = null;
		setCell(null);
		viewSpecificAttributes.clearViewSpecific(edge);
		source = null;
		target = null;
		if (renderer != null) {
			renderer.cleanUp();
			renderer = null;
		}
	}

	public DirectedGraphEdge<?, ?> getEdge() {
		return edge;
	}

	public boolean isPIP() {
		return isPIP;
	}

}
