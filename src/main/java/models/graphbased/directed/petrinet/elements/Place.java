package models.graphbased.directed.petrinet.elements;

import java.awt.Dimension;

import javax.swing.SwingConstants;

import models.graphbased.AttributeMap;
import models.graphbased.directed.AbstractDirectedGraph;
import models.graphbased.directed.petrinet.PetrinetEdge;
import models.graphbased.directed.petrinet.PetrinetNode;
import models.shapes.Ellipse;

public class Place extends PetrinetNode {

	private static final long serialVersionUID = -140549782392304694L;

	public Place(String label,
			AbstractDirectedGraph<PetrinetNode, PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> net) {
		this(label, net, null);
	}

	public Place(String label,
			AbstractDirectedGraph<PetrinetNode, PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> net,
			ExpandableSubNet parent) {

		super(net, parent, label);
		getAttributeMap().put(AttributeMap.SHAPE, new Ellipse());
		getAttributeMap().put(AttributeMap.SQUAREBB, true);
		getAttributeMap().put(AttributeMap.RESIZABLE, true);
		getAttributeMap().put(AttributeMap.SIZE, new Dimension(25, 25));
		getAttributeMap().put(AttributeMap.SHOWLABEL, false);
		getAttributeMap().put(AttributeMap.LABELVERTICALALIGNMENT, SwingConstants.CENTER);
	}
}
