/**
 * 
 */
package petrinet.structuralanalysis;

import cern.colt.matrix.DoubleMatrix1D;

/**
 * This class provide static variable and unique operation(s) in
 * SignIncidenceMatrix This class is used in generating minimal siphons and
 * traps
 * 
 * @author arya
 * @email arya.adriansyah@gmail.com
 * @version Oct 18, 2008
 */
public class SignIncidenceMatrixOperator {
	public static final int NEUTRAL = Integer.MAX_VALUE;

	public static DoubleMatrix1D addMatrix1Column(DoubleMatrix1D first, DoubleMatrix1D second) {
		DoubleMatrix1D result = first.copy();
		for (int i = 0; i < result.size(); i++) {
			result.set(i, SignIncidenceMatrixOperator.add(first.get(i), second.get(i)));
		}
		return result;
	}

	/**
	 * @param x
	 * @param y
	 * @return
	 */
	public static double add(double x, double y) {
		// case if both have the same sign
		if (x == y) {
			return x;
		} else {
			// case when one of them is neutral
			if ((Double.compare(x, SignIncidenceMatrixOperator.NEUTRAL) == 0)
					|| (Double.compare(y, SignIncidenceMatrixOperator.NEUTRAL) == 0)) {
				return SignIncidenceMatrixOperator.NEUTRAL;
			} else {
				// case when both opposite sign
				if (((x < 0) && (y > 0)) || ((y < 0) && (x > 0))) {
					return SignIncidenceMatrixOperator.NEUTRAL;
				} else {
					// case if one of them is zero
					if (Double.compare(x, 0) == 0) {
						return y;
					} else {
						return x;
					}
				}
			}
		}
	}

}
