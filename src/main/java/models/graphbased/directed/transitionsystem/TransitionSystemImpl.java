package models.graphbased.directed.transitionsystem;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.SwingConstants;

import models.graphbased.AttributeMap;
import models.graphbased.directed.AbstractDirectedGraph;
import models.graphbased.directed.DirectedGraph;
import models.graphbased.directed.DirectedGraphEdge;
import models.graphbased.directed.DirectedGraphElement;
import models.graphbased.directed.DirectedGraphNode;

public class TransitionSystemImpl extends AbstractDirectedGraph<State, Transition> implements TransitionSystem {

	private final Map<Object, State> states = new LinkedHashMap<Object, State>();
	private final Set<State> nodes = new LinkedHashSet<State>();
	private final Map<Object, Set<Transition>> transitions = new LinkedHashMap<Object, Set<Transition>>();
	private final Set<Transition> edges = new LinkedHashSet<Transition>();

	private Map<Object, Object> proxyMap;
	
	public TransitionSystemImpl(String label) {
		super();
		getAttributeMap().put(AttributeMap.LABEL, label);
		getAttributeMap().put(AttributeMap.PREF_ORIENTATION, SwingConstants.NORTH);
		
		proxyMap = new HashMap<Object, Object>();
	}

	@Override
	protected synchronized Map<DirectedGraphElement, DirectedGraphElement> cloneFrom(
			DirectedGraph<State, Transition> graph) {
		assert (graph instanceof TransitionSystemImpl);
		Map<DirectedGraphElement, DirectedGraphElement> mapping = new HashMap<DirectedGraphElement, DirectedGraphElement>();

		TransitionSystemImpl ts = (TransitionSystemImpl) graph;
		for (Object identifier : ts.states.keySet()) {
			addState(identifier);
			mapping.put(ts.states.get(identifier), getNode(identifier));
		}
		for (Transition trans : getEdges()) {
			addTransition(trans.getSource().getIdentifier(), trans.getTarget().getIdentifier(), trans.getIdentifier());
			mapping.put(trans, findTransition(trans.getSource().getIdentifier(), trans.getTarget().getIdentifier(),
					trans.getIdentifier()));
		}
		return mapping;
	}

	@Override
	protected synchronized AbstractDirectedGraph<State, Transition> getEmptyClone() {
		return new TransitionSystemImpl(getLabel());
	}

	@Override
	public synchronized void removeEdge(@SuppressWarnings("rawtypes") DirectedGraphEdge edge) {
		assert (edge instanceof Transition);
		Transition t = (Transition) edge;
		removeTransition(t);
	}

	public synchronized void removeNode(DirectedGraphNode node) {
		if (node instanceof State) {
			removeStateInternal((State) node);
		} else {
			assert (false);
		}
	}

	public synchronized Collection<Object> getTransitions() {
		return transitions.keySet();
	}

	private synchronized Object removeTransition(Transition transition) {
		Transition result = removeNodeFromCollection(edges, transition);
		if (result == null) {
			return null;
		} else {
			Object identifier = result.getIdentifier();
			Set<Transition> set = transitions.get(identifier);
			set.remove(transition);
			if (set.isEmpty()) {
				transitions.remove(identifier);
			}
			return result.getIdentifier();
		}
	}

	public synchronized Set<Transition> getEdges() {
		return Collections.unmodifiableSet(edges);
	}

	public synchronized Set<State> getNodes() {
		return nodes;
	}

	public synchronized boolean addState(Object identifier) {
		if (!states.containsKey(identifier)) {
			State state = new State(identifier, this);
			states.put(identifier, state);
			nodes.add(state);
			graphElementAdded(state);
			return true;
		} else {
			return false;
		}
	}

	public synchronized boolean addTransition(Object fromState, Object toState, Object identifier) {
		State source = getNode(fromState);
		State target = getNode(toState);
		checkAddEdge(source, target);
		Transition trans = new Transition(source, target, identifier);
		if (edges.add(trans)) {
			Set<Transition> set = transitions.get(identifier);
			if (set == null) {
				set = new LinkedHashSet<Transition>();
				transitions.put(identifier, set);
			}
			set.add(trans);
			graphElementAdded(trans);
			return true;
		} else {
			return false;
		}
	}

	public synchronized Collection<?> getStates() {
		return states.keySet();
	}

	public synchronized Object removeState(Object state) {
		Object removed = removeNodeFromCollection(states.keySet(), state);
		nodes.remove(state);
		return removed;
	}

	private synchronized Object removeStateInternal(State state) {
		return removeState(state.getIdentifier());

	}

	public synchronized State getNode(Object identifier) {
		return states.get(getProxy(identifier));
	}

	public synchronized Transition findTransition(Object fromState, Object toState, Object identifier) {
		for (Transition t : getEdges(getNode(fromState), getNode(toState), getEdges())) {
			if (getProxy(t.getIdentifier()).equals(getProxy(identifier))) {
				return t;
			}
		}
		return null;
	}

	public synchronized Object removeTransition(Object fromState, Object toState, Object identifier) {

		return removeTransition(new Transition(getNode(fromState), getNode(toState), identifier));
	}

	public Collection<Transition> getEdges(Object identifier) {
		return transitions.get(identifier);
	}

	public void putProxy(Object obj, Object proxy) {
		proxyMap.put(obj, proxy);
	}
	
	private Object getProxy(Object obj) {
		while (proxyMap.containsKey(obj)) {
			obj = proxyMap.get(obj);
		}
		return obj;
	}
	
	public void addProxyMap(TransitionSystemImpl ts) {
		for (Object key: ts.proxyMap.keySet()) {
			proxyMap.put(key, ts.proxyMap.get(key));
		}
	}
}
