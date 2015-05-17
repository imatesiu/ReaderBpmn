package models.graphbased.directed.petrinet;

import java.util.Collection;

import models.graphbased.directed.DirectedGraph;
import models.graphbased.directed.petrinet.elements.Arc;
import models.graphbased.directed.petrinet.elements.ExpandableSubNet;
import models.graphbased.directed.petrinet.elements.Place;
import models.graphbased.directed.petrinet.elements.Transition;
import models.semantics.petrinet.Marking;

public interface PetrinetGraph extends
		DirectedGraph<PetrinetNode, PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> {

	String getLabel();

	// transitions
	Transition addTransition(String label);

	Transition addTransition(String label, ExpandableSubNet parent);

	Transition removeTransition(Transition transition);

	Collection<Transition> getTransitions();

	// transitions
	ExpandableSubNet addGroup(String label);

	ExpandableSubNet addGroup(String label, ExpandableSubNet parent);

	ExpandableSubNet removeGroup(ExpandableSubNet transition);

	Collection<ExpandableSubNet> getGroups();

	// places
	Place addPlace(String label);

	Place addPlace(String label, ExpandableSubNet parent);

	Place removePlace(Place place);

	Collection<Place> getPlaces();

	// arcs
	Arc addArc(Place p, Transition t, int weight);

	Arc addArc(Place p, Transition t);

	Arc addArc(Transition t, Place p, int weight);

	Arc addArc(Transition t, Place p);

	Arc addArc(Place p, Transition t, int weight, ExpandableSubNet parent);

	Arc addArc(Place p, Transition t, ExpandableSubNet parent);

	Arc addArc(Transition t, Place p, int weight, ExpandableSubNet parent);

	Arc addArc(Transition t, Place p, ExpandableSubNet parent);

	Arc removeArc(PetrinetNode source, PetrinetNode target);

	Arc getArc(PetrinetNode source, PetrinetNode target);
	
	String toDOT(/*Marking m*/);

}
