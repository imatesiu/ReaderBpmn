package petrinet.analysis;

import models.graphbased.directed.petrinet.PetrinetNode;

public class NonExtendedFreeChoiceClustersSet extends AbstractComponentSet<PetrinetNode> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3826959691484087598L;

	public NonExtendedFreeChoiceClustersSet() {
		super("Non-extended-free-choice Clusters");
	}

	public boolean equals(Object o) {
		if (o instanceof NonExtendedFreeChoiceClustersSet) {
			return (super.equals(o));
		} else {
			return false;
		}
	}

}
