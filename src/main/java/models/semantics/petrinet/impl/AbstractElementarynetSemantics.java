/**
 * 
 */
package models.semantics.petrinet.impl;

import java.util.ArrayList;
import java.util.Collection;

import models.graphbased.directed.petrinet.PetrinetEdge;
import models.graphbased.directed.petrinet.PetrinetNode;
import models.graphbased.directed.petrinet.elements.Arc;
import models.graphbased.directed.petrinet.elements.Transition;

/**
 * Implementation of elementary-net semantics In this semantic, a transition can
 * only fires if all of its output places are empty
 * 
 * @author arya
 * @email arya.adriansyah@gmail.com
 * @version Nov 10, 2008
 */
abstract class AbstractElementarynetSemantics extends AbstractResetInhibitorNetSemantics {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7193203897297873842L;

	public AbstractElementarynetSemantics() {
	}

	/**
	 * method to override executable transition
	 */
	public Collection<Transition> getExecutableTransitions() {
		if (state == null) {
			return null;
		}
		// the tokens are divided over the places according to state
		ArrayList<Transition> enabled = new ArrayList<Transition>();
		transitionCheck: for (Transition trans : getTransitions()) {
			if (isEnabled(state, getRequired(trans), trans)) {
				// check if all of the output places are empty
				for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> e : trans.getGraph().getOutEdges(
						trans)) {
					if (e instanceof Arc) {
						Arc arc = (Arc) e;
						if (state.occurrences(arc.getTarget()) > 0) {
							continue transitionCheck;
						}
					}
				}

				enabled.add(trans);
			}
		}
		return enabled;
	}

	public String toString() {
		return "Elementary Semantics";
	}

	public int hashCode() {
		return getClass().hashCode();
	}

	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		return this.getClass().equals(o.getClass());
	}

}
