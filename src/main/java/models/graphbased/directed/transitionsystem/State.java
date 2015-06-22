package models.graphbased.directed.transitionsystem;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;

import models.utils.Cast;
import framework.util.HTMLToString;
import models.graphbased.AttributeMap;
import models.graphbased.directed.AbstractDirectedGraphNode;
import models.shapes.Decorated;
import models.shapes.Ellipse;

public class State extends AbstractDirectedGraphNode implements Decorated {

	/**
	 * this object identifies the state.
	 */
	private final Object identifier;
	private final TransitionSystemImpl graph;

	/**
	 * This accepting is stored for painting the state in the graph (see method
	 * decorate).
	 */
	private boolean accepting;

	public State(Object identifier, TransitionSystemImpl graph) {
		super();
		this.identifier = identifier;
		this.graph = graph;
		getAttributeMap().put(AttributeMap.SHAPE, new Ellipse());
		getAttributeMap().put(AttributeMap.SQUAREBB, false);
		getAttributeMap().put(AttributeMap.RESIZABLE, true);
		getAttributeMap().put(AttributeMap.SIZE, new Dimension(100, 60));
		getAttributeMap().put(AttributeMap.FILLCOLOR, Color.LIGHT_GRAY);
		if (identifier instanceof HTMLToString) {
			getAttributeMap().put(AttributeMap.LABEL, Cast.<HTMLToString>cast(identifier).toHTMLString(true));
		} else {
			getAttributeMap().put(AttributeMap.LABEL, identifier.toString());
		}
		getAttributeMap().put(AttributeMap.AUTOSIZE, false);
		setAccepting(false);
	}

	public Object getIdentifier() {
		return identifier;
	}

	public boolean equals(Object o) {
		return (o instanceof State ? identifier.equals(((State) o).identifier) : false);
	}

	public int hashCode() {
		return identifier.hashCode();
	}

	@Override
	public TransitionSystemImpl getGraph() {
		return graph;
	}

	public void setAccepting(boolean accepting) {
		this.accepting = accepting;
	}

	public boolean isAccepting() {
		return accepting;
	}

	public void setLabel(String label) {
		getAttributeMap().put(AttributeMap.LABEL, label);
	}

	public String getLabel() {
		return (String) getAttributeMap().get(AttributeMap.LABEL);
	}

	/**
	 * If this state is an accepting state, then an extra line is painted in the
	 * GUI as the border of this state.
	 */
	public void decorate(Graphics2D g2d, double x, double y, double width, double height) {
		if (isAccepting()) {
			Float line = getAttributeMap().get(AttributeMap.LINEWIDTH, 1f);
			int pointOffset = 3 + line.intValue() / 2;
			int sizeOffset = (2 * pointOffset) + 1;
			// Remember current stroke.
			Stroke stroke = g2d.getStroke();
			// Use thin line for extra line.
			g2d.setStroke(new BasicStroke(1f));
			// Draw extra line.
			g2d.draw(new Ellipse2D.Double(x + pointOffset, y + pointOffset, width - sizeOffset, height - sizeOffset));
			// Reset remembered stroke.
			g2d.setStroke(stroke);
		}
	}
}
