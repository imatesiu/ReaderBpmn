package petrinet.pnml.graphics;

import java.awt.Color;

import org.jgraph.graph.GraphConstants;
import models.graphbased.AbstractGraphElement;
import models.graphbased.AttributeMap;
import models.graphbased.directed.petrinet.elements.Arc;
import petrinet.pnml.Pnml;
import petrinet.pnml.PnmlElement;
import petrinet.pnml.graphics.utils.PnmlColor;
import org.xmlpull.v1.XmlPullParser;

/**
 * PNML graphics line object.
 * 
 * @author hverbeek
 */
public class PnmlLine extends PnmlElement {

	/**
	 * PNML line tag.
	 */
	public final static String TAG = "line";

	/**
	 * Possible shapes.
	 */
	enum Shape {
		SHAPE_DEFAULT, SHAPE_LINE, SHAPE_CURVE;
	}

	/**
	 * Possible styles.
	 */
	enum Style {
		STYLE_DEFAULT, STYLE_SOLID, STYLE_DASH, STYLE_DOT;
	}

	/**
	 * Shape attribute.
	 */
	private Shape shape;
	/**
	 * Shape color.
	 */
	private String color;
	/**
	 * Shape width (and whether valid).
	 */
	private boolean hasWidth;
	private double width;
	/**
	 * Shape style.
	 */
	private Style style;

	/**
	 * Creates a fresh PNML line object.
	 */
	public PnmlLine() {
		super(TAG);
		shape = Shape.SHAPE_DEFAULT;
		color = null;
		hasWidth = false;
		style = Style.STYLE_DEFAULT;
	}

	/**
	 * Imports all known attributes.
	 */
	protected void importAttributes(XmlPullParser xpp, Pnml pnml) {
		super.importAttributes(xpp, pnml);
		/*
		 * Import shape attribute.
		 */
		importShape(xpp, pnml);
		/*
		 * Import color attribute.
		 */
		importColor(xpp, pnml);
		/*
		 * Import width attribute.
		 */
		importWidth(xpp, pnml);
		/*
		 * Import style attribute.
		 */
		importStyle(xpp, pnml);
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes(Pnml pnml) {
		return super.exportAttributes(pnml) + exportShape(pnml) + exportColor(pnml) + exportWidth(pnml)
				+ exportStyle(pnml);
	}

	/**
	 * Imports shape attribute.
	 * 
	 * @param xpp
	 * @param pnml
	 */
	private void importShape(XmlPullParser xpp, Pnml pnml) {
		String value = xpp.getAttributeValue(null, "shape");
		if (value != null) {
			if (value.equals("line")) {
				shape = Shape.SHAPE_LINE;
			} else if (value.equals("curve")) {
				shape = Shape.SHAPE_CURVE;
			}
		}
	}

	/**
	 * Exports shape attribute.
	 * 
	 * @return
	 */
	private String exportShape(Pnml pnml) {
		switch (shape) {
			case SHAPE_LINE :
				return exportAttribute("shape", "line", pnml);
			case SHAPE_CURVE :
				return exportAttribute("shape", "curve", pnml);
			default :
				// fall thru	
		}
		return "";
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
	 * Imports width attribute.
	 * 
	 * @param xpp
	 * @param pnml
	 */
	private void importWidth(XmlPullParser xpp, Pnml pnml) {
		String value = xpp.getAttributeValue(null, "width");
		if (value != null) {
			try {
				width = Double.valueOf(value);
				hasWidth = true;
			} catch (NumberFormatException e) {
				width = 1.0;
			}
		}
	}

	/**
	 * Exports width attribute.
	 * 
	 * @return
	 */
	private String exportWidth(Pnml pnml) {
		if (hasWidth) {
			return exportAttribute("width", String.valueOf(width), pnml);
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
			if (value.equals("solid")) {
				style = Style.STYLE_SOLID;
			} else if (value.equals("dash")) {
				style = Style.STYLE_DASH;
			} else if (value.equals("dot")) {
				style = Style.STYLE_DOT;
			}
		}
	}

	/**
	 * Exports style attribute.
	 * 
	 * @return
	 */
	private String exportStyle(Pnml pnml) {
		switch (style) {
			case STYLE_SOLID :
				return exportAttribute("style", "solid", pnml);
			case STYLE_DASH :
				return exportAttribute("style", "dash", pnml);
			case STYLE_DOT :
				return exportAttribute("style", "dot", pnml);
			default :
				// fall thru	
		}
		return "";
	}

	/**
	 * Sets the line of the given graph element to this line.
	 * 
	 * @param element
	 *            The given element.
	 */
	public void convertToNet(AbstractGraphElement element) {
		if (shape != Shape.SHAPE_DEFAULT) {
			int style = (shape == Shape.SHAPE_LINE ? GraphConstants.STYLE_ORTHOGONAL : GraphConstants.STYLE_SPLINE);
			element.getAttributeMap().put(AttributeMap.STYLE, style);
		} else {
			element.getAttributeMap().put(AttributeMap.STYLE, GraphConstants.STYLE_ORTHOGONAL);
		}
		if (color != null) {
			/*
			 * Set color.
			 */
			Color decodedColor = PnmlColor.decode(color);
			if (decodedColor != null) {
				if (element instanceof Arc) {
					element.getAttributeMap().put(AttributeMap.EDGECOLOR, decodedColor);
				} else {
					element.getAttributeMap().put(AttributeMap.STROKECOLOR, decodedColor);
				}
			}
		}
		if (hasWidth) {
			/*
			 * Set width.
			 */
			// This is an utterly stupid way to convert a Double to a Float, but is there another way?
			element.getAttributeMap().put(AttributeMap.LINEWIDTH, Float.valueOf(String.valueOf(width)));
		}
		if (style != Style.STYLE_DEFAULT) {
			/*
			 * Set style.
			 */
			float[] dashPattern = new float[2];
			switch (style) {
				case STYLE_DASH :
					dashPattern[0] = (float) 3.0;
					dashPattern[1] = (float) 3.0;
					break;
				case STYLE_DOT :
					dashPattern[0] = (float) 1.0;
					dashPattern[1] = (float) 3.0;
					break;
				default :
					dashPattern[0] = (float) 1.0;
					dashPattern[1] = (float) 0.0;
					break;
			}
			element.getAttributeMap().put(AttributeMap.DASHPATTERN, dashPattern);
		}
	}

	public PnmlLine convertFromNet(AbstractGraphElement element) {
		PnmlLine line = null;
		try {
			int style = element.getAttributeMap().get(AttributeMap.STYLE, GraphConstants.STYLE_ORTHOGONAL);
			switch (style) {
				case GraphConstants.STYLE_SPLINE :
					// Fall thru. PNML supports only line and curve, both Bezier and Spline are mapped onto curve.
				case GraphConstants.STYLE_BEZIER :
					shape = Shape.SHAPE_CURVE;
					break;
				case GraphConstants.STYLE_ORTHOGONAL :
					shape = Shape.SHAPE_LINE;
					break;
			}
		} catch (Exception ex) {
			System.err.println(ex);
		}
		try {
			Color color;
			if (element instanceof Arc) {
				color = element.getAttributeMap().get(AttributeMap.EDGECOLOR, new Color(0));
			} else {
				color = element.getAttributeMap().get(AttributeMap.STROKECOLOR, new Color(0));
			}
			if (!color.equals(Color.BLACK)) {
				this.color = PnmlColor.encode(color);
				line = this;
			}
		} catch (Exception ex) {
			System.err.println(ex);
		}
		try {
			Float width = element.getAttributeMap().get(AttributeMap.LINEWIDTH, 0.0F);
			if (width != 0.0) {
				hasWidth = true;
				this.width = Double.valueOf(String.valueOf(width));
				line = this;
			}
		} catch (Exception ex) {
		}
		try {
			float[] dash = element.getAttributeMap().get(AttributeMap.DASHPATTERN, new float[] {});
			if (dash.length > 0) {
				if (dash[0] == dash[1]) {
					style = Style.STYLE_DASH;
				} else if (dash[0] < dash[1]) {
					style = Style.STYLE_DOT;
				} else {
					style = Style.STYLE_SOLID;
				}
				line = this;
			}
		} catch (Exception ex) {
		}
		return line;
	}
}
