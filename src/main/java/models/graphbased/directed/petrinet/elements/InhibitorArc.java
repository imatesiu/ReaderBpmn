package models.graphbased.directed.petrinet.elements;

import models.graphbased.AttributeMap;
import models.graphbased.AttributeMap.ArrowType;
import models.graphbased.directed.petrinet.PetrinetEdge;

public class InhibitorArc extends PetrinetEdge<Place, Transition> {

	public InhibitorArc(Place source, Transition target, String label) {
		this(source, target, label, null);
	}

	public InhibitorArc(Place source, Transition target, String label, ExpandableSubNet parent) {
		super(parent, source, target, label);
		getAttributeMap().put(AttributeMap.EDGEEND, ArrowType.ARROWTYPE_CIRCLE);
		getAttributeMap().put(AttributeMap.EDGEENDFILLED, false);
	}

}
