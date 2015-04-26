package petrinet.analysis;

import models.graphbased.directed.petrinet.PetrinetNode;

public class UnboundedSequences extends AbstractInvariantSet<PetrinetNode> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6834173473872627348L;

	public UnboundedSequences() {
		super("Unbounded Sequences");
	}

	public boolean equals(Object o) {
		if (o instanceof UnboundedSequences) {
			return (super.equals(o));
		} else {
			return false;
		}
	}

}
