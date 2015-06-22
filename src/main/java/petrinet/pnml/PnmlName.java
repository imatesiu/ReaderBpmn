package petrinet.pnml;

import models.graphbased.AbstractGraphElement;
import models.graphbased.AttributeMap;

/**
 * Basic PNML name object.
 * 
 * @author hverbeek
 */
public class PnmlName extends PnmlAnnotation {

	/**
	 * PNML name tag.
	 */
	public final static String TAG = "name";

	/**
	 * Creates a fresh PNML name.
	 */
	public PnmlName() {
		super(TAG);
	}

	public PnmlName(String text) {
		super(text, TAG);
	}

	public String getName(String defaultName) {
		if (text != null) {
			return text.getText();
		}
		return defaultName;
	}

	public PnmlName convertFromNet(AbstractGraphElement element) {
		PnmlName result = null;
		if (element.getAttributeMap().containsKey(AttributeMap.LABEL)) {
			text = new PnmlText(element.getAttributeMap().get(AttributeMap.LABEL, ""));
			result = this;
		}
		return result;
	}
}
