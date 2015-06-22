/***********************************************************
 * This software is part of the ProM package * http://www.processmining.org/ * *
 * Copyright (c) 2003-2008 TU/e Eindhoven * and is licensed under the * LGPL
 * License, Version 1.0 * by Eindhoven University of Technology * Department of
 * Information Systems * http://www.processmining.org * *
 ***********************************************************/

package petrinet.structuralanalysis.invariants;

import java.util.SortedSet;


import framework.util.collection.SortedMultiSet;
import framework.util.collection.TreeMultiSet;

import models.graphbased.directed.petrinet.Petrinet;
import models.graphbased.directed.petrinet.PetrinetGraph;
import petrinet.analysis.TransitionInvariantSet;
import models.graphbased.directed.petrinet.elements.Transition;
import petrinet.structuralanalysis.IncidenceMatrixFactory;

import cern.colt.matrix.DoubleMatrix2D;

/**
 * This class is a plugin to calculate basis of semi-positive transition
 * invariants of petri net and inhibitor net. Modified from
 * TransitionInvariantCalculator class implemented in ProM 5
 * 
 * 
 * 
 * 
 */

public class TransitionInvariantCalculator {

	
	public TransitionInvariantSet calculateTransitionInvariant(Petrinet net) {
		return calculateTransitionInvariantInternal( net);
	}

	
	

	/**
	 * Main method to calculate transition invariant
	 * 
	 * 
	 *            context of the net
	 * 
	 *            net to be calculated
	 * 
	 */
	private TransitionInvariantSet calculateTransitionInvariantInternal(PetrinetGraph net) {
		System.out.println("Basis of semi-positive transition invariants of " + net.getLabel());

		// get incidence matrix for the net
		DoubleMatrix2D incidenceMatrix = IncidenceMatrixFactory.getTransposedIncidenceMatrix(net);

		System.out.println("INCIDENCE MATRIX");
		System.out.println("number of rows : " + incidenceMatrix.rows());
		System.out.println("number of cols : " + incidenceMatrix.columns());

		StringBuffer tempOut = new StringBuffer();
		for (int i = 0; i < incidenceMatrix.rows(); i++) {
			tempOut.delete(0, tempOut.length());
			for (int j = 0; j < incidenceMatrix.columns(); j++) {
				tempOut.append(" ");
				tempOut.append(incidenceMatrix.get(i, j));
			}
			System.out.println(tempOut.toString());
		}

		// calculate invariants
		TransitionInvariantSet tinv = calculate(net); // petrinet is always
		// short-circuited in
		// ProM 5

		// add connection for invariant marking
		
		return tinv;
	}

	/**
	 * Calculation of transition invariant marking This method is public in
	 * order to enable invariant calculation as intermediate step
	 * 
	 * 
	 *            net to be calculated
	 * 
	 */
	public TransitionInvariantSet calculate(PetrinetGraph net) {
		// short circuit the net

		// calculate invariant, store it in inv
		TransitionInvariantSet inv = new TransitionInvariantSet("Basis of semi-positive transition invariants of "
				+ net.getLabel());
		DoubleMatrix2D m = InvariantCalculator.calculate(IncidenceMatrixFactory.getTransposedIncidenceMatrix(net));
		for (int i = 0; i < m.rows(); i++) {
			// For each row, make a set over the places
			SortedMultiSet<Transition> b = new TreeMultiSet<Transition>();

			int j = 0;
			for (Transition t : net.getTransitions()) {
				for (int k = 0; k < m.get(i, j); k++) {
					b.add(t);
				}
				j++;
			}

			// Arya: apparently, the result may not be the basis yet. Need to
			// divide the number of elements with their greatest common divisor
			// (GCD)
			SortedSet<Transition> base = b.baseSet();
			int[] occurrences = new int[base.size()];
			int counter = 0;
			for (Transition p : base) {
				occurrences[counter] = b.occurrences(p);
				counter++;
			}
			int gcd = InvariantCalculator.getGCDs(occurrences);
			if (gcd > 1) {
				// divide all occurrences
				SortedMultiSet<Transition> bprime = new TreeMultiSet<Transition>();
				for (Transition p : base) {
					bprime.add(p, b.occurrences(p) / gcd);
				}
				inv.add(bprime);
			} else {
				inv.add(b);
			}
			// end of addition
		}

		return inv;
	}
}
