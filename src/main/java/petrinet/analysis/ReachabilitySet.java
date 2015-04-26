package petrinet.analysis;

import models.semantics.petrinet.Marking;

public class ReachabilitySet extends AbstractMarkingSet<Marking> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4694839865490777262L;

	public ReachabilitySet(Marking[] markings) {
		super("Reachability Set", markings);
	}

	public boolean equals(Object o) {
		if (o instanceof ReachabilitySet) {
			return (super.equals(o));
		} else {
			return false;
		}
	}

}
