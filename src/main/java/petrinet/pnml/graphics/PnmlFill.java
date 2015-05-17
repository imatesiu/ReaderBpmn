package petrinet.pnml.graphics;

import java.awt.Color;
import java.net.URI;

import models.graphbased.AbstractGraphElement;
import models.graphbased.AttributeMap;
import petrinet.pnml.Pnml;
import petrinet.pnml.PnmlElement;
import petrinet.pnml.graphics.utils.PnmlColor;
import org.xmlpull.v1.XmlPullParser;

/**
 * PNML graphics fill element.
 * 
 * @author hverbeek
 */
public class PnmlFill extends PnmlElement {

	/**
	 * PNML fill tag.
	 */
	public final static String TAG = "fill";

	/**
	 * Possible gradient rotations.
	 */
	enum GradientRotation {
		GRADIENT_ROTATION_DEFAULT, GRADIENT_ROTATION_VERTICAL, GRADIENT_ROTATION_HORIZONTAL, GRADIENT_ROTATION_DIAGONAL;
	}

	/**
	 * Color attribute.
	 */
	private String color;
	/**
	 * Gradient color attribute.
	 */
	private String gradientColor;
	/**
	 * Gradient rotation attribute.
	 */
	private GradientRotation gradientRotation;
	/**
	 * Image attribute.
	 */
	private URI image;

	/**
	 * Creates a fresh fill element.
	 */
	public PnmlFill() {
		super(TAG);
		color = null;
		gradientColor = null;
		gradientRotation = GradientRotation.GRADIENT_ROTATION_DEFAULT;
		image = null;
	}

	/**
	 * Imports all known attributes.
	 */
	protected void importAttributes(XmlPullParser xpp, Pnml pnml) {
		super.importAttributes(xpp, pnml);
		/*
		 * Import color attribute.
		 */
		importColor(xpp, pnml);
		/*
		 * Import gradient color attribute.
		 */
		importGradientColor(xpp, pnml);
		/*
		 * Import gradient rotation attribute.
		 */
		importGradientRotation(xpp, pnml);
		/*
		 * Import image attribute.
		 */
		importImage(xpp, pnml);
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes(Pnml pnml) {
		return super.exportAttributes(pnml) + exportColor(pnml) + exportGradientColor(pnml)
				+ exportGradientRotation(pnml) + exportImage(pnml);
	}

	/**
	 * Imports color attribute.
	 * 
	 * @param xpp
	 * @param pnml
	 */
	private void importColor(XmlPullParser xpp, Pnml pnml) {
		String value = xpp.getAttributeValue(null, "color");
		if (value != null) {
			color = value;
		}
	}

	/**
	 * Exports color attribute.
	 * 
	 * @return
	 */
	private String exportColor(Pnml pnml) {
		if (color != null) {
			return exportAttribute("color", color, pnml);
		}
		return "";
	}

	/**
	 * Imports gradient color attribute.
	 * 
	 * @param xpp
	 * @param pnml
	 */
	private void importGradientColor(XmlPullParser xpp, Pnml pnml) {
		String value = xpp.getAttributeValue(null, "gradient-color");
		if (value != null) {
			gradientColor = value;
		}
	}

	/**
	 * Exports gradient color attribute.
	 * 
	 * @return
	 */
	private String exportGradientColor(Pnml pnml) {
		if (gradientColor != null) {
			return exportAttribute("gradient-color", gradientColor, pnml);
		}
		return "";
	}

	/**
	 * Imports gradient rotation attribute.
	 * 
	 * @param xpp
	 * @param pnml
	 */
	private void importGradientRotation(XmlPullParser xpp, Pnml pnml) {
		String value = xpp.getAttributeValue(null, "gradient-rotation");
		if (value != null) {
			if (value.equalsIgnoreCase("vertical")) {
				gradientRotation = GradientRotation.GRADIENT_ROTATION_VERTICAL;
			} else if (value.equalsIgnoreCase("horizontal")) {
				gradientRotation = GradientRotation.GRADIENT_ROTATION_HORIZONTAL;
			} else if (value.equalsIgnoreCase("diagonal")) {
				gradientRotation = GradientRotation.GRADIENT_ROTATION_DIAGONAL;
			}
		}
	}

	/**
	 * Exports gradient rotation attribute.
	 * 
	 * @return
	 */
	private String exportGradientRotation(Pnml pnml) {
		switch (gradientRotation) {
			case GRADIENT_ROTATION_VERTICAL :
				return exportAttribute("gradient-rotation", "vertical", pnml);
			case GRADIENT_ROTATION_HORIZONTAL :
				return exportAttribute("gradient-rotation", "horizontal", pnml);
			case GRADIENT_ROTATION_DIAGONAL :
				return exportAttribute("gradient-rotation", "diagonal", pnml);
			default :
				// fall thru
		}
		return "";
	}

	/**
	 * Imports image attribute.
	 * 
	 * @param xpp
	 * @param pnml
	 */
	private void importImage(XmlPullParser xpp, Pnml pnml) {
		String value = xpp.getAttributeValue(null, "image");
		if (value != null) {
			image = URI.create(value);
		}
	}

	/**
	 * Exports image attribute.
	 * 
	 * @return
	 */
	private String exportImage(Pnml pnml) {
		if (image != null) {
			exportAttribute("image", image.toString(), pnml);
		}
		return "";
	}

	/**
	 * Sets the fill of the given graph element to this fill.
	 * 
	 * @param element
	 *            The given element.
	 */
	public void convertToNet(AbstractGraphElement element) {
		if (color != null) {
			Color decodedColor = PnmlColor.decode(color);
			if (decodedColor != null) {
				element.getAttributeMap().put(AttributeMap.FILLCOLOR, decodedColor);
			}
		}
	}

	public PnmlFill convertFromNet(AbstractGraphElement element) {
		PnmlFill fill = null;
		try {
			Color color = element.getAttributeMap().get(AttributeMap.FILLCOLOR, new Color(0));
			if (!Color.BLACK.equals(color)) {
				this.color = PnmlColor.encode(color);
				fill = this;
			}
		} catch (Exception ex) {
		}
		return fill;
	}
}
