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
public class AggPNBehavioralAnalysisResult extends HashMap<String, Object> {
	private static final long serialVersionUID = 987444330701827089L;

	// all available results
	public static String DEADTRANSITIONS = "Dead Transitions";
	public static String BOUNDEDNESS = "Boundedness";
	public static String HOMEMARKINGSET = "Home Marking";
	public static String LIVENESS = "Liveness";

	//	public static String FINALMARKINGSET = "FinalMarkingSet";
	//	public static String NONEXTENDEDFREECHOICECLUSTERSSET = "NonExtendedFreeChoiceClustersSet";
	//	public static String NONFREECHOICECLUSTERSSET = "NonFreeChoiceClustersSet";
	//	public static String NONLIVETRANSITIONSSET = "NonLiveTransitionsSet";
	//	public static String NOTPCOVEREDNODESSET = "NotPCoveredNodesSet";
	//	public static String NOTSCOVEREDNODESSET = "NotSCoveredNodesSet";
	//	public static String REACHABILITYSET = "ReachabilitySet";
	//	public static String SINKPLACESSET = "SinkPlacesSet";
	//	public static String SOURCEPLACESSET = "SourcePlacesSet";
	//	public static String UNCONNECTEDNODESSET = "UnconnectedNodesSet";
	//	public static String WORKFLOWNETUTILS = "WorkflowNetUtils";
	//	public static String COVERABILITYSET = "CoverabilitySet";

	public AggPNBehavioralAnalysisResult() {
		super();
	}
	
	public boolean equals(Object o) {
		if (o instanceof AggPNBehavioralAnalysisResult ) {
			return (super.equals(o));
		} else {
			return false;
		}
	}

}