package plugins.bpmn;

import java.util.Collection;
import java.util.Map;

import models.graphbased.directed.bpmn.BPMNDiagram;
import models.graphbased.directed.bpmn.BPMNNode;
import models.graphbased.directed.bpmn.elements.Association;
import org.xmlpull.v1.XmlPullParser;


public class BpmnAssociation extends BpmnFlow {

	public enum AssociationDirection {NONE, ONE, BOTH};
	
	private AssociationDirection direction;
	
	public BpmnAssociation(String tag) {
		super(tag);
	}
	
	public void unmarshall(BPMNDiagram diagram, Map<String, BPMNNode> id2node) {
		Association association = diagram.addAssociation(id2node.get(sourceRef), id2node.get(targetRef), direction);
		association.getAttributeMap().put("Original id", id);
		association.setDirection(direction);
	}

	public void unmarshall(BPMNDiagram diagram, Collection<String> elements, Map<String, BPMNNode> id2node) {
		if (elements.contains(sourceRef) && elements.contains(targetRef)) {
			Association association = diagram.addAssociation(id2node.get(sourceRef), id2node.get(targetRef), direction);
			association.getAttributeMap().put("Original id", id);
			association.setDirection(direction);
		}
	}
	
	public void marshall(Association association) {
		super.marshall(association);
		direction = association.getDirection();
	}
	
	public void setDirection(AssociationDirection direction) {
		this.direction = direction;
	}
	
	protected void importAttributes(XmlPullParser xpp, Bpmn bpmn) {
		super.importAttributes(xpp, bpmn);
		String value = xpp.getAttributeValue(null, "associationDirection");
		if (value != null) {
			direction = AssociationDirection.valueOf(value.toUpperCase());
		}
	}
	
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (direction != null) {
			s += exportAttribute("associationDirection", direction.toString().substring(0, 1).toUpperCase() 
					+ direction.toString().substring(1));
		}
		return s;
	}
}
