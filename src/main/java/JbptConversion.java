
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import models.graphbased.directed.AbstractDirectedGraph;
import models.graphbased.directed.AbstractDirectedGraphNode;
import models.graphbased.directed.DirectedGraphEdge;
import models.graphbased.directed.petrinet.Petrinet;
import models.graphbased.directed.petrinet.PetrinetEdge;
import models.graphbased.directed.petrinet.PetrinetNode;
import models.graphbased.directed.petrinet.elements.Place;
import models.graphbased.directed.petrinet.elements.Transition;
import models.graphbased.directed.petrinet.impl.PetrinetFactory;


public class JbptConversion {

	@SuppressWarnings("unchecked")
	public static org.jbpt.petri.NetSystem convert(
			Petrinet _pn) {
		org.jbpt.petri.NetSystem ns = new org.jbpt.petri.NetSystem();
		Map<AbstractDirectedGraphNode, org.jbpt.petri.Node> nodeMap = new HashMap<AbstractDirectedGraphNode, org.jbpt.petri.Node>();
		for (Transition _t : _pn
				.getTransitions()) {
			org.jbpt.petri.Transition t = new org.jbpt.petri.Transition(
					_t.getLabel());
			if (_t.isInvisible()) {
				t.setLabel("");
			}
			ns.addTransition(t);
			nodeMap.put(_t, t);
		}
		for (Place _p : _pn
				.getPlaces()) {
			org.jbpt.petri.Place p = new org.jbpt.petri.Place(
					_p.getLabel());
			ns.addPlace(p);
			
			if (_p.getGraph().getInEdges(_p).isEmpty()) {
				ns.getMarking().put(p, 1);
			}
			nodeMap.put(_p, p);
		}
		Set<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> _edges = _pn
				.getEdges();
		for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> _e : _edges) {
			PetrinetNode _tail = _e
					.getSource();
			PetrinetNode _head = _e
					.getTarget();
			ns.addFlow(nodeMap.get(_tail), nodeMap.get(_head));
		}
		return ns;
	}

	public static Petrinet convert(
			org.jbpt.petri.unfolding.CompletePrefixUnfolding _cpu) {
		Petrinet pn =  PetrinetFactory.newPetrinet("");
		Map<org.jbpt.hypergraph.abs.Vertex, AbstractDirectedGraphNode> nodeMap = new HashMap<org.jbpt.hypergraph.abs.Vertex, AbstractDirectedGraphNode>();
		for (org.jbpt.petri.unfolding.Event _e : _cpu.getEvents()) {
			Transition t = pn.addTransition(_e.getLabel());
			nodeMap.put(_e, t);
		}
		for (org.jbpt.petri.unfolding.Condition _c : _cpu.getConditions()) {
			Place p = pn.addPlace(_c.getLabel());
			
			nodeMap.put(_c, p);
		}
		for (org.jbpt.petri.unfolding.Event _e : _cpu.getEvents()) {
			for (org.jbpt.petri.unfolding.Condition _preC : _e
					.getPreConditions()) {
				pn.addArc((Place) nodeMap
						.get(_preC), (Transition) nodeMap
						.get(_e));
				
			}
		}
		for (org.jbpt.petri.unfolding.Condition _c : _cpu.getConditions()) {
			org.jbpt.petri.unfolding.Event _preE = _c.getPreEvent();
			if (_preE != null) {
				pn.addArc(
						(Transition) nodeMap
								.get(_preE),
						(Place) nodeMap
								.get(_c));
				
			}
		}
		return pn;
	}

}