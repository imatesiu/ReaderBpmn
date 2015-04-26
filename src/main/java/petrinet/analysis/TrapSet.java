package petrinet.analysis;

import models.graphbased.directed.petrinet.elements.Place;

public class TrapSet extends AbstractComponentSet<Place> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4265303162763169680L;

	public TrapSet() {
		super("Traps");
	}

	public boolean equals(Object o) {
		if (o instanceof TrapSet) {
			return (super.equals(o));
		} else {
			return false;
		}
	}

}
