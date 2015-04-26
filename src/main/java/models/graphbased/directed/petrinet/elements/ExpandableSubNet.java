package models.graphbased.directed.petrinet.elements;

import java.awt.Color;
import java.awt.Dimension;
import java.util.HashSet;
import java.util.Set;

import javax.swing.SwingConstants;

import models.graphbased.AttributeMap;
import models.graphbased.directed.AbstractDirectedGraph;
import models.graphbased.directed.ContainableDirectedGraphElement;
import models.graphbased.directed.ContainingDirectedGraphNode;
import models.graphbased.directed.petrinet.PetrinetEdge;
import models.graphbased.directed.petrinet.PetrinetNode;
import models.shapes.Rectangle;

public class ExpandableSubNet extends PetrinetNode implements ContainingDirectedGraphNode {

	private final Set<ContainableDirectedGraphElement> children;

	public ExpandableSubNet(String label,
			AbstractDirectedGraph<PetrinetNode, PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> net) {
		this(label, net, null);
	}

	public ExpandableSubNet(String label,
			AbstractDirectedGraph<PetrinetNode, PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> net,
			ExpandableSubNet parent) {
		super(net, parent, label);
		children = new HashSet<ContainableDirectedGraphElement>();
		getAttributeMap().put(AttributeMap.SHAPE, new Rectangle());
		getAttributeMap().put(AttributeMap.RESIZABLE, false);
		getAttributeMap().put(AttributeMap.LABELVERTICALALIGNMENT, SwingConstants.TOP);
		getAttributeMap().put(AttributeMap.FILLCOLOR, new Color(.95F, .95F, .95F, .95F));
		getAttributeMap().put(AttributeMap.PREF_ORIENTATION, SwingConstants.WEST);
	}

	public void addChild(ContainableDirectedGraphElement child) {
		children.add(child);
	}

	public Set<? extends ContainableDirectedGraphElement> getChildren() {
		return children;
	}

	public Dimension getCollapsedSize() {
		return new Dimension(stdWidth, stdHeight);
	}

}
