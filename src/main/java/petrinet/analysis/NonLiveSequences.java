package petrinet.analysis;

import models.graphbased.directed.petrinet.PetrinetNode;

public class NonLiveSequences extends AbstractInvariantSet<PetrinetNode> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8831992116612127313L;

	public NonLiveSequences() {
		super("Non-lived Sequences");
	}

	public boolean equals(Object o) {
		if (o instanceof NonLiveSequences) {
			return (super.equals(o));
		} else {
			return false;
		}
	}

}
