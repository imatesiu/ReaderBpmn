package models.graphbased.directed.transitionsystem;

import java.util.Collection;

import models.graphbased.directed.DirectedGraph;

public interface TransitionSystem extends DirectedGraph<State, Transition> {

	String getLabel();

	// transitions
	boolean addTransition(Object fromState, Object toState, Object identifier);

	Object removeTransition(Object fromState, Object toState, Object identifier);

	Collection<Object> getTransitions();

	Collection<Transition> getEdges(Object identifier);

	// states
	boolean addState(Object identifier);

	Object removeState(Object identifier);

	Collection<? extends Object> getStates();

	State getNode(Object identifier);

	Transition findTransition(Object fromState, Object toState, Object identifier);

}
