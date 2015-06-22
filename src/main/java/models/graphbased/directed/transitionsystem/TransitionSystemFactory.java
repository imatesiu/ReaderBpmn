package models.graphbased.directed.transitionsystem;

public class TransitionSystemFactory {

	private TransitionSystemFactory() {
	}

	public static TransitionSystem newTransitionSystem(String label) {
		return new TransitionSystemImpl(label);
	}

	public static ReachabilityGraph newReachabilityGraph(String label) {
		return new ReachabilityGraph(label);
	}

	public static CoverabilityGraph newCoverabilityGraph(String label) {
		return new CoverabilityGraph(label);
	}

	public static TransitionSystem cloneTransitionSystem(TransitionSystem ts) {
		TransitionSystemImpl newTS = new TransitionSystemImpl(ts.getLabel());
		newTS.cloneFrom(ts);
		return newTS;
	}

	public static ReachabilityGraph cloneReachabilityGraph(ReachabilityGraph ts) {
		ReachabilityGraph newTS = new ReachabilityGraph(ts.getLabel());
		newTS.cloneFrom(ts);
		return newTS;
	}

	public static CoverabilityGraph cloneCoverabilityGraph(CoverabilityGraph ts) {
		CoverabilityGraph newTS = new CoverabilityGraph(ts.getLabel());
		newTS.cloneFrom(ts);
		return newTS;
	}

	public static CoverabilityGraph toCoverabilityGraph(ReachabilityGraph ts) {
		CoverabilityGraph newTS = new CoverabilityGraph(ts.getLabel());
		newTS.cloneFrom(ts);
		return newTS;
	}
}
