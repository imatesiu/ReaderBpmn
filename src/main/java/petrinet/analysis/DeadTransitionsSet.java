package petrinet.analysis;

import models.graphbased.directed.petrinet.elements.Transition;

public class DeadTransitionsSet extends AbstractComponentSet<Transition> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3921976251135606947L;

	public DeadTransitionsSet() {
		super("Dead Transitions");
	}

	public boolean equals(Object o) {
		if (o instanceof DeadTransitionsSet) {
			return (super.equals(o));
		} else {
			return false;
		}
	}

}
