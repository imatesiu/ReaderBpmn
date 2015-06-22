/***********************************************************
 * This software is part of the ProM package * http://www.processmining.org/ * *
 * Copyright (c) 2003-2008 TU/e Eindhoven * and is licensed under the * LGPL
 * License, Version 1.0 * by Eindhoven University of Technology * Department of
 * Information Systems * http://www.processmining.org * *
 ***********************************************************/

package petrinet.structuralanalysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
 

import models.graphbased.directed.petrinet.Petrinet;
import models.graphbased.directed.petrinet.PetrinetGraph;
import petrinet.analysis.TrapSet;
import models.graphbased.directed.petrinet.elements.Place;

import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;

/**
 * This class identify minimal traps, based mainly from : Boer, Ermin R. and
 * Murata, Tadao. (1992). Sign Incidence Matrix and Generation of Basis Siphons
 * and Traps of Petri Nets. IEEE International Conference on Systems, Man, and
 * Cybernetic.pp.632-637.
 * 
 * @author arya
 * @email arya.adriansyah@gmail.com
 * @version Oct 19, 2008
 */

public class TrapGenerator {

	
	public TrapSet calculateTraps( Petrinet net)  {
		return calculateTrapsInternal( net);
	}

	
	

	/**
	 * Main method to calculate Traps (with context)
	 * 
	 * 
	 *            context of the net
	 * 
	 *            net to be analyzed
	 * @return TrapSet set of trap places
	 * @throws Exception
	 */
	private TrapSet calculateTrapsInternal( PetrinetGraph net)  {
		System.out.println("Minimal Traps of " + net.getLabel());

		// get incidence matrix
		DoubleMatrix2D incidenceMatrix = IncidenceMatrixFactory.getTransposedSignedIncidenceMatrix(net);

		// call main method
		TrapSet nodeMarking = identifyTrap(incidenceMatrix, new ArrayList<Place>(net.getPlaces()));

		// add connection for trap marking
		

		return nodeMarking;
	}

	/**
	 * main method to identify minimal trap, based on incidenceMatrix
	 * 
	 * 
	 *            incidence matrix
	 * 
	 *            list of places
	 * @return TrapSet set of trap places
	 */
	private TrapSet identifyTrap(DoubleMatrix2D incidenceMatrix, ArrayList<Place> places) {
		// identify netralizer (column that 'netralize' negative value in a row)
		// note that this trap generator is only a slight modification from
		// siphon generator
		IntArrayList rowList = new IntArrayList();
		IntArrayList columnList = new IntArrayList();
		DoubleArrayList valueList = new DoubleArrayList();
		incidenceMatrix.getNonZeros(rowList, columnList, valueList);

		Map<Integer, Set<Integer>> netralizer = new Hashtable<Integer, Set<Integer>>(places.size());
		for (int i = 0; i < rowList.size(); i++) {
			int row = rowList.get(i);
			int column = columnList.get(i);

			if (Double.compare(incidenceMatrix.get(row, column), 0) > 0) {
				Set<Integer> colNetralizers = netralizer.get(row);
				if (colNetralizers == null) { // there is no netralizer column
					// before
					colNetralizers = new HashSet<Integer>();
				}
				colNetralizers.add(column);
				netralizer.put(row, colNetralizers);
			}
		}

		// from this line, if we want to know which columns 'netralizes' a
		// certain row, just query the netralizer map.

		// identify trap
		List<Set<Integer>> finalResult = new LinkedList<Set<Integer>>();

		Queue<ExpansionItem> toBeExpanded = new LinkedList<ExpansionItem>();
		Queue<Set<Integer>> result = new LinkedList<Set<Integer>>(); // result
		// per
		// root

		// helping variables
		IntArrayList rowIndex = new IntArrayList();
		DoubleArrayList rowValueArrayList = new DoubleArrayList();
		Set<Set<Integer>> triedCombination = new HashSet<Set<Integer>>();

		// process each column
		nextColumn: for (int i = 0; i < incidenceMatrix.columns(); i++) { // for
			// each
			// column
			// (seed)
			DoubleMatrix1D columnMatrix = incidenceMatrix.viewColumn(i);

			// get all negative rows
			columnMatrix.getNonZeros(rowIndex, rowValueArrayList);

			if (rowIndex.size() > 0) {
				boolean isAlreadyBasisTrap = true;
				for (int j = 0; j < rowValueArrayList.size(); j++) {
					if (Double.compare(rowValueArrayList.get(j), 0) < 0) {
						// this node is not a basis trap
						isAlreadyBasisTrap = false;

						// it means, need to be neutralized. Get all netralizer
						// columns
						if (netralizer.get(rowIndex.get(j)) != null) {
							for (int netralizerCol : netralizer.get(rowIndex.get(j))) {

								// create new ExpansionItem
								Set<Integer> expandedColumn = new HashSet<Integer>();
								expandedColumn.add(i);
								ExpansionItem expItem = new ExpansionItem(expandedColumn, columnMatrix, netralizerCol);

								// add it to item to be expanded
								toBeExpanded.add(expItem);
							}
						} else {// netralizer does not exist, no use to process
							// this column.
							continue nextColumn;
						}
					} // the row value is already positive, no problem
				} // end for

				if (isAlreadyBasisTrap) {
					Set<Integer> colBasisTrap = new HashSet<Integer>();
					colBasisTrap.add(i);
					finalResult.add(colBasisTrap);
					continue nextColumn;
				}
			} else {
				// this column is already a basis trap, no non zeros are found
				Set<Integer> colBasisTrap = new HashSet<Integer>();
				colBasisTrap.add(i);
				finalResult.add(colBasisTrap);
				continue nextColumn;
			}

			// up to this point, initial expansion for a column is already
			// defined. We just have to process things iteratively
			expansion: while (toBeExpanded.size() > 0) {
				ExpansionItem currExpItem = toBeExpanded.poll();

				// add current index
				Set<Integer> updatedExpandedColumn = currExpItem.getExpandedColumns();
				updatedExpandedColumn.add(currExpItem.getToBeExpandedBy());

				// check if its ever been visited
				if (!triedCombination.contains(updatedExpandedColumn)) {
					// update triedCombination
					triedCombination.add(updatedExpandedColumn);

					// check whether subset or superset of this
					// updateCombinedCol is already in the set
					if (isNotSupersetOfAll(updatedExpandedColumn, result)) {
						// if its not a superset, expand this again
						DoubleMatrix1D updatedColumnMatrix = SignIncidenceMatrixOperator.addMatrix1Column(currExpItem
								.getCurrentColumnState(), incidenceMatrix.viewColumn(currExpItem.getToBeExpandedBy()));

						// get all rows that are negative
						rowIndex.clear();
						rowValueArrayList.clear();

						boolean isLeaf = true;

						updatedColumnMatrix.getNonZeros(rowIndex, rowValueArrayList);
						for (int j = 0; j < rowValueArrayList.size(); j++) {
							if (Double.compare(rowValueArrayList.get(j), 0) < 0) { // the
								// constant
								// SignIncidenceMatrixOperator.NEUTRAL
								// is
								// >
								// 0
								// it means, need to be neutralized. Get all
								// netralizer columns
								isLeaf = false;
								if (netralizer.get(rowIndex.get(j)) != null) {
									for (int netralizerCol : netralizer.get(rowIndex.get(j))) {
										if (!updatedExpandedColumn.contains(netralizerCol)) {
											// create new ExpansionItem
											Set<Integer> expandedColumn = new HashSet<Integer>();
											expandedColumn.addAll(updatedExpandedColumn);
											ExpansionItem expItem = new ExpansionItem(expandedColumn,
													updatedColumnMatrix, netralizerCol);

											// add it to item to be expanded
											toBeExpanded.add(expItem);
										}
									}
								} else {
									continue expansion;
								}// neutralizer does not exist, no need to
								// furthermore process this column
							} // the row value is negative, no problem
						}

						// if the execution reaches this line, means the column
						// is a leaf
						if (isLeaf) {
							result.add(updatedExpandedColumn);
						}
					}
				} // this combination has been visited
			}

			// accumulate result for a column
			finalResult.addAll(result);
			result.clear();
			triedCombination.clear();

		} // end of iterating all column

		// after all basis traps are generated, time to find minimal traps(traps
		// that are not contained by others)
		int finalResultSize = finalResult.size();
		int basisTrapIndex = 0;
		int localCounter = 0;
		process: while (basisTrapIndex < finalResultSize) {
			Set<Integer> basisTrap = finalResult.remove(basisTrapIndex);
			finalResultSize--;

			localCounter = basisTrapIndex;

			while (localCounter < finalResultSize) {
				if (finalResult.get(localCounter).containsAll(basisTrap)) {
					// the finalResult.get(localCounter) is not a minimal trap
					finalResult.remove(localCounter);
					finalResultSize--;
				} else {
					if (basisTrap.containsAll(finalResult.get(localCounter))) {
						// this basisTrap is not a minimal siphon, no need to
						// continue process this basisTrap
						continue process;
					}
					localCounter++;
				}
			}

			// if this line is passed, current basisTrap is minimal
			finalResult.add(0, basisTrap);
			finalResultSize++;
			basisTrapIndex++;
		}

		// result variable
		TrapSet nodeMarking = new TrapSet();

		// change every set of integer in result into set of petrinetNode in
		// nodeMarking
		Iterator<Set<Integer>> iterator = finalResult.iterator();
		while (iterator.hasNext()) {
			Collection<Integer> col = iterator.next();
			SortedSet<Place> set = new TreeSet<Place>();
			for (int i : col) {
				set.add(places.get(i));
			}
			nodeMarking.add(set);
		}
		return nodeMarking;
	}

	/**
	 * 
	 * 
	 * @return
	 */
	private boolean isNotSupersetOfAll(Set<Integer> updatedCombinedCol, Queue<Set<Integer>> result) {
		// remove all element in result that are superset of updatedCombinedCol
		int size = result.size();

		while (size > 0) {
			Set<Integer> oldSet = result.poll();

			if (updatedCombinedCol.containsAll(oldSet)) { // updatedCombinedCol
				// is a superset of
				// minimal trap or
				// the same
				result.add(oldSet);
				return false;
			} else { // check if the oldSet is a superset of updatedCombinedCol
				if (!oldSet.containsAll(updatedCombinedCol)) { // means that the
					// oldSet is not
					// a superset,
					// return the
					// oldSet
					result.add(oldSet);
				}
			}
			size--;
		}

		// return true iff there is no single set that is a subset of
		// updatedCombinedCol or the same as updatedCombinedCol
		return true;
	}

	// additional data structure to calculate expansion
	protected class ExpansionItem {
		private Set<Integer> expandedColumns;
		private DoubleMatrix1D currentColumnState;
		private int toBeExpandedBy;

		protected ExpansionItem(Set<Integer> expandedColumns, DoubleMatrix1D currentColumnState, int toBeExpandedBy) {
			this.expandedColumns = new HashSet<Integer>(expandedColumns);
			this.currentColumnState = currentColumnState;
			this.toBeExpandedBy = toBeExpandedBy;
		}

		/**
		 * @return the executedColumn
		 */
		public Set<Integer> getExpandedColumns() {
			return expandedColumns;
		}

		/**
		 * 
		 *            the executedColumn to set
		 */
		public void setExpandedColumn(Set<Integer> expandedColumns) {
			this.expandedColumns = expandedColumns;
		}

		/**
		 * @return the currentColumnState
		 */
		public DoubleMatrix1D getCurrentColumnState() {
			return currentColumnState;
		}

		/**
		 * 
		 *            the currentColumnState to set
		 */
		public void setCurrentColumnState(DoubleMatrix1D currentColumnState) {
			this.currentColumnState = currentColumnState;
		}

		/**
		 * @return the toBeExpandedBy
		 */
		public int getToBeExpandedBy() {
			return toBeExpandedBy;
		}

		/**
		 * 
		 *            the toBeExpandedBy to set
		 */
		public void setToBeExpandedBy(int toBeExpandedBy) {
			this.toBeExpandedBy = toBeExpandedBy;
		}
	}
}
