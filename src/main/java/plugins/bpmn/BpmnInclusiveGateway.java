package plugins.bpmn;

import java.util.Collection;
import java.util.Map;

import models.graphbased.directed.bpmn.BPMNDiagram;
import models.graphbased.directed.bpmn.BPMNNode;
import models.graphbased.directed.bpmn.elements.Gateway;
import models.graphbased.directed.bpmn.elements.Gateway.GatewayType;
import models.graphbased.directed.bpmn.elements.SubProcess;
import models.graphbased.directed.bpmn.elements.Swimlane;

public class BpmnInclusiveGateway extends BpmnAbstractGateway {

	public BpmnInclusiveGateway(String tag) {
		super(tag);
	}
	
	public void unmarshall(BPMNDiagram diagram, Map<String, BPMNNode> id2node, Swimlane lane) {
		Gateway gateway = diagram.addGateway(name, GatewayType.INCLUSIVE, lane);
		gateway.getAttributeMap().put("Original id", id);
		id2node.put(id, gateway);
	}

	public void unmarshall(BPMNDiagram diagram, Collection<String> elements, Map<String, BPMNNode> id2node, Swimlane lane) {
		if (elements.contains(id)) {
			Gateway gateway = diagram.addGateway(name, GatewayType.INCLUSIVE, lane);
			gateway.getAttributeMap().put("Original id", id);
			id2node.put(id, gateway);
		}
	}
	
	public void unmarshall(BPMNDiagram diagram, Map<String, BPMNNode> id2node, SubProcess subProcess) {
		Gateway gateway = diagram.addGateway(name, GatewayType.INCLUSIVE, subProcess);
		gateway.getAttributeMap().put("Original id", id);
		id2node.put(id, gateway);
	}

	public void unmarshall(BPMNDiagram diagram, Collection<String> elements, Map<String, BPMNNode> id2node, SubProcess subProcess) {
		if (elements.contains(id)) {
			Gateway gateway = diagram.addGateway(name, GatewayType.INCLUSIVE, subProcess);
			gateway.getAttributeMap().put("Original id", id);
			id2node.put(id, gateway);
		}
	}
}
