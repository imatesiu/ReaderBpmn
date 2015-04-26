package petrinet.analysis;

import models.graphbased.directed.petrinet.PetrinetNode;

public class NonFreeChoiceClustersSet extends AbstractComponentSet<PetrinetNode> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5257330434611228634L;

	public NonFreeChoiceClustersSet() {
		super("Non-free-choice Clusters");
	}

	public boolean equals(Object o) {
		if (o instanceof NonFreeChoiceClustersSet) {
			return (super.equals(o));
		} else {
			return false;
		}
	}

}
