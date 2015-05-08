import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jbpt.petri.Marking;
import org.jbpt.petri.NetSystem;
import org.jbpt.petri.PetriNet;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;

import models.graphbased.directed.bpmn.BPMNDiagram;
import models.graphbased.directed.bpmn.BPMNEdge;
import models.graphbased.directed.bpmn.BPMNNode;
import models.graphbased.directed.bpmn.elements.Activity;
import models.graphbased.directed.bpmn.elements.Event;
import models.graphbased.directed.bpmn.elements.Event.EventTrigger;
import models.graphbased.directed.bpmn.elements.Event.EventType;
import models.graphbased.directed.bpmn.elements.Flow;
import models.graphbased.directed.bpmn.elements.Gateway;
import models.graphbased.directed.bpmn.elements.Gateway.GatewayType;


public class BPMNtoNetSystem {

	PetriNet net=null;
	BPMNtoNetSystem(BPMNDiagram d){
		net = getNetSystem(d);
	}
	
	
	private PetriNet getNetSystem(BPMNDiagram diagram){
		
		
		LinkedHashMap<Flow, Place> flowMap = new LinkedHashMap<Flow, Place>();
		String nameBPMNDiagram =  diagram.getLabel();
		if(nameBPMNDiagram.length()<=0){
			nameBPMNDiagram="Unnamed";

		}	
		net = new PetriNet();
		Marking marking = new Marking(net);
	

		traslateFlow(diagram, flowMap, net);

		translateTask(diagram, flowMap, net,0);

		translateGateway(diagram, flowMap, net);

		translateEvent(diagram, flowMap, net, marking);
		
		
		return net;
	}
	
	
	private void traslateFlow(BPMNDiagram bpmn,
			LinkedHashMap<Flow, Place> flowMap, PetriNet net) {
		// gli archi del diagramma BPMN diventano piazze della rete Petri
		for (Flow g : bpmn.getFlows()) {
			String f = g.getSource().getLabel() + "#";
			String z = g.getTarget().getLabel();
		
			// System.out.println("parent: " + parent);
				Place newp = new Place(f + z);
				Place p = net.addPlace(newp);

				flowMap.put(g, p);
			

		}

	}
	
	private void translateTask(BPMNDiagram bpmn,
			LinkedHashMap<Flow, Place> flowMap, PetriNet net, int typelifecycle) {

		for (Activity c : bpmn.getActivities()) {
			
				String id = c.getLabel();
				if(typelifecycle==0){
					Transition t = new Transition(id + "+start");
					t = net.addTransition(t);
					Transition t1  = new Transition(id + "+complete");
					net.addTransition(t1);
					Place p = new Place(id);
					 net.addPlace(p);
					net.addFlow(t, p);

					net.addFlow(p, t1);

					for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> s : c
							.getGraph().getInEdges(c)) {
						if (s instanceof Flow) {

							Place pst = flowMap.get(s);

							net.addFlow(pst, t);
						}

					}
					for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> s : c
							.getGraph().getOutEdges(c)) {
						if (s instanceof Flow) {

							Place pst = flowMap.get(s);

							net.addFlow(t1, pst);
						}
					}
				}else{
					Transition t = new Transition(id + "+complete");
					 net.addTransition(t);
					for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> s : c
							.getGraph().getInEdges(c)) {
						if (s instanceof Flow) {

							Place pst = flowMap.get(s);

							net.addFlow(pst, t);
						}

					}
					for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> s : c
							.getGraph().getOutEdges(c)) {
						if (s instanceof Flow) {

							Place pst = flowMap.get(s);

							net.addFlow(t, pst);
						}
					}
					
				}

			
		}

	}

	private void translateGateway(BPMNDiagram bpmn,
			LinkedHashMap<Flow, Place> flowMap, PetriNet net) {
		for (Gateway g : bpmn.getGateways()) {
		
				// gateway data-based
				if (g.getGatewayType().equals(GatewayType.DATABASED)) {
					int i = 0;
					Map<String, Transition> tranMap = new HashMap<String, Transition>();

					// gateway data-based if branch
					if (g.getGraph().getOutEdges(g).size() > 1
							&& g.getGraph().getInEdges(g).size() == 1) {
						for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> s : g
								.getGraph().getOutEdges(g)) {
							String source = s.getSource().getLabel();
							String target = s.getTarget().getLabel();
							Transition t = new Transition(g.getLabel() + "_"
									+ i++);
							 net.addTransition(t);
							
							tranMap.put(target + source, t);

							Place pst = flowMap.get(s);

							net.addFlow(t, pst);

						}
						for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> s : g
								.getGraph().getInEdges(g)) {
							String source = s.getSource().getLabel();
							String target = s.getTarget().getLabel();

							Place pst = flowMap.get(s);

							for (Transition t : tranMap.values()) {

								net.addFlow(pst, t);

							}
						}
					} else {
						// gateway merge
						if (g.getGraph().getOutEdges(g).size() == 1
								&& g.getGraph().getInEdges(g).size() > 1) {

							Place ps = null;
							for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> out : g
									.getGraph().getOutEdges(g)) {
								ps = flowMap.get(out);
							}
							i = 0;
							for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> s : g
									.getGraph().getInEdges(g)) {

								Place pst = flowMap.get(s);

								Transition t = new Transition(g.getLabel()
										+ "_" + i++);
								net.addTransition(t);
								
								net.addFlow(pst, t);

								net.addFlow(t, ps);

							}
						} else {
							if (g.getGraph().getOutEdges(g).size() > 1
									&& g.getGraph().getInEdges(g).size() > 1) {
								i = 0;
								Place placecentral = new Place(g.getLabel());
								net.addPlace(placecentral);
								for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> s : g
										.getGraph().getOutEdges(g)) {
									String source = s.getSource().getLabel();
									String target = s.getTarget().getLabel();

									Transition t = new Transition(
											g.getLabel() + "_" + i++
											);
									net.addTransition(t);
									tranMap.put(target + source, t);

									Place pst = flowMap.get(s);

									net.addFlow(t, pst);

									net.addFlow(placecentral, t);
								}
								//i = 0;
								for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> s : g
										.getGraph().getInEdges(g)) {
									String source = s.getSource().getLabel();
									String target = s.getTarget().getLabel();

									Place pst = flowMap.get(s);

									Transition t = new Transition(
											g.getLabel() + "_" + i++);
									net.addTransition(t);
									net.addFlow(pst, t);

									net.addFlow(t, placecentral);
									/*
									 * Place pst = flowMap.get(s);
									 * 
									 * for (Transition t : tranMap.values()) {
									 * 
									 * net.addFlow(pst, t, 1);
									 * 
									 * }
									 */
								}
							}

						}
					}

				} else {
					if (g.getGatewayType().equals(GatewayType.PARALLEL)) {
						// gateway parallel fork
						if (g.getGraph().getOutEdges(g).size() > 1
								&& g.getGraph().getInEdges(g).size() == 1) {
							BPMNEdge<? extends BPMNNode, ? extends BPMNNode> so = g
									.getGraph().getInEdges(g).iterator().next();

							Place ps = flowMap.get(so);
							
							Transition t = new Transition(g.getLabel()
									+ "_fork");
							
									net.addTransition(t);
						
							net.addFlow(ps, t);
							for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> s : g
									.getGraph().getOutEdges(g)) {

								Place pst = flowMap.get(s);
								net.addFlow(t, pst);

							}

						} else {
							// gateway parallel Join
							if (g.getGraph().getOutEdges(g).size() == 1
									&& g.getGraph().getInEdges(g).size() > 1) {
								BPMNEdge<? extends BPMNNode, ? extends BPMNNode> so = g
										.getGraph().getOutEdges(g).iterator()
										.next();

								Place ps = flowMap.get(so);
								Transition t = new Transition((g.getLabel()
										+ "_join"));
										
								net.addTransition(t);
								
								net.addFlow(t, ps);
								for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> s : g
										.getGraph().getInEdges(g)) {

									Place pst = flowMap.get(s);
									net.addFlow(pst, t);

								}

							}
						}
					} else {
						// gateway event-based
						if (g.getGatewayType().equals(GatewayType.EVENTBASED)) {
							// Exclusive event gateway
							if (g.getGraph().getOutEdges(g).size() > 1
									&& g.getGraph().getInEdges(g).size() == 1) {
								BPMNEdge<? extends BPMNNode, ? extends BPMNNode> so = g
										.getGraph().getInEdges(g).iterator()
										.next();

								Place ps = flowMap.get(so);
								int i = 0;
								for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> s : g
										.getGraph().getOutEdges(g)) {

									Place pst = flowMap.get(s);

									Transition t = new Transition(
											g.getLabel() + "_" + i++
											);
									
									net.addTransition(t);
								
									net.addFlow(t, pst);

									net.addFlow(ps, t);

								}

							}

						}
					}
				}
			
		}
	}

	private void translateEvent(BPMNDiagram bpmn,
			LinkedHashMap<Flow, Place> flowMap, PetriNet net,
			Marking marking) {
		for (Event e : bpmn.getEvents()) {
		
				if (e.getEventType().equals(EventType.START)
						&& e.getEventTrigger().equals(EventTrigger.NONE)) {

					// Place p = new Place(e.getLabel(), net);
					String name = e.getLabel().toUpperCase();
					Place p = new Place("p" + e.getLabel());
							net.addPlace(p);

					Transition t = new Transition("t_" + name);
							net.addTransition(t);
					
					net.addFlow(p, t);
					
					
						marking.put(p, 1);
					
					for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> s : e
							.getGraph().getOutEdges(e)) {

						Place pst = flowMap.get(s);

						net.addFlow(t, pst);

					}

				}
				if (e.getEventType().equals(EventType.END)
						&& e.getEventTrigger().equals(EventTrigger.NONE)) {

					Place p = new  Place("p" + e.getLabel());
					net.addPlace(p);

					Transition t = new Transition("t_" + e.getLabel());
					net.addTransition(t);

				
					net.addFlow(t, p);

					for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> s : e
							.getGraph().getInEdges(e)) {

						Place pst = flowMap.get(s);

						net.addFlow(pst, t);

					}

				}
				if (e.getEventType().equals(EventType.INTERMEDIATE)
						&& !e.getEventTrigger().equals(EventTrigger.NONE)) {

					Transition t = new Transition(e.getLabel());
					net.addTransition(t);

					if (e.getBoundingNode() == null) {
						BPMNEdge<? extends BPMNNode, ? extends BPMNNode> g = e
								.getGraph().getInEdges(e).iterator().next();
						if (g instanceof Flow && g != null) {
							Place ps_pre = flowMap.get(g);

							g = e.getGraph().getOutEdges(e).iterator().next();
							;
							if (g instanceof Flow && g != null) {
								Place ps_post = flowMap.get(g);

								net.addFlow(ps_pre, t);
								net.addFlow(t, ps_post);
							}

						}
					} else {
						//  un evento di confine

					}

				}

			}
		

	}


	public PetriNet getPN() {
		
		return null;
	}

	
}
