package models.graphbased.directed.petrinet.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import models.graphbased.AttributeMap;
import models.graphbased.directed.AbstractDirectedGraph;
import models.graphbased.directed.DirectedGraph;
import models.graphbased.directed.DirectedGraphEdge;
import models.graphbased.directed.DirectedGraphElement;
import models.graphbased.directed.DirectedGraphNode;
import models.graphbased.directed.petrinet.PetrinetEdge;
import models.graphbased.directed.petrinet.PetrinetNode;
import models.graphbased.directed.petrinet.elements.Arc;
import models.graphbased.directed.petrinet.elements.ExpandableSubNet;
import models.graphbased.directed.petrinet.elements.InhibitorArc;
import models.graphbased.directed.petrinet.elements.Place;
import models.graphbased.directed.petrinet.elements.ResetArc;
import models.graphbased.directed.petrinet.elements.Transition;

// This abstract class implements Petrinet, Resetnet, InhibitorNet, but
// does not declare that. Declaration is done in subclasses, so the
// inheritance relation is not mixed (i.e. each Petrinet is a Resetnet,
// but no resetEdges can be added to a Petrinet).

// All implementing classes should decide which interfaces to implement.

public abstract class AbstractResetInhibitorNet extends
		AbstractDirectedGraph<PetrinetNode, PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> {

	protected final Set<Transition> transitions;
	protected final Set<ExpandableSubNet> substitutionTransitions;
	protected final Set<Place> places;
	protected final Set<Arc> arcs;
	protected final Set<ResetArc> resetArcs;
	protected final Set<InhibitorArc> inhibitorArcs;

	public AbstractResetInhibitorNet(boolean allowsReset, boolean allowsInhibitors) {
		super();
		transitions = new LinkedHashSet<Transition>();
		substitutionTransitions = new LinkedHashSet<ExpandableSubNet>();
		places = new LinkedHashSet<Place>();
		arcs = new LinkedHashSet<Arc>();
		resetArcs = allowsReset ? new LinkedHashSet<ResetArc>() : Collections.<ResetArc>emptySet();
		inhibitorArcs = allowsInhibitors ? new LinkedHashSet<InhibitorArc>() : Collections.<InhibitorArc>emptySet();
	}

	public synchronized ResetArc addResetArc(Place p, Transition t) {
		return addResetArc(p, t, null, null);
	}

	public synchronized ResetArc addResetArc(Place p, Transition t, ExpandableSubNet parent) {
		return addResetArc(p, t, null, parent);
	}

	public synchronized ResetArc addResetArc(Place p, Transition t, String label) {
		return addResetArc(p, t, label, null);
	}

	public synchronized ResetArc addResetArc(Place p, Transition t, String label, ExpandableSubNet parent) {
		checkAddEdge(p, t);
		ResetArc a = new ResetArc(p, t, (label == null ? p.toString() + " -->> " + t.toString() : label), parent);
		if (resetArcs.add(a)) {
			graphElementAdded(a);
			return a;
		} else {
			for (ResetArc existing : resetArcs) {
				if (existing.equals(a)) {
					if (label != null) {
						existing.getAttributeMap().put(AttributeMap.LABEL, label);
					}
					return existing;
				}
			}
		}
		assert (false);
		return null;
	}

	public synchronized ResetArc removeResetArc(Place p, Transition t) {
		return removeFromEdges(p, t, resetArcs);
	}

	public synchronized InhibitorArc addInhibitorArc(Place p, Transition t, String label) {
		return addInhibitorArc(p, t, label, null);
	}

	public synchronized InhibitorArc addInhibitorArc(Place p, Transition t, String label, ExpandableSubNet parent) {
		checkAddEdge(p, t);
		InhibitorArc a = new InhibitorArc(p, t, (label == null ? p.toString() + " ---O " + t.toString() : label),
				parent);
		if (inhibitorArcs.add(a)) {
			graphElementAdded(a);
			return a;
		} else {
			for (InhibitorArc existing : inhibitorArcs) {
				if (existing.equals(a)) {
					if (label != null) {
						existing.getAttributeMap().put(AttributeMap.LABEL, label);
					}
					return existing;
				}
			}
		}
		assert (false);
		return null;
	}

	public synchronized InhibitorArc addInhibitorArc(Place p, Transition t) {
		return addInhibitorArc(p, t, null, null);
	}

	public synchronized InhibitorArc addInhibitorArc(Place p, Transition t, ExpandableSubNet parent) {
		return addInhibitorArc(p, t, null, parent);
	}

	public synchronized InhibitorArc removeInhibitorArc(Place p, Transition t) {
		return removeFromEdges(p, t, inhibitorArcs);
	}

	public synchronized InhibitorArc getInhibitorArc(Place p, Transition t) {
		Collection<InhibitorArc> set = getEdges(p, t, inhibitorArcs);
		return (set.isEmpty() ? null : set.iterator().next());
	}

	public synchronized ResetArc getResetArc(Place p, Transition t) {
		Collection<ResetArc> set = getEdges(p, t, resetArcs);
		return (set.isEmpty() ? null : set.iterator().next());
	}

	public synchronized Transition addTransition(String label) {
		return addTransition(label, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * models.graphbased.petrinet.Petrinet#addTransition(java
	 * .lang.String)
	 */

	public synchronized Transition addTransition(String label, ExpandableSubNet parent) {
		Transition t = new Transition(label, this, parent);
		transitions.add(t);
		graphElementAdded(t);
		return t;
	}

	public synchronized ExpandableSubNet addGroup(String label) {
		return addGroup(label, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * models.graphbased.petrinet.Petrinet#addTransition(java
	 * .lang.String)
	 */

	public synchronized ExpandableSubNet addGroup(String label, ExpandableSubNet parent) {
		ExpandableSubNet t = new ExpandableSubNet(label, this, parent);
		substitutionTransitions.add(t);
		graphElementAdded(t);
		return t;
	}

	public synchronized Place addPlace(String label) {
		return addPlace(label, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * models.graphbased.petrinet.Petrinet#addPlace(java.lang
	 * .String)
	 */
	public synchronized Place addPlace(String label, ExpandableSubNet parent) {
		Place p = new Place(label, this, parent);
		places.add(p);
		graphElementAdded(p);
		return p;
	}

	//**************************************************************************
	// Adding regular arcs p -> t

	protected synchronized Arc addArcPrivate(PetrinetNode src, PetrinetNode trgt, int weight, ExpandableSubNet parent) {
		synchronized (arcs) {
			checkAddEdge(src, trgt);
			Arc a = new Arc(src, trgt, weight, parent);
			if (arcs.add(a)) {
				graphElementAdded(a);
				return a;
			} else {
				for (Arc existing : arcs) {
					if (existing.equals(a)) {
						existing.setWeight(existing.getWeight() + weight);
						return existing;
					}
				}
			}
			assert (false);
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seemodels.graphbased.petrinet.Petrinet#addArc(org.
	 * processmining.models.graphbased.petrinet.Place,
	 * models.graphbased.petrinet.Transition, int)
	 */
	public synchronized Arc addArc(Place p, Transition t, int weight) {
		return addArcPrivate(p, t, weight, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seemodels.graphbased.petrinet.Petrinet#addArc(org.
	 * processmining.models.graphbased.petrinet.Place,
	 * models.graphbased.petrinet.Transition)
	 */
	public synchronized Arc addArc(Place p, Transition t) {
		return addArc(p, t, 1);
	}

	//**************************************************************************
	// Adding regular arcs t -> p

	/*
	 * (non-Javadoc)
	 * 
	 * @seemodels.graphbased.petrinet.Petrinet#addArc(org.
	 * processmining.models.graphbased.petrinet.Transition,
	 * models.graphbased.petrinet.Place, int)
	 */
	public synchronized Arc addArc(Transition t, Place p, int weight) {
		return addArcPrivate(t, p, weight, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seemodels.graphbased.petrinet.Petrinet#addArc(org.
	 * processmining.models.graphbased.petrinet.Transition,
	 * models.graphbased.petrinet.Place)
	 */
	public synchronized Arc addArc(Transition t, Place p) {
		return addArc(t, p, 1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seemodels.graphbased.petrinet.Petrinet#addArc(org.
	 * processmining.models.graphbased.petrinet.Place,
	 * models.graphbased.petrinet.Transition, int)
	 */
	public synchronized Arc addArc(Place p, Transition t, int weight, ExpandableSubNet parent) {
		return addArcPrivate(p, t, weight, parent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seemodels.graphbased.petrinet.Petrinet#addArc(org.
	 * processmining.models.graphbased.petrinet.Place,
	 * models.graphbased.petrinet.Transition)
	 */
	public synchronized Arc addArc(Place p, Transition t, ExpandableSubNet parent) {
		return addArc(p, t, 1, parent);
	}

	//**************************************************************************
	// Adding regular arcs t -> p

	/*
	 * (non-Javadoc)
	 * 
	 * @seemodels.graphbased.petrinet.Petrinet#addArc(org.
	 * processmining.models.graphbased.petrinet.Transition,
	 * models.graphbased.petrinet.Place, int)
	 */
	public synchronized Arc addArc(Transition t, Place p, int weight, ExpandableSubNet parent) {
		return addArcPrivate(t, p, weight, parent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seemodels.graphbased.petrinet.Petrinet#addArc(org.
	 * processmining.models.graphbased.petrinet.Transition,
	 * models.graphbased.petrinet.Place)
	 */
	public synchronized Arc addArc(Transition t, Place p, ExpandableSubNet parent) {
		return addArc(t, p, 1, parent);
	}

	public synchronized Arc getArc(PetrinetNode source, PetrinetNode target) {
		Collection<Arc> set = getEdges(source, target, arcs);
		return (set.isEmpty() ? null : set.iterator().next());
	}

	public synchronized Arc removeArc(PetrinetNode source, PetrinetNode target) {
		Arc a = removeFromEdges(source, target, arcs);
		return a;
	}

	@SuppressWarnings("unchecked")
	public synchronized void removeEdge(DirectedGraphEdge edge) {
		if (edge instanceof InhibitorArc) {
			inhibitorArcs.remove(edge);
		} else if (edge instanceof ResetArc) {
			resetArcs.remove(edge);
		} else if (edge instanceof Arc) {
			arcs.remove(edge);
		} else {
			assert (false);
		}
		graphElementRemoved(edge);
	}

	public synchronized void removeNode(DirectedGraphNode node) {
		if (node instanceof Transition) {
			removeTransition((Transition) node);
		} else if (node instanceof Place) {
			removePlace((Place) node);
		} else {
			assert (false);
		}
	}

	public synchronized Place removePlace(Place place) {
		removeSurroundingEdges(place);
		return removeNodeFromCollection(places, place);
	}

	public synchronized Transition removeTransition(Transition transition) {
		removeSurroundingEdges(transition);
		return removeNodeFromCollection(transitions, transition);
	}

	public synchronized ExpandableSubNet removeGroup(ExpandableSubNet transition) {
		removeSurroundingEdges(transition);
		return removeNodeFromCollection(substitutionTransitions, transition);
	}

	public synchronized Set<PetrinetNode> getNodes() {
		Set<PetrinetNode> nodes = new HashSet<PetrinetNode>();
		nodes.addAll(transitions);
		nodes.addAll(places);
		nodes.addAll(substitutionTransitions);
		return nodes;
	}

	public synchronized Set<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> getEdges() {
		Set<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> edges = new HashSet<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>>();
		edges.addAll(arcs);
		edges.addAll(inhibitorArcs);
		edges.addAll(resetArcs);
		return edges;
	}

	// It's safe to assume that the input is an AbstractResetInhibitorNet.
	protected synchronized Map<DirectedGraphElement, DirectedGraphElement> cloneFrom(
			DirectedGraph<PetrinetNode, PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> graph) {
		AbstractResetInhibitorNet net = (AbstractResetInhibitorNet) graph;
		return cloneFrom(net, true, true, true, true, true);
	}

	// It's safe to assume that the input is an AbstractResetInhibitorNet.
	protected synchronized Map<DirectedGraphElement, DirectedGraphElement> cloneFrom(AbstractResetInhibitorNet net,
			boolean transitions, boolean places, boolean arcs, boolean resets, boolean inhibitors) {

		HashMap<DirectedGraphElement, DirectedGraphElement> mapping = new HashMap<DirectedGraphElement, DirectedGraphElement>();

		if (transitions) {
			for (Transition t : net.transitions) {
				Transition copy = addTransition(t.getLabel());
				copy.setInvisible(t.isInvisible());
				mapping.put(t, copy);
			}
		}
		if (places) {
			for (Place p : net.places) {
				Place copy = addPlace(p.getLabel());
				mapping.put(p, copy);
			}
		}
		if (arcs) {
			for (Arc a : net.arcs) {
				mapping.put(a, addArcPrivate((PetrinetNode) mapping.get(a.getSource()), (PetrinetNode) mapping.get(a
						.getTarget()), a.getWeight(), a.getParent()));
			}
		}
		if (inhibitors) {
			for (InhibitorArc a : net.inhibitorArcs) {
				mapping.put(a, addInhibitorArc((Place) mapping.get(a.getSource()), (Transition) mapping.get(a
						.getTarget()), a.getLabel()));
			}
		}
		if (resets) {
			for (ResetArc a : net.resetArcs) {
				mapping.put(a, addResetArc((Place) mapping.get(a.getSource()), (Transition) mapping.get(a.getTarget()),
						a.getLabel()));
			}
		}

		getAttributeMap().clear();
		AttributeMap map = net.getAttributeMap();
		for (String key : map.keySet()) {
			getAttributeMap().put(key, map.get(key));
		}

		return mapping;
	}

	public synchronized Collection<Place> getPlaces() {
		return Collections.unmodifiableCollection(places);
	}

	public synchronized Collection<Transition> getTransitions() {
		return Collections.unmodifiableCollection(transitions);
	}

	public synchronized Collection<ExpandableSubNet> getGroups() {
		return Collections.unmodifiableCollection(substitutionTransitions);
	}

}
