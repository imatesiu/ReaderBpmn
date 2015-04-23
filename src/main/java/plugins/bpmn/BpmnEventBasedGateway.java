package plugins.bpmn;

import java.util.Collection;
import java.util.Map;

import models.graphbased.directed.bpmn.BPMNDiagram;
import models.graphbased.directed.bpmn.BPMNNode;
import models.graphbased.directed.bpmn.elements.Gateway;
import models.graphbased.directed.bpmn.elements.Swimlane;

/**
 * Created by napoli on 7/08/14.
 */
public class BpmnEventBasedGateway extends BpmnAbstractGateway{

    public BpmnEventBasedGateway(String tag) {
        super(tag);
    }

    @Override
    public void unmarshall(BPMNDiagram diagram, Map<String, BPMNNode> id2node, Swimlane lane) {
        Gateway gateway = diagram.addGateway(name, Gateway.GatewayType.EVENTBASED, lane);
        gateway.getAttributeMap().put("Original id", id);
        id2node.put(id, gateway);
    }

    @Override
    public void unmarshall(BPMNDiagram diagram, Collection<String> elements, Map<String, BPMNNode> id2node, Swimlane lane) {
        if (elements.contains(id)) {
            Gateway gateway = diagram.addGateway(name, Gateway.GatewayType.EVENTBASED, lane);
            gateway.getAttributeMap().put("Original id", id);
            id2node.put(id, gateway);
        }
    }
}
