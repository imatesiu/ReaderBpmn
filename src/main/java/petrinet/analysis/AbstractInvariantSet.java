/**
 * 
 */
package petrinet.analysis;

import java.util.HashSet;

import framework.util.collection.MultiSet;
import models.graphbased.directed.petrinet.PetrinetNode;

/**
 * This class represents a node marking. A set in this class stores
 * place/transition which is need to be marked. A set of set enables this class
 * to store several set of place/transition which are need to be marked.
 * 
 * @author arya
 * @email arya.adriansyah@gmail.com
 * @version Oct 5, 2008
 */
public abstract class AbstractInvariantSet<T extends PetrinetNode> extends HashSet<MultiSet<T>> {

	private static final long serialVersionUID = -6222567583748289635L;
	private final String label;

	/**
	 * Default constructor
	 */
	public AbstractInvariantSet(String label) {
		this.label = label;
	}

	/**
	 * getter for label
	 * 
	 * @return
	 */
	public String getLabel() {
		return label;
	}

}
