package petrinet.analysis;

import models.semantics.petrinet.Marking;

public class FinalMarkingSet extends AbstractMarkingSet<Marking> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1319443063264273225L;

	public FinalMarkingSet(Marking[] markings) {
		super("Final Markings", markings);
		// TODO Auto-generated constructor stub
	}

	public boolean equals(Object o) {
		if (o instanceof FinalMarkingSet) {
			return (super.equals(o));
		} else {
			return false;
		}
	}

}
