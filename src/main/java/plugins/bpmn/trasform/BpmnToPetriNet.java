package plugins.bpmn.trasform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import models.graphbased.NodeID;
import models.graphbased.directed.ContainableDirectedGraphElement;
import models.graphbased.directed.ContainingDirectedGraphNode;
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
import models.graphbased.directed.bpmn.elements.SubProcess;
import models.graphbased.directed.bpmn.elements.Swimlane;
import models.graphbased.directed.petrinet.PetrinetGraph;
import models.graphbased.directed.petrinet.PetrinetNode;
import models.graphbased.directed.petrinet.elements.ExpandableSubNet;
import models.graphbased.directed.petrinet.elements.Place;
import models.graphbased.directed.petrinet.elements.Transition;
import models.graphbased.directed.petrinet.impl.PetrinetFactory;
import models.semantics.petrinet.Marking;

public class BpmnToPetriNet {

	private PetrinetGraph net=null;
	private ExpandableSubNet subNet = null;
	private Marking marking  = new Marking();
	private Map<PetrinetNode,BPMNNode> mapPNBP;
	private Map<BPMNNode,PetrinetNode> mapBPPN;
	private boolean exploresub=false;
	public BpmnToPetriNet(BPMNDiagram bpmn){
		mapPNBP = new HashMap<PetrinetNode,BPMNNode>();
		mapBPPN = new HashMap<BPMNNode,PetrinetNode>();
		if(bpmn!=null)
			createPetriNet(bpmn);

	}

	public BpmnToPetriNet(BPMNDiagram bpmn, boolean exploresub){
		mapPNBP = new HashMap<PetrinetNode,BPMNNode>();
		mapBPPN = new HashMap<BPMNNode,PetrinetNode>();
		if(bpmn!=null)
			createPetriNet(bpmn);
		this.exploresub=exploresub;
	}

	public Map<PetrinetNode,BPMNNode> getMap(){
		return mapPNBP;
	}

	public PetrinetGraph getPetriNet(){

		return net;
	}

	public Marking getMarking() {
		return marking;
	}

	private void createPetriNet(BPMNDiagram bpmn ){
		LinkedHashMap<Flow, Place> flowMap = new LinkedHashMap<Flow, Place>();
		String nameBPMNDiagram =  bpmn.getLabel();
		if(nameBPMNDiagram.length()<=0){
			nameBPMNDiagram="Unnamed";

		}




		net = PetrinetFactory.newPetrinet(nameBPMNDiagram);

		Collection<Swimlane> collezioneswimlane = bpmn.getSwimlanes();
		ContainingDirectedGraphNode parent = null;

		traslateFlow(bpmn, flowMap, net, parent);

		translateTask(bpmn, flowMap, net, parent,1);

		translateGateway(bpmn, flowMap, net, parent);



		if(exploresub){
			translateSubProcess(bpmn, flowMap, net, marking, parent);
		}else{
			translateSubProcessNE(bpmn, flowMap, net, marking, parent);
		}

		translateEvent(bpmn, flowMap, net, marking, parent);






	}


	private void translateSubProcess(BPMNDiagram bpmn,
			LinkedHashMap<Flow, Place> flowMap, PetrinetGraph net,
			Marking marking, ContainingDirectedGraphNode parent) {
		for (SubProcess sub : bpmn.getSubProcesses()) {
			HashSet<ContainableDirectedGraphElement> childre = (HashSet<ContainableDirectedGraphElement>) sub
					.getChildren();
			List<BPMNNode> listnode = new ArrayList<BPMNNode>();
			List<Flow> listFlow = new ArrayList<Flow>();
			for (ContainableDirectedGraphElement containableDirectedGraphElement : childre) {
				if (containableDirectedGraphElement instanceof BPMNNode) {

					listnode.add((BPMNNode) containableDirectedGraphElement);
					System.out.println(""
							+ ((BPMNNode) containableDirectedGraphElement)
							.getLabel());
				} else {
					if (containableDirectedGraphElement instanceof Flow) {

						listFlow.add((Flow) containableDirectedGraphElement);
						System.out.println(""
								+ ((Flow) containableDirectedGraphElement)
								.getEdgeID());
					}
				}
			}



			traslateFlow(bpmn, flowMap, net, sub);

			translateTask(bpmn, flowMap, net, sub,1);

			translateGateway(bpmn, flowMap, net, sub);

			translateEvent(bpmn, flowMap, net, marking, sub);

			Transition t = net.addTransition(sub.getLabel() + "start",
					this.subNet);
			t.setInvisible(true);
			Transition tend = net.addTransition(sub.getLabel() + "end",
					this.subNet);
			tend.setInvisible(true);

			for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> s : sub
					.getGraph().getInEdges(sub)) {
				if (s instanceof Flow) {

					Place pst = flowMap.get(s);

					net.addArc(pst, t, 1, this.subNet);
				}

			}

			for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> s : sub
					.getGraph().getOutEdges(sub)) {
				if (s instanceof Flow) {

					Place pst = flowMap.get(s);

					net.addArc(tend, pst, 1, this.subNet);
				}

			}

			Set<ContainableDirectedGraphElement> children = sub.getChildren();
			for (ContainableDirectedGraphElement containableDirectedGraphElement : children) {
				if (containableDirectedGraphElement instanceof Event) {
					Event evnt = (Event) containableDirectedGraphElement;
					if (evnt.getEventType() == EventType.START
							& evnt.getEventTrigger() == EventTrigger.NONE) {
						for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> s : evnt
								.getGraph().getOutEdges(evnt)) {
							if (s instanceof Flow) {

								Place pst = flowMap.get(s);

								Transition t2 = (Transition) pst.getGraph()
										.getInEdges(pst).iterator().next()
										.getSource();
								Place pt2 = (Place) t2.getGraph()
										.getInEdges(t2).iterator().next()
										.getSource();
								net.addArc(t, pt2, 1, this.subNet);
							}

						}
					} else {
						if (evnt.getEventType() == EventType.END
								& evnt.getEventTrigger() == EventTrigger.NONE) {
							for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> s : evnt
									.getGraph().getInEdges(evnt)) {
								if (s instanceof Flow) {

									Place pst = flowMap.get(s);

									Transition t2 = (Transition) pst.getGraph()
											.getOutEdges(pst).iterator().next()
											.getTarget();
									Place pt2 = (Place) t2.getGraph()
											.getOutEdges(t2).iterator().next()
											.getTarget();
									net.addArc(pt2, tend, 1, this.subNet);
								}

							}
						}
					}
				} else {
					if (containableDirectedGraphElement instanceof Activity) {

					}
				}
			}

		}
	}

	private void translateSubProcessNE(BPMNDiagram bpmn,
			LinkedHashMap<Flow, Place> flowMap, PetrinetGraph net,
			Marking marking, ContainingDirectedGraphNode parent) {
		for (SubProcess sub : bpmn.getSubProcesses()) {
			HashSet<ContainableDirectedGraphElement> childre = (HashSet<ContainableDirectedGraphElement>) sub
					.getChildren();
			List<BPMNNode> listnode = new ArrayList<BPMNNode>();
			List<Flow> listFlow = new ArrayList<Flow>();
			for (ContainableDirectedGraphElement containableDirectedGraphElement : childre) {
				if (containableDirectedGraphElement instanceof BPMNNode) {

					listnode.add((BPMNNode) containableDirectedGraphElement);
					System.out.println(""
							+ ((BPMNNode) containableDirectedGraphElement)
							.getLabel());
				} else {
					if (containableDirectedGraphElement instanceof Flow) {

						listFlow.add((Flow) containableDirectedGraphElement);
						System.out.println(""
								+ ((Flow) containableDirectedGraphElement)
								.getEdgeID());
					}
				}
			}

			//transFlows(listFlow, flowMap, net);

			//transNodes(listnode, flowMap, net);

			sub.isBMultiinstance();


			Transition t = net.addTransition(sub.getLabel(),
					this.subNet);
			t.setInvisible(true);


			mapPNBP.put( t,sub);
			mapBPPN.put( sub,t);


			for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> s : sub
					.getGraph().getInEdges(sub)) {
				if (s instanceof Flow) {

					Place pst = flowMap.get(s);

					net.addArc(pst, t, 1, this.subNet);
				}

			}

			for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> s : sub
					.getGraph().getOutEdges(sub)) {
				if (s instanceof Flow) {

					Place pst = flowMap.get(s);

					net.addArc(t, pst, 1, this.subNet);
				}

			}

			/*traslateFlow(bpmn, flowMap, net, sub);

			translateTask(bpmn, flowMap, net, sub);

			translateGateway(bpmn, flowMap, net, sub);

			translateEvent(bpmn, flowMap, net, marking, sub);

			Transition t = net.addTransition(sub.getLabel() + "start",
					this.subNet);
			t.setInvisible(true);
			Transition tend = net.addTransition(sub.getLabel() + "end",
					this.subNet);
			tend.setInvisible(true);

			for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> s : sub
					.getGraph().getInEdges(sub)) {
				if (s instanceof Flow) {

					Place pst = flowMap.get(s);

					net.addArc(pst, t, 1, this.subNet);
				}

			}

			for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> s : sub
					.getGraph().getOutEdges(sub)) {
				if (s instanceof Flow) {

					Place pst = flowMap.get(s);

					net.addArc(tend, pst, 1, this.subNet);
				}

			}

			Set<ContainableDirectedGraphElement> children = sub.getChildren();
			for (ContainableDirectedGraphElement containableDirectedGraphElement : children) {
				if (containableDirectedGraphElement instanceof Event) {
					Event evnt = (Event) containableDirectedGraphElement;
					if (evnt.getEventType() == EventType.START
							& evnt.getEventTrigger() == EventTrigger.NONE) {
						for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> s : evnt
								.getGraph().getOutEdges(evnt)) {
							if (s instanceof Flow) {

								Place pst = flowMap.get(s);

								Transition t2 = (Transition) pst.getGraph()
										.getInEdges(pst).iterator().next()
										.getSource();
								Place pt2 = (Place) t2.getGraph()
										.getInEdges(t2).iterator().next()
										.getSource();
								net.addArc(t, pt2, 1, this.subNet);
							}

						}
					} else {
						if (evnt.getEventType() == EventType.END
								& evnt.getEventTrigger() == EventTrigger.NONE) {
							for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> s : evnt
									.getGraph().getInEdges(evnt)) {
								if (s instanceof Flow) {

									Place pst = flowMap.get(s);

									Transition t2 = (Transition) pst.getGraph()
											.getOutEdges(pst).iterator().next()
											.getTarget();
									Place pt2 = (Place) t2.getGraph()
											.getOutEdges(t2).iterator().next()
											.getTarget();
									net.addArc(pt2, tend, 1, this.subNet);
								}

							}
						}
					}
				} else {
					if (containableDirectedGraphElement instanceof Activity) {

					}
				}
			}
			 */
		}
	}

	private void traslateFlow(BPMNDiagram bpmn,
			LinkedHashMap<Flow, Place> flowMap, PetrinetGraph net,
			ContainingDirectedGraphNode parent) {
		// gli archi del diagramma BPMN diventano piazze della rete Petri
		for (Flow g : bpmn.getFlows()) {
			String f = sanitizeorid(g.getSource().getLabel(),g.getSource().getId() )+ "#";
			String z = sanitizeorid(g.getTarget().getLabel(),g.getTarget().getId());
			ContainingDirectedGraphNode parentFlow = g.getParentSubProcess();
			// System.out.println("parent: " + parent);
			if (parentFlow == parent) {
				Place p = net.addPlace(f + z, this.subNet);

				flowMap.put(g, p);
			}

		}

	}

	private String sanitizeorid(String input,NodeID nodeid){
		String san = nodeid.toString();
		if(input!=null){
			if(input.length()>0){

				san = input.replace("\n", " ").replace("\r", " ").replace("  ", " ");
			}
		}
		return san;
	}
	private void translateTask(BPMNDiagram bpmn,
			LinkedHashMap<Flow, Place> flowMap, PetrinetGraph net,
			ContainingDirectedGraphNode parent,int typelifecycle) {

		for (Activity c : bpmn.getActivities()) {
			if (parent == c.getParentSubProcess()) {
				String id = sanitizeorid(c.getLabel(), c.getId());
				if(typelifecycle==0){
					Transition t = net.addTransition(id + "+start", this.subNet);
					Transition t1 = net.addTransition(id + "+complete", this.subNet);

					Place p = net.addPlace(id, this.subNet);
					net.addArc(t, p, 1, this.subNet);

					net.addArc(p, t1, 1, this.subNet);

					for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> s : c
							.getGraph().getInEdges(c)) {
						if (s instanceof Flow) {

							Place pst = flowMap.get(s);

							net.addArc(pst, t, 1, this.subNet);
						}

					}
					for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> s : c
							.getGraph().getOutEdges(c)) {
						if (s instanceof Flow) {

							Place pst = flowMap.get(s);

							net.addArc(t1, pst, 1, this.subNet);
						}
					}
				}else{
					Transition t = net.addTransition(id , this.subNet);
					mapPNBP.put( t,c);
					mapBPPN.put( c,t);
					for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> s : c
							.getGraph().getInEdges(c)) {
						if (s instanceof Flow) {

							Place pst = flowMap.get(s);

							net.addArc(pst, t, 1, this.subNet);
						}

					}
					for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> s : c
							.getGraph().getOutEdges(c)) {
						if (s instanceof Flow) {

							Place pst = flowMap.get(s);

							net.addArc(t, pst, 1, this.subNet);
						}
					}

				}

			}
		}

	}

	private void translateGateway(BPMNDiagram bpmn,
			LinkedHashMap<Flow, Place> flowMap, PetrinetGraph net,
			ContainingDirectedGraphNode parent) {
		for (Gateway g : bpmn.getGateways()) {
			if (parent == g.getParentSubProcess()) {
				// gateway data-based
				if (g.getGatewayType().equals(GatewayType.DATABASED) || g.getGatewayType().equals(GatewayType.INCLUSIVE) || g.getGatewayType().equals(GatewayType.EVENTBASED)) {
					int i = 0;
					Map<String, Transition> tranMap = new HashMap<String, Transition>();

					// gateway data-based if branch
					if (g.getGraph().getOutEdges(g).size() > 1
							&& g.getGraph().getInEdges(g).size() == 1) {
						for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> s : g
								.getGraph().getOutEdges(g)) {
							String source = sanitizeorid(s.getSource().getLabel(),s.getSource().getId() );
							String target = sanitizeorid(s.getTarget().getLabel(),s.getTarget().getId());

							Transition t = net.addTransition(sanitizeorid(g.getLabel(),g.getId()) + "_"
									+ i++, this.subNet);
							t.setInvisible(true);
							tranMap.put(target + source, t);

							Place pst = flowMap.get(s);

							net.addArc(t, pst, 1, this.subNet);

						}
						for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> s : g
								.getGraph().getInEdges(g)) {
							//	String source = s.getSource().getLabel();
							//	String target = s.getTarget().getLabel();

							Place pst = flowMap.get(s);

							for (Transition t : tranMap.values()) {

								net.addArc(pst, t, 1, this.subNet);

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

								Transition t = net.addTransition(sanitizeorid(g.getLabel(),g.getId())
										+ "_" + i++, this.subNet);
								t.setInvisible(true);
								net.addArc(pst, t, 1, this.subNet);

								net.addArc(t, ps, this.subNet);

							}
						} else {
							if (g.getGraph().getOutEdges(g).size() > 1
									&& g.getGraph().getInEdges(g).size() > 1) {
								i = 0;
								Place placecentral = net.addPlace(sanitizeorid(g.getLabel(),g.getId()),
										this.subNet);
								for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> s : g
										.getGraph().getOutEdges(g)) {
									String source = sanitizeorid(s.getSource().getLabel(),s.getSource().getId() );
									String target = sanitizeorid(s.getTarget().getLabel(),s.getTarget().getId());

									Transition t = net.addTransition(
											sanitizeorid(g.getLabel(),g.getId()) + "_" + i++,
											this.subNet);
									t.setInvisible(true);
									tranMap.put(target + source, t);

									Place pst = flowMap.get(s);

									net.addArc(t, pst, 1, this.subNet);

									net.addArc(placecentral, t, this.subNet);
								}
								//i = 0;
								for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> s : g
										.getGraph().getInEdges(g)) {
									//		String source = s.getSource().getLabel();
									//		String target = s.getTarget().getLabel();

									Place pst = flowMap.get(s);

									Transition t = net.addTransition(
											sanitizeorid(g.getLabel(),g.getId()) + "_" + i++,
											this.subNet);
									t.setInvisible(true);
									net.addArc(pst, t, 1, this.subNet);

									net.addArc(t, placecentral, this.subNet);
									/*
									 * Place pst = flowMap.get(s);
									 * 
									 * for (Transition t : tranMap.values()) {
									 * 
									 * net.addArc(pst, t, 1, this.subNet);
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
							Transition t = net.addTransition(sanitizeorid(g.getLabel(),g.getId())
									+ "_fork", this.subNet);
							t.setInvisible(true);
							net.addArc(ps, t, 1, this.subNet);
							for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> s : g
									.getGraph().getOutEdges(g)) {

								Place pst = flowMap.get(s);
								net.addArc(t, pst, 1, this.subNet);

							}

						} else {
							// gateway parallel Join
							if (g.getGraph().getOutEdges(g).size() == 1
									&& g.getGraph().getInEdges(g).size() > 1) {
								BPMNEdge<? extends BPMNNode, ? extends BPMNNode> so = g
										.getGraph().getOutEdges(g).iterator()
										.next();

								Place ps = flowMap.get(so);
								Transition t = net.addTransition(sanitizeorid(g.getLabel(),g.getId())
										+ "_join", this.subNet);
								t.setInvisible(true);
								net.addArc(t, ps, 1, this.subNet);
								for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> s : g
										.getGraph().getInEdges(g)) {

									Place pst = flowMap.get(s);
									net.addArc(pst, t, 1, this.subNet);

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

									Transition t = net.addTransition(
											sanitizeorid(g.getLabel(),g.getId()) + "_" + i++,
											this.subNet);
									t.setInvisible(true);
									net.addArc(t, pst, 1, this.subNet);

									net.addArc(ps, t, this.subNet);

								}

							}

						}
					}
				}
			}
		}
	}

	private void translateEvent(BPMNDiagram bpmn,
			LinkedHashMap<Flow, Place> flowMap, PetrinetGraph net,
			Marking marking, ContainingDirectedGraphNode parent) {
		for (Event e : bpmn.getEvents()) {
			if (parent == e.getParentSubProcess()) {
				if (e.getEventType().equals(EventType.START)
						&& (e.getEventTrigger().equals(EventTrigger.NONE)|| e.getEventTrigger().equals(EventTrigger.MESSAGE))) {

					// Place p = new Place(e.getLabel(), net);
					String name = sanitizeorid(e.getLabel(),e.getId());
					Place p = net.addPlace("p" + name, this.subNet);

					Transition t = net.addTransition("t_" + name.toUpperCase(), this.subNet);
					t.setInvisible(true);
					net.addArc(p, t, 1, this.subNet);
					SubProcess sub = e.getParentSubProcess();
					if (sub == null) {
						marking.add(p, 1);
					}else{
						marking.add(p, 1);
						//marking.add(p, 190);
					}
					for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> s : e
							.getGraph().getOutEdges(e)) {

						Place pst = flowMap.get(s);

						net.addArc(t, pst, 1, this.subNet);

					}

				}
				if (e.getEventType().equals(EventType.END)
						&& (e.getEventTrigger().equals(EventTrigger.NONE)|| e.getEventTrigger().equals(EventTrigger.MESSAGE))) {
					String name =sanitizeorid(e.getLabel(),e.getId());
					Place p = net.addPlace("p" + name, this.subNet);

					Transition t = net.addTransition("t_" + name,
							this.subNet);

					t.setInvisible(true);
					net.addArc(t, p, 1, this.subNet);

					for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> s : e
							.getGraph().getInEdges(e)) {

						Place pst = flowMap.get(s);

						net.addArc(pst, t, 1, this.subNet);

					}

				}
				if (e.getEventType().equals(EventType.INTERMEDIATE)
						&& !e.getEventTrigger().equals(EventTrigger.NONE)) {


					Transition t = net.addTransition(sanitizeorid(e.getLabel(),e.getId()), this.subNet);

					if (e.getBoundingNode() == null) {
						BPMNEdge<? extends BPMNNode, ? extends BPMNNode> g = e
								.getGraph().getInEdges(e).iterator().next();
						if (g instanceof Flow && g != null) {
							Place ps_pre = flowMap.get(g);

							g = e.getGraph().getOutEdges(e).iterator().next();
							;
							if (g instanceof Flow && g != null) {
								Place ps_post = flowMap.get(g);

								net.addArc(ps_pre, t, 1, this.subNet);
								net.addArc(t, ps_post, 1, this.subNet);
							}

						}
					} else {
						Activity a = e.getBoundingNode();
						Place ps_pre;
						for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> s : a
								.getGraph().getOutEdges(a)) {
							if (s instanceof Flow) {

								ps_pre = flowMap.get(s);

								net.addArc(ps_pre, t, 1, this.subNet);
							}
						}


						//  un evento di confine
						BPMNEdge<? extends BPMNNode, ? extends BPMNNode> g = e.getGraph().getOutEdges(e).iterator().next();
						if (g instanceof Flow && g != null ) {
							Place ps_post = flowMap.get(g);


							net.addArc(t, ps_post, 1, this.subNet);

						}

					}

				}

			}
		}

	}


}
