package petrinet.analysis;

import models.graphbased.directed.petrinet.elements.Transition;

public class TransitionInvariantSet extends AbstractInvariantSet<Transition> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5247545898931212658L;

	public TransitionInvariantSet() {
		super("Transition Invariants");
	}

	public TransitionInvariantSet(String label) {
		super(label);
	}
	
	public boolean equals(Object o) {
		if (o instanceof TransitionInvariantSet) {
			return (super.equals(o));
		} else {
			return false;
		}
	}

}
