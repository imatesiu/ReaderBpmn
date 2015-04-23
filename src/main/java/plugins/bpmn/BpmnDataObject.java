package plugins.bpmn;

import java.util.Collection;
import java.util.Map;

import models.graphbased.directed.bpmn.BPMNDiagram;
import models.graphbased.directed.bpmn.BPMNNode;
import models.graphbased.directed.bpmn.elements.DataObject;
import models.graphbased.directed.bpmn.elements.Swimlane;
import org.xmlpull.v1.XmlPullParser;


public class BpmnDataObject extends BpmnIdName {

    private String dataObject;

	public BpmnDataObject(String tag) {
		super(tag);
	}

    protected void importAttributes(XmlPullParser xpp, Bpmn bpmn) {
        super.importAttributes(xpp, bpmn);
        String value = xpp.getAttributeValue(null, "dataObject");
        if (value != null) {
            dataObject = value;
        }
    }

    protected String exportAttributes() {
        String s = super.exportAttributes();
        if (dataObject != null) {
            s += exportAttribute("dataObject", dataObject);
        }
        return s;
    }

    public void unmarshall(BPMNDiagram diagram, Map<String, BPMNNode> id2node) {
//        DataObject dataObject = diagram.addDataObject(name);
//        dataObject.getAttributeMap().put("Original id", id);
//        id2node.put(id, diagram.addDataObject(name));
    }

    public void unmarshall(BPMNDiagram diagram, Collection<String> elements, Map<String, BPMNNode> id2node, Swimlane lane) {
//        if (elements.contains(id)) {
//            DataObject dataObject = diagram.addDataObject(name);
//            dataObject.getAttributeMap().put("Original id", id);
//            id2node.put(id, dataObject);
//        }
    }

    public void marshall(DataObject dataObject) {
        super.marshall(dataObject);
    }
}
