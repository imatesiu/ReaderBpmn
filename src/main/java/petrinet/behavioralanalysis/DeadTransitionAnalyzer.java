/***********************************************************
 * This software is part of the ProM package * http://www.processmining.org/ * *
 * Copyright (c) 2003-2008 TU/e Eindhoven * and is licensed under the * LGPL
 * License, Version 1.0 * by Eindhoven University of Technology * Department of
 * Information Systems * http://www.processmining.org * *
 ***********************************************************/

package petrinet.behavioralanalysis;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

import framework.connections.ConnectionCannotBeObtained;

import models.graphbased.directed.petrinet.Petrinet;
import petrinet.analysis.DeadTransitionsSet;
import models.graphbased.directed.petrinet.elements.Transition;
import models.graphbased.directed.transitionsystem.CoverabilityGraph;
import models.semantics.Semantics;
import models.semantics.petrinet.Marking;
import models.semantics.petrinet.PetrinetSemantics;
import models.semantics.petrinet.impl.PetrinetSemanticsFactory;

/**
 * This class analyze dead transitions in a given net Based on coverability
 * graph written by Murata, Tadao. Petri Nets:Properties, Analysis, and
 * Applications. Proceedings of the IEEE vol. 77, No.4, April 1989. A transition
 * T is dead iff it does not appear as an arc label in a coverability graph's
 * transition
 * 
 * 
 * 
 * 
 */

		
public class DeadTransitionAnalyzer {

	

	// variant with only petri net, marking and semantics
	
	public static DeadTransitionsSet analyzeDeadTransitionPetriNet( Petrinet net, Marking state,
			PetrinetSemantics semantics, CoverabilityGraph graph)  {
		semantics.initialize(net.getTransitions(), new Marking(state));

		
		return analyzeDeadTransitionAssumingConnection(net, state, graph, semantics);
	}

	/**
	 * Main method to analyze transition with an assumption that all provided
	 * parameters are connected
	 * 
	 * 
	 *            context of the net
	 * 
	 *            net to be analyzed
	 * 
	 *            initial state (initial marking)
	 * 
	 *            coverability graph
	 * 
	 *            semantic
	 * 
	 * 
	 */
	private static DeadTransitionsSet analyzeDeadTransitionAssumingConnection( Petrinet net,
			Marking state, CoverabilityGraph graph, Semantics<Marking, Transition> semantics) {
		// for each transition, if it's not included in coverability graph, add it to list of dead transition
		SortedSet<Transition> result = new TreeSet<Transition>();

		// complete path is needed as other Transition class from transitionsystem package is also used in the program
		for (Transition trans : net.getTransitions()) {
			Collection<Object> identifiers = graph.getTransitions();
			if (!identifiers.contains(trans)) {
				result.add(trans);
			}

		}
		DeadTransitionsSet nodeMarking = new DeadTransitionsSet();
		nodeMarking.add(result);

		
		System.out.println("Dead Transition of " + net.getLabel());

		return nodeMarking;
	}

	public static DeadTransitionsSet analyzeDeadTransitionPetriNet(
			Petrinet net, Marking initialMarking) {
		PetrinetSemantics semantics = PetrinetSemanticsFactory.regularPetrinetSemantics(Petrinet.class);
		semantics.initialize(net.getTransitions(), new Marking(initialMarking));
		
		CoverabilityGraph covergraph = CGGenerator.getCoverabilityGraph(net, initialMarking, semantics);
		
		return analyzeDeadTransitionPetriNet(net,initialMarking,semantics,covergraph);
	}

}
