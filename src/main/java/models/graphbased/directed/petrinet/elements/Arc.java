package models.graphbased.directed.petrinet.elements;

import models.graphbased.AttributeMap;
import models.graphbased.AttributeMap.ArrowType;
import models.graphbased.directed.petrinet.PetrinetEdge;
import models.graphbased.directed.petrinet.PetrinetNode;

public class Arc extends PetrinetEdge<PetrinetNode, PetrinetNode> {

	private int weight;

	public Arc(PetrinetNode source, PetrinetNode target, int weight) {
		this(source, target, weight, null);
	}

	public Arc(PetrinetNode source, PetrinetNode target, int weight, ExpandableSubNet parent) {
		super(parent, source, target, "" + weight);
		this.weight = weight;
		getAttributeMap().put(AttributeMap.LABEL, "" + weight);
		getAttributeMap().put(AttributeMap.EDGEEND, ArrowType.ARROWTYPE_TECHNICAL);
		getAttributeMap().put(AttributeMap.EDGEENDFILLED, true);
		getAttributeMap().put(AttributeMap.SHOWLABEL, weight > 1);
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
		getAttributeMap().put(AttributeMap.LABEL, "" + weight);
		getAttributeMap().put(AttributeMap.SHOWLABEL, weight > 1);
		getGraph().graphElementChanged(this);
	}

	public String toString() {
		return getLabel() + " (" + weight + ")";
	}

}
