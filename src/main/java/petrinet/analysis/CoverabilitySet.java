package petrinet.analysis;

import models.semantics.petrinet.CTMarking;

public class CoverabilitySet extends AbstractMarkingSet<CTMarking> {

	private static final long serialVersionUID = 3773252793683665035L;

	public CoverabilitySet(CTMarking[] markings) {
		super("Coverability Set", markings);
	}

	public boolean equals(Object o) {
		if (o instanceof CoverabilitySet ) {
			return (super.equals(o));
		} else {
			return false;
		}
	}

}
