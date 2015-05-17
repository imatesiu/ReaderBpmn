package petrinet.pnml.extensions;

import models.graphbased.directed.petrinet.elements.Place;
import models.semantics.petrinet.Marking;
import petrinet.pnml.Pnml;
import petrinet.pnml.PnmlAnnotation;
import petrinet.pnml.PnmlText;
import org.xmlpull.v1.XmlPullParser;

/**
 * Extension PNML initial marking object.
 * 
 * @author hverbeek
 */
public class PnmlInitialMarking extends PnmlAnnotation {

	/**
	 * PNML initial marking tag.
	 */
	public final static String TAG = "initialMarking";

	/**
	 * Creates a fresh initial marking object.
	 */
	public PnmlInitialMarking() {
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
		 * Initial marking should be non-negative integer.
		 */
		int value = Integer.valueOf(text.getText());
		if (value < 0) {
			pnml.log(tag, lineNumber, "Expected non-negative integer");
		}
	}

	/**
	 * Exports the initial marking.
	 */
	protected String exportElements(Pnml pnml) {
		return super.exportElements(pnml);
	}

	/**
	 * Gets the initial marking, returns 0 if not specified.
	 * 
	 * @return
	 */
	public int getInitialMarking() {
		try {
			return Integer.valueOf(text.getText());
		} catch (Exception ex) {
		}
		return 0;
	}

	public PnmlInitialMarking convertFromNet(Marking marking, Place place) {
		PnmlInitialMarking result = null;
		if (marking.contains(place)) {
			super.convertFromNet(place);
			text = new PnmlText();
			text.setText("" + marking.occurrences(place));
			result = this;
		}
		return result;
	}

}
