package plugins.bpmn;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.jgraph.graph.AbstractCellView;
import org.jgraph.graph.DefaultGraphCell;

import models.graphbased.directed.DirectedGraphNode;
import models.graphbased.directed.bpmn.BPMNDiagram;
import models.graphbased.directed.bpmn.BPMNEdge;
import models.graphbased.directed.bpmn.BPMNNode;
import models.graphbased.directed.bpmn.elements.Association;
import models.graphbased.directed.bpmn.elements.MessageFlow;
import models.graphbased.directed.bpmn.elements.SubProcess;
import models.graphbased.directed.bpmn.elements.Swimlane;
import models.graphbased.directed.bpmn.elements.TextAnnotation;

import models.jgraph.views.JGraphPortView;

import plugins.bpmn.diagram.BpmnDcBounds;
import plugins.bpmn.diagram.BpmnDiEdge;
import plugins.bpmn.diagram.BpmnDiPlane;
import plugins.bpmn.diagram.BpmnDiShape;
import plugins.bpmn.diagram.BpmnDiWaypoint;
import plugins.bpmn.diagram.BpmnDiagram;
import org.xmlpull.v1.XmlPullParser;

public class BpmnDefinitions extends BpmnElement {

	private Collection<BpmnResource> resources;
	private Collection<BpmnProcess> processes;
	private Collection<BpmnCollaboration> collaborations;
	private Collection<BpmnMessage> messages;	
	private Collection<BpmnDiagram> diagrams;
	
	public BpmnDefinitions(String tag) {
		super(tag);
		resources = new HashSet<BpmnResource>();
		processes = new HashSet<BpmnProcess>();
		collaborations = new HashSet<BpmnCollaboration>();
		messages = new HashSet<BpmnMessage>();
		diagrams = new HashSet<BpmnDiagram>();
	}
	
	public BpmnDefinitions(String tag, BpmnDefinitionsBuilder builder) {
		super(tag);
		resources = builder.resources;
		processes = builder.processes;
		collaborations = builder.collaborations;
		messages = new HashSet<BpmnMessage>();		
		diagrams = builder.diagrams;
	}
	
	/**
	 * Builds a BPMN model (BpmnDefinitions) from BPMN diagram
	 *
	 * @author Anna Kalenkova
	 * Sep 22, 2013
	 */
	public static class BpmnDefinitionsBuilder {

		private Collection<BpmnResource> resources;
		private Collection<BpmnProcess> processes;
		private Collection<BpmnCollaboration> collaborations;	
		private Collection<BpmnDiagram> diagrams;
		
		public BpmnDefinitionsBuilder( BPMNDiagram diagram) {
			resources = new HashSet<BpmnResource>();
			processes = new HashSet<BpmnProcess>();
			collaborations = new HashSet<BpmnCollaboration>();
			diagrams = new HashSet<BpmnDiagram>();

			buildFromDiagram(diagram);
		}

		/**
		 * Build BpmnDefinitions from BPMNDiagram (BPMN picture)
		 * 
		 * @param context if null, then graphics info is not added
		 * @param diagram
		 */
		private void buildFromDiagram( BPMNDiagram diagram) {
			BpmnCollaboration bpmnCollaboration = new BpmnCollaboration("collaboration");
			bpmnCollaboration.setId("col_" + bpmnCollaboration.hashCode());

			// Build pools and participants
			for (Swimlane pool : diagram.getPools()) {
				BpmnParticipant bpmnParticipant = new BpmnParticipant("participant");
				bpmnParticipant.id = pool.getId().toString().replace(' ', '_');
				bpmnParticipant.name = pool.getLabel();
				// If pool is not a "black box", create a process
				if (!pool.getChildren().isEmpty()) {
					BpmnProcess bpmnProcess = new BpmnProcess("process");
					bpmnProcess.marshall(diagram, pool);
					bpmnProcess.setId("proc_" + bpmnProcess.hashCode());
					processes.add(bpmnProcess);
					bpmnParticipant.setProcessRef(bpmnProcess.getId());
				}
				bpmnCollaboration.addParticipant(bpmnParticipant);
			}

			// Discover "internal" process
			BpmnProcess intBpmnProcess = new BpmnProcess("process");
			intBpmnProcess.setId("proc_" + intBpmnProcess.hashCode());
			// If there are elements without parent pool, add process
			if (intBpmnProcess.marshall(diagram, null)) {
				processes.add(intBpmnProcess);
			}

			// Build message flows
			for (MessageFlow messageFlow : diagram.getMessageFlows()) {
				BpmnMessageFlow bpmnMessageFlow = new BpmnMessageFlow("messageFlow");
				bpmnMessageFlow.marshall(messageFlow);
				bpmnCollaboration.addMessageFlow(bpmnMessageFlow);
			}
			
			// Build text annotations
			for (TextAnnotation textAnnotation : diagram.getTextAnnotations(null)) {
				BpmnTextAnnotation bpmnTextAnnotation = new BpmnTextAnnotation("textAnnotation");
				bpmnTextAnnotation.marshall(textAnnotation);
				bpmnCollaboration.addTextAnnotation(bpmnTextAnnotation);;
			}
			
			// Build associations
			for (Association association : diagram.getAssociations(null)) {
				BpmnAssociation bpmnAssociation = new BpmnAssociation("association");
				bpmnAssociation.marshall(association);
				bpmnCollaboration.addAssociation(bpmnAssociation);
			}
			
			// Build resources
			for(Swimlane swimlane : diagram.getSwimlanes()) {
				if(swimlane.getPartitionElement() != null) {
					BpmnResource resource = new BpmnResource("resource");
					resource.marshall(swimlane);
					resources.add(resource);
				}
			}
			
			// Build graphics info
			BpmnDiagram bpmnDiagram = new BpmnDiagram("bpmndi:BPMNDiagram");
			bpmnDiagram.setId("id_" + diagram.hashCode());
			BpmnDiPlane plane = new BpmnDiPlane("bpmndi:BPMNPlane");
			if (diagram.getPools().size() > 0) {
				collaborations.add(bpmnCollaboration);
				plane.setBpmnElement(bpmnCollaboration.id);
			}
			else {
				plane.setBpmnElement(intBpmnProcess.id);
			}
			bpmnDiagram.addPlane(plane);
			

			diagrams.add(bpmnDiagram);
		}

		/**
		 * Fill graphics info
		 * 
		 * @param context
		 * @param diagram
		 * @param bpmnDiagram
		 * @param plane
		 */
		private synchronized static void fillGraphicsInfo(BPMNDiagram diagram,
				BpmnDiagram bpmnDiagram, BpmnDiPlane plane) {

			
		}
		
		/**
		 * Retrieve graphics info from graphCell
		 * 
		 * @param graphCell
		 * @param plane
		 */
		private static void addCellGraphicsInfo(DefaultGraphCell graphCell, BpmnDiPlane plane) {
			DirectedGraphNode graphNode = null;
		
			// Create BPMNShape
			String bpmnElement = graphNode.getId().toString().replace(' ', '_');
			boolean isExpanded = false;
			boolean isHorizontal = false;
			if(graphNode instanceof SubProcess) {
				SubProcess subProcess = (SubProcess)graphNode;
				if(!subProcess.isBCollapsed()) {
					isExpanded = true;
				}
			}
			if(graphNode instanceof Swimlane) {				
				isExpanded = true;
				isHorizontal = true;
			}
			AbstractCellView view = null;
			
			Rectangle2D rectangle = view.getBounds();
			
			double x = rectangle.getX();
			double y = rectangle.getY();
			double width = rectangle.getWidth();
			double height = rectangle.getHeight();
			
			BpmnDcBounds bounds = new BpmnDcBounds("dc:Bounds", x, y, width, height);
			BpmnDiShape shape = new BpmnDiShape("bpmndi:BPMNShape", bpmnElement, bounds, isExpanded, isHorizontal);
			plane.addShape(shape);
			
		}
	}
		
		/**
		 * Retrieve graphics info from graphEdge
		 * 
		 * @param graphEdge
		 * @param plane
		 */
		
	protected boolean importElements(XmlPullParser xpp, Bpmn bpmn) {
		if (super.importElements(xpp, bpmn)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("process")) {
			BpmnProcess process = new BpmnProcess("process");
			process.importElement(xpp, bpmn);
			processes.add(process);
			return true;
		} else if (xpp.getName().equals("collaboration")) {
			BpmnCollaboration collaboration = new BpmnCollaboration("collaboration");
			collaboration.importElement(xpp, bpmn);
			collaborations.add(collaboration);
			return true;
		} else if (xpp.getName().equals("message")) {
			BpmnMessage message = new BpmnMessage("message");
			message.importElement(xpp, bpmn);
			messages.add(message);
			return true; 
		} else if (xpp.getName().equals("resource")) {
			BpmnResource resource = new BpmnResource("resource");
			resource.importElement(xpp, bpmn);
			resources.add(resource);
			return true;
		} else if (xpp.getName().equals("BPMNDiagram")) {
			BpmnDiagram diagram = new BpmnDiagram("BPMNDiagram");
			diagram.importElement(xpp, bpmn);
			diagrams.add(diagram);
			return true;
		}
		/*
		 * Unknown tag.
		 */
		return false;
	}
	
	public String exportElements() {
		/*
		 * Export node child elements.
		 */
		String s = super.exportElements();
        for (BpmnCollaboration collaboration : collaborations) {
            s += collaboration.exportElement();
        }
		for (BpmnResource resource : resources) {
			s += resource.exportElement();
		}
		for (BpmnProcess process : processes) {
			s += process.exportElement();
		}
		for (BpmnMessage message : messages) {
			s += message.exportElement();
		}
		for (BpmnDiagram diagram : diagrams) {
			s += diagram.exportElement();
		}
		return s;
	}
	
	public void unmarshall(BPMNDiagram diagram, Map<String, BPMNNode> id2node, Map<String, Swimlane> id2lane) {
		for (BpmnCollaboration collaboration : collaborations) {
			collaboration.unmarshallParticipants(diagram, id2node, id2lane);
		}
		for (BpmnProcess process : processes) {
			process.unmarshall(diagram, id2node, id2lane);
		}
		for (BpmnCollaboration collaboration : collaborations) {
			collaboration.unmarshallMessageFlows(diagram, id2node);
			collaboration.unmarshallTextAnnotations(diagram, id2node);
			collaboration.unmarshallAssociations(diagram, id2node);
		}
		for (BpmnDiagram bpmnDiagram : diagrams) {
			bpmnDiagram.unmarshallIsExpanded(id2node);
		}
	}

	public void unmarshall(BPMNDiagram diagram, Collection<String> elements, Map<String, BPMNNode> id2node, Map<String, Swimlane> id2lane) {
		for (BpmnCollaboration collaboration : collaborations) {
			collaboration.unmarshallParticipants(diagram, elements, id2node, id2lane);
		}
		for (BpmnProcess process : processes) {
			process.unmarshall(diagram, elements, id2node, id2lane);
		}
		for (BpmnCollaboration collaboration : collaborations) {
			collaboration.unmarshallMessageFlows(diagram, elements, id2node);
			collaboration.unmarshallTextAnnotations(diagram, elements, id2node);
			collaboration.unmarshallAssociations(diagram, elements, id2node);
		}
		for (BpmnDiagram bpmnDiagram : diagrams) {
			bpmnDiagram.unmarshallIsExpanded(id2node);
		}
	}
	
	public Collection<BpmnResource> getResources() {
		return resources;
	}
	
	public Collection<BpmnProcess> getProcesses() {
		return processes;
	}

	public Collection<BpmnCollaboration> getCollaborations() {
		return collaborations;
	}
	
	public Collection<BpmnMessage> getMessages() {
		return messages;
	}
	
	public Collection<BpmnDiagram> getDiagrams() {
		return diagrams;
	}
}
