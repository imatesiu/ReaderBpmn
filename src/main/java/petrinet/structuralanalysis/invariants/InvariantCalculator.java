package petrinet.structuralanalysis.invariants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import cern.colt.matrix.DoubleFactory1D;
import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.linalg.SeqBlas;

/**
 * <p>
 * Title: InvariantCalculator
 * </p>
 * <p>
 * Description: This static class calculates a minimal set of semi-positive
 * vectors v, such that incidenceMatrix*v = 0
 * </p>
 * <p>
 * Modified from its original version in ProM 5 module for compatibility with
 * Java 1.6
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: Technische Universiteit Eindhoven
 * </p>
 * 
 * @author Boudewijn van Dongen, modified by Arya Adriansyah
 * @version 1.1
 */

class InvariantCalculator {

	public static synchronized DoubleMatrix2D calculate(DoubleMatrix2D incidenceMatrix) {
		// Copy the incidence Matrix in A
		DoubleMatrix2D A = incidenceMatrix;
		// Make an identity matrix F such that [A|F] is a matrix;
		DoubleMatrix2D F = DoubleFactory2D.sparse.identity(A.rows());
		// Combine A and F
		DoubleMatrix2D AF = DoubleFactory2D.sparse.appendColumns(A, F);

		//	    Message.add(AF.toStringShort());

		// build pi and tau
		DoubleMatrix1D pi = DoubleFactory1D.dense.make(A.columns(), 0);
		DoubleMatrix1D tau = DoubleFactory1D.dense.make(A.columns(), 0);
		for (int i = 0; i < A.rows(); i++) {
			for (int k = 0; k < A.columns(); k++) {
				if (A.get(i, k) > 0) {
					pi.set(k, pi.get(k) + 1);
				}
				if (A.get(i, k) < 0) {
					tau.set(k, tau.get(k) + 1);
				}
			}
		}

		int k = 0;

		while (k > -1) {
			//System.err.println(AF.toString());
			k = 0;
			// Now, select a column k such that pi[k]==tau[k]==1
			while ((k < tau.size()) && !((pi.get(k) == 1) && (tau.get(k) == 1))) {
				k++;
			}
			// if k==tau.size() then select column k for which pi[k]*tau[k] is minimal
			if (k == tau.size()) {
				int prod = Integer.MAX_VALUE; //tau.size() * tau.size();
				int min_column = -1;
				k = 0;
				while ((prod > 0) && (k < tau.size())) {
					if ((tau.get(k) * pi.get(k) < prod) && (tau.get(k) > 0)
							&& (Math.abs(tau.get(k)) + Math.abs(pi.get(k)) > 1)) {
						prod = (int) (tau.get(k) * pi.get(k));
						min_column = k;
					}
					k++;
				}
				k = min_column;
			}
			if (k == -1) {
				continue;
			}

			// Select the first row j of AF whose column k in A is non zero.
			int j = 0;
			while (AF.get(j, k) == 0) {
				j++;
			}

			// We selected row j to do a sweep
			// Perform the sweep
			//			Message.add(AF.toString());
			//			Message.add("Sweeping with row: " + j + ", due to column " + k);
			//System.err.println("Row " + j + ", col " + k);

			List<Integer> keep = new ArrayList<Integer>();

			int rows = AF.rows();
			for (int row = 0; row < rows; row++) {
				if ((AF.get(row, k) == 0) || (row == j)) {
					if (row != j) {
						keep.add(new Integer(row));
					}
					continue;
				}
				// We found a row with a non zero element on [row,k]
				//double factor = -AF.get(row, k) / AF.get(j, k);
				/**
				 * We have to scale both rows so they add up yielding 0 in
				 * column 'k'. First, we determine the gcd.
				 */
				int gcd = gcd((int) Math.abs(AF.get(row, k)), (int) Math.abs(AF.get(j, k)));//

				/**
				 * Copy and scale row 'j'.
				 */
				DoubleMatrix1D scaledj = AF.viewRow(j).copy();//
				if ((AF.get(j, k) * AF.get(row, k)) > 0) {//
					/**
					 * Both rows have the same sign in column 'k'. Change the
					 * sign of row 'j' so that they add up nicely. It is
					 * important to change the sign ok 'j' and not the sign or
					 * 'row'.
					 */
					SeqBlas.seqBlas.dscal(-Math.abs(AF.get(row, k) / gcd), scaledj);//
				} else {//
					/**
					 * Both rows have different signs in column 'k'. OK.
					 */
					SeqBlas.seqBlas.dscal(Math.abs(AF.get(row, k) / gcd), scaledj);//
				}//
				/**
				 * Copy and scale row 'row'.
				 */
				DoubleMatrix1D scaledrow = AF.viewRow(row).copy();
				SeqBlas.seqBlas.dscal(Math.abs(AF.get(j, k) / gcd), scaledrow);//
				/**
				 * Now add the scaled row 'j' to the scaled row 'row'.
				 */
				SeqBlas.seqBlas.daxpy(/* factor */1, /* AF.viewRow(j) */scaledj, scaledrow);

				// update pi and tau based on newrow and AF.viewRow(row);
				for (int l = 0; l < pi.size(); l++) {
					if (scaledrow.get(l) > 0) {
						pi.set(l, pi.get(l) + 1);
					}
					if (scaledrow.get(l) < 0) {
						tau.set(l, tau.get(l) + 1);
					}
					if (AF.viewRow(row).get(l) > 0) {
						pi.set(l, pi.get(l) - 1);
					}
					if (AF.viewRow(row).get(l) < 0) {
						tau.set(l, tau.get(l) - 1);
					}
				}
				DoubleMatrix2D newrowcpy = DoubleFactory2D.dense.make(1, scaledrow.size());
				for (int l = 0; l < scaledrow.size(); l++) {
					newrowcpy.set(0, l, scaledrow.get(l));
				}
				AF = DoubleFactory2D.sparse.appendRows(AF, newrowcpy);

				//				Message.add(AF.toStringShort());

				keep.add(new Integer(AF.rows() - 1));

			}
			// Now eliminate all rows we don't need
			int[] list = new int[keep.size()];
			for (int i = 0; i < list.length; i++) {
				list[i] = keep.get(i);
			}
			// update pi and tau for deleting row j
			for (int l = 0; l < pi.size(); l++) {
				if (AF.get(j, l) > 0) {
					pi.set(l, pi.get(l) - 1);
				}
				if (AF.get(j, l) < 0) {
					tau.set(l, tau.get(l) - 1);
				}
			}
			// Update AF
			AF = AF.viewSelection(list, null).copy();

		}

		DoubleMatrix2D BT = null;
		{
			// We have now made a basis for the p-flows. Store this basis in F
			List<Integer> keep = new ArrayList<Integer>();
			for (int i = 0; i < AF.rows(); i++) {
				if (AF.viewRow(i).viewPart(0, A.columns()).cardinality() == 0) {
					keep.add(new Integer(i));
				}
			}
			// Now eliminate all rows we don't need
			int[] list = new int[keep.size()];
			for (int i = 0; i < list.length; i++) {
				list[i] = keep.get(i);
			}

			AF = AF.viewSelection(list, null);
			BT = AF.viewPart(0, A.columns(), AF.rows(), AF.columns() - A.columns()).copy();
		}

		// Using BT, we can now find minimal semi-POSITIVE vectors that are a
		// basis of the invariants.

		/*
		 * Message.add("Basis:\n" + BT.toString(), Message.DEBUG);
		 * Message.add("---", Message.DEBUG);
		 */

		DoubleMatrix2D U = BT.copy();
		SortedSet<Integer> U_minus = new TreeSet<Integer>();
		List<Integer> U_plus = new ArrayList<Integer>();
		// supports is an array of sets

		// build pi and tau
		pi = DoubleFactory1D.dense.make(BT.columns(), 0);
		tau = DoubleFactory1D.dense.make(BT.columns(), 0);
		for (int i = 0; i < BT.rows(); i++) {
			for (int l = 0; l < BT.columns(); l++) {
				if (BT.get(i, l) > 0) {
					pi.set(l, pi.get(l) + 1);
				}
				if (BT.get(i, l) < 0) {
					tau.set(l, tau.get(l) + 1);
				}
			}
		}

		/*
		 * Message.add("pi : "+pi.toString(), Message.DEBUG);
		 * Message.add("tau: "+tau.toString(), Message.DEBUG);
		 */

		k = 0;

		while (k > -1) {
			k = 0;
			// Now, select a column k such that (pi[k]-1)*tau[k]<0
			while ((k < tau.size()) && !((pi.get(k) - 1) * tau.get(k) < 0)) {
				k++;
			}
			// if k==tau.size() then select column k for which pi[k]*tau[k] is minimal
			if (k == tau.size()) {
				int prod = Integer.MAX_VALUE;
				int min_column = -1;
				k = 0;
				while ((prod > 0) && (k < tau.size())) {
					if ((tau.get(k) * pi.get(k) < prod) && (tau.get(k) > 0)
					/* && (Math.abs(tau.get(k)) + Math.abs(pi.get(k)) > 1) */) {
						prod = (int) (tau.get(k) * pi.get(k));
						min_column = k;
					}
					k++;
				}
				k = min_column;
			}
			if (k == -1) {
				//				Message.add("k=-1", Message.DEBUG);
				continue;
			}
			//			Message.add("k=" + k+" pi[k]="+pi.get(k)+" tau[k]="+tau.get(k)+" pi[k]*tau[k]="+pi.get(k)  * tau.get(k)+" (pi[k]-1)*tau[k]="+(pi.get(k) - 1) * tau.get(k) , Message.DEBUG);
			// We work with column k
			// Initialise U_minus and U_plus
			U_minus.clear();
			U_plus.clear();

			for (int l = 0; l < U.rows(); l++) {
				if (U.get(l, k) < 0) {
					U_minus.add(new Integer(l));
				}
				if (U.get(l, k) > 0) {
					U_plus.add(new Integer(l));
				}
			}

			//			ArrayList addedFlows = new ArrayList();
			Iterator<Integer> it = U_minus.iterator();
			while (it.hasNext()) {
				// Select a row from U_minus
				int row1 = it.next();
				Iterator<Integer> it2 = U_plus.iterator();
				while (it2.hasNext()) {
					// Select a row from U_plus
					int row2 = it2.next();

					Set<Integer> support = new HashSet<Integer>();

					// Calculate the indices i of U[row1] for which  U[row1,i]>0
					for (int i = 0; i < U.columns(); i++) {
						if (U.get(row1, i) > 0) {
							support.add(new Integer(i));
						}
						if (U.get(row2, i) > 0) {
							support.add(new Integer(i));
						}
					}

					// Check whether the new semiflow is minimal.
					// Check whether the support of the new semiflow does not
					// contain any of the supports of U
					boolean containsNone = true;
					for (int i = 0; (i < U.rows()) && containsNone; i++) {
						if ((i != row1) && (i != row2)) {
							Set<Integer> existingRowSup = new HashSet<Integer>();
							for (int j = 0; j < U.columns(); j++) {
								if (U.get(i, j) > 0) {
									existingRowSup.add(new Integer(j));
								}
							}

							containsNone = containsNone && !support.containsAll(existingRowSup);
						}
					}
					if (!containsNone) {
						continue;
					}
					// We found a new minimal semiflow
					int alpha = Math.abs((int) U.get(row2, k));
					int beta = Math.abs((int) U.get(row1, k));

					DoubleMatrix2D newRow = DoubleFactory2D.sparse.make(1, U.columns(), 0);
					SeqBlas.seqBlas.daxpy(alpha, U.viewRow(row1), newRow.viewRow(0));
					SeqBlas.seqBlas.daxpy(beta, U.viewRow(row2), newRow.viewRow(0));

					// Calculate the GCD of alpha and beta and all elements of the new semiflow
					int gcd = gcd(alpha, beta);
					int i = 0;
					while ((i < U.columns()) && (gcd > 1)) {
						if (newRow.get(0, i) > 0) {
							gcd = gcd(gcd, (int) newRow.get(0, i));
						}
						i++;
					}
					i = 0;
					while (i < U.columns()) {
						if (gcd == 0) {
							gcd = 0;
						}
						if (newRow.get(0, i) % gcd != 0) {
							int x = gcd;
							gcd = x;
						}
						newRow.set(0, i, (int) (newRow.get(0, i)) / gcd);
						// update tau and pi
						if (newRow.get(0, i) > 0) {
							pi.set(i, pi.get(i) + 1);
						}
						if (newRow.get(0, i) < 0) {
							tau.set(i, tau.get(i) + 1);
						}
						i++;

					}

					// Add the new row to U.
					U = DoubleFactory2D.sparse.appendRows(U, newRow);

				}
			}
			// After iteration, do a cleanup:

			// Eliminate all rows indicated by U_minus
			//			ArrayList<Integer> lambdaNew = new ArrayList<Integer>();
			int[] keep = new int[U.rows() - U_minus.size()];
			int j = 0;
			for (int i = 0; i < U.rows(); i++) {
				if (U_minus.contains(new Integer(i))) {
					// We eliminate row i from U
					// update tau and pi
					for (int l = 0; l < U.columns(); l++) {
						if (U.get(i, l) > 0) {
							pi.set(l, pi.get(l) - 1);
						}
						if (U.get(i, l) < 0) {
							tau.set(l, tau.get(l) - 1);
						}
					}
					continue;
				}
				keep[j] = i;
				j++;
			}
			U = U.viewSelection(keep, null).copy();

		}

		return U;

	}

	public static int getGCDs(int[] numbers) {
		int n = numbers[0]; // first number from numbers
		for (int index = 1; index < numbers.length; index++) {
			n = gcd(n, numbers[index]);
		}
		return n;
	}

	public static synchronized int gcd(int m, int n) {
		while ((m != 0) & (n != 0)) {
			if (m > n) {
				m = m % n;
			} else {
				n = n % m;
			}
		}
		return m + n;
	}
}
