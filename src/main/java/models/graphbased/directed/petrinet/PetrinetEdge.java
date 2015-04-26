package models.graphbased.directed.petrinet;

import models.graphbased.AttributeMap;
import models.graphbased.directed.AbstractDirectedGraphEdge;
import models.graphbased.directed.ContainableDirectedGraphElement;
import models.graphbased.directed.petrinet.elements.ExpandableSubNet;

public abstract class PetrinetEdge<S extends PetrinetNode, T extends PetrinetNode> extends
		AbstractDirectedGraphEdge<S, T> implements ContainableDirectedGraphElement {

	private final ExpandableSubNet parent;

	public PetrinetEdge(ExpandableSubNet parent, S source, T target, String label) {
		super(source, target);
		this.parent = parent;
		getAttributeMap().put(AttributeMap.LABEL, label);
	}

	public ExpandableSubNet getParent() {
		return parent;
	}
}
