package plugins.xpdl.converter;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;



import javax.swing.SwingConstants;

import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.JGraphLayout;
import com.jgraph.layout.hierarchical.JGraphHierarchicalLayout;

import models.connections.GraphLayoutConnection;
import models.graphbased.AttributeMap;
import models.graphbased.ViewSpecificAttributeMap;
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
import models.graphbased.directed.bpmn.elements.MessageFlow;
import models.graphbased.directed.bpmn.elements.SubProcess;
import models.graphbased.directed.bpmn.elements.Swimlane;
import models.jgraph.CustomGraphModel;
import models.jgraph.CustomJGraph;
import models.jgraph.elements.CustomGraphCell;
import models.jgraph.elements.CustomGraphEdge;
import models.jgraph.views.JGraphPortView;
import models.jgraph.visualization.CustomJGraphPanel;
import plugins.xpdl.Xpdl;
import plugins.xpdl.XpdlAuthor;
import plugins.xpdl.XpdlBlockActivity;
import plugins.xpdl.XpdlCreated;
import plugins.xpdl.XpdlEndEvent;
import plugins.xpdl.XpdlEvent;
import plugins.xpdl.XpdlImplementation;
import plugins.xpdl.XpdlIntermediateEvent;
import plugins.xpdl.XpdlJoin;
import plugins.xpdl.XpdlPackageHeader;
import plugins.xpdl.XpdlProcessHeader;
import plugins.xpdl.XpdlRedefinableHeader;
import plugins.xpdl.XpdlRoute;
import plugins.xpdl.XpdlSplit;
import plugins.xpdl.XpdlStartEvent;
import plugins.xpdl.XpdlTask;
import plugins.xpdl.XpdlTransitionRestriction;
import plugins.xpdl.XpdlTriggerResultMessage;
import plugins.xpdl.collections.XpdlActivities;
import plugins.xpdl.collections.XpdlActivitySets;
import plugins.xpdl.collections.XpdlArtifacts;
import plugins.xpdl.collections.XpdlAssociations;
import plugins.xpdl.collections.XpdlLanes;
import plugins.xpdl.collections.XpdlMessageFlows;
import plugins.xpdl.collections.XpdlPools;
import plugins.xpdl.collections.XpdlTransitionRefs;
import plugins.xpdl.collections.XpdlTransitionRestrictions;
import plugins.xpdl.collections.XpdlTransitions;
import plugins.xpdl.collections.XpdlWorkflowProcesses;
import plugins.xpdl.graphics.XpdlConnectorGraphicsInfo;
import plugins.xpdl.graphics.XpdlCoordinates;
import plugins.xpdl.graphics.XpdlNodeGraphicsInfo;
import plugins.xpdl.graphics.collections.XpdlConnectorGraphicsInfos;
import plugins.xpdl.graphics.collections.XpdlNodeGraphicsInfos;
import plugins.xpdl.idname.XpdlActivity;
import plugins.xpdl.idname.XpdlActivitySet;
import plugins.xpdl.idname.XpdlLane;
import plugins.xpdl.idname.XpdlMessageFlow;
import plugins.xpdl.idname.XpdlMessageType;
import plugins.xpdl.idname.XpdlPool;
import plugins.xpdl.idname.XpdlTransition;
import plugins.xpdl.idname.XpdlTransitionRef;
import plugins.xpdl.idname.XpdlWorkflowProcess;
import plugins.xpdl.text.XpdlDescription;
import plugins.xpdl.text.XpdlDocumentation;
import plugins.xpdl.text.XpdlPriority;
import plugins.xpdl.text.XpdlText;
import plugins.xpdl.text.XpdlVendor;
import plugins.xpdl.text.XpdlVersion;
import plugins.xpdl.text.XpdlXpdlVersion;

public class BPMN2XPDLConversion {
	
	public static final String ID_DEFAULT_POOL = "Default-Pool";
	public static final String ID_MAIN_PROCESS_WF = "MainProcess-WF_";
	public static final String ID_PROCESS_WF = "Process-WF_";
	public static final String ID_XPDL = "XPDL_";
	public static final String ID_SUB_PROCESS_SUBWF = "subProcId";
	
	private static final List<EventTrigger> listOfXpdlEndTrigers = Arrays.asList(EventTrigger.NONE,
			EventTrigger.MESSAGE, EventTrigger.ERROR, EventTrigger.CANCEL, 
			EventTrigger.COMPENSATION, EventTrigger.SIGNAL, EventTrigger.TERMINATE, 
			EventTrigger.MULTIPLE);  
	
	private BPMNDiagram bpmn;
	private Xpdl xpdl;
	
	public BPMN2XPDLConversion(BPMNDiagram bpmn) {
		this.bpmn=bpmn;
	}
	
	public Xpdl convert2XPDL_noLayout() {
		xpdl = fillXPDL();
		return xpdl;
	}
	
	public Xpdl convert2XPDL() {
		xpdl = fillXPDL();
		return xpdl;
	}
	
	/**
	 * Convert BPMN diagram to XPDL without layout information
	 * @return XPDL model
	 */
//	public Xpdl convert2XPDL_noLayout(Abstract context) {
//
//		fillXpdlActivities(context);
//		fillXpdlTransitions(context);
////		fillSwimlanes();
//		return xpdl;
//	}

//	private void fillSwimlanes() {
//		// Create pool for each workflow process according to the XPDL specification
//		for(XpdlWorkflowProcess process : xpdl.getWorkflowProcesses().getList()) {
//			fillSwimlane(process);
//		}
//	}
	
//	private void fillSwimlane(XpdlWorkflowProcess process) {
//		List<XpdlActivity> activityList = process.getActivities().getList();
//
////		Map<String, String> lanes = new HashMap<String, String>();
////		for (XpdlActivity activity : activityList) {
////			
////			if (bpmnNode.getParentLane() != null) {
////				lanes.put("" + bpmnNode.hashCode(), "" + bpmnNode.getParentLane().hashCode());
////			}
////		}
//		Swimlane bpmnPool = retrievePool(bpmn);
//		XpdlPools xpdlPools = xpdl.getPools();
//		XpdlPool xpdlPool = new XpdlPool("Pool");
//		xpdlPool.setId(bpmnPool.getId().toString().replace(' ', '_'));
//		xpdlPool.setBoundaryVisible("false");
//		xpdlPool.setProcess(xpdl.getWorkflowProcesses().getList().get(0).getId());
//		xpdlPool.setMainPool("true");
//
//		XpdlLanes xpdlLanes = new XpdlLanes("Lanes");
//
//		Set<Swimlane> bpmnLanes = retrieveLanes(bpmn, bpmnPool);
//		for (Swimlane swimlane : bpmnLanes) {
//			XpdlLane lane = new XpdlLane("Lane");
//			lane.setParentPool(xpdlPool.getId());
//			lane.setName(swimlane.getLabel());
//			lane.setId("" + swimlane.hashCode());
//			xpdlLanes.add2List(lane);
//		}
//		xpdlPool.setLanes(xpdlLanes);
//
//		xpdlPools.add2List(xpdlPool);
//
//		for (XpdlActivity xpdlActivity : activityList) {
//			if (xpdlActivity.getNodeGraphicsInfos() != null) {
//				List<XpdlNodeGraphicsInfo> infos = xpdlActivity.getNodeGraphicsInfos().getList();
//				for (XpdlNodeGraphicsInfo xpdlNodeGraphicsInfo : infos) {
//					xpdlNodeGraphicsInfo.setLaneId(map3.get(xpdlActivity.getId()));
//				}
//			}
//		}
//	}

//	/**
//	 * @param bpmnDiagram
//	 * @return
//	 */
//	private Swimlane retrievePool(BPMNDiagram bpmnDiagram) {
//		for (Swimlane swimlane : bpmnDiagram.getSwimlanes()) {
//			if (swimlane.getParentSwimlane() == null) {
//				return swimlane;
//			}
//		}
//		return null;
//	}
//	
///**
// * 
// * @param pool
// * @return
// */
//	private Set<Swimlane> retrieveLanes(BPMNDiagram diagram, Swimlane pool) {
//		Set<Swimlane> lanes = new HashSet<Swimlane>();
//		for (Swimlane swimlane : diagram.getSwimlanes()) {
//			Swimlane parent = swimlane.getParentSwimlane();
//			if (parent != null && parent.equals(pool)) {
//				lanes.add(swimlane);
//			}
//		}
//		return lanes;
//	}

//	/**
//	 * @param context
//	 * @param bpmn
//	 * @param xpdl
//	 */
//	private void fillXpdlActivityPositions( context) {
//		CustomJGraphPanel graphPanel = CustomJGraphVisualizer.instance().visualizeGraph( bpmn);
//		CustomGraphModel graphModel = graphPanel.getGraph().getModel();
//
//		@SuppressWarnings("rawtypes")
//		List CustomGraphCellList = graphModel.getRoots();
//		XpdlWorkflowProcess workflowProcess = xpdl.getWorkflowProcesses().getList().get(0);
//		Map<String, XpdlActivity> map = new HashMap<String, XpdlActivity>();
//		for (XpdlActivity xpdlActivity : workflowProcess.getActivities().getList()) {
//			map.put(xpdlActivity.getId(), xpdlActivity);
//		}
//		System.out.println("");
//		for (Object object : CustomGraphCellList) {
//			if (object instanceof CustomGraphCell) {
//				CustomGraphCell CustomGraphCell = (CustomGraphCell) object;
//				XpdlActivity xpdlActivity = map.get("" + CustomGraphCell.hashCode());
//				if (xpdlActivity != null) {
//					Rectangle2D rectangle = CustomGraphCell.getView().getBounds();
//					XpdlNodeGraphicsInfos nodeGraphicsInfos = new XpdlNodeGraphicsInfos("NodeGraphicsInfos");
//
//					XpdlNodeGraphicsInfo nodeGraphicsInfo = new XpdlNodeGraphicsInfo("NodeGraphicsInfo");
//					nodeGraphicsInfo.setToolId("Custom");
//
//					XpdlCoordinates coordinates = new XpdlCoordinates("Coordinates");
//
//					nodeGraphicsInfo.setHeight("" + (int) rectangle.getHeight());
//					nodeGraphicsInfo.setWidth("" + (int) rectangle.getWidth());
//
//					coordinates.setxCoordinate("" + (int) rectangle.getX());
//					coordinates.setyCoordinate("" + (int) rectangle.getY());
//					nodeGraphicsInfo.setCoordinates(coordinates);
//
//					nodeGraphicsInfos.add2List(nodeGraphicsInfo);
//					xpdlActivity.setNodeGraphicsInfos(nodeGraphicsInfos);
//				} else {
//					@SuppressWarnings("unchecked")
//					List<Object> cells = CustomGraphCell.getChildren();
//					for (Object object2 : cells) {
//						if (object2 instanceof CustomGraphCell) {
//							CustomGraphCell CustomGraphCell2 = (CustomGraphCell) object2;
//							XpdlActivity xpdlActivity2 = map.get("" + CustomGraphCell2.hashCode());
//							if (xpdlActivity2 != null) {
//								Rectangle2D rectangle = CustomGraphCell2.getView().getBounds();
//								XpdlNodeGraphicsInfos nodeGraphicsInfos = new XpdlNodeGraphicsInfos("NodeGraphicsInfos");
//
//								XpdlNodeGraphicsInfo nodeGraphicsInfo = new XpdlNodeGraphicsInfo("NodeGraphicsInfo");
//								nodeGraphicsInfo.setToolId("Custom");
//
//								XpdlCoordinates coordinates = new XpdlCoordinates("Coordinates");
//
//								nodeGraphicsInfo.setHeight("" + (int) rectangle.getHeight());
//								nodeGraphicsInfo.setWidth("" + (int) rectangle.getWidth());
//
//								coordinates.setxCoordinate("" + (int) rectangle.getX());
//								coordinates.setyCoordinate("" + (int) rectangle.getY());
//								nodeGraphicsInfo.setCoordinates(coordinates);
//
//								nodeGraphicsInfos.add2List(nodeGraphicsInfo);
//								xpdlActivity2.setNodeGraphicsInfos(nodeGraphicsInfos);
//							}
//						}
//					}
//				}
//			}
//		}
//
//	}
//
//	/**
//	 * @param bpmn
//	 * @param vendorSettings
//	 */
//	private void fillXpdlActivities(Abstract context) {
//
//		List<XpdlActivity> activityList = new ArrayList<XpdlActivity>();
//		// XpdlImplementation, XpdlDocumentation, XpdlDescription, XpdlLimit is excluded
//		fillActivities( activityList, null);
//		fillEvents( activityList, null);
//		fillGateways( activityList, null);
//
//		XpdlWorkflowProcess mainWorkflowProcess = xpdl.getWorkflowProcesses().getList().get(0);
//
//		for (SubProcess subProcess : bpmn.getSubProcesses()) {
//			// define a subprocess as a sub flow activity in the main process
//			XpdlActivity xpdlActivity = new XpdlActivity("Activity");
//			xpdlActivity.setId("" + subProcess.hashCode());
//			xpdlActivity.setName(subProcess.getLabel());
//
//			XpdlBlockActivity activityBlockActivity = new XpdlBlockActivity("BlockActivity");
//			activityBlockActivity.setActivitySetId(ID_SUB_PROCESS_SUBWF + subProcess.getId().toString().replace(' ', '_'));
//			activityBlockActivity.setView("EXPANDED");
//
//			xpdlActivity.setBlockActivity(activityBlockActivity);
//			activityList.add(xpdlActivity);
//
//			// create workflow process for each subprocess
//			List<XpdlActivity> tempActivityList = new ArrayList<XpdlActivity>();
//			fillActivities( tempActivityList, subProcess);
//			fillEvents( tempActivityList, subProcess);
//			fillGateways( tempActivityList, subProcess);
//			XpdlActivitySet xpdlActivitySet = null;
//
//			for (XpdlActivitySet tempXpdlActivitySet : mainWorkflowProcess.getActivitySets().getList()) {
//				if (tempXpdlActivitySet.getId().equals(ID_SUB_PROCESS_SUBWF + subProcess.getId().toString().replace(' ', '_'))) {
//					xpdlActivitySet = tempXpdlActivitySet;
//					break;
//				}
//			}
//			xpdlActivitySet.getActivities().setList(tempActivityList);
//			mainWorkflowProcess.getActivitySets().add2List(xpdlActivitySet);
//		}
//		mainWorkflowProcess.getActivities().setList(activityList);
//	}

	/**
	 * @param bpmn
	 * @param vendorSettings
	 * @param activityList
	 */
	private void fillGateways( List<XpdlActivity> activityList, 
			ContainingDirectedGraphNode container) {
		for (Gateway gateway : bpmn.getGateways()) {
			if (gateway.getParent() == container) {
				XpdlActivity xpdlActivity = new XpdlActivity("Activity");
				xpdlActivity.setId("" + gateway.hashCode());
				xpdlActivity.setName(gateway.getLabel());

				XpdlRoute route = new XpdlRoute("Route");
				String gatewayTypeStr = getGatewayType(gateway.getGatewayType());

				if(!gatewayTypeStr.equals("XOR")) {
					if(gatewayTypeStr.equals("AND")) {
						route.setGatewayType("Parallel");
					} else {
						route.setGatewayType(gatewayTypeStr);
					}
				} 
				setMarkerVendorSpecific(gateway, route);
				xpdlActivity.setRoute(route);

				XpdlTransitionRestrictions transitionRestrictions = new XpdlTransitionRestrictions(
						"TransitionRestrictions");
				XpdlTransitionRestriction transitionRestriction = new XpdlTransitionRestriction("TransitionRestriction");

				XpdlTransitionRefs transitionRefs = new XpdlTransitionRefs("TransitionRefs");

				if (isSplit(bpmn, gateway)) {

					for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> gatewayOutEdge : bpmn.getOutEdges(gateway)) {
						XpdlTransitionRef transitionRef = new XpdlTransitionRef("TransitionRef");
						transitionRef.setId("" + gatewayOutEdge.hashCode());
						transitionRef.setName("" + gatewayOutEdge.hashCode());
						transitionRefs.add2List(transitionRef);
					}

					XpdlSplit xpdlSplit = new XpdlSplit("Split");
					xpdlSplit.setType(gatewayTypeStr);
					xpdlSplit.setTransitionRefs(transitionRefs);
					transitionRestriction.setSplit(xpdlSplit);

				} else {
					XpdlJoin xpdlJoin = new XpdlJoin("Join");
					xpdlJoin.setType(gatewayTypeStr);
					transitionRestriction.setJoin(xpdlJoin);
				}
				transitionRestrictions.add2List(transitionRestriction);
				xpdlActivity.setTransitionRestrictions(transitionRestrictions);
				
					XpdlNodeGraphicsInfos nodeGraphicsInfos 
						= retrieveGraphicsForGraphNode( gateway);
					xpdlActivity.setNodeGraphicsInfos(nodeGraphicsInfos);
				
				activityList.add(xpdlActivity);
			}

		}
	}

/**
 * @param activityList
 * @param container
 */
	private void fillEvents(  List<XpdlActivity> activityList, ContainingDirectedGraphNode container) {
		Map<EventTrigger, String> eventTriggerMap = new HashMap<EventTrigger, String>() {
			{
				put(EventTrigger.MESSAGE, "Message");
				put(EventTrigger.NONE, "None");
				put(EventTrigger.TIMER, "Timer");
				put(EventTrigger.CONDITIONAL, "Rule");
				put(EventTrigger.LINK, "Link");
				put(EventTrigger.MULTIPLE, "Multiple");
				put(EventTrigger.ERROR, "Error");
				put(EventTrigger.COMPENSATION, "Compensation");
				put(EventTrigger.CANCEL, "Cancel");
				put( EventTrigger.TERMINATE, "Terminate");
				put(EventTrigger.SIGNAL, "Signal");
			}
		};
		for (Event event : bpmn.getEvents()) {
			String trigger = eventTriggerMap.get(event.getEventTrigger());
					
			if (event.getParent() == container) {
				XpdlActivity xpdlActivity = new XpdlActivity("Activity");
				xpdlActivity.setId("" + event.hashCode());
				xpdlActivity.setName(event.getLabel());

				XpdlEvent e1 = null;
				if (event.getEventType().equals(EventType.END)) {
					e1 = new XpdlEvent("Event");
					XpdlEndEvent endEvent = null;
					endEvent = new XpdlEndEvent("EndEvent");
					if(listOfXpdlEndTrigers.contains(event.getEventTrigger())) {
						endEvent.setResult(trigger);
					} else {
						endEvent.setResult("None");
					}
					e1.setEndEvent(endEvent);
				} else if (event.getEventType().equals(EventType.START)) {
					e1 = new XpdlEvent("Event");
					XpdlStartEvent startEvent = null;
					startEvent = new XpdlStartEvent("StartEvent");
					if (event.getEventTrigger() != null) {
						startEvent.setTrigger(trigger);
					} else {
						startEvent.setTrigger("None");
					}
					e1.setStartEvent(startEvent);
				} else if (event.getEventType().equals(EventType.INTERMEDIATE)) {
					e1 = new XpdlEvent("Event");
					XpdlIntermediateEvent intermediateEvent = null;
					intermediateEvent = new XpdlIntermediateEvent("IntermediateEvent");
					if (event.getEventTrigger() != null) {
						intermediateEvent.setTrigger(trigger);
						if(event.getEventTrigger().equals(EventTrigger.MESSAGE)) {
							XpdlTriggerResultMessage resultMessage 
								= new XpdlTriggerResultMessage("TriggerResultMessage");
							resultMessage.setCatchThrow(event.getEventUse().toString());
							XpdlMessageType message = new XpdlMessageType("Message");
							resultMessage.setMessage(message);
							intermediateEvent.setTriggerResultMessage(resultMessage);
						}
					} else {
						intermediateEvent.setTrigger("None");
					}
					e1.setIntermediateEvent(intermediateEvent);
				}
				xpdlActivity.setEvent(e1);
				
					XpdlNodeGraphicsInfos nodeGraphicsInfos 
						= retrieveGraphicsForGraphNode( event);
					xpdlActivity.setNodeGraphicsInfos(nodeGraphicsInfos);
				
				activityList.add(xpdlActivity);
			}

		}
	}

/**
 * @param context
 * @param activityList
 * @param container
 */
	private void fillActivities( 
			List<XpdlActivity> activityList, ContainingDirectedGraphNode container) {
		for (Activity activity : bpmn.getActivities()) {
			if (activity.getParent() == container) {
				XpdlActivity xpdlActivity = new XpdlActivity("Activity");
				xpdlActivity.setId("" + activity.hashCode());
				xpdlActivity.setName(activity.getLabel());
				XpdlImplementation xpdlImplementation = new XpdlImplementation("Implementation");
				XpdlTask xpdlTask = new XpdlTask("Task");
				xpdlImplementation.setTask(xpdlTask);
				xpdlActivity.setImplementation(xpdlImplementation);
				
					XpdlNodeGraphicsInfos nodeGraphicsInfos 
						= retrieveGraphicsForGraphNode( activity);
					xpdlActivity.setNodeGraphicsInfos(nodeGraphicsInfos);
				
				activityList.add(xpdlActivity);
			}
		}
	}
	
	private void fillTransitions( 
			List<XpdlTransition> transitionList, ContainingDirectedGraphNode container) {
		for (Flow flow : bpmn.getFlows()) {
			if (flow.getParent() == container) {
				XpdlTransition xpdlTransition = new XpdlTransition("Transition");
				xpdlTransition.setFrom("" + flow.getSource().hashCode());
				xpdlTransition.setTo("" + flow.getTarget().hashCode());
				xpdlTransition.setId("" + flow.hashCode());

				
					XpdlConnectorGraphicsInfos tConnectorGraphicsInfos 
						= retrieveGraphicsForGraphEdge( flow);
					xpdlTransition.setConnectorGraphicsInfos(tConnectorGraphicsInfos);
				
				transitionList.add(xpdlTransition);
			}
		}
	}
	
	private void fillMessageFlows(  List<XpdlMessageFlow> listOfFlows) {
		for (MessageFlow flow : bpmn.getMessageFlows()) {
			XpdlMessageFlow xpdlMessageFlow = new XpdlMessageFlow("MessageFlow");
			xpdlMessageFlow.setSource("" + flow.getSource().hashCode());
			xpdlMessageFlow.setTarget("" + flow.getTarget().hashCode());
			xpdlMessageFlow.setId("" + flow.hashCode());

			
				XpdlConnectorGraphicsInfos tConnectorGraphicsInfos = retrieveGraphicsForGraphEdge( flow);
				xpdlMessageFlow.setConnectorsGraphicsInfos(tConnectorGraphicsInfos);
			
			listOfFlows.add(xpdlMessageFlow);
		}
	}

	/**
	 * @param bpmn
	 * @param gateway
	 * @return
	 */
	private boolean isSplit(BPMNDiagram bpmn, Gateway gateway) {
		boolean isSplit = true;
		isSplit = (bpmn.getOutEdges(gateway).size() > 1);
		return isSplit;
	}

	/**
	 * @param gateway
	 * @param route
	 */
	private void setMarkerVendorSpecific(Gateway gateway, XpdlRoute route) {

		if (gateway.getGatewayType().equals(GatewayType.INCLUSIVE)) {
			route.setMarkerVisible(""+gateway.isMarkerVisible());
		} else if (gateway.getGatewayType().equals(GatewayType.DATABASED)) {
			route.setMarkerVisible(""+gateway.isMarkerVisible());
		} else {
			route.setMarkerVisible(""+gateway.isMarkerVisible());
		}
	}

	/**
	 * @param activityNameStr
	 * @return
	 */
	private String getGatewayType(GatewayType type) {
		String typeStr = null;
		if (type.equals(GatewayType.EVENTBASED) || type.equals(GatewayType.DATABASED)) {
			typeStr = "XOR";
		} else if (type.equals(GatewayType.PARALLEL)) {
			typeStr = "AND";
		} else if (type.equals(GatewayType.INCLUSIVE)) {
			typeStr = "OR";
		}
		return typeStr;
	}

	/**
	 * @param bpmn
	 * @param xpdl
	 */
//	private void fillXpdlTransitions(Abstract context) {
//		for (Flow flow : bpmn.getFlows()) {
//			XpdlTransition t = new XpdlTransition("Transition");
//			t.setFrom("" + flow.getSource().hashCode());
//			t.setTo("" + flow.getTarget().hashCode());
//			t.setId("" + flow.hashCode());
//
//			if (layout) {
//				XpdlConnectorGraphicsInfos tConnectorGraphicsInfos 
//					= retrieveGraphicsForGraphEdge( flow);
//				t.setConnectorGraphicsInfos(tConnectorGraphicsInfos);
//			}
//
//			XpdlWorkflowProcess mainWorkflowProcess = xpdl.getWorkflowProcesses().getList().get(0);
//
//			if (flow.getParentSubProcess() == null) {
//				// transition is belong to main process
//				mainWorkflowProcess.getTransitions().add2List(t);
//			} else {
//				// transition is belong to subprocess's activity set
//				XpdlActivitySet activitySet = null;
//				for (XpdlActivitySet tempActivitySet : mainWorkflowProcess.getActivitySets().getList()) {
//					if (tempActivitySet.getId().equals(ID_SUB_PROCESS_SUBWF + flow.getParentSubProcess().getId().toString().replace(' ', '_'))) {
//						activitySet = tempActivitySet;
//						break;
//					}
//				}
//				activitySet.getTransitions().add2List(t);
//			}
//		}
//	}

	/**
	 * @param xpdl
	 * @return
	 */
	private Xpdl fillXPDL() {
		// TODO No participant study is done
		// TODO No datatype study is done
		// TODO No extension study is done

		Xpdl xpdl = new Xpdl();

		// XPDL PACKAGE HEADER
		XpdlPackageHeader packageHeader = setPackageHeader();

		XpdlPools xpdlPools = new XpdlPools("Pools");

		// XPDL WORKFLOW PROCESSES
		XpdlWorkflowProcesses xpdlWorkflowProcesses = new XpdlWorkflowProcesses("WorkflowProcesses");
		
		// BUILD MAIN WORKFLOW PROCESS
		XpdlWorkflowProcess mainWorkflowProcess = fillWorkflowProcess(null);
		XpdlPool xpdlPool = fillPool( null);
		xpdlPool.setProcess(mainWorkflowProcess.getId());
		xpdlPools.add2List(xpdlPool);	
		xpdlWorkflowProcesses.add2List(mainWorkflowProcess);
		
		// BUILD WORKFLOW PROCESSES FOR POOLS
		for(Swimlane pool : bpmn.getPools()) {
			XpdlWorkflowProcess workflowProcess = fillWorkflowProcess(pool);
			xpdlPool = fillPool( pool);
			xpdlPool.setProcess(workflowProcess.getId());
			xpdlPools.add2List(xpdlPool);
			xpdlWorkflowProcesses.add2List(workflowProcess);
		}
		XpdlMessageFlows messageFlows = new XpdlMessageFlows("MessageFlows");
		List<XpdlMessageFlow> xpdlMessageFlowList = new ArrayList<XpdlMessageFlow>();
		messageFlows.setList(xpdlMessageFlowList);
		xpdl.setMessageFlows(messageFlows);
		fillMessageFlows( xpdlMessageFlowList);

		// FILL XPDL SETTINGS
		xpdl.setId(ID_XPDL + bpmn.getLabel().replace(' ', '_'));
		xpdl.setName(bpmn.getLabel().replace(' ', '_'));
		xpdl.setPackageHeader(packageHeader);
		xpdl.setPools(xpdlPools);
		xpdl.setWorkflowProcesses(xpdlWorkflowProcesses);

		return xpdl;
	}
	
	private XpdlPool fillPool(  Swimlane pool) {
		XpdlPool xpdlPool = new XpdlPool("Pool");
		if (pool == null) { 
			xpdlPool.setBoundaryVisible("false");
			xpdlPool.setMainPool("true");
			xpdlPool.setId(ID_DEFAULT_POOL + bpmn.getLabel().replace(' ', '_'));

		} else {
			xpdlPool.setBoundaryVisible("true");
			xpdlPool.setMainPool("false");
			xpdlPool.setId(pool.getId().toString().replace(' ', '_'));
			
				XpdlNodeGraphicsInfos graphicsInfos = retrieveGraphicsForGraphNode( pool);
				xpdlPool.setNodeGraphicsInfos(graphicsInfos);
			
		}
		fillLanes( pool);
		return xpdlPool;
	}
				
	private XpdlLanes fillLanes(  Swimlane pool) {
		XpdlLanes xpdlLanes = new XpdlLanes("Lanes");
		for (Swimlane lane : bpmn.getLanes(pool)) {
			XpdlLane xpdlLane = new XpdlLane("Lane");
			xpdlLane.setId(lane.getId().toString());
			xpdlLane.setName(lane.getLabel());
			if (lane.getParentLane() != null) {
				xpdlLane.setParentLane(lane.getParentLane().getId().toString());
			}
			
				XpdlNodeGraphicsInfos graphicsInfos = retrieveGraphicsForGraphNode( lane);
				xpdlLane.setNodeGraphicsInfos(graphicsInfos);
			
			xpdlLanes.add2List(xpdlLane);
		}
		return xpdlLanes;
	}
	
	
	private XpdlNodeGraphicsInfos retrieveGraphicsForGraphNode( 	BPMNNode bpmnNode) {
		CustomJGraphPanel graphPanel = getCustomJGraphPanel( bpmn);
		CustomGraphModel graphModel = graphPanel.getGraph().getModel();		
		for(Object o : retrieveAllGraphElements(graphModel)) {
			if (o instanceof CustomGraphCell) {
				CustomGraphCell graphCell = (CustomGraphCell) o;			
				if(bpmnNode.equals(graphCell.getNode())) {
					XpdlNodeGraphicsInfos xpdlNodeGraphicsInfos 
						= retrieveCellGraphicsInfo( graphCell);
					return xpdlNodeGraphicsInfos;
				}
			}
		}		
		return null;
	}
	
	private Set<Object> retrieveAllGraphElements(CustomGraphModel graphModel) {
		Set<Object> modelElements = new HashSet<Object>();
		for (Object o : graphModel.getRoots()) {
			modelElements.add(o);
			if (o instanceof CustomGraphCell) {
				modelElements.addAll(retrieveAllChildren((CustomGraphCell)o));
			}
		}
		return modelElements;
	}
	
	private Set<Object> retrieveAllChildren(CustomGraphCell graphCell) {
		Set<Object> resultSet = new HashSet<Object>();
		for (Object o : graphCell.getChildren()) {
			resultSet.add(o);
			if (o instanceof CustomGraphCell) {
				resultSet.addAll(retrieveAllChildren((CustomGraphCell) o));
			}
		}
		return resultSet;
	}
	
	private  XpdlConnectorGraphicsInfos retrieveGraphicsForGraphEdge(  
			BPMNEdge bpmnEdge) {
		CustomJGraphPanel graphPanel = getCustomJGraphPanel( bpmn);
		CustomGraphModel graphModel = graphPanel.getGraph().getModel();
		for(Object o : retrieveAllGraphElements(graphModel)) {
			if (o instanceof CustomGraphEdge) {
				CustomGraphEdge graphEdge = (CustomGraphEdge) o;
				if(bpmnEdge.equals(graphEdge.getEdge())) {
					XpdlConnectorGraphicsInfos xpdlNodeGraphicsInfos 
						= retrieveEdgeGraphicsInfo( graphEdge);
					return xpdlNodeGraphicsInfos;
				}
			}
		}
		return null;
	}
	protected static JGraphLayout getLayout(int orientation) {
		JGraphHierarchicalLayout layout = new JGraphHierarchicalLayout();
		layout.setDeterministic(false);
		layout.setCompactLayout(false);
		layout.setFineTuning(true);
		layout.setParallelEdgeSpacing(15);
		layout.setFixRoots(false);

		layout.setOrientation(orientation);

		return layout;
	}
	private CustomJGraphPanel getCustomJGraphPanel(BPMNDiagram graph) {
		CustomGraphModel model = new CustomGraphModel(graph);
		ViewSpecificAttributeMap map =new ViewSpecificAttributeMap();
		GraphLayoutConnection layoutConnection =  new GraphLayoutConnection(graph);
		CustomJGraph jgraph = new CustomJGraph(model,map,layoutConnection);
		jgraph.repositionToOrigin();
		JGraphLayout layout = getLayout(map.get(graph, AttributeMap.PREF_ORIENTATION, SwingConstants.SOUTH));

		if (!layoutConnection.isLayedOut()) {

			JGraphFacade facade = new JGraphFacade(jgraph);

			facade.setOrdered(false);
			facade.setEdgePromotion(true);
			facade.setIgnoresCellsInGroups(false);
			facade.setIgnoresHiddenCells(false);
			facade.setIgnoresUnconnectedCells(false);
			facade.setDirected(true);
			facade.resetControlPoints();
			if (layout instanceof JGraphHierarchicalLayout) {
				facade.run((JGraphHierarchicalLayout) layout, true);
			} else {
				facade.run(layout, true);
			}

			Map<?, ?> nested = facade.createNestedMap(true, true);

			jgraph.getGraphLayoutCache().edit(nested);
			//				jgraph.repositionToOrigin();
			layoutConnection.setLayedOut(true);

		}

		jgraph.setUpdateLayout(layout);
		return new CustomJGraphPanel(jgraph);
	}
	
	private XpdlConnectorGraphicsInfos retrieveEdgeGraphicsInfo(  
			CustomGraphEdge edge) {
		
		XpdlConnectorGraphicsInfos connGraphicsInfos = new XpdlConnectorGraphicsInfos("ConnectorGraphicsInfos");
		XpdlConnectorGraphicsInfo connGraphicsInfo = new XpdlConnectorGraphicsInfo("ConnectorGraphicsInfo");
		connGraphicsInfos.add2List(connGraphicsInfo);
		// Construct graph info
		List<Object> objects = edge.getView().getPoints();
		for(Object o : objects) {
			if(o instanceof JGraphPortView) {
				JGraphPortView portView = (JGraphPortView)o;
				Point2D point = portView.getLocation();
				XpdlCoordinates xpdlCoordinates = new XpdlCoordinates("Coordinates");
				xpdlCoordinates.setxCoordinate(new Double(point.getX()).toString());
				xpdlCoordinates.setyCoordinate(new Double(point.getY()).toString());
				connGraphicsInfo.addCoordinates(xpdlCoordinates);
			}	
		}
		return connGraphicsInfos;
	}
	
	private XpdlNodeGraphicsInfos retrieveCellGraphicsInfo( 
			CustomGraphCell graphCell) {
		XpdlNodeGraphicsInfos nodeGraphicsInfos = new XpdlNodeGraphicsInfos("NodeGraphicsInfos");	
		XpdlNodeGraphicsInfo nodeGraphicsInfo = new XpdlNodeGraphicsInfo("NodeGraphicsInfo");
		nodeGraphicsInfos.add2List(nodeGraphicsInfo);
		XpdlCoordinates xpdlCoordinates = new XpdlCoordinates("Coordinates");
		// Construct graph info
		Rectangle2D rectangle = graphCell.getView().getBounds();
		xpdlCoordinates.setxCoordinate(new Double(rectangle.getX()).toString());
		xpdlCoordinates.setyCoordinate(new Double(rectangle.getY()).toString());				
		nodeGraphicsInfo.setCoordinates(xpdlCoordinates);
		nodeGraphicsInfo.setWidth(new Double(rectangle.getWidth()).toString());
		nodeGraphicsInfo.setHeight(new Double(rectangle.getHeight()).toString());
		
		return nodeGraphicsInfos;
	}
				
	private XpdlWorkflowProcess fillWorkflowProcess(Swimlane pool) {
		// XPDL MAIN WORKFLOW PROCESS
		XpdlWorkflowProcess workflowProcess = new XpdlWorkflowProcess("WorkflowProcess");
		String id = (pool == null ? ID_MAIN_PROCESS_WF + bpmn.getLabel() 
				: ID_PROCESS_WF + pool.getId().toString()).replace(' ', '_');
		workflowProcess.setId(id);
		String name = pool == null ? bpmn.getLabel().replace(' ', '_') : pool.getLabel();
		workflowProcess.setName(name);
		workflowProcess.setAccessLevel("PUBLIC");

		// XPDL WORKFLOW PROCESS HEADER
		XpdlProcessHeader processHeader = setProcessHeader();
		workflowProcess.setProcessHeader(processHeader);

		// XPDL WORKFLOW PROCESS REDEFINABLE HEADER
		XpdlRedefinableHeader redefinableHeader = setRedefinableHeader();
		workflowProcess.setRedefinableHeader(redefinableHeader);

		// XPDL WORKFLOW PROCESS PARTICIPANTS
//		HashMap<String, String> participantAndType = new HashMap<String, String>();
//		participantAndType.put("currentOwner", "ROLE");
//		XpdlParticipants participants = setParticipants(participantAndType);
//		workflowProcess.setParticipants(participants);
		
		// XPDL ACTIVITIES
		XpdlActivities mainXpdlActivities = new XpdlActivities("Activities");
		List<XpdlActivity> activities = new ArrayList<XpdlActivity>();
		
		// XPDL WORKFLOW PROCESS ACTIVITY SETS FOR SUBPROCESSES
		XpdlActivitySets activitySets = new XpdlActivitySets("ActivitySets");
		List<XpdlActivitySet> activitySetList = new ArrayList<XpdlActivitySet>();
		for (SubProcess subProcess : bpmn.getSubProcesses(pool)) {
			XpdlActivitySet xpdlActivitySet = new XpdlActivitySet("ActivitySet");
			xpdlActivitySet.setId(ID_SUB_PROCESS_SUBWF + subProcess.getId().toString().replace(' ', '_'));
			xpdlActivitySet.setName(subProcess.getLabel());
			xpdlActivitySet.setArtifacts(new XpdlArtifacts("Artifacts"));
			xpdlActivitySet.setAssociations(new XpdlAssociations("Associations"));

			// XPDL ACTIVITIES FOR SUBPROCESS
			XpdlActivities xpdlActivities = new XpdlActivities("Activities");
			List<XpdlActivity> subActivities = new ArrayList<XpdlActivity>();
			xpdlActivities.setList(subActivities);
			xpdlActivitySet.setActivities(xpdlActivities);
			fillActivities( subActivities, subProcess);
			fillEvents( subActivities, subProcess);
			fillGateways( subActivities, subProcess);

			// XPDL WORKFLOW PROCESS TRANSITIONS FOR SUBPROCESS
			XpdlTransitions transitions = new XpdlTransitions("Transitions");
			List<XpdlTransition> xpdlTransitionList = new ArrayList<XpdlTransition>();
			transitions.setList(xpdlTransitionList);
			xpdlActivitySet.setTransitions(transitions);
			fillTransitions( xpdlTransitionList, subProcess);

			activitySetList.add(xpdlActivitySet);
			
			// CREATE SUB_PROCESS
			XpdlActivity xpdlSubProcess = new XpdlActivity("Activity"); 
			xpdlSubProcess.setId("" + subProcess.hashCode());
			XpdlBlockActivity blockActivity = new XpdlBlockActivity("BlockActivity");
			blockActivity.setActivitySetId(ID_SUB_PROCESS_SUBWF + subProcess.getId().toString().replace(' ', '_'));
			blockActivity.setView(subProcess.isBCollapsed()? "COLLAPSED" : "EXPANDED");
			xpdlSubProcess.setBlockActivity(blockActivity);
			activities.add(xpdlSubProcess);
			
				XpdlNodeGraphicsInfos nodeGraphicsInfos 
					= retrieveGraphicsForGraphNode( subProcess);
				xpdlSubProcess.setNodeGraphicsInfos(nodeGraphicsInfos);
			
		}
		activitySets.setList(activitySetList);
		workflowProcess.setActivitySets(activitySets);

		fillActivities( activities, pool);
		fillEvents( activities, pool);
		fillGateways( activities, pool);
		mainXpdlActivities.setList(activities);
		workflowProcess.setActivities(mainXpdlActivities);

		// XPDL WORKFLOW PROCESS TRANSITIONS
		XpdlTransitions mainXpdlTransitions = new XpdlTransitions("Transitions");
		List<XpdlTransition> xpdlTransitionList = new ArrayList<XpdlTransition>();
		fillTransitions( xpdlTransitionList, pool);
		mainXpdlTransitions.setList(xpdlTransitionList);
		workflowProcess.setTransitions(mainXpdlTransitions);
		
		return workflowProcess;
	}
	
//	private XpdlParticipants setParticipants(HashMap<String, String> participantAndType) {
//		XpdlParticipants participants = new XpdlParticipants("Participants");
//
//		for (String tempParticipant : participantAndType.keySet()) {
//			XpdlParticipant participant = new XpdlParticipant("Participant");
//			participant.setId(tempParticipant);
//			XpdlParticipantType participantType = new XpdlParticipantType("ParticipantType");
//			participantType.setType(participantAndType.get(tempParticipant));
//			participant.setParticipantType(participantType);
//			participants.add2List(participant);
//		}
//		return participants;
//	}

	private XpdlRedefinableHeader setRedefinableHeader() {
		XpdlRedefinableHeader redefinableHeader = new XpdlRedefinableHeader("RedefinableHeader");

		XpdlAuthor author = new XpdlAuthor("Author");
		XpdlText authorXpdlText = new XpdlText("XpdlText");
		authorXpdlText.setText("Custom");
		author.setXpdlText(authorXpdlText);
		redefinableHeader.setAuthor(author);

		XpdlVersion version = new XpdlVersion("Version");
		XpdlText versionXpdlText = new XpdlText("XpdlText");
		versionXpdlText.setText("3.6");
		version.setXpdlText(versionXpdlText);
		redefinableHeader.setVersion(version);
		return redefinableHeader;
	}

	private XpdlProcessHeader setProcessHeader() {
		XpdlProcessHeader processHeader = new XpdlProcessHeader("ProcessHeader");

		XpdlCreated processHeaderCreated = new XpdlCreated("Created");
		String processHeaderCreateDate = new SimpleDateFormat("dd/MM/yyyy hh:mm").format(new Date());

		XpdlText processHeaderCreateXpdlText = new XpdlText("XpdlText");
		processHeaderCreateXpdlText.setText(processHeaderCreateDate);
		processHeaderCreated.setXpdlText(processHeaderCreateXpdlText);

		XpdlPriority processHeaderXpdlPriority = new XpdlPriority("Priority");
		XpdlText processHeaderPriorityXpdlText = new XpdlText("XpdlText");
		processHeaderPriorityXpdlText.setText("Normal");
		processHeaderXpdlPriority.setXpdlText(processHeaderPriorityXpdlText);

		processHeader.setCreated(processHeaderCreated);
		processHeader.setPriority(processHeaderXpdlPriority);
		return processHeader;
	}

	/**
	 * @param packageHeaderVersion
	 * @param packageHeaderVendor
	 * @param packageHeaderToday
	 * @param packageHeaderDescription
	 * @param packageHeaderDocumentation
	 * @return
	 */
	private XpdlPackageHeader setPackageHeader() {

		// SET PACKAGE HEADER VARIABLES
		String packageHeaderVersion = "2.2";
		String packageHeaderVendor = "Custom";
		Date packageHeaderToday = new Date();
		String packageHeaderDescription = "Custom Mined BPMN Model";
		String packageHeaderDocumentation = "Custom Mined BPMN Model";

		XpdlPackageHeader packageHeader = new XpdlPackageHeader("PackageHeader");

		XpdlXpdlVersion xpdlXpdlVersion = new XpdlXpdlVersion("XPDLVersion");
		XpdlText versionText = new XpdlText("XpdlText");
		versionText.setText(packageHeaderVersion);
		xpdlXpdlVersion.setXpdlText(versionText);

		XpdlVendor xpdlVendor = new XpdlVendor("Vendor");
		XpdlText xpdlVendorXpdlText = new XpdlText("XpdlText");
		xpdlVendorXpdlText.setText(packageHeaderVendor);
		xpdlVendor.setXpdlText(xpdlVendorXpdlText);

		XpdlCreated created = new XpdlCreated("Created");
		String now = new SimpleDateFormat("dd/MM/yyyy hh:mm").format(packageHeaderToday);

		XpdlText createdXpdlText = new XpdlText("XpdlText");
		createdXpdlText.setText(now);
		created.setXpdlText(createdXpdlText);

		XpdlDescription xpdlDescription = new XpdlDescription("Description");
		XpdlText descriptionXpdlText = new XpdlText("XpdlText");
		descriptionXpdlText.setText(packageHeaderDescription);
		xpdlDescription.setXpdlText(descriptionXpdlText);

		XpdlDocumentation xpdlDocumentation = new XpdlDocumentation("Documentation");
		XpdlText documentationXpdlText = new XpdlText("XpdlText");
		documentationXpdlText.setText(packageHeaderDocumentation);
		xpdlDocumentation.setXpdlText(documentationXpdlText);

		packageHeader.setVersion(xpdlXpdlVersion);
		packageHeader.setVendor(xpdlVendor);
		packageHeader.setCreated(created);
		packageHeader.setDescription(xpdlDescription);
		packageHeader.setDocumentation(xpdlDocumentation);
		return packageHeader;
	}

}
