package petrinet.analysis;

import models.graphbased.directed.petrinet.elements.Transition;

public class NonLiveTransitionsSet extends AbstractComponentSet<Transition> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2498853569754675419L;

	public NonLiveTransitionsSet() {
		super("Non-live Transitions");
	}

	public boolean equals(Object o) {
		if (o instanceof NonLiveTransitionsSet) {
			return (super.equals(o));
		} else {
			return false;
		}
	}

}
