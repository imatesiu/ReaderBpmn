package models.graphbased.directed;

import models.graphbased.NodeID;

public interface DirectedGraphNode extends DirectedGraphElement, Comparable<DirectedGraphNode> {

	NodeID getId();

}
