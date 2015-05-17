package petrinet.pnml.graphics;

import models.graphbased.AbstractGraphElement;
import petrinet.pnml.Pnml;
import petrinet.pnml.PnmlElement;
import org.xmlpull.v1.XmlPullParser;

/**
 * PNML graphics offset object.
 * 
 * @author hverbeek
 */
public class PnmlOffset extends PnmlElement {

	/**
	 * PNML offset tag.
	 */
	public final static String TAG = "offset";

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
	 * Creates a fresh PNML offset.
	 */
	public PnmlOffset() {
		super(TAG);
		hasX = false;
		hasY = false;
	}

	public PnmlOffset(double x, double y) {
		super(TAG);
		hasX = true;
		hasY = true;
		this.x = x;
		this.y = y;
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
			return exportAttribute("x", String.valueOf(x), pnml);
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
			return exportAttribute("y", String.valueOf(y), pnml);
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

	/**
	 * Sets the offset of the given graph element to this offset.
	 * 
	 * @param element
	 *            The given element.
	 */
	public void convertToNet(AbstractGraphElement element) {

	}

	public PnmlOffset convertFromNet(AbstractGraphElement element) {
		return null;
	}
}
