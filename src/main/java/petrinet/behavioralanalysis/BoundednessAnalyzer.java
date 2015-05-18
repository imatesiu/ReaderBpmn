/***********************************************************
 * This software is part of the ProM package * http://www.processmining.org/ * *
 * Copyright (c) 2003-2008 TU/e Eindhoven * and is licensed under the * LGPL
 * License, Version 1.0 * by Eindhoven University of Technology * Department of
 * Information Systems * http://www.processmining.org * *
 ***********************************************************/

package petrinet.behavioralanalysis;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;



import framework.util.collection.MultiSet;
import framework.util.collection.TreeMultiSet;
import models.graphbased.directed.petrinet.Petrinet;
import models.graphbased.directed.petrinet.PetrinetGraph;
import models.graphbased.directed.petrinet.PetrinetNode;
import petrinet.analysis.CoverabilitySet;
import petrinet.analysis.NetAnalysisInformation;
import petrinet.analysis.UnboundedPlacesSet;
import petrinet.analysis.UnboundedSequences;
import petrinet.analysis.NetAnalysisInformation.UnDetBool;
import models.graphbased.directed.petrinet.elements.Place;
import models.graphbased.directed.petrinet.elements.Transition;
import models.graphbased.directed.transitionsystem.CoverabilityGraph;
import models.graphbased.directed.transitionsystem.State;
import models.semantics.petrinet.CTMarking;
import models.semantics.petrinet.Marking;
import models.semantics.petrinet.PetrinetSemantics;
import models.semantics.petrinet.impl.PetrinetSemanticsFactory;

/**
 * Class to analyze whether a given PetriNet is bounded Based on Murata, Tadao.
 * Petri Nets:Properties, Analysis, and Applications. Proceedings of the IEEE
 * vol. 77, No.4, April 1989 a net is bounded iff omega notation does not appear
 * in a any node labels in coverability graph
 * 
 * @author arya
 * @email arya.adriansyah@gmail.com
 * @version Dec 13, 2008
 */

public class BoundednessAnalyzer {
	
	NetAnalysisInformation.BOUNDEDNESS boundednessRes = null;
	UnboundedPlacesSet UnbountedPlacesSet = null;
	UnboundedSequences UnboundedSequences = null;
	
	public BoundednessAnalyzer(Petrinet net, Marking state){
		CGGenerator cg = new CGGenerator();
		Object[] result = cg.petriNetToCoverabilityGraph(net,state);
		CoverabilitySet covSet = (CoverabilitySet) result[1];
		PetrinetSemantics semantics = PetrinetSemanticsFactory
		.regularPetrinetSemantics(Petrinet.class);
		analyzeBoundednessPetriNetInternal( net, state, covSet, semantics);
	}
	
	public BoundednessAnalyzer(Petrinet net, Marking state,
		CoverabilitySet covSet, PetrinetSemantics semantics){
		analyzeBoundednessPetriNetInternal( net, state, covSet, semantics);
	}
	
	
	

	public NetAnalysisInformation.BOUNDEDNESS getBoundednessRes() {
		return boundednessRes;
	}

	public UnboundedPlacesSet getUnbountedPlacesSet() {
		return UnbountedPlacesSet;
	}

	public UnboundedSequences getUnboundedSequences() {
		return UnboundedSequences;
	}

	/**
	 * Analyze boundedness of a petri net
	 * 
	 * @param context
	 *            Context of the petri net
	 * @param net
	 *            net to be analyzed
	 * @param state
	 *            Initial state (initial marking)
	 * @param covSet
	 *            Coverability set of this petri net and marking
	 * @param semantics
	 *            Semantic of this petri net
	 * @return An array of three objects: 1. type NetAnalysisInformation, info
	 *         about boundedness of the net 2. type UnboundedPlacesSet, contains
	 *         set of set of unbounded places 3. type UnboudnedSequences,
	 *         contains set of unbounded sequences
	 * @throws Exception
	 */
	private Object[] analyzeBoundednessPetriNetInternal( PetrinetGraph net, Marking state,
			CoverabilitySet covSet, PetrinetSemantics semantics) {
		// check connection between coverability graph, net, and marking
		

		semantics.initialize(net.getTransitions(), state);
		Object[] result = analyzeBoundednessAssumingConnection( net, state, covSet, semantics);
		
		return result;

	}

	/**
	 * Static method to check boundedness, given a coverability graph
	 * 
	 * @param graph
	 *            Coverability graph
	 * @return true if the net is bounded, false if it is not
	 */
	public static boolean isBounded(CoverabilityGraph graph) {
		boolean boundedness = true;
		Iterator<?> it = graph.getStates().iterator();
		while (boundedness && it.hasNext()) {
			CTMarking mark = (CTMarking) it.next();
			if (!mark.getOmegaPlaces().isEmpty()) {
				boundedness = false;
			}
		}
		return boundedness;
	}

	/**
	 * Analyze boundedness without further checking of connection
	 * 
	 * @param net
	 *            net to be analyzed
	 * @param state
	 *            Initial state (initial marking)
	 * @param covSet
	 *            Coverability set
	 * @return NetAnalysisInformation about boundedness of this net
	 * @throws ExecutionException
	 * @throws InterruptedException
	 * @throws CancellationException
	 */
	private Object[] analyzeBoundednessAssumingConnection( PetrinetGraph net, Marking state,
			CoverabilitySet covSet, PetrinetSemantics semantics) {

		// if there is an omega in coverability graph, the graph is not bounded
		boolean boundedness = true;
		SortedSet<Place> unboundedPlaces = new TreeSet<Place>();
		int bound = 0;
		Iterator<CTMarking> it = covSet.iterator();
		while (it.hasNext()) {
			CTMarking mark = it.next();
			if (!mark.getOmegaPlaces().isEmpty()) {
				boundedness = false;
				unboundedPlaces.addAll(mark.getOmegaPlaces());
			} else {
				for (Place p : mark) {
					bound = Math.max(bound, mark.occurrences(p));
				}
			}
		}

		boundednessRes = new NetAnalysisInformation.BOUNDEDNESS();
		UnbountedPlacesSet = new UnboundedPlacesSet();
		UnbountedPlacesSet.add(unboundedPlaces);
		UnboundedSequences=null;;

		if (boundedness) {
			boundednessRes.setValue(UnDetBool.TRUE);
			UnboundedSequences = new UnboundedSequences();
		} else {
			boundednessRes.setValue(UnDetBool.FALSE);
			CoverabilityGraph cg = null;
			cg =  CGGenerator.getCoverabilityGraph((Petrinet)net, state, semantics);
					

			UnboundedSequences = getUnboundedSequences(net, state, cg);
		}
		// add connection
		return new Object[] { boundednessRes, UnbountedPlacesSet, UnboundedSequences };
	}

	private UnboundedSequences getUnboundedSequences(PetrinetGraph net, Marking initialState,
			CoverabilityGraph coverabilityGraph) {
		UnboundedSequences sequences = new UnboundedSequences();
		Collection<State> greenStates = new HashSet<State>();
		Collection<State> yellowStates = new HashSet<State>();
		Collection<State> redStates = new HashSet<State>();

		/**
		 * First, color all states green.
		 */
		greenStates.addAll(coverabilityGraph.getNodes());
		/**
		 * Second, color all unbounded states and their predecessors red.
		 */
		for (State state : coverabilityGraph.getNodes()) {
			CTMarking marking = (CTMarking) state.getIdentifier();
			if (marking.hasOmegaPlace()) {
				colorBackwards(coverabilityGraph, state, redStates, greenStates, initialState);
			}
		}
		/**
		 * Third, color all red predecessors of green states yellow.
		 */
		for (models.graphbased.directed.transitionsystem.Transition edge : coverabilityGraph
				.getEdges()) {
			if (greenStates.contains(edge.getTarget())) {
				colorBackwards(coverabilityGraph, edge.getSource(), yellowStates, redStates, initialState);
			}
		}

		if (yellowStates.isEmpty()) {
			/**
			 * No yellow states, hence no green state, hence unboundedness
			 * inevitable. Put all transitions in the sequence to visualize
			 * this.
			 */
			MultiSet<PetrinetNode> sequence = new TreeMultiSet<PetrinetNode>();
			sequence.addAll(net.getTransitions());
			sequences.add(sequence);
		} else {
			for (models.graphbased.directed.transitionsystem.Transition edge : coverabilityGraph
					.getEdges()) {
				if (yellowStates.contains(edge.getSource()) && redStates.contains(edge.getTarget())) {
					MultiSet<PetrinetNode> sequence = new TreeMultiSet<PetrinetNode>();
					sequence.addAll((Marking) edge.getSource().getIdentifier());
					sequence.add((Transition) edge.getIdentifier());
					sequences.add(sequence);
				}
			}
		}

		return sequences;
	}

	private void colorBackwards(CoverabilityGraph graph, State state, Collection<State> newCollection,
			Collection<State> oldCollection, Marking initialState) {
		if (oldCollection.contains(state)) {
			oldCollection.remove(state);
			newCollection.add(state);
			if (((Marking) state.getIdentifier()).compareTo(initialState) != 0) {
				for (models.graphbased.directed.transitionsystem.Transition edge : graph
						.getInEdges(state)) {
					colorBackwards(graph, edge.getSource(), newCollection, oldCollection, initialState);
				}
			}
		}
	}

}
