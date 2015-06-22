/***********************************************************
 * This software is part of the ProM package * http://www.processmining.org/ * *
 * Copyright (c) 2003-2008 TU/e Eindhoven * and is licensed under the * LGPL
 * License, Version 1.0 * by Eindhoven University of Technology * Department of
 * Information Systems * http://www.processmining.org * *
 ***********************************************************/

package petrinet.structuralanalysis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;


import framework.util.collection.MultiSet;

import models.graphbased.directed.petrinet.Petrinet;
import models.graphbased.directed.petrinet.PetrinetGraph;
import models.graphbased.directed.petrinet.PetrinetNode;
import petrinet.analysis.PlaceInvariantSet;
import petrinet.analysis.SComponentSet;
import models.graphbased.directed.petrinet.elements.Place;
import models.graphbased.directed.petrinet.elements.Transition;
import petrinet.structuralanalysis.invariants.PlaceInvariantCalculator;
import petrinet.structuralanalysis.util.SelfLoopTransitionExtract;

import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import cern.colt.matrix.DoubleMatrix2D;

/**
 * Class to decompose petri net into its S-components (if there is any) this
 * implementation based on characteristic of place invariants:
 * 
 * "any S-component consists of the support of a minimal support P-invariant and the transitions adjacent to these places"
 * 
 * Taken from : D. Hauschildt, E. Verbeek and W. van der Aalst. WOFLAN: A
 * Petri-net-based Workflow Analyzer. Computing Science Reports 97/12.
 * Eindhoven, August 1997. p24.
 * 
 * 
 * 
 * 
 */

		
public class SComponentGenerator {

	// variants with place invariant as input
	
	public SComponentSet calculateSComponentsPetriNet( Petrinet net, PlaceInvariantSet invMarking)
			 {
		return calculateSComponentsInternal( net, invMarking);
	}

	
	

	// variants with no input in form of place invariant
	
	public SComponentSet calculateSComponentsPetriNet( Petrinet net)
			 {
		return calculateSComponentsInternalWithInvariantAddition( net);
	}

	
	

	/**
	 * This method generate invariants or taking an existing invariants from
	 * context in order to continue calculation of S Component
	 * 
	 * 
	 *            context of the net
	 * 
	 *            net to be calculated
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	private SComponentSet calculateSComponentsInternalWithInvariantAddition( PetrinetGraph net)
			 {
		
		
		PlaceInvariantCalculator pic = new PlaceInvariantCalculator();
		
		
		PlaceInvariantSet invMarking = pic.calculatePlaceInvariant((Petrinet)net);;

		
		
		// check whether there is a transition invariant before
		//invMarking = context.tryToFindOrConstructFirstObject(PlaceInvariantSet.class, PlaceInvariantConnection.class,
		//		AbstractInvariantMarkingConnection.INVARIANTMARKING, net);

		return calculateSComponentsInternal( net, invMarking);
	}

	/**
	 * Main method to calculate S-Components (with context)
	 * 
	 * 
	 *            context of the net
	 * 
	 *            net to be calculated
	 * 
	 *            Place invariant of the corresponding net
	 * 
	 * 
	 */
	private SComponentSet calculateSComponentsInternal( PetrinetGraph net,
			PlaceInvariantSet invMarking)  {
		
		// calculate S-Component
		SComponentSet nodeMarking = calculateSComponents(net, invMarking);

		// add connection for s-component marking
		
		System.out.println("S-Components of " + net.getLabel());

		return nodeMarking;
	}

	/**
	 * Main method to calculate S-Component
	 * 
	 * 
	 *            PetrinetGraph with its S-components to be calculate
	 * 
	 *            Invariant to be used in calculation. If its null, then place
	 *            invariants will be counted
	 * 
	 */
	private SComponentSet calculateSComponents(PetrinetGraph net, PlaceInvariantSet invMarking) {
		// initialization
		SComponentSet nodeMarking = new SComponentSet(); // to store final
		// result

		// get place invariants
		if (invMarking == null) {
			// calculate place invariants
			PlaceInvariantCalculator calculator = new PlaceInvariantCalculator();
			invMarking = calculator.calculate(net);
		}

		// references
		DoubleMatrix2D incidenceMatrix = IncidenceMatrixFactory.getIncidenceMatrix(net);
		List<Place> placeList = new ArrayList<Place>(net.getPlaces());
		List<Transition> transitionList = new ArrayList<Transition>(net.getTransitions());

		// for each place invariants filter only the one with 1 or 0 as its
		// member
		invariantLoop: for (MultiSet<Place> set : invMarking) {
			// for one set of invariant, check each element
			Set<Integer> placeIndex = new HashSet<Integer>();
			for (PetrinetNode node : set) {
				if (set.occurrences(node) == 1) {
					placeIndex.add(placeList.indexOf(node));
				} else {
					continue invariantLoop;
				}
			}

			// until here, we have an invariant which only consists of 0 and 1

			// iterate the places, generate array of integer to dice
			// incidenceMatrix
			int[] places = new int[placeIndex.size()];
			Iterator<Integer> it = placeIndex.iterator();
			int counter = 0;
			while (it.hasNext()) {
				places[counter] = it.next();
				counter++;
			}
			// iterate all of transition, generate array of integer to dice
			// incidenceMatrix
			int[] transitions = new int[transitionList.size()];
			for (int i = 0; i < transitionList.size(); i++) {
				transitions[i] = i;
			}

			// dice incidence matrix so that it only include necessary place and
			// transitions
			DoubleMatrix2D tempIncidenceMatrix = incidenceMatrix.viewSelection(places, transitions);

			Set<Integer> transitionIndex = new HashSet<Integer>(); // to store
			// result
			// for
			// transition

			// for each columns on the diced incidence matrix, there can only be
			// 2 nonzero value with the sum of 0
			IntArrayList tempTransition = new IntArrayList();
			DoubleArrayList tempTransValue = new DoubleArrayList();
			for (int i = 0; i < tempIncidenceMatrix.columns(); i++) {
				tempIncidenceMatrix.viewColumn(i).getNonZeros(tempTransition, tempTransValue);

				// as only 1 ingoing and 1 outgoing arc is permitted, number of
				// element should be 2
				// as there can only be 1 ingoing and 1 outgoing, addition
				// should be 0
				if ((tempTransition.size() == 2)
						&& (Double.compare(0.0, tempTransValue.get(0) + tempTransValue.get(1)) == 0)) {
					// add to transitionIndex
					transitionIndex.add(i);
				} else if (tempTransition.size() != 0) {
					// this invariant has invalid transition inclusion. Continue
					// to other invariants
					continue invariantLoop;
				}
			}

			// until here, we have our S-components, add all transition and
			// places as an S-component
			SortedSet<PetrinetNode> result = new TreeSet<PetrinetNode>();
			// add all places
			Set<Place> consideredPlaces = new HashSet<Place>(placeIndex.size());

			for (int tempIndex : placeIndex) {
				Place place = placeList.get(tempIndex);
				result.add(place);
				consideredPlaces.add(place);
			}
			// add transition corresponds to each place
			for (int tempIndex : transitionIndex) {
				result.add(transitionList.get(tempIndex));
			}

			// check self loop transitions only if it is Petri net
			Map<Transition, Set<Place>> selfLoopT = SelfLoopTransitionExtract.getSelfLoopTransitions(net);
			for (Entry<Transition, Set<Place>> e : selfLoopT.entrySet()) {
				if (consideredPlaces.containsAll(e.getValue())) {
					result.add(e.getKey());
				}
			}

			// add to final set
			nodeMarking.add(result);
		}

		return nodeMarking;
	}
}
