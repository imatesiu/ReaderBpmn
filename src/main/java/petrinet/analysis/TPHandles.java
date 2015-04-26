package petrinet.analysis;

import models.graphbased.directed.petrinet.elements.Place;
import models.graphbased.directed.petrinet.elements.Transition;

public class TPHandles extends AbstractNodePairSet<Transition, Place> {

	public TPHandles() {
		super("T-P Handles");
	}

	private static final long serialVersionUID = -1384057081561747558L;

	public boolean equals(Object o) {
		if (o instanceof TPHandles) {
			return (super.equals(o));
		} else {
			return false;
		}
	}

}
