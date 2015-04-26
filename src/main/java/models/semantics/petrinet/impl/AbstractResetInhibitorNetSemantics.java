package models.semantics.petrinet.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import models.graphbased.directed.petrinet.PetrinetEdge;
import models.graphbased.directed.petrinet.PetrinetNode;
import models.graphbased.directed.petrinet.elements.Arc;
import models.graphbased.directed.petrinet.elements.InhibitorArc;
import models.graphbased.directed.petrinet.elements.Place;
import models.graphbased.directed.petrinet.elements.ResetArc;
import models.graphbased.directed.petrinet.elements.Transition;
import models.semantics.IllegalTransitionException;
import models.semantics.petrinet.Marking;
import models.semantics.petrinet.PetrinetExecutionInformation;

abstract class AbstractResetInhibitorNetSemantics {

	protected Marking state;
	private Collection<Transition> transitions;

	public AbstractResetInhibitorNetSemantics() {
		this(null);
	}

	public AbstractResetInhibitorNetSemantics(Marking state) {
		this.state = state;

	}

	public void initialize(Collection<Transition> transitions, Marking state) {
		this.transitions = transitions;
		setCurrentState(state);
	}

	protected Collection<Transition> getTransitions() {
		return Collections.unmodifiableCollection(transitions);
	}

	protected boolean isEnabled(Marking state, Marking required, Transition t) {
		if (required.isLessOrEqual(state)) {
			for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> e : t.getGraph().getInEdges(t)) {
				if (e instanceof InhibitorArc) {
					InhibitorArc arc = (InhibitorArc) e;
					if (state.occurrences(arc.getSource()) > 0) {
						return false;
					}
				}
			}
			return true;
		}
		return false;
	}

	public Marking getCurrentState() {
		return state;
	}

	public void setCurrentState(Marking currentState) {
		state = currentState;
	}

	public PetrinetExecutionInformation executeExecutableTransition(Transition toExecute)
			throws IllegalTransitionException {
		Marking required = getRequired(toExecute);
		Marking newState = new Marking(state);
		if (!isEnabled(state, required, toExecute)) {
			throw new IllegalTransitionException(toExecute, newState);
		}
		Marking produced = getProduced(toExecute);
		newState.addAll(produced);
		Marking toRemove = getRemoved(toExecute);
		newState.removeAll(toRemove);
		state = newState;
		return new PetrinetExecutionInformation(required, toRemove, produced, toExecute);
	}

	public Collection<Transition> getExecutableTransitions() {
		if (state == null) {
			return null;
		}
		// the tokens are divided over the places according to state
		ArrayList<Transition> enabled = new ArrayList<Transition>();
		for (Transition trans : getTransitions()) {
			if (isEnabled(state, getRequired(trans), trans)) {
				enabled.add(trans);
			}
		}
		return enabled;
	}

	protected Marking getRequired(Transition trans) {
		Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> edges = trans.getGraph().getInEdges(
				trans);
		Marking required = new Marking();
		for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> e : edges) {
			if (e instanceof Arc) {
				Arc arc = (Arc) e;
				required.add((Place) arc.getSource(), arc.getWeight());
			}
		}
		return required;

	}

	protected Marking getProduced(Transition trans) {
		Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> edges = trans.getGraph().getOutEdges(
				trans);
		Marking produced = new Marking();
		for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> e : edges) {
			if (e instanceof Arc) {
				Arc arc = (Arc) e;
				produced.add((Place) arc.getTarget(), arc.getWeight());
			}
		}

		return produced;

	}

	protected Marking getRemoved(Transition trans) {
		Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> edges = trans.getGraph().getInEdges(
				trans);
		Marking removed = new Marking(getRequired(trans));
		for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> e : edges) {
			if (e instanceof ResetArc) {
				ResetArc arc = (ResetArc) e;
				removed.add(arc.getSource(), state.occurrences(arc.getSource()));
			}
		}
		return removed;
	}

	public String toString() {
		return "Regular Semantics";
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
