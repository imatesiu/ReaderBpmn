/**
 * 
 */
package petrinet.analysis;

/**
 * @author s072211
 * @email arya.adriansyah@gmail.com
 * @version Oct 6, 2008
 */
public abstract class NetAnalysisInformation<T> {

	private static final long serialVersionUID = -5795049028623481951L;

	public static enum UnDetBool {
		TRUE("True"), //
		FALSE("False"), //
		UNDETERMINED("Undetermined");
		private final String label;

		private UnDetBool(String label) {
			this.label = label;
		}

		public String toString() {
			return label;
		}
	}

	public static class BOUNDEDNESS extends NetAnalysisInformation<UnDetBool> {
		private static final long serialVersionUID = 3577400828545830945L;

		public BOUNDEDNESS() {
			super("Net is bounded");
		}
	}

	public static class FREECHOICE extends NetAnalysisInformation<UnDetBool> {
		private static final long serialVersionUID = 3577400828545830945L;

		public FREECHOICE() {
			super("Net is free-choice");
		}
	}

	public static class EXTFREECHOICE extends NetAnalysisInformation<UnDetBool> {
		private static final long serialVersionUID = 3577400828545830945L;

		public EXTFREECHOICE() {
			super("Net is extended free-choice");
		}
	}

	public static class LIVENESS extends NetAnalysisInformation<UnDetBool> {
		private static final long serialVersionUID = 3577400828545830945L;

		public LIVENESS() {
			super("Net is live");
		}
	}
	
	// AA: add analysis result for relaxed soundness analysis
	public static class RELAXEDSOUND extends NetAnalysisInformation<UnDetBool> {
		public RELAXEDSOUND() {
			super("Net is relaxed sound");
		}
	}

	private final String label;
	private T value;

	/**
	 * default constructor
	 */
	public NetAnalysisInformation(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public boolean equals(Object o) {
		if (o.getClass().equals(this.getClass())) {
			NetAnalysisInformation<?> info = (NetAnalysisInformation<?>) o;
			return (value.equals(info.getValue()) && label.equals(info.getLabel()));
		} else {
			return false;
		}
	}

	public void setValue(T value) {
		this.value = value;
	}

	public T getValue() {
		return value;
	}

}
