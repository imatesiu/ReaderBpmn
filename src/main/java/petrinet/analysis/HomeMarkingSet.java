package petrinet.analysis;

import models.semantics.petrinet.Marking;

public class HomeMarkingSet extends AbstractMarkingSet<Marking> {
	private static final long serialVersionUID = -8606015016458758368L;

	public HomeMarkingSet(Marking[] markings) {
		super("Home Markings", markings);
	}

	public boolean equals(Object o) {
		if (o instanceof HomeMarkingSet) {
			return (super.equals(o));
		} else {
			return false;
		}
	}

}
