/**
 * 
 */
package petrinet.analysis;

import java.util.HashMap;

/**
 * @author Arya Adriansyah
 * @email a.adriansyah@tue.nl
 * @version May 28, 2010
 */
public class AggPNStructuralAnalysisResult extends HashMap<String, Object> {
	private static final long serialVersionUID = 1324462666645431358L;

	// all available results
	public static String PLACEINVARIANT = "Basis of Semi-Positive Place Invariants";
	public static String TRANSITIONINVARIANT = "Basis of Semi-Positive Transition Invariants";
	public static String TPHANDLES = "T-P Handles";
	public static String PTHANDLES = "P-T Handles";
	public static String SIPHONSET = "Minimal Siphons";
	public static String TRAPSET = "Minimal Traps";
	public static String SCOMPONENTSET = "S-Components";
	public static String TCOMPONENTSET = "T-Components";
	public static String FREECHOICE = "Free choice/extended free choice identification";

	public AggPNStructuralAnalysisResult() {
		super();
	}
	
	public boolean equals(Object o) {
		if (o instanceof AggPNStructuralAnalysisResult) {
			return (super.equals(o));
		} else {
			return false;
		}
	}

}
