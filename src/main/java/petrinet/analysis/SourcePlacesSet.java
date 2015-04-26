package petrinet.analysis;

import models.graphbased.directed.petrinet.elements.Place;

public class SourcePlacesSet extends AbstractComponentSet<Place> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8078833652029774781L;

	public SourcePlacesSet() {
		super("Source places");
	}

	public boolean equals(Object o) {
		if (o instanceof SourcePlacesSet) {
			return (super.equals(o));
		} else {
			return false;
		}
	}

}
