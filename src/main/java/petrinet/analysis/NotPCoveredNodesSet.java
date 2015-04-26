package petrinet.analysis;

import models.graphbased.directed.petrinet.elements.Place;

public class NotPCoveredNodesSet extends AbstractComponentSet<Place> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5492826790366603138L;

	public NotPCoveredNodesSet() {
		super("Not P-covered places");
	}

	public boolean equals(Object o) {
		if (o instanceof NotPCoveredNodesSet) {
			return (super.equals(o));
		} else {
			return false;
		}
	}

}
