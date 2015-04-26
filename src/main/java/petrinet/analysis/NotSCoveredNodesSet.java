package petrinet.analysis;

import models.graphbased.directed.petrinet.PetrinetNode;

public class NotSCoveredNodesSet extends AbstractComponentSet<PetrinetNode> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 135742540321605681L;

	public NotSCoveredNodesSet() {
		super("Not S-covered nodes");
	}
	
	public boolean equals(Object o) {
		if (o instanceof NotSCoveredNodesSet) {
			return (super.equals(o));
		} else {
			return false;
		}
	}

}
