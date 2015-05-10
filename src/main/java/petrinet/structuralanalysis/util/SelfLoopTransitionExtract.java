/**
 * 
 */
package petrinet.structuralanalysis.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import models.graphbased.directed.petrinet.PetrinetEdge;
import models.graphbased.directed.petrinet.PetrinetGraph;
import models.graphbased.directed.petrinet.PetrinetNode;
import models.graphbased.directed.petrinet.elements.Arc;
import models.graphbased.directed.petrinet.elements.InhibitorArc;
import models.graphbased.directed.petrinet.elements.Place;
import models.graphbased.directed.petrinet.elements.ResetArc;
import models.graphbased.directed.petrinet.elements.Transition;

/**
 * @author aadrians Aug 13, 2012
 * 
 */
public class SelfLoopTransitionExtract {
	
	/**
	 * Return mapping from self-loop transitions and their input/output place
	 * Reset arcs and inhibitor arcs are ignored
	 * Weight of arcs is taken into account
	 *  
	 * @param net
	 * @return
	 */
	public static Map<Transition, Set<Place>> getSelfLoopTransitions(PetrinetGraph net) {
		// get self-loop transitions for further specific handling
		Map<Transition, Set<Place>> selfLoopT = new HashMap<Transition, Set<Place>>();
		checking: for (Transition t : net.getTransitions()) {
			Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> outE = net.getOutEdges(t);
			Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> inE = net.getInEdges(t);

			if ((outE != null) && (inE != null) && (outE.size() > 0) && (inE.size() > 0)) {
				// check if they are thesame
				for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> e : inE) {
					if (!(e instanceof InhibitorArc) && !(e instanceof ResetArc)) {
						Arc arc = net.getArc(e.getTarget(), e.getSource());
						if ((arc == null) || (arc.getWeight() != net.getArc(e.getSource(), e.getTarget()).getWeight())) {
							continue checking;
						}
					}
				}

				// up to this point, the same arc for input
				for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> e : outE) {
					if (!(e instanceof InhibitorArc) && !(e instanceof ResetArc)) {
						Arc arc = net.getArc(e.getTarget(), e.getSource());
						if ((arc == null) || (arc.getWeight() != net.getArc(e.getSource(), e.getTarget()).getWeight())) {
							continue checking;
						}
					}
				}

				// up to this point, we get transitions with the same input and output
				Set<Place> places = new HashSet<Place>(inE.size());
				for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> e : inE) {
					places.add((Place) e.getSource());
				}
				selfLoopT.put(t, places);
			}
		}
		return selfLoopT;
	}
}
