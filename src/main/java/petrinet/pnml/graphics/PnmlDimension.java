package petrinet.pnml.graphics;

import java.awt.Dimension;
import java.awt.geom.Point2D;

import framework.util.Pair;
import models.graphbased.AbstractGraphElement;
import models.graphbased.AttributeMap;
import petrinet.pnml.Pnml;
import petrinet.pnml.PnmlElement;
import org.xmlpull.v1.XmlPullParser;

/**
 * PNML graphics dimension object.
 * 
 * @author hverbeek
 */
public class PnmlDimension extends PnmlElement {

	/**
	 * PNML dimension tag.
	 */
	public final static String TAG = "dimension";

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

	/**
	 * Creates a fresh PNML dimension.
	 */
	public PnmlDimension() {
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
				x = PnmlPosition.SCALE * Double.valueOf(value);
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
			return exportAttribute("x", String.valueOf(x / PnmlPosition.SCALE), pnml);
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
				y = PnmlPosition.SCALE * Double.valueOf(value);
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
			return exportAttribute("y", String.valueOf(y / PnmlPosition.SCALE), pnml);
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
	 * Sets the size of the given graph element to this dimension.
	 * 
	 * @param element
	 *            The given element.
	 */
	public void convertToNet(AbstractGraphElement element, Pair<Point2D.Double, Point2D.Double> boundingBox) {
		if (hasX && hasY) {
			Dimension dim = new Dimension();
			dim.setSize(boundingBox.getSecond().x - boundingBox.getFirst().x, boundingBox.getSecond().y
					- boundingBox.getFirst().y);
			element.getAttributeMap().put(AttributeMap.SIZE, dim);
		}
	}

	public PnmlDimension convertFromNet(AbstractGraphElement element) {
		PnmlDimension dimension = null;
		try {
			Dimension size = element.getAttributeMap().get(AttributeMap.SIZE, new Dimension());
			x = size.getWidth();
			y = size.getHeight();
			hasX = true;
			hasY = true;
			dimension = this;
		} catch (Exception ex) {
		}
		return dimension;
	}
}
