/**
 * 
 */
package petrinet.analysis;

import java.util.HashSet;
import java.util.SortedSet;

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
public abstract class AbstractComponentSet<T extends PetrinetNode> extends HashSet<SortedSet<T>> {

	private static final long serialVersionUID = -3251452860647366163L;
	private final String label;

	/**
	 * Default constructor
	 */
	public AbstractComponentSet(String label) {
		super();
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
