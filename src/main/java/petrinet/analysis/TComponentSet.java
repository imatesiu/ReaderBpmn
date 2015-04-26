package petrinet.analysis;

import models.graphbased.directed.petrinet.PetrinetNode;

public class TComponentSet extends AbstractComponentSet<PetrinetNode> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4283509342764646664L;

	public TComponentSet() {
		super("T-Components");
	}

	public boolean equals(Object o) {
		if (o instanceof TComponentSet) {
			return (super.equals(o));
		} else {
			return false;
		}
	}

}
