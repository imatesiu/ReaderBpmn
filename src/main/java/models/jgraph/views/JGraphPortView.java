package models.jgraph.views;

import java.awt.geom.Point2D;

import org.jgraph.graph.EdgeView;
import org.jgraph.graph.PortView;
import models.utils.Cleanable;
import models.graphbased.ViewSpecificAttributeMap;
import models.jgraph.elements.CustomGraphPort;
import models.jgraph.renderers.CustomPortRenderer;

public class JGraphPortView extends PortView implements Cleanable {

	private static final long serialVersionUID = 5279360045457316828L;
	private static CustomPortRenderer renderer;
	private final boolean isPIP;
	private final ViewSpecificAttributeMap viewSpecificAttributes;
	private final CustomGraphPort cell;

	public JGraphPortView(CustomGraphPort cell, boolean isPIP, ViewSpecificAttributeMap viewSpecificAttributes) {
		super(cell);
		this.cell = cell;
		this.isPIP = isPIP;
		this.viewSpecificAttributes = viewSpecificAttributes;

		groupBounds = null;
	}

	@Override
	public CustomPortRenderer getRenderer() {
		if (renderer == null) {
			renderer = new CustomPortRenderer();
		}
		return renderer;
	}

	public ViewSpecificAttributeMap getViewSpecificAttributeMap() {
		return viewSpecificAttributes;
	}

	public void cleanUp() {
		setCell(null);
		parent = null;
		lastParent = null;
		if (renderer != null) {
			renderer.cleanUp();
			renderer = null;
		}
	}

	public boolean isPIP() {
		return isPIP;
	}

	@Override
	public Point2D getLocation(EdgeView edge, Point2D nearest) {
		Point2D pos = super.getLocation(edge, nearest);
		Point2D pos2 = pos;
		if (renderer != null && nearest != null && //
				cell.isBoundaryNode()) {
			pos2 = renderer.getPerimeterPoint(this, pos, nearest);
		}
		return pos2;
	}
}
