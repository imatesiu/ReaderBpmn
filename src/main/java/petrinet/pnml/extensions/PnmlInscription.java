package petrinet.pnml.extensions;

import models.graphbased.AbstractGraphElement;
import models.graphbased.directed.petrinet.elements.Arc;
import petrinet.pnml.Pnml;
import petrinet.pnml.PnmlAnnotation;
import org.xmlpull.v1.XmlPullParser;

/**
 * Extension PNML inscription object.
 * 
 * @author hverbeek
 */
public class PnmlInscription extends PnmlAnnotation {

	/**
	 * PNML inscription tag.
	 */
	public final static String TAG = "inscription";

	/**
	 * Creates a fresh inscription.
	 */
	public PnmlInscription() {
		super(TAG);
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
			/*
			 * Start tag corresponds to a known child element of a PNML
			 * annotation.
			 */
			return true;
		}
		/*
		 * Check whether text element present. If not, bail out.
		 */
		if (text == null) {
			return false;
		}
		return true;
	}

	protected void checkValidity(Pnml pnml) {
		super.checkValidity(pnml);
		/*
		 * Initial marking should be positive integer.
		 */
		int value = Integer.valueOf(text.getText());
		if (value <= 0) {
			pnml.log(tag, lineNumber, "Expected positive integer");
		}
	}

	/**
	 * Exports the inscription.
	 */
	protected String exportElements(Pnml pnml) {
		return super.exportElements(pnml);
	}

	/**
	 * Gets the inscription. Returns 1 if not specified.
	 * 
	 * @return
	 */
	public int getInscription() {
		try {
			return Integer.valueOf(text.getText());
		} catch (Exception ex) {
		}
		return 1;
	}

	/**
	 * Sets the weight of the given arc in the given net to this inscription.
	 * 
	 * @param arc
	 *            The given arc.
	 */
	public void convertToNet(Arc arc) {
		if (text != null) {
			super.convertToNet(arc);
		}
	}

	public PnmlInscription convertFromNet(AbstractGraphElement element) {
		PnmlInscription result = null;
		if (super.convertFromNet(element) != null) {
			result = this;
		}
		return result;
	}
}
