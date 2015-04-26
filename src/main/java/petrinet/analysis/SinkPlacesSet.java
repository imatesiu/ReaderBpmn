package petrinet.analysis;

import models.graphbased.directed.petrinet.elements.Place;

public class SinkPlacesSet extends AbstractComponentSet<Place> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4054839887327904941L;

	public SinkPlacesSet() {
		super("Sink places");
	}

	public boolean equals(Object o) {
		if (o instanceof SinkPlacesSet) {
			return (super.equals(o));
		} else {
			return false;
		}
	}

}
