package models.graphbased.directed.petrinet;


import models.graphbased.directed.petrinet.elements.ExpandableSubNet;
import models.graphbased.directed.petrinet.elements.Place;
import models.graphbased.directed.petrinet.elements.ResetArc;
import models.graphbased.directed.petrinet.elements.Transition;


public interface ResetNet extends PetrinetGraph {

	// reset arcs
	ResetArc addResetArc(Place p, Transition t, String label);

	ResetArc addResetArc(Place p, Transition t);

	ResetArc removeResetArc(Place p, Transition t);

	ResetArc getResetArc(Place p, Transition t);

	ResetArc addResetArc(Place p, Transition t, String label, ExpandableSubNet parent);

	ResetArc addResetArc(Place p, Transition t, ExpandableSubNet parent);


}
