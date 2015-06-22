package petrinet.pnml;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.utils.Pair;
import models.connections.GraphLayoutConnection;
import models.graphbased.AbstractGraphElement;

import models.graphbased.directed.petrinet.PetrinetGraph;
import models.graphbased.directed.petrinet.elements.ExpandableSubNet;
import models.graphbased.directed.petrinet.elements.Transition;
import org.xmlpull.v1.XmlPullParser;

/**
 * Basic PNML transition object.
 * 
 * @author hverbeek
 */
public class PnmlTransition extends PnmlNode {

	/**
	 * PNML transition tag.
	 */
	public final static String TAG = "transition";

	
	/**
	 * Creates a fresh PNML transition.
	 */
	public PnmlTransition() {
		super(TAG);

		
	}

	protected boolean importElements(XmlPullParser xpp, Pnml pnml) {
		if (super.importElements(xpp, pnml)) {
			/*
			 * Start tag corresponds to a known child element of a PNML
			 * annotation.
			 */
			return true;
		}
		/*
		 * If Open net, then handle Open net edges.
		 */
		
		return false;
	}

	protected String exportElements(Pnml pnml) {
		String s = super.exportElements(pnml);
		/*
		 * Check for any Open net edges and handle them properly.
		 */
		
		return s;
	}

	/**
	 * Converts this transition to a Petri net transition.
	 * 
	 * @param net
	 *            The net to add the transition to.
	 * @param subNet
	 *            The sub net to add the transition to.
	 * @param map
	 *            The transitions found so far.
	 */
	public void convertToNet(PetrinetGraph net, ExpandableSubNet subNet, Map<String, Transition> map,
			Point2D.Double displacement, GraphLayoutConnection layout) {
		/*
		 * Add the transition to the net.
		 */
		Transition transition = net.addTransition((((name != null) && (name.text != null)) ? name.text.getText() : id),
				subNet);
		super.convertToNet(subNet, transition, displacement, layout);
		/*
		 * Register new transition found.
		 */
		map.put(id, transition);

		/*
		 * If Open net, then handle Open net edges.
		 */

		
	}

	public PnmlTransition convertFromNet(PetrinetGraph net, ExpandableSubNet parent, Transition element,
			Map<Pair<AbstractGraphElement, ExpandableSubNet>, String> idMap, GraphLayoutConnection layout) {
		super.convertFromNet(parent, element, idMap, layout);

		/*
		 * Check for any Open net edges and handle them properly.
		 */
		
		return this;
	}
}
