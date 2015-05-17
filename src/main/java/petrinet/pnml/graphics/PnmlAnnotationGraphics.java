package petrinet.pnml.graphics;

import models.graphbased.AbstractGraphElement;
import petrinet.pnml.Pnml;
import petrinet.pnml.PnmlElement;
import org.xmlpull.v1.XmlPullParser;

/**
 * PNML annotation graphics.
 * 
 * @author hverbeek
 */
public class PnmlAnnotationGraphics extends PnmlElement {

	/**
	 * PNML annotation graphics tag.
	 */
	public final static String TAG = "graphics";

	/**
	 * Offset element.
	 */
	private PnmlOffset offset;
	/**
	 * Font element.
	 */
	private PnmlFont font;
	/**
	 * Fill element.
	 */
	private PnmlFill fill;
	/**
	 * Line element.
	 */
	private PnmlLine line;

	/**
	 * Creates a fresh PNML annotation graphics.
	 */
	public PnmlAnnotationGraphics() {
		super(TAG);
		offset = null;
		font = null;
		fill = null;
		line = null;
	}

	/**
	 * Checks whether the current start tag is known. If known, it imports the
	 * corresponding child element and returns true. Otherwise, it returns
	 * false.
	 * 
	 * @return Whether the start tag was known.
	 */
	protected boolean importElements(XmlPullParser xpp, Pnml pnml) {
		if (super.importElements(xpp, pnml)) {
			return true;
		}
		if (xpp.getName().equals(PnmlOffset.TAG)) {
			/*
			 * Offset element.
			 */
			offset = new PnmlOffset();
			offset.importElement(xpp, pnml);
			return true;
		}
		if (xpp.getName().equals(PnmlFont.TAG)) {
			/*
			 * Font element.
			 */
			font = new PnmlFont();
			font.importElement(xpp, pnml);
			return true;
		}
		if (xpp.getName().equals(PnmlFill.TAG)) {
			/*
			 * Fill element.
			 */
			fill = new PnmlFill();
			fill.importElement(xpp, pnml);
			return true;
		}
		if (xpp.getName().equals(PnmlLine.TAG)) {
			/*
			 * Line element.
			 */
			line = new PnmlLine();
			line.importElement(xpp, pnml);
			return true;
		}
		/*
		 * Unknown start tag.
		 */
		return false;
	}

	/**
	 * Exports the annotation graphics.
	 */
	protected String exportElements(Pnml pnml) {
		String s = super.exportElements(pnml);
		if (offset != null) {
			s += offset.exportElement(pnml);
		}
		if (font != null) {
			s += font.exportElement(pnml);
		}
		if (fill != null) {
			s += fill.exportElement(pnml);
		}
		if (line != null) {
			s += line.exportElement(pnml);
		}
		return s;
	}

	/**
	 * Checks validity. Should have an offset element.
	 */
	protected void checkValidity(Pnml pnml) {
		super.checkValidity(pnml);
		if (offset == null) {
			pnml.log(tag, lineNumber, "Expected offset");
		}
	}

	/**
	 * Sets the graphics for the given graph element.
	 * 
	 * @param net
	 *            The given net.
	 * @param subNet
	 *            The given sub net.
	 * @param element
	 *            The given element.
	 */
	public void convertToNet(AbstractGraphElement element) {
		if (offset != null) {
			offset.convertToNet(element);
		}
		if (font != null) {
			font.convertToNet(element);
		}
		if (fill != null) {
			fill.convertToNet(element);
		}
		if (line != null) {
			line.convertToNet(element);
		}
	}

	public PnmlAnnotationGraphics convertFromNet(AbstractGraphElement element) {
		PnmlAnnotationGraphics result = null;
		try {
			offset = new PnmlOffset().convertFromNet(element);
			font = new PnmlFont().convertFromNet(element);
			fill = new PnmlFill().convertFromNet(element);
			line = new PnmlLine().convertFromNet(element);
			if ((offset != null) || (font != null) || (fill != null) || (line != null)) {
				if (offset == null) {
					offset = new PnmlOffset(0.0, 0.0);
				}
				result = this;
			}
		} catch (Exception ex) {
		}
		return result;
	}
}
