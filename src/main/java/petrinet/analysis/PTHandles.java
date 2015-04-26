package petrinet.analysis;

import models.graphbased.directed.petrinet.elements.Place;
import models.graphbased.directed.petrinet.elements.Transition;

public class PTHandles extends AbstractNodePairSet<Place, Transition> {

	public PTHandles() {
		super("P-T Handles");
	}

	private static final long serialVersionUID = -2459339556727382847L;

	public boolean equals(Object o) {
		if (o instanceof PTHandles ) {
			return (super.equals(o));
		} else {
			return false;
		}
	}

}
