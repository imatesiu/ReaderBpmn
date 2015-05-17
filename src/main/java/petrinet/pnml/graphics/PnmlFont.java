package petrinet.pnml.graphics;

import models.graphbased.AbstractGraphElement;
import petrinet.pnml.Pnml;
import petrinet.pnml.PnmlElement;
import org.xmlpull.v1.XmlPullParser;

/**
 * PNML graphics font object.
 * 
 * @author hverbeek
 */
public class PnmlFont extends PnmlElement {

	/**
	 * PNML font tag.
	 */
	public final static String TAG = "font";

	/**
	 * Possible decorations.
	 */
	enum Decoration {
		DECORATION_DEFAULT, DECORATION_UNDERLINE, DECORATION_OVERLINE, DECORATION_LINETHROUGH;
	}

	/**
	 * Possible alignments.
	 */
	enum Align {
		ALIGN_DEFAULT, ALIGN_LEFT, ALIGN_CENTER, ALIGN_RIGHT;
	}

	/**
	 * Family attribute.
	 */
	private String family;
	/**
	 * Style attribute.
	 */
	private String style;
	/**
	 * Weight attribute.
	 */
	private String weight;
	/**
	 * Size attribute.
	 */
	private String size;
	/**
	 * Decoration attribute.
	 */
	private Decoration decoration;
	/**
	 * Align attribute.
	 */
	private Align align;
	/**
	 * Rotation attribute (and whether valid).
	 */
	private boolean hasRotation;
	private double rotation;

	/**
	 * Creates a fresh PNML font object.
	 */
	public PnmlFont() {
		super(TAG);
		family = null;
		style = null;
		weight = null;
		size = null;
		decoration = Decoration.DECORATION_DEFAULT;
		align = Align.ALIGN_DEFAULT;
		hasRotation = false;
	}

	/**
	 * Imports all known attributes.
	 */
	protected void importAttributes(XmlPullParser xpp, Pnml pnml) {
		super.importAttributes(xpp, pnml);
		/*
		 * Import family attribute.
		 */
		importFamily(xpp, pnml);
		/*
		 * Import style attribute.
		 */
		importStyle(xpp, pnml);
		/*
		 * Import weight attribute.
		 */
		importWeight(xpp, pnml);
		/*
		 * Import size attribute.
		 */
		importSize(xpp, pnml);
		/*
		 * Import decoration attribute.
		 */
		importDecoration(xpp, pnml);
		/*
		 * Import align attribute.
		 */
		importAlign(xpp, pnml);
		/*
		 * Import rotation attribute.
		 */
		importRotation(xpp, pnml);
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes(Pnml pnml) {
		return super.exportAttributes(pnml) + exportFamily(pnml) + exportStyle(pnml) + exportWeight(pnml)
				+ exportSize(pnml) + exportDecoration(pnml) + exportAlign(pnml) + exportRotation(pnml);
	}

	/**
	 * Imports family attribute.
	 * 
	 * @param xpp
	 * @param pnml
	 */
	private void importFamily(XmlPullParser xpp, Pnml pnml) {
		String value = xpp.getAttributeValue(null, "family");
		if (value != null) {
			family = value;
		}
	}

	/**
	 * Exports family attribute.
	 * 
	 * @return
	 */
	private String exportFamily(Pnml pnml) {
		if (family != null) {
			return exportAttribute("family", family, pnml);
		}
		return "";
	}

	/**
	 * Imports style attribute.
	 * 
	 * @param xpp
	 * @param pnml
	 */
	private void importStyle(XmlPullParser xpp, Pnml pnml) {
		String value = xpp.getAttributeValue(null, "style");
		if (value != null) {
			style = value;
		}
	}

	/**
	 * Exports style attribute.
	 * 
	 * @return
	 */
	private String exportStyle(Pnml pnml) {
		if (style != null) {
			return exportAttribute("style", style, pnml);
		}
		return "";
	}

	/**
	 * Imports weight attribute.
	 * 
	 * @param xpp
	 * @param pnml
	 */
	private void importWeight(XmlPullParser xpp, Pnml pnml) {
		String value = xpp.getAttributeValue(null, "weight");
		if (value != null) {
			weight = value;
		}
	}

	/**
	 * Exports weight attribute.
	 * 
	 * @return
	 */
	private String exportWeight(Pnml pnml) {
		if (weight != null) {
			return exportAttribute("weight", weight, pnml);
		}
		return "";
	}

	/**
	 * Imports size attribute.
	 * 
	 * @param xpp
	 * @param pnml
	 */
	private void importSize(XmlPullParser xpp, Pnml pnml) {
		String value = xpp.getAttributeValue(null, "size");
		if (value != null) {
			size = value;
		}
	}

	/**
	 * Exports size attribute.
	 * 
	 * @return
	 */
	private String exportSize(Pnml pnml) {
		if (size != null) {
			return exportAttribute("size", size, pnml);
		}
		return "";
	}

	/**
	 * Imports decoration attribute.
	 * 
	 * @param xpp
	 * @param pnml
	 */
	private void importDecoration(XmlPullParser xpp, Pnml pnml) {
		String value = xpp.getAttributeValue(null, "decoration");
		if (value != null) {
			if (value.equalsIgnoreCase("underline")) {
				decoration = Decoration.DECORATION_UNDERLINE;
			} else if (value.equalsIgnoreCase("overline")) {
				decoration = Decoration.DECORATION_OVERLINE;
			} else if (value.equalsIgnoreCase("line-through")) {
				decoration = Decoration.DECORATION_LINETHROUGH;
			}
		}
	}

	/**
	 * Exports decoration attribute.
	 * 
	 * @return
	 */
	private String exportDecoration(Pnml pnml) {
		switch (decoration) {
			case DECORATION_UNDERLINE :
				return exportAttribute("decoration", "underline", pnml);
			case DECORATION_OVERLINE :
				return exportAttribute("decoration", "overline", pnml);
			case DECORATION_LINETHROUGH :
				return exportAttribute("decoration", "line-through", pnml);
			default :
				// fall thru
		}
		return "";
	}

	/**
	 * Imports align attribute.
	 * 
	 * @param xpp
	 * @param pnml
	 */
	private void importAlign(XmlPullParser xpp, Pnml pnml) {
		String value = xpp.getAttributeValue(null, "align");
		if (value != null) {
			if (value.equalsIgnoreCase("left")) {
				align = Align.ALIGN_LEFT;
			} else if (value.equalsIgnoreCase("center")) {
				align = Align.ALIGN_CENTER;
			} else if (value.equalsIgnoreCase("right")) {
				align = Align.ALIGN_RIGHT;
			}
		}
	}

	/**
	 * Exports align attribute.
	 * 
	 * @return
	 */
	private String exportAlign(Pnml pnml) {
		switch (align) {
			case ALIGN_LEFT :
				return exportAttribute("align", "left", pnml);
			case ALIGN_CENTER :
				return exportAttribute("align", "center", pnml);
			case ALIGN_RIGHT :
				return exportAttribute("align", "right", pnml);
			default :
				// fall thru
		}
		return "";
	}

	/**
	 * Imports rotation attribute.
	 * 
	 * @param xpp
	 * @param pnml
	 */
	private void importRotation(XmlPullParser xpp, Pnml pnml) {
		String value = xpp.getAttributeValue(null, "rotation");
		if (value != null) {
			try {
				rotation = Double.valueOf(value);
				hasRotation = true;
			} catch (NumberFormatException e) {
				rotation = 0.0;
			}
		}
	}

	/**
	 * Exports rotation attribute.
	 * 
	 * @return
	 */
	private String exportRotation(Pnml pnml) {
		if (hasRotation) {
			return exportAttribute("rotation", String.valueOf(rotation), pnml);
		}
		return "";
	}

	/**
	 * Sets the font of the given graph element to this font.
	 * 
	 * @param element
	 *            The given element.
	 */
	public void convertToNet(AbstractGraphElement element) {

	}

	public PnmlFont convertFromNet(AbstractGraphElement element) {
		return null;
	}

}
