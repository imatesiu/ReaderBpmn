package models.graphbased.directed.petrinet;

import models.graphbased.AttributeMap;
import models.graphbased.directed.AbstractDirectedGraph;
import models.graphbased.directed.AbstractDirectedGraphNode;
import models.graphbased.directed.ContainableDirectedGraphElement;
import models.graphbased.directed.petrinet.elements.ExpandableSubNet;

public abstract class PetrinetNode extends AbstractDirectedGraphNode implements ContainableDirectedGraphElement {

	private final ExpandableSubNet parent;
	protected int stdWidth = 50;
	protected int stdHeight = 50;

	private final AbstractDirectedGraph<PetrinetNode, PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> graph;

	public PetrinetNode(
			AbstractDirectedGraph<PetrinetNode, PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> net,
			ExpandableSubNet parent, String label) {
		super();
		graph = net;
		this.parent = parent;
		if (parent != null) {
			parent.addChild(this);
		}
		getAttributeMap().put(AttributeMap.LABEL, label);
	}

	public AbstractDirectedGraph<PetrinetNode, PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> getGraph() {
		return graph;
	}

	public ExpandableSubNet getParent() {
		return parent;
	}

}
