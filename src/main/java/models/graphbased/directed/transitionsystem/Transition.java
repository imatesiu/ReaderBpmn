package models.graphbased.directed.transitionsystem;

import models.graphbased.AttributeMap;
import models.graphbased.AttributeMap.ArrowType;
import models.graphbased.directed.AbstractDirectedGraphEdge;

public class Transition extends AbstractDirectedGraphEdge<State, State> {

	/**
	 * This field identifies the transition, i.e. it is the object corresponding
	 * to this transition.
	 */
	private final Object identifier;

	public Transition(State source, State target, Object identifier) {
		super(source, target);
		this.identifier = identifier;
		getAttributeMap().put(AttributeMap.LABEL, identifier.toString());
		getAttributeMap().put(AttributeMap.EDGEEND, ArrowType.ARROWTYPE_SIMPLE);
		getAttributeMap().put(AttributeMap.EDGEENDFILLED, true);
		getAttributeMap().put(AttributeMap.SHOWLABEL, true);
	}

	// The type-cast is safe, since the super.equals(o) tests for class
	// equivalence
	public boolean equals(Object o) {
		return super.equals(o) && identifier.equals(((Transition) o).identifier);
	}

	public Object getIdentifier() {
		return identifier;
	}

	public void setLabel(String label) {
		getAttributeMap().put(AttributeMap.LABEL, label);
	}

	public String getLabel() {
		Object o = getAttributeMap().get(AttributeMap.LABEL);
		return (o == null ? null : (String) o);
	}

}
