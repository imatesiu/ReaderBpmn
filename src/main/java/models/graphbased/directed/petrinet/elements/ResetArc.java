package models.graphbased.directed.petrinet.elements;

import models.graphbased.AttributeMap;
import models.graphbased.AttributeMap.ArrowType;
import models.graphbased.directed.petrinet.PetrinetEdge;

public class ResetArc extends PetrinetEdge<Place, Transition> {

	public ResetArc(Place source, Transition target, String label) {
		this(source, target, label, null);
	}

	public ResetArc(Place source, Transition target, String label, ExpandableSubNet parent) {
		super(parent, source, target, label);
		getAttributeMap().put(AttributeMap.EDGEEND, ArrowType.ARROWTYPE_SIMPLE);
		getAttributeMap().put(AttributeMap.EDGEENDFILLED, false);
	}

}
