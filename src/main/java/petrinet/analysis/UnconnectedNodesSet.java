package petrinet.analysis;

import models.graphbased.directed.petrinet.PetrinetNode;

public class UnconnectedNodesSet extends AbstractComponentSet<PetrinetNode> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3977932090025024925L;

	public UnconnectedNodesSet() {
		super("Unconnected Nodes");
	}

	public boolean equals(Object o) {
		if (o instanceof UnconnectedNodesSet) {
			return (super.equals(o));
		} else {
			return false;
		}
	}

}
