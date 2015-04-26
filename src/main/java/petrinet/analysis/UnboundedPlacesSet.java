package petrinet.analysis;

import models.graphbased.directed.petrinet.elements.Place;

public class UnboundedPlacesSet extends AbstractComponentSet<Place> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2592610099968245201L;

	public UnboundedPlacesSet() {
		super("Non-bounded Places");
	}

	public boolean equals(Object o) {
		if (o instanceof UnboundedPlacesSet) {
			return (super.equals(o));
		} else {
			return false;
		}
	}

}
