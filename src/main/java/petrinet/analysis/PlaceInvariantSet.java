package petrinet.analysis;

import models.graphbased.directed.petrinet.elements.Place;

public class PlaceInvariantSet extends AbstractInvariantSet<Place> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 259873416674078415L;

	public PlaceInvariantSet() {
		super("Place Invariants");
	}

	public PlaceInvariantSet(String label) {
		super(label);
	}
	
	public boolean equals(Object o) {
		if (o instanceof PlaceInvariantSet) {
			return (super.equals(o));
		} else {
			return false;
		}
	}
}
