/**
 * 
 */
package petrinet.structuralanalysis.invariants;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;


import framework.util.collection.SortedMultiSet;
import framework.util.collection.TreeMultiSet;

import models.graphbased.directed.petrinet.Petrinet;
import models.graphbased.directed.petrinet.PetrinetEdge;
import models.graphbased.directed.petrinet.PetrinetGraph;
import models.graphbased.directed.petrinet.PetrinetNode;
import petrinet.analysis.PlaceInvariantSet;
import models.graphbased.directed.petrinet.elements.Place;
import petrinet.structuralanalysis.IncidenceMatrixFactory;

import cern.colt.matrix.DoubleMatrix2D;

/**
 * This class represents a plugin to calculate basis of semi-positive place
 * invariants of petri net Modified from PlaceInvariantCalculator class
 * implemented in ProM 5
 * 
 * 
 * 
 * 
 */

public class PlaceInvariantCalculator {

	
	public PlaceInvariantSet calculatePlaceInvariant( Petrinet net) {
		return calculatePlaceInvariantInternal( net);
	}

	
	

	/**
	 * Main method to calculate place invariant
	 * 
	 * 
	 *            context of the net
	 * 
	 *            net to be calculated
	 * 
	 */
	private PlaceInvariantSet calculatePlaceInvariantInternal( PetrinetGraph net) {
		System.out.println("Basis of semi-positive place invariants of " + net.getLabel());

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
			//System.out.println(tempOut.toString());
		}

		// calculate invariants
		PlaceInvariantSet pinv = calculate(net); // petrinet is always
		// short-circuited in ProM 5

		// add connection for invariant marking
		
		return pinv;
	}

	/**
	 * Calculation of place invariant marking This method is public in order to
	 * enable invariant calculation as intermediate step
	 * 
	 * 
	 *            net to be calculated
	 * 
	 */
	public PlaceInvariantSet calculate(PetrinetGraph net) {

		// calculate invariant, store it in inv
		PlaceInvariantSet inv = new PlaceInvariantSet("Basis of semi-positive place invariants of " + net.getLabel());
		DoubleMatrix2D m = InvariantCalculator.calculate(IncidenceMatrixFactory.getTransposedIncidenceMatrix(net)
				.viewDice());

		// create a reference table to check edges
		Set<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> edges = net.getEdges();
		Iterator<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> iterator = edges.iterator();

		HashMap<PetrinetNode, PetrinetNode> reference = new HashMap<PetrinetNode, PetrinetNode>();
		while (iterator.hasNext()) {
			PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge = iterator.next();
			reference.put(edge.getSource(), edge.getTarget());
		}

		// calculate
		for (int i = 0; i < m.rows(); i++) {
			// For each row, make a set over the places
			SortedMultiSet<Place> b = new TreeMultiSet<Place>();

			int j = 0;
			for (Place p : net.getPlaces()) {
				for (int k = 0; k < m.get(i, j); k++) {
					b.add(p);
				}
				j++;
			}

			// Arya: apparently, the result may not be the basis yet. Need to
			// divide the number of elements with their greatest common divisor
			// (GCD)
			SortedSet<Place> base = b.baseSet();
			int[] occurrences = new int[base.size()];
			int counter = 0;
			for (Place p : base) {
				occurrences[counter] = b.occurrences(p);
				counter++;
			}
			int gcd = InvariantCalculator.getGCDs(occurrences);
			if (gcd > 1) {
				// divide all occurrences
				SortedMultiSet<Place> bprime = new TreeMultiSet<Place>();
				for (Place p : base) {
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
