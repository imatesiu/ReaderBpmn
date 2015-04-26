/**
 * 
 */
package petrinet.analysis;

import java.util.HashMap;

/**
 * @author Arya Adriansyah
 * @email a.adriansyah@tue.nl
 * @version Jun 10, 2010
 */
public class AggCGGraph extends HashMap<String, Object> {
	private static final long serialVersionUID = 7481110692167379898L;

	// all available results
	public static String COVERABILITYSET = "Reachability Set";
	public static String ACCEPTSTATESET = "Accept State Set";
	public static String STARTSTATESET = "Start State Set";
	public static String COVERABILITYGRAPH = "Coverability Graph";

	public AggCGGraph() {
		super();
	}

	public boolean equals(Object o) {
		if (o instanceof AggCGGraph ) {
			return (super.equals(o));
		} else {
			return false;
		}
	}

}
