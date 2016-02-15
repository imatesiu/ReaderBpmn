package plugins.bpmn;

import java.util.Collection;
import java.util.Map;

import models.graphbased.directed.bpmn.BPMNDiagram;
import models.graphbased.directed.bpmn.BPMNNode;
import models.graphbased.directed.bpmn.elements.Flow;

public class BpmnSequenceFlow extends BpmnFlow {
	
	String conditionExpression;
	
	public BpmnSequenceFlow(String tag) {
		super(tag);
	}
	
	public Flow unmarshall(BPMNDiagram diagram, Map<String, BPMNNode> id2node) {
		Flow flow = diagram.addFlow(id2node.get(sourceRef), id2node.get(targetRef), name);
		flow.getAttributeMap().put("Original id", id);
		flow.setConditionExpression(conditionExpression);
		id2node.put(id, flow.getTarget());
		return flow;
	}

	public Flow unmarshall(BPMNDiagram diagram, Collection<String> elements,
			Map<String, BPMNNode> id2node) {
		if (elements.contains(sourceRef) && elements.contains(targetRef)) {
			Flow flow = diagram.addFlow(id2node.get(sourceRef), id2node.get(targetRef), name);
			flow.getAttributeMap().put("Original id", id);
			flow.setConditionExpression(conditionExpression);
			//id2node.put(id, flow);
			return flow;
		}
		return null;
	}
	
	public void marshall(Flow flow) {
		super.marshall(flow);
		conditionExpression = flow.getConditionExpression();
	}
	
	public void setConditionExpression(String conditionExpression) {
		this.conditionExpression = conditionExpression;
	}
	
	public String getConditionExpression() {
		return conditionExpression;
	}
}
