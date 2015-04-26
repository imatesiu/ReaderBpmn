/**
 * 
 */
package petrinet.analysis;

import models.graphbased.directed.petrinet.elements.Transition;

/**
 * @author aadrians
 *
 */
public class NonRelaxedSoundTransitionsSet extends AbstractComponentSet<Transition> {
	private static final long serialVersionUID = 3702391482517528837L;

	public NonRelaxedSoundTransitionsSet() {
		super("Unfireable transitions in any sound firing sequence");
	}

	public boolean equals(Object o) {
		if (o instanceof NonRelaxedSoundTransitionsSet) {
			return (super.equals(o));
		} else {
			return false;
		}
	}

}
