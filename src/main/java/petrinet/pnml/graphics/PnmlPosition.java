package petrinet.pnml.graphics;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.List;

import models.utils.Pair;
import models.connections.GraphLayoutConnection;
import models.graphbased.AbstractGraphElement;
import models.graphbased.AttributeMap;
import models.graphbased.directed.petrinet.elements.ExpandableSubNet;
import petrinet.pnml.Pnml;
import petrinet.pnml.PnmlElement;
import org.xmlpull.v1.XmlPullParser;

/**
 * PNML graphics position object.
 * 
 * @author hverbeek
 */
public class PnmlPosition extends PnmlElement {

	/**
	 * PNML position tag.
	 */
	public final static String TAG = "position";

	/**
	 * Whether the coordinates are valid.
	 */
	private boolean hasX;
	private boolean hasY;
	/**
	 * The coordinates.
	 */
	private double x;
	private double y;

	public static final double SCALE = 2.0;

	/**
	 * Creates a fresh PNML position.
	 */
	public PnmlPosition() {
		super(TAG);
		hasX = false;
		hasY = false;
	}

	/**
	 * Imports the known attributes.
	 */
	protected void importAttributes(XmlPullParser xpp, Pnml pnml) {
		super.importAttributes(xpp, pnml);
		/*
		 * Import the x attribute.
		 */
		importX(xpp, pnml);
		/*
		 * Import the y attribute.
		 */
		importY(xpp, pnml);
	}

	/**
	 * Exports the dimension.
	 */
	protected String exportAttributes(Pnml pnml) {
		return super.exportAttributes(pnml) + exportX(pnml) + exportY(pnml);
	}

	/**
	 * Imports the x attribute.
	 * 
	 * @param xpp
	 * @param pnml
	 */
	private void importX(XmlPullParser xpp, Pnml pnml) {
		String value = xpp.getAttributeValue(null, "x");
		if (value != null) {
			try {
				x = SCALE * Double.valueOf(value);
				hasX = true;
			} catch (NumberFormatException e) {
			}
		}
	}

	/**
	 * Exports the x attribute.
	 * 
	 * @return
	 */
	private String exportX(Pnml pnml) {
		if (hasX) {
			return exportAttribute("x", String.valueOf(x / SCALE), pnml);
		}
		return "";
	}

	/**
	 * Imports the y attribute.
	 * 
	 * @param xpp
	 * @param pnml
	 */
	private void importY(XmlPullParser xpp, Pnml pnml) {
		String value = xpp.getAttributeValue(null, "y");
		if (value != null) {
			try {
				y = SCALE * Double.valueOf(value);
				hasY = true;
			} catch (NumberFormatException e) {
			}
		}
	}

	/**
	 * Exports the y attribute.
	 * 
	 * @return
	 */
	private String exportY(Pnml pnml) {
		if (hasY) {
			return exportAttribute("y", String.valueOf(y / SCALE), pnml);
		}
		return "";
	}

	/**
	 * Checks validity. Should have both an x and a y attribute.
	 */
	protected void checkValidity(Pnml pnml) {
		super.checkValidity(pnml);
		if (!hasX || !hasY) {
			pnml.log(tag, lineNumber, "Expected x and y");
		}
	}

	public double getX() {
		return hasX ? x : 0.0;
	}

	public double getY() {
		return hasY ? y : 0.0;
	}

	public void setX(Double x) {
		this.x = x;
		hasX = true;
	}

	public void setY(Double y) {
		this.y = y;
		hasY = true;
	}

	/**
	 * Sets the position of the given graph element to this position.
	 * 
	 * @param subNet
	 *            The given sub net.
	 * @param element
	 *            The given element.
	 */
	public void convertToNet(ExpandableSubNet subNet, AbstractGraphElement element, Point2D.Double displacement,
			Pair<Point2D.Double, Point2D.Double> boundingBox, GraphLayoutConnection layout) {
		if (hasX && hasY) {
			//			System.err.println(subNet.getLabel() + ", " + element.getLabel() + ": " + displacement + ", " + boundingBox);
			layout.setPosition(element,
					new Point2D.Double(boundingBox.getFirst().x + displacement.x, boundingBox.getFirst().y
							+ displacement.y));
		}
	}

	/**
	 * Extends the positions of the given graph element (should be an edge) with
	 * this position.
	 * 
	 * @param subNet
	 *            The given sub net.
	 * @param element
	 *            The given element.
	 * @param displacement
	 *            The displacement for this sub net.
	 */
	public void convertToNet(ExpandableSubNet subNet, AbstractGraphElement element, List<Point2D> list,
			Point2D.Double displacement) {
		if (hasX && hasY) {
			list.add(new Point2D.Double(x + displacement.x, y + displacement.y));
		}
	}

	/**
	 * Creates a PnmlPosition object for the given element with the given
	 * parent.
	 * 
	 * @param parent
	 *            The given parent.
	 * @param element
	 *            The given element.
	 * @return The created PnmlPosition object.
	 */
	public PnmlPosition convertFromNet(ExpandableSubNet parent, AbstractGraphElement element,
			GraphLayoutConnection layout) {
		PnmlPosition result = null;
		try {
			/*
			 * Map the position from position-of-left-upper-corner to
			 * position-of-center.
			 */
			Point2D pos = layout.getPosition(element);
			if (pos == null) {
				pos = new Point2D.Double(10,10);
			}
			Dimension size = (Dimension) element.getAttributeMap().get(AttributeMap.SIZE);
			x = pos.getX() + size.getWidth() / 2;
			y = pos.getY() + size.getHeight() / 2;
			/*
			 * If this happens to be a sub net, then the current position is the
			 * position of the rectangular space surrounding it. Correct this
			 * (the empty space around the sub net measures 20 pixels, so it
			 * seems.
			 */
			if (element instanceof ExpandableSubNet) {
				x += 20.0;
				y += 20.0;
			}
			hasX = true;
			hasY = true;
			result = this;
		} catch (Exception ex) {
		}
		return result;
	}

	public PnmlPosition convertFromNet(ExpandableSubNet parent, Point2D point) {
		PnmlPosition result = null;
		try {
			x = point.getX();
			y = point.getY();
			//			while (parent != null) {
			//				Point2D.Double parentPos = parent.getAttributeMap().get(AttributeMap.POSITION, new Point2D.Double());
			//				x -= parentPos.x;
			//				y -= parentPos.y;
			//				parent = parent.getParent();
			//			}
			hasX = true;
			hasY = true;
			result = this;
		} catch (Exception ex) {
		}
		return result;
	}
}
