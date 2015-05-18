/***********************************************************
 * This software is part of the ProM package * http://www.processmining.org/ * *
 * Copyright (c) 2003-2008 TU/e Eindhoven * and is licensed under the * LGPL
 * License, Version 1.0 * by Eindhoven University of Technology * Department of
 * Information Systems * http://www.processmining.org * *
 ***********************************************************/

package petrinet.behavioralanalysis;

import java.util.HashSet;
import java.util.Set;



import models.graphbased.directed.analysis.ShortestPathFactory;
import models.graphbased.directed.analysis.ShortestPathInfo;
import models.graphbased.directed.petrinet.Petrinet;
import models.graphbased.directed.petrinet.PetrinetGraph;
import models.graphbased.directed.petrinet.ResetNet;
import petrinet.analysis.HomeMarkingSet;
import models.graphbased.directed.petrinet.elements.Transition;
import models.graphbased.directed.transitionsystem.ReachabilityGraph;
import models.graphbased.directed.transitionsystem.State;
import models.semantics.Semantics;
import models.semantics.petrinet.Marking;
import models.semantics.petrinet.PetrinetSemantics;

import models.semantics.petrinet.impl.PetrinetSemanticsFactory;

/**
 * Class to analyze all home marking Home marking is reachable from all of
 * available marking. This class can only analyze bounded net
 * 
 * @author arya
 * @email arya.adriansyah@gmail.com
 * @version Dec 13, 2008
 */

public class HomeMarkingAnalyzer {
	/**
	 * Variant of net and marking
	 */
	// variant with Petrinet and marking
	
	public HomeMarkingSet analyzeHomeMarkingPetriNet( Petrinet net, Marking state)
			 {
		return analyzeHomeMarkingPetriNet( net, state, PetrinetSemanticsFactory
				.regularPetrinetSemantics(Petrinet.class));
	}



	/**
	 * Variant with net, marking, and semantics
	 */
	// variant with Petrinet and marking
	
	public HomeMarkingSet analyzeHomeMarkingPetriNet( Petrinet net, Marking state,
			PetrinetSemantics semantics)  {
		semantics.initialize(net.getTransitions(), new Marking(state));
		return analyzeHomeMarkingPetriNetPrivate( net, state, null, semantics);
	}



	/**
	 * variants with net, marking, and reachability graph
	 */
	// variant with Petrinet and marking
	
	public HomeMarkingSet analyzeHomeMarkingPetriNet( Petrinet net, Marking state,
			ReachabilityGraph reachabilityGraph)  {
		return analyzeHomeMarkingPetriNetPrivate( net, state, reachabilityGraph);
	}

	// variant with InhibitorNet and marking
	
	

	// variant with ResetNet and marking
	
	public HomeMarkingSet analyzeHomeMarkingPetriNet( ResetNet net, Marking state,
			ReachabilityGraph reachabilityGraph)  {
		return analyzeHomeMarkingPetriNetPrivate( net, state, reachabilityGraph);
	}

	// variant with ResetInhibitorNet and marking
	
	
	/**
	 * Executed method when reachability graph is provided without semantics.
	 * Then, semantic is retrieved from connection
	 * 
	 * 
	 *            context of the net
	 * 
	 *            net to be analyzed
	 * 
	 *            initial state (initial marking)
	 * 
	 *            reachability graph of this net and marking
	 * @return HomeMarkingSet a set of home marking
	 * @throws Exception
	 */
	private HomeMarkingSet analyzeHomeMarkingPetriNetPrivate( PetrinetGraph net, Marking state,
			ReachabilityGraph reachabilityGraph )  {
		// reachability graph must be produced by a certain semantics, therefore
		// try catch block is skipped
		
		Semantics<Marking, Transition> sem =null;
		sem.initialize(net.getTransitions(), state);
		return analyzeHomeMarkingPetriNetPrivate( net, state, reachabilityGraph, sem);
	}

	/**
	 * Method to determine home marking for a net. Given net, marking, semantic,
	 * and reachability graph
	 * 
	 * 
	 *            context of the net
	 * 
	 *            net to be analyzed
	 * 
	 *            initial state (initial marking)
	 * 
	 *            reachability graph of this net and marking
	 * @return HomeMarkingSet a set of home marking
	 * @throws Exception
	 */
	private HomeMarkingSet analyzeHomeMarkingPetriNetPrivate( PetrinetGraph net, Marking state,
			ReachabilityGraph reachabilityGraph, Semantics<Marking, Transition> semantics)
			 {
		

		// get reachability graph
		// in case no reachabilityGraph provided, means semantic is provided
		// check whether there is a corresponding reachability graph related to
		// the semantic
		if (reachabilityGraph == null) {
			
			// if the reachability graph cannot be constructed, home marking can not be detected
			HomeMarkingSet set = new HomeMarkingSet(new Marking[0]);
			System.out.println("Statespace construction to identify home marking failed. Maximum number of states reached");
			
			// add connection
		
			System.out.println("Home Markings of " + net.getLabel());
			
		
			System.out.println("Exception happened. Maximum number of states reached");
			return set;
		}
		
	

		// variable to hold the result
		Set<Marking> homeMarkings = new HashSet<Marking>();

		// get shortest path
		ShortestPathInfo<State, models.graphbased.directed.transitionsystem.Transition> shortestPathInfo = ShortestPathFactory
				.calculateAllShortestDistanceDijkstra(reachabilityGraph);

		checkState: for (State stateSpace1 : reachabilityGraph.getNodes()) {
			// check in all mappings, is there any value goes to stateSpace
			for (State stateSpace2 : reachabilityGraph.getNodes()) {
				if (shortestPathInfo.getShortestPathLength(stateSpace2, stateSpace1) < 0) {
					continue checkState;
				}
			}
			homeMarkings.add((Marking) stateSpace1.getIdentifier());
		}

		// transform to array
		Marking[] marking = new Marking[homeMarkings.size()];
		int counter = 0;
		for (Marking m : homeMarkings) {
			marking[counter] = m;
			counter++;
		}
		HomeMarkingSet set = new HomeMarkingSet(marking);

		// add connection
		
		System.out.println("Home Markings of " + net.getLabel());
		return set;
	}
}