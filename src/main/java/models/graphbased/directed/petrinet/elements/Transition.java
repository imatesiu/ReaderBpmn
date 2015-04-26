package models.graphbased.directed.petrinet.elements;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Arrays;
import java.util.Collection;

import models.graphbased.AttributeMap;
import models.graphbased.directed.AbstractDirectedGraph;
import models.graphbased.directed.petrinet.PetrinetEdge;
import models.graphbased.directed.petrinet.PetrinetNode;
import models.graphbased.directed.utils.GraphIterator;
import models.graphbased.directed.utils.GraphIterator.EdgeAcceptor;
import models.graphbased.directed.utils.GraphIterator.NodeAcceptor;
import models.shapes.Rectangle;

public class Transition extends PetrinetNode {

	private static final long serialVersionUID = 4097151075956253592L;

	private boolean isInvisible;

	public Transition(String label,
			AbstractDirectedGraph<PetrinetNode, PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> net) {
		this(label, net, null);
	}

	public Transition(String label,
			AbstractDirectedGraph<PetrinetNode, PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> net,
			ExpandableSubNet parent) {
		super(net, parent, label);
		getAttributeMap().put(AttributeMap.SHAPE, new Rectangle());
		getAttributeMap().put(AttributeMap.RESIZABLE, true);
		setInvisible(false);
	}

	public void setInvisible(boolean invisible) {
		isInvisible = invisible;
		if (isInvisible) {
			getAttributeMap().put(AttributeMap.SIZE, new Dimension(30, 30));
			getAttributeMap().put(AttributeMap.SHOWLABEL, false);
			getAttributeMap().put(AttributeMap.FILLCOLOR, Color.BLACK);
		} else {
			getAttributeMap().put(AttributeMap.SIZE, new Dimension(50, 40));
			getAttributeMap().put(AttributeMap.SHOWLABEL, true);
			getAttributeMap().put(AttributeMap.FILLCOLOR, null);
		}
	}

	public boolean isInvisible() {
		return isInvisible;
	}

	public Collection<Transition> getVisiblePredecessors() {

		final NodeAcceptor<PetrinetNode> nodeAcceptor = new NodeAcceptor<PetrinetNode>() {
			public boolean acceptNode(PetrinetNode node, int depth) {
				return ((depth != 0) && (node instanceof Transition) && !((Transition) node).isInvisible());
			}
		};

		Collection<PetrinetNode> transitions = GraphIterator.getDepthFirstPredecessors(this, getGraph(),
				new EdgeAcceptor<PetrinetNode, PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>>() {

					public boolean acceptEdge(PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge,
							int depth) {
						return !nodeAcceptor.acceptNode(edge.getTarget(), depth);
					}
				}, nodeAcceptor);
		return Arrays.asList(transitions.toArray(new Transition[0]));

	}

	public Collection<Transition> getVisibleSuccessors() {

		final NodeAcceptor<PetrinetNode> nodeAcceptor = new NodeAcceptor<PetrinetNode>() {
			public boolean acceptNode(PetrinetNode node, int depth) {
				return ((depth != 0) && (node instanceof Transition) && !((Transition) node).isInvisible());
			}
		};

		Collection<PetrinetNode> transitions = GraphIterator.getDepthFirstSuccessors(this, getGraph(),
				new EdgeAcceptor<PetrinetNode, PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>>() {

					public boolean acceptEdge(PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge,
							int depth) {
						return !nodeAcceptor.acceptNode(edge.getSource(), depth);
					}
				}, nodeAcceptor);

		return Arrays.asList(transitions.toArray(new Transition[0]));
	}

}
