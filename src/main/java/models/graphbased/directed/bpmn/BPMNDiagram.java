package models.graphbased.directed.bpmn;

import java.util.Collection;
import java.util.Set;

import models.graphbased.directed.ContainingDirectedGraphNode;
import models.graphbased.directed.DirectedGraph;
import models.graphbased.directed.bpmn.elements.Activity;
import models.graphbased.directed.bpmn.elements.Artifacts;
import models.graphbased.directed.bpmn.elements.Artifacts.ArtifactType;
import models.graphbased.directed.bpmn.elements.Association;
import models.graphbased.directed.bpmn.elements.CallActivity;
import models.graphbased.directed.bpmn.elements.DataAssociation;
import models.graphbased.directed.bpmn.elements.DataObject;
import models.graphbased.directed.bpmn.elements.Event;
import models.graphbased.directed.bpmn.elements.Event.EventTrigger;
import models.graphbased.directed.bpmn.elements.Event.EventType;
import models.graphbased.directed.bpmn.elements.Event.EventUse;
import models.graphbased.directed.bpmn.elements.Flow;
import models.graphbased.directed.bpmn.elements.FlowAssociation;
import models.graphbased.directed.bpmn.elements.Gateway;
import models.graphbased.directed.bpmn.elements.Gateway.GatewayType;
import models.graphbased.directed.bpmn.elements.MessageFlow;
import models.graphbased.directed.bpmn.elements.SubProcess;
import models.graphbased.directed.bpmn.elements.Swimlane;
import models.graphbased.directed.bpmn.elements.SwimlaneType;
import models.graphbased.directed.bpmn.elements.TextAnnotation;
import plugins.bpmn.BpmnAssociation.AssociationDirection;

public interface BPMNDiagram extends DirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> {

	String getLabel();

	//Activities
	Activity addActivity(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation, boolean bMultiinstance,
			boolean bCollapsed);

	Activity addActivity(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation, boolean bMultiinstance,
			boolean bCollapsed, SubProcess parentSubProcess);

	Activity addActivity(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation, boolean bMultiinstance,
			boolean bCollapsed, Swimlane parentSwimlane);

	Activity removeActivity(Activity activity);

	Collection<Activity> getActivities();
	
	Collection<Activity> getActivities(Swimlane pool);

    //callActivities
    CallActivity addCallActivity(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation, boolean bMultiinstance,
                         boolean bCollapsed);

    CallActivity addCallActivity(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation, boolean bMultiinstance,
                         boolean bCollapsed, SubProcess parentSubProcess);

    CallActivity addCallActivity(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation, boolean bMultiinstance,
                         boolean bCollapsed, Swimlane parentSwimlane);

    CallActivity removeCallActivity(CallActivity activity);

    Collection<CallActivity> getCallActivities();

    Collection<CallActivity> getCallActivities(Swimlane pool);

	//SubProcesses
	SubProcess addSubProcess(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation,
			boolean bMultiinstance, boolean bCollapsed);

	SubProcess addSubProcess(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation,
			boolean bMultiinstance, boolean bCollapsed, SubProcess parentSubProcess);

	SubProcess addSubProcess(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation,
			boolean bMultiinstance, boolean bCollapsed, Swimlane parentSwimlane);
	
	SubProcess addSubProcess(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation,
			boolean bMultiinstance, boolean bCollapsed, boolean bTriggeredByEvent);

	SubProcess addSubProcess(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation,
			boolean bMultiinstance, boolean bCollapsed, boolean bTriggeredByEvent, SubProcess parentSubProcess);

	SubProcess addSubProcess(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation,
			boolean bMultiinstance, boolean bCollapsed, boolean bTriggeredByEvent, Swimlane parentSwimlane);


	Activity removeSubProcess(SubProcess subprocess);

	Collection<SubProcess> getSubProcesses();
	
	Collection<SubProcess> getSubProcesses(Swimlane pool);

	//Events
	@Deprecated
	Event addEvent(String label, EventType eventType, EventTrigger eventTrigger, EventUse eventUse,
			Activity exceptionFor);

	@Deprecated
	Event addEvent(String label, EventType eventType, EventTrigger eventTrigger, EventUse eventUse,
			SubProcess parentSubProcess, Activity exceptionFor);

	@Deprecated
	Event addEvent(String label, EventType eventType, EventTrigger eventTrigger, EventUse eventUse,
			Swimlane parentSwimlane, Activity exceptionFor);
	
	Event addEvent(String label, EventType eventType, EventTrigger eventTrigger, EventUse eventUse,
			boolean isInterrupting, Activity exceptionFor);

	Event addEvent(String label, EventType eventType, EventTrigger eventTrigger, EventUse eventUse,
			SubProcess parentSubProcess, boolean isInterrupting, Activity exceptionFor);

	Event addEvent(String label, EventType eventType, EventTrigger eventTrigger, EventUse eventUse,
			Swimlane parentSwimlane, boolean isInterrupting, Activity exceptionFor);

	Event removeEvent(Event event);

	Collection<Event> getEvents();
	
	Collection<Event> getEvents(Swimlane pool);

	//Gateways
	Gateway addGateway(String label, GatewayType gatewayType);

	Gateway addGateway(String label, GatewayType gatewayType, SubProcess parentSubProcess);

	Gateway addGateway(String label, GatewayType gatewayType, Swimlane parentSwimlane);

	Gateway removeGateway(Gateway gateway);

	Collection<Gateway> getGateways();
	
	Collection<Gateway> getGateways(Swimlane pool);
	
	//Data objects
	DataObject addDataObject(String label);

	DataObject removeDataObject(DataObject dataObject);
	
	Collection<DataObject> getDataObjects();
	
	//Artifacts
	TextAnnotation addTextAnnotation(String label);
	
	Collection<TextAnnotation> getTextAnnotations();
	
	Collection<TextAnnotation> getTextAnnotations(Swimlane pool);
	
	Association addAssociation(BPMNNode source, BPMNNode target, AssociationDirection direction);
	
	Collection<Association> getAssociations();
	
	Collection<Association> getAssociations(Swimlane pool);

	//Flows
	Flow addFlow(BPMNNode source, BPMNNode target, String label);

	@Deprecated	
	Flow addFlow(BPMNNode source, BPMNNode target, Swimlane parent, String label);

	@Deprecated
	Flow addFlow(BPMNNode source, BPMNNode target, SubProcess parent, String label);

	Collection<Flow> getFlows();
	
	Collection<Flow> getFlows(Swimlane pool);
	
	Collection<Flow> getFlows(SubProcess subProcess);

	//MessageFlows
	MessageFlow addMessageFlow(BPMNNode source, BPMNNode target, String label);

	MessageFlow addMessageFlow(BPMNNode source, BPMNNode target, Swimlane parent, String label);

	MessageFlow addMessageFlow(BPMNNode source, BPMNNode target, SubProcess parent, String label);

	Set<MessageFlow> getMessageFlows();
	
	//DataAssociatons
	DataAssociation addDataAssociation(BPMNNode source, BPMNNode target, String label);
	
	Collection<DataAssociation> getDataAssociations();
	
	//TextAnnotations
	TextAnnotation addTextAnnotations(TextAnnotation textAnnotation);
	
	Collection<TextAnnotation> getTextannotations();

	Swimlane addSwimlane(String label, ContainingDirectedGraphNode parent);
	
	Swimlane addSwimlane(String label, ContainingDirectedGraphNode parent, SwimlaneType type);

	Swimlane removeSwimlane(Swimlane swimlane);

	Collection<Swimlane> getSwimlanes();
	
	Collection<Swimlane> getPools();
	
	Collection<Swimlane> getLanes(ContainingDirectedGraphNode parent);

	//FlowAssociation
	FlowAssociation addFlowAssociation(BPMNNode source, BPMNNode target);

	FlowAssociation addFlowAssociation(BPMNNode source, BPMNNode target, SubProcess parent);

	FlowAssociation addFlowAssociation(BPMNNode source, BPMNNode target, Swimlane parentSwimlane);

	Set<FlowAssociation> getFlowAssociation();

	String toDOT();
}
