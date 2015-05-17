package petrinet.pnml;

import java.util.Map;

import framework.util.Pair;
import models.connections.GraphLayoutConnection;
import models.graphbased.AbstractGraphElement;
import models.graphbased.directed.petrinet.PetrinetGraph;
import models.graphbased.directed.petrinet.elements.ExpandableSubNet;
import models.graphbased.directed.petrinet.elements.Place;
import org.xmlpull.v1.XmlPullParser;

/**
 * Basic PNML referencePlace object.
 * 
 * @author hverbeek
 */
public class PnmlReferencePlace extends PnmlNode {

	/**
	 * PNML referencePlace tag.
	 */
	public final static String TAG = "referencePlace";

	/**
	 * Ref attribute.
	 */
	private String idRef;

	/**
	 * Creates a fresh reference place.
	 */
	public PnmlReferencePlace() {
		super(TAG);
		idRef = null;
	}

	/**
	 * Imports all known attributes.
	 */
	protected void importAttributes(XmlPullParser xpp, Pnml pnml) {
		/*
		 * Import all known node attributes.
		 */
		super.importAttributes(xpp, pnml);
		/*
		 * Import ref attribute.
		 */
		importIdRef(xpp, pnml);
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes(Pnml pnml) {
		return super.exportAttributes(pnml) + exportIdRef(pnml);
	}

	/**
	 * Imports ref attribute.
	 * 
	 * @param xpp
	 * @param pnml
	 */
	private void importIdRef(XmlPullParser xpp, Pnml pnml) {
		String value = xpp.getAttributeValue(null, "ref");
		if (value != null) {
			idRef = value;
		}
	}

	/**
	 * Exports ref attribute.
	 * 
	 * @return
	 */
	private String exportIdRef(Pnml pnml) {
		if (idRef != null) {
			return exportAttribute("ref", idRef, pnml);
		}
		return "";
	}

	/**
	 * Check validity. Should have a ref attribute.
	 */
	protected void checkValidity(Pnml pnml) {
		super.checkValidity(pnml);
		if (idRef == null) {
			pnml.log(tag, lineNumber, "Expected ref");
		}
	}

	/**
	 * Converts this reference place to a regular Petri net place.
	 * 
	 * @param net
	 *            The net to add the place to.
	 * @param subNet
	 *            The sub net to add the place to.
	 * @param map
	 *            The places found so far.
	 */
	public void convertToNet(PetrinetGraph net, ExpandableSubNet subNet, Map<String, Place> map) {
		if (!map.containsKey(id) && map.containsKey(idRef)) {
			map.put(id, map.get(idRef));
		}
	}

	public PnmlReferencePlace convertFromNet(ExpandableSubNet parent, Place place,
			Map<Pair<AbstractGraphElement, ExpandableSubNet>, String> idMap, GraphLayoutConnection layout) {
		super.convertFromNet(parent, place, idMap, layout);
		idRef = idMap.get(new Pair<AbstractGraphElement, ExpandableSubNet>(place, place.getParent()));
		return this;
	}
}
