package models.graphbased.directed.bpmn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.SwingConstants;


import models.graphbased.AttributeMap;
import models.graphbased.directed.AbstractDirectedGraph;
import models.graphbased.directed.DirectedGraph;
import models.graphbased.directed.DirectedGraphEdge;
import models.graphbased.directed.DirectedGraphElement;
import models.graphbased.directed.DirectedGraphNode;
import models.graphbased.directed.bpmn.elements.*;
import models.graphbased.directed.bpmn.elements.Artifacts.ArtifactType;
import models.graphbased.directed.bpmn.elements.Gateway.*;
import models.graphbased.directed.bpmn.elements.Event.*;
import plugins.bpmn.BpmnAssociation.AssociationDirection;

// objects of this type should be represented in the framework by the
// BPMNDiagram interface.

public class BPMNDiagramImpl extends AbstractDirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>>
implements BPMNDiagram {

	protected final Set<Event> events;
	protected final List<Activity> activities;
	protected final Set<SubProcess> subprocesses;
	protected final List<Gateway> gateways;
	protected final Set<DataObject> dataObjects;
	protected final Set<TextAnnotation> textAnnotations;
	protected final List<Flow> flows;
	protected final Set<MessageFlow> messageFlows;
	protected final Set<DataAssociation> dataAssociations;
	protected final Set<Association> associations;
	protected final List<Swimlane> swimlanes;
	protected final Set<CallActivity> callActivities;	
	protected final Set<Artifacts> artifacts;
	protected final Set<FlowAssociation> flowsassociation;

	public BPMNDiagramImpl(String label) {
		super();
		events = new LinkedHashSet<Event>();
		activities = new ArrayList<Activity>();
		subprocesses = new LinkedHashSet<SubProcess>();
		gateways = new ArrayList<Gateway>();
		dataObjects = new LinkedHashSet<DataObject>();
		textAnnotations = new LinkedHashSet<TextAnnotation>();
		flows = new ArrayList<Flow>();
		messageFlows = new LinkedHashSet<MessageFlow>();
		dataAssociations = new LinkedHashSet<DataAssociation>();
		associations = new LinkedHashSet<Association>();
		swimlanes = new ArrayList<Swimlane>();
		callActivities = new LinkedHashSet<CallActivity>();
		artifacts= new LinkedHashSet<Artifacts>();
		flowsassociation= new LinkedHashSet<FlowAssociation>();

		getAttributeMap().put(AttributeMap.PREF_ORIENTATION, SwingConstants.WEST);
		getAttributeMap().put(AttributeMap.LABEL, label);
	}

	@Override
	protected BPMNDiagramImpl getEmptyClone() {
		return new BPMNDiagramImpl(getLabel());
	}

	protected Map<DirectedGraphElement, DirectedGraphElement> cloneFrom(
			DirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> graph) {
		BPMNDiagram bpmndiagram = (BPMNDiagram) graph;
		HashMap<DirectedGraphElement, DirectedGraphElement> mapping = new HashMap<DirectedGraphElement, DirectedGraphElement>();

		boolean newSwimlanes = true;
		while (newSwimlanes) {
			newSwimlanes = false;
			for (Swimlane s : bpmndiagram.getSwimlanes()) {
				// If swimlane has not been added yet
				if (!mapping.containsKey(s)) {
					newSwimlanes = true;
					Swimlane parentSwimlane = s.getParentSwimlane();
					// If there is no parent or parent has been added, add swimlane
					if (parentSwimlane == null) {
						mapping.put(s, addSwimlane(s.getLabel(), null, s.getSwimlaneType()));
					} else if (mapping.containsKey(parentSwimlane)) {
						mapping.put(s,
								addSwimlane(s.getLabel(), (Swimlane) mapping.get(parentSwimlane), 
										s.getSwimlaneType()));
					}
				}
			}
		}
		for (SubProcess s : bpmndiagram.getSubProcesses()) {
			if (s.getParentSubProcess() != null) {
				if (mapping.containsKey(s.getParentSubProcess())) {
					mapping.put(
							s,
							addSubProcess(s.getLabel(), s.isBLooped(), s.isBAdhoc(), s.isBCompensation(),
									s.isBMultiinstance(), s.isBCollapsed(),
									(SubProcess) mapping.get(s.getParentSubProcess())));
				}
			} else if (s.getParentSwimlane() != null) {
				if (mapping.containsKey(s.getParentSwimlane())) {
					mapping.put(
							s,
							addSubProcess(s.getLabel(), s.isBLooped(), s.isBAdhoc(), s.isBCompensation(),
									s.isBMultiinstance(), s.isBCollapsed(),
									(Swimlane) mapping.get(s.getParentSwimlane())));
				}
			} else

				mapping.put(
						s,
						addSubProcess(s.getLabel(), s.isBLooped(), s.isBAdhoc(), s.isBCompensation(),
								s.isBMultiinstance(), s.isBCollapsed()));
		}
		for (Activity a : bpmndiagram.getActivities()) {
			if (a.getParentSubProcess() != null) {
				if (mapping.containsKey(a.getParentSubProcess())) {
					mapping.put(
							a,
							addActivity(a.getLabel(), a.isBLooped(), a.isBAdhoc(), a.isBCompensation(),
									a.isBMultiinstance(), a.isBCollapsed(),
									(SubProcess) mapping.get(a.getParentSubProcess())));
				}
			} else if (a.getParentSwimlane() != null) {
				if (mapping.containsKey(a.getParentSwimlane())) {
					mapping.put(
							a,
							addActivity(a.getLabel(), a.isBLooped(), a.isBAdhoc(), a.isBCompensation(),
									a.isBMultiinstance(), a.isBCollapsed(),
									(Swimlane) mapping.get(a.getParentSwimlane())));
				}
			} else
				mapping.put(
						a,
						addActivity(a.getLabel(), a.isBLooped(), a.isBAdhoc(), a.isBCompensation(),
								a.isBMultiinstance(), a.isBCollapsed()));
		}

		for (CallActivity a : bpmndiagram.getCallActivities()) {
			if (a.getParentSubProcess() != null) {
				if (mapping.containsKey(a.getParentSubProcess())) {
					mapping.put(
							a,
							addCallActivity(a.getLabel(), a.isBLooped(), a.isBAdhoc(), a.isBCompensation(),
									a.isBMultiinstance(), a.isBCollapsed(),
									(SubProcess) mapping.get(a.getParentSubProcess())));
				}
			} else if (a.getParentSwimlane() != null) {
				if (mapping.containsKey(a.getParentSwimlane())) {
					mapping.put(
							a,
							addCallActivity(a.getLabel(), a.isBLooped(), a.isBAdhoc(), a.isBCompensation(),
									a.isBMultiinstance(), a.isBCollapsed(),
									(Swimlane) mapping.get(a.getParentSwimlane())));
				}
			} else
				mapping.put(
						a,
						addCallActivity(a.getLabel(), a.isBLooped(), a.isBAdhoc(), a.isBCompensation(),
								a.isBMultiinstance(), a.isBCollapsed()));
		}

		for (Event e : bpmndiagram.getEvents()) {
			if (e.getParentSubProcess() != null) {
				if (mapping.containsKey(e.getParentSubProcess())) {
					mapping.put(
							e,
							addEvent(e.getLabel(), e.getEventType(), e.getEventTrigger(), e.getEventUse(),
									(SubProcess) mapping.get(e.getParentSubProcess()), e.getBoundingNode()));
				}
			} else if (e.getParentSwimlane() != null) {
				if (mapping.containsKey(e.getParentSwimlane())) {
					mapping.put(
							e,
							addEvent(e.getLabel(), e.getEventType(), e.getEventTrigger(), e.getEventUse(),
									(Swimlane) mapping.get(e.getParentSwimlane()), e.getBoundingNode()));
				}
			} else
				mapping.put(
						e,
						addEvent(e.getLabel(), e.getEventType(), e.getEventTrigger(), e.getEventUse(),
								e.getBoundingNode()));
		}
		for (Gateway g : bpmndiagram.getGateways()) {
			if (g.getParentSubProcess() != null) {
				if (mapping.containsKey(g.getParentSubProcess())) {
					mapping.put(
							g,
							addGateway(g.getLabel(), g.getGatewayType(),
									(SubProcess) mapping.get(g.getParentSubProcess())));
				}
			} else if (g.getParentSwimlane() != null) {
				if (mapping.containsKey(g.getParentSwimlane())) {
					mapping.put(g,
							addGateway(g.getLabel(), g.getGatewayType(), (Swimlane) mapping.get(g.getParentSwimlane())));
				}
			} else
				mapping.put(g, addGateway(g.getLabel(), g.getGatewayType()));
		}

		for (DataObject d : bpmndiagram.getDataObjects()) {
			mapping.put(d, addDataObject(d.getLabel()));
		}

		for (Flow f : bpmndiagram.getFlows()) {
			mapping.put(f, addFlow((BPMNNode) mapping.get(f.getSource()), 
					(BPMNNode) mapping.get(f.getTarget()), f.getLabel()));
		}

		for (Artifacts a : bpmndiagram.getArtifacts()) {

			mapping.put(a, addArtifacts(a.getLabel(), a.getArtifactType(), (Swimlane)	 mapping.get(a.getParentSwimlane())));
		}
		for (FlowAssociation  f : bpmndiagram.getFlowAssociation()) {
			mapping.put(f, addFlowAssociation((BPMNNode) mapping.get(f.getSource()), (BPMNNode) mapping.get(f.getTarget()),
					(Swimlane)	 mapping.get(f.getParentSwimlane())));
		}

		getAttributeMap().clear();
		AttributeMap map = bpmndiagram.getAttributeMap();
		for (String key : map.keySet()) {
			getAttributeMap().put(key, map.get(key));
		}
		return mapping;
	}

	@SuppressWarnings("rawtypes")
	public void removeEdge(DirectedGraphEdge edge) {
		if (edge instanceof Flow) {
			flows.remove(edge);
		} else if (edge instanceof MessageFlow) {
			messageFlows.remove(edge);
		} else  {if (edge instanceof FlowAssociation) {
			flowsassociation.remove(edge);
		}else{
			assert (false);
		}
		}
		graphElementRemoved(edge);
	}

	public Set<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> getEdges() {
		Set<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> edges = new HashSet<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>>();
		edges.addAll(flows);
		edges.addAll(messageFlows);
		edges.addAll(dataAssociations);
		edges.addAll(associations);
		return edges;
	}

	public Set<BPMNNode> getNodes() {
		Set<BPMNNode> nodes = new HashSet<BPMNNode>();
		nodes.addAll(activities);
		nodes.addAll(subprocesses);
		nodes.addAll(events);
		nodes.addAll(gateways);
		nodes.addAll(dataObjects);
		nodes.addAll(swimlanes);
		nodes.addAll(textAnnotations);
		nodes.addAll(callActivities);
		return nodes;
	}

	public void removeNode(DirectedGraphNode node) {
		if (node instanceof Activity) {
			removeActivity((Activity) node);
		} else if (node instanceof SubProcess) {
			removeSubProcess((SubProcess) node);
		} else if (node instanceof Swimlane) {
			removeSwimlane((Swimlane) node);
		} else if (node instanceof Event) {
			removeEvent((Event) node);
		} else if (node instanceof Gateway) {
			removeGateway((Gateway) node);
		} else if (node instanceof DataObject) {
			removeDataObject((DataObject) node);
		} else {
			assert (false);
		}
	}

	public Activity addActivity(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation,
			boolean bMultiinstance, boolean bCollapsed) {
		Activity a = new Activity(this, label, bLooped, bAdhoc, bCompensation, bMultiinstance, bCollapsed);
		activities.add(a);
		graphElementAdded(a);
		return a;
	}

	public Activity addActivity(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation,
			boolean bMultiinstance, boolean bCollapsed, Swimlane parentSwimlane) {
		Activity a = new Activity(this, label, bLooped, bAdhoc, bCompensation, bMultiinstance, bCollapsed,
				parentSwimlane);
		activities.add(a);
		graphElementAdded(a);
		return a;
	}

	public Activity addActivity(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation,
			boolean bMultiinstance, boolean bCollapsed, SubProcess parentSubProcess) {
		Activity a = new Activity(this, label, bLooped, bAdhoc, bCompensation, bMultiinstance, bCollapsed,
				parentSubProcess);
		activities.add(a);
		graphElementAdded(a);
		return a;
	}

	public CallActivity addCallActivity(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation,
			boolean bMultiinstance, boolean bCollapsed) {
		CallActivity a = new CallActivity(this, label, bLooped, bAdhoc, bCompensation, bMultiinstance, bCollapsed);
		callActivities.add(a);
		graphElementAdded(a);
		return a;
	}

	public CallActivity addCallActivity(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation,
			boolean bMultiinstance, boolean bCollapsed, Swimlane parentSwimlane) {
		CallActivity a = new CallActivity(this, label, bLooped, bAdhoc, bCompensation, bMultiinstance, bCollapsed,
				parentSwimlane);
		callActivities.add(a);
		graphElementAdded(a);
		return a;
	}

	public CallActivity addCallActivity(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation,
			boolean bMultiinstance, boolean bCollapsed, SubProcess parentSubProcess) {
		CallActivity a = new CallActivity(this, label, bLooped, bAdhoc, bCompensation, bMultiinstance, bCollapsed,
				parentSubProcess);
		callActivities.add(a);
		graphElementAdded(a);
		return a;
	}

	public SubProcess addSubProcess(String label, boolean looped, boolean adhoc, boolean compensation,
			boolean multiinstance, boolean collapsed) {
		SubProcess s = new SubProcess(this, label, looped, adhoc, compensation, multiinstance, collapsed);
		subprocesses.add(s);
		graphElementAdded(s);
		return s;
	}

	public SubProcess addSubProcess(String label, boolean looped, boolean adhoc, boolean compensation,
			boolean multiinstance, boolean collapsed, SubProcess parentSubProcess) {
		SubProcess s = new SubProcess(this, label, looped, adhoc, compensation, multiinstance, collapsed,
				parentSubProcess);
		subprocesses.add(s);
		graphElementAdded(s);
		return s;
	}

	public SubProcess addSubProcess(String label, boolean looped, boolean adhoc, boolean compensation,
			boolean multiinstance, boolean collapsed, Swimlane parentSwimlane) {
		SubProcess s = new SubProcess(this, label, looped, adhoc, compensation, multiinstance, collapsed,
				parentSwimlane);
		subprocesses.add(s);
		graphElementAdded(s);
		return s;
	}

	public SubProcess addSubProcess(String label, boolean looped, boolean adhoc, boolean compensation,
			boolean multiinstance, boolean collapsed, boolean triggeredByEvent) {
		SubProcess s = new SubProcess(this, label, looped, adhoc, compensation, multiinstance, collapsed,
				triggeredByEvent);
		subprocesses.add(s);
		graphElementAdded(s);
		return s;
	}

	public SubProcess addSubProcess(String label, boolean looped, boolean adhoc, boolean compensation,
			boolean multiinstance, boolean collapsed, boolean triggeredByEvent, SubProcess parentSubProcess) {
		SubProcess s = new SubProcess(this, label, looped, adhoc, compensation, multiinstance, collapsed,
				triggeredByEvent, parentSubProcess);
		subprocesses.add(s);
		graphElementAdded(s);
		return s;
	}

	public SubProcess addSubProcess(String label, boolean looped, boolean adhoc, boolean compensation,
			boolean multiinstance, boolean collapsed, boolean triggeredByEvent, Swimlane parentSwimlane) {
		SubProcess s = new SubProcess(this, label, looped, adhoc, compensation, multiinstance, collapsed,
				triggeredByEvent, parentSwimlane);
		subprocesses.add(s);
		graphElementAdded(s);
		return s;
	}

	@Deprecated
	public Event addEvent(String label, EventType eventType, EventTrigger eventTrigger, EventUse eventUse,
			Activity exceptionFor) {
		Event e = new Event(this, label, eventType, eventTrigger, eventUse, exceptionFor);
		if(exceptionFor != null) {
			SubProcess parentSubProcess = exceptionFor.getParentSubProcess();
			if(parentSubProcess != null) {
				e.setParentSubprocess(parentSubProcess);
			} else {
				Swimlane parentSwimlane = exceptionFor.getParentSwimlane();
				e.setParentSwimlane(parentSwimlane);
			}
		}
		events.add(e);
		graphElementAdded(e);
		return e;
	}

	@Deprecated
	public Event addEvent(String label, EventType eventType, EventTrigger eventTrigger, EventUse eventUse,
			SubProcess parentSubProcess, Activity exceptionFor) {
		Event e = new Event(this, label, eventType, eventTrigger, eventUse, parentSubProcess, exceptionFor);
		events.add(e);
		graphElementAdded(e);
		return e;
	}

	@Deprecated
	public Event addEvent(String label, EventType eventType, EventTrigger eventTrigger, EventUse eventUse,
			Swimlane parentSwimlane, Activity exceptionFor) {
		Event e = new Event(this, label, eventType, eventTrigger, eventUse, parentSwimlane, exceptionFor);
		events.add(e);
		graphElementAdded(e);
		return e;
	}

	public Event addEvent(String label, EventType eventType, EventTrigger eventTrigger, EventUse eventUse,
			boolean isInterrupting, Activity exceptionFor) {
		Event e = new Event(this, label, eventType, eventTrigger, eventUse, isInterrupting, exceptionFor);
		if(exceptionFor != null) {
			SubProcess parentSubProcess = exceptionFor.getParentSubProcess();
			if(parentSubProcess != null) {
				e.setParentSubprocess(parentSubProcess);
			} else {
				Swimlane parentSwimlane = exceptionFor.getParentSwimlane();
				e.setParentSwimlane(parentSwimlane);
			}
		}
		events.add(e);
		graphElementAdded(e);
		return e;
	}

	public Event addEvent(String label, EventType eventType, EventTrigger eventTrigger, EventUse eventUse,
			SubProcess parentSubProcess, boolean isInterrupting, Activity exceptionFor) {
		Event e = new Event(this, label, eventType, eventTrigger, eventUse, parentSubProcess, isInterrupting,
				exceptionFor);
		events.add(e);
		graphElementAdded(e);
		return e;
	}

	public Event addEvent(String label, EventType eventType, EventTrigger eventTrigger, EventUse eventUse,
			Swimlane parentSwimlane, boolean isInterrupting, Activity exceptionFor) {
		Event e = new Event(this, label, eventType, eventTrigger, eventUse, parentSwimlane, isInterrupting,
				exceptionFor);
		events.add(e);
		graphElementAdded(e);
		return e;
	}

	public DataObject addDataObject(String label) {
		DataObject d = new DataObject(this, label);
		dataObjects.add(d);
		graphElementAdded(d);
		return d;
	}

	public TextAnnotation addTextAnnotation(String label) {
		TextAnnotation t = new TextAnnotation(this, label);
		textAnnotations.add(t);
		graphElementAdded(t);
		return t;
	}

	@Deprecated
	public Flow addFlow(BPMNNode source, BPMNNode target, SubProcess parent, String label) {
		Flow f = new Flow(source, target, parent, label);
		flows.add(f);
		graphElementAdded(f);
		return f;
	}

	@Deprecated
	public Flow addFlow(BPMNNode source, BPMNNode target, Swimlane parent, String label) {
		Flow f = new Flow(source, target, parent, label);
		flows.add(f);
		graphElementAdded(f);
		return f;
	}

	public Flow addFlow(BPMNNode source, BPMNNode target, String label) {
		Flow f = new Flow(source, target, label);
		flows.add(f);
		graphElementAdded(f);
		return f;
	}

	public MessageFlow addMessageFlow(BPMNNode source, BPMNNode target, SubProcess parent, String label) {
		MessageFlow f = new MessageFlow(source, target, parent, label);
		messageFlows.add(f);
		graphElementAdded(f);
		return f;
	}

	public MessageFlow addMessageFlow(BPMNNode source, BPMNNode target, Swimlane parent, String label) {
		MessageFlow f = new MessageFlow(source, target, parent, label);
		messageFlows.add(f);
		graphElementAdded(f);
		return f;
	}

	public MessageFlow addMessageFlow(BPMNNode source, BPMNNode target, String label) {
		MessageFlow f = new MessageFlow(source, target, label);
		messageFlows.add(f);
		graphElementAdded(f);
		return f;
	}

	public DataAssociation addDataAssociation(BPMNNode source, BPMNNode target, String label) {
		DataAssociation d = new DataAssociation(source, target, label);
		dataAssociations.add(d);
		graphElementAdded(d);
		return d;
	}

	public Association addAssociation(BPMNNode source, BPMNNode target, AssociationDirection direction) {
		Association a = new Association(source, target, direction);
		associations.add(a);
		graphElementAdded(a);
		return a;
	}

	public Gateway addGateway(String label, GatewayType gatewayType, SubProcess parentSubProcess) {
		Gateway g = new Gateway(this, label, gatewayType, parentSubProcess);
		gateways.add(g);
		graphElementAdded(g);
		return g;
	}

	public Gateway addGateway(String label, GatewayType gatewayType, Swimlane parentSwimlane) {
		Gateway g = new Gateway(this, label, gatewayType, parentSwimlane);
		gateways.add(g);
		graphElementAdded(g);
		return g;
	}

	public Gateway addGateway(String label, GatewayType gatewayType) {
		Gateway g = new Gateway(this, label, gatewayType);
		gateways.add(g);
		graphElementAdded(g);
		return g;
	}

	@Deprecated
	public Swimlane addSwimlane(String label, Swimlane parentSwimlane) {
		Swimlane s = new Swimlane(this, label, parentSwimlane);
		swimlanes.add(s);
		graphElementAdded(s);
		return s;
	}

	public Swimlane addSwimlane(String label, Swimlane parentSwimlane, SwimlaneType type) {
		Swimlane s = new Swimlane(this, label, parentSwimlane, type);
		swimlanes.add(s);
		graphElementAdded(s);
		return s;
	}

	public Collection<Activity> getActivities() {
		return activities;
	}

	public Collection<CallActivity> getCallActivities() {
		return callActivities;
	}

	public Collection<Activity> getActivities(Swimlane pool) {
		List<Activity> activitiesFromPool = new ArrayList<Activity>();
		for (Activity activity : activities) {
			if (activity.getParentSubProcess() == null) {
				if ((pool != null) && (pool.equals(activity.getParentPool()))) {
					activitiesFromPool.add(activity);
				} else if (pool == null) {
					if (activity.getParentPool() == null) {
						activitiesFromPool.add(activity);
					}
				}
			}
		}
		return activitiesFromPool;
	}

	public Collection<CallActivity> getCallActivities(Swimlane pool) {
		Set<CallActivity> activitiesFromPool = new HashSet<CallActivity>();
		for (CallActivity activity : callActivities) {
			if (activity.getParentSubProcess() == null) {
				if ((pool != null) && (pool.equals(activity.getParentPool()))) {
					activitiesFromPool.add(activity);
				} else if (pool == null) {
					if (activity.getParentPool() == null) {
						activitiesFromPool.add(activity);
					}
				}
			}
		}
		return activitiesFromPool;
	}

	public Collection<SubProcess> getSubProcesses() {
		return subprocesses;
	}

	public Collection<SubProcess> getSubProcesses(Swimlane pool) {
		Set<SubProcess> subProcessesFromPool = new HashSet<SubProcess>();
		for (SubProcess subProcess : subprocesses) {
			if ((pool != null) && (pool.equals(subProcess.getParentPool()))) {
				subProcessesFromPool.add(subProcess);
			} else if (pool == null) {
				if (subProcess.getParentPool() == null) {
					subProcessesFromPool.add(subProcess);
				}
			}
		}
		return subProcessesFromPool;
	}

	public Collection<Event> getEvents() {
		return events;
	}

	public Collection<Event> getEvents(Swimlane pool) {
		Set<Event> eventsFromPool = new HashSet<Event>();
		for (Event event : events) {
			if (event.getParentSubProcess() == null) {
				if ((pool != null) && (pool.equals(event.getParentPool()))) {
					eventsFromPool.add(event);
				} else if (pool == null) {
					if (event.getParentPool() == null) {
						eventsFromPool.add(event);
					}
				}
			}
		}
		return eventsFromPool;
	}

	public Collection<DataObject> getDataObjects() {
		return dataObjects;
	}

	public Collection<TextAnnotation> getTextAnnotations() {
		return textAnnotations;
	}

	public Collection<TextAnnotation> getTextAnnotations(Swimlane pool) {
		Set<TextAnnotation> textAnnotationsFromPool = new HashSet<TextAnnotation>();
		for (TextAnnotation textAnnotation : textAnnotations) {
			if (textAnnotation.getParentSubProcess() == null) {
				if ((pool != null) && (pool.equals(textAnnotation.getParentPool()))) {
					textAnnotationsFromPool.add(textAnnotation);
				} else if (pool == null) {
					if (textAnnotation.getParentPool() == null) {
						textAnnotationsFromPool.add(textAnnotation);
					}
				}
			}
		}
		return textAnnotationsFromPool;
	}

	public Collection<Flow> getFlows() {
		return Collections.unmodifiableCollection(flows);
	}

	public Collection<Flow> getFlows(Swimlane pool) {
		List<Flow> flowsFromPool = new ArrayList<Flow>();
		for (Flow flow : flows) {
			BPMNNode source = flow.getSource();
			BPMNNode target = flow.getTarget();
			if (source.getParentSubProcess() == null) {
				if ((source.getParentPool() == pool) && (target.getParentPool() == pool)) {
					flowsFromPool.add(flow);
				}
			}
		}
		return Collections.unmodifiableCollection(flowsFromPool);
	}

	public Collection<Flow> getFlows(SubProcess subProcess) {
		List<Flow> flowsFromSubProcess = new ArrayList<Flow>();
		for (Flow flow : flows) {
			BPMNNode source = flow.getSource();
			BPMNNode target = flow.getTarget();
			if ((source.getParentSubProcess() == subProcess) 
					&& (target.getParentSubProcess() == subProcess)) {
				flowsFromSubProcess.add(flow);
			}
		}
		return Collections.unmodifiableCollection(flowsFromSubProcess);
	}


	public Set<MessageFlow> getMessageFlows() {
		return Collections.unmodifiableSet(messageFlows);
	}

	public Collection<Gateway> getGateways() {
		return gateways;
	}

	public Collection<Gateway> getGateways(Swimlane pool) {
		List<Gateway> gatewaysFromPool = new ArrayList<Gateway>();
		for (Gateway gateway : gateways) {
			if (gateway.getParentSubProcess() == null) {
				if ((pool != null) && (pool.equals(gateway.getParentPool()))) {
					gatewaysFromPool.add(gateway);
				} else if (pool == null) {
					if (gateway.getParentPool() == null) {
						gatewaysFromPool.add(gateway);
					}
				}
			}
		}
		return gatewaysFromPool;
	}

	public Activity removeActivity(Activity activity) {
		removeSurroundingEdges(activity);
		return removeNodeFromCollection(activities, activity);
	}

	public CallActivity removeCallActivity(CallActivity activity) {
		removeSurroundingEdges(activity);
		return removeNodeFromCollection(callActivities, activity);
	}

	public Activity removeSubProcess(SubProcess subprocess) {
		//TODO: it is probably necessary to remove all nodes that are contained in the subprocess as well 
		removeSurroundingEdges(subprocess);
	return removeNodeFromCollection(subprocesses, subprocess);
	}

	public Event removeEvent(Event event) {
		removeSurroundingEdges(event);
		return removeNodeFromCollection(events, event);
	}

	public Gateway removeGateway(Gateway gateway) {
		removeSurroundingEdges(gateway);
		return removeNodeFromCollection(gateways, gateway);
	}

	public DataObject removeDataObject(DataObject dataObject) {
		removeSurroundingEdges(dataObject);
		return removeNodeFromCollection(dataObjects, dataObject);
	}

	public Swimlane removeSwimlane(Swimlane swimlane) {
		removeSurroundingEdges(swimlane);
		return removeNodeFromCollection(swimlanes, swimlane);
	}

	public Collection<Swimlane> getSwimlanes() {
		return swimlanes;
	}

	public Collection<Swimlane> getPools() {
		Collection<Swimlane> result = new HashSet<Swimlane>();
		for (Swimlane swimlane : swimlanes) {
			if (swimlane.getSwimlaneType() == SwimlaneType.POOL) {
				result.add(swimlane);
			}
		}
		return result;
	}

	public Collection<Swimlane> getLanes(Swimlane pool) {
		List<Swimlane> lanesFromPool = new ArrayList<Swimlane>();
		for (Swimlane lane : swimlanes) {
			if (SwimlaneType.LANE.equals(lane.getSwimlaneType())) {
				if ((pool != null) && (pool.equals(lane.getParentSwimlane()))) {
					lanesFromPool.add(lane);
				} else if (pool == null) {
					if (lane.getParentSwimlane() == null) {
						lanesFromPool.add(lane);
					}
				}
			}
		}
		return lanesFromPool;
	}

	public Collection<DataAssociation> getDataAssociations() {
		return dataAssociations;
	}

	public Collection<Association> getAssociations() {
		return associations;
	}

	public Collection<Association> getAssociations(Swimlane pool) {
		Set<Association> associationsFromPool = new HashSet<Association>();
		for (Association association : associations) {
			if ((association.getTarget().getParentSubProcess() == null) 
					&& (association.getSource().getParentSubProcess() == null)) {
				if ((pool != null) && (pool.equals(association.getTarget().getParentSubProcess())
						&&(pool.equals((association.getSource().getParentSubProcess()))))) {
					associationsFromPool.add(association);
				} else if (pool == null) {
					if  ((association.getTarget().getParentPool() == null) 
							|| (association.getSource().getParentPool() == null)) {
						associationsFromPool.add(association);
					}
				}
			}
		}
		return associationsFromPool;
	}
	

	public Artifacts addArtifacts(String label, ArtifactType artifactType,
			SubProcess parent) {
		Artifacts a = new Artifacts(this, label,artifactType , parent);
		artifacts.add(a);
		graphElementAdded(a);
		return a;
	}


	public Artifacts removeArtifact(Artifacts artifact) {
		removeSurroundingEdges(artifact);
		return removeNodeFromCollection(artifacts, artifact);
	}


	public Collection<Artifacts> getArtifacts() {

		return this.artifacts;
	}


	public FlowAssociation addFlowAssociation(BPMNNode source, BPMNNode target,
			SubProcess parent) {
		FlowAssociation f = new FlowAssociation(source, target, parent);
		flowsassociation.add(f);
		graphElementAdded(f);
		return f;
	}


	public Set<FlowAssociation> getFlowAssociation() {

		return Collections.unmodifiableSet(flowsassociation);
	}


	public Artifacts addArtifacts(String label, ArtifactType artifactType) {
		Artifacts a = new Artifacts(this, label,artifactType);
		artifacts.add(a);
		graphElementAdded(a);
		return a;
	}


	public Artifacts addArtifacts(String label, ArtifactType artifactType,
			Swimlane parentSwimlane) {
		Artifacts a = new Artifacts(this, label,artifactType,parentSwimlane);
		artifacts.add(a);
		graphElementAdded(a);
		return a;
	}


	public FlowAssociation addFlowAssociation(BPMNNode source, BPMNNode target) {

		FlowAssociation f = new FlowAssociation(source, target);
		flowsassociation.add(f);
		graphElementAdded(f);
		return f;
	}


	public FlowAssociation addFlowAssociation(BPMNNode source, BPMNNode target,
			Swimlane parentSwimlane) {


		FlowAssociation f = new FlowAssociation(source, target, parentSwimlane);
		flowsassociation.add(f);
		graphElementAdded(f);
		return f;
	}

	//TextAnnotations
	public TextAnnotation addTextAnnotations(TextAnnotation textAnnotation) {
		textAnnotations.add(textAnnotation);
		graphElementAdded(textAnnotation);
		return textAnnotation;
	}

	public Collection<TextAnnotation> getTextannotations() {
		return textAnnotations;
	}
}
