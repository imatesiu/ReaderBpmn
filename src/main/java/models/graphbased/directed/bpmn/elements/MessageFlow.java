package models.graphbased.directed.bpmn.elements;

import java.awt.Graphics2D;

import models.graphbased.AttributeMap;
import models.graphbased.AttributeMap.ArrowType;
import models.graphbased.directed.bpmn.BPMNEdge;
import models.graphbased.directed.bpmn.BPMNNode;
import models.shapes.Decorated;

public class MessageFlow extends BPMNEdge<BPMNNode, BPMNNode> implements Decorated {

	private IGraphElementDecoration decorator = null;

	public MessageFlow(BPMNNode source, BPMNNode target, String label) {
		super(source, target);
		fillAttributes(label);
	}

	public MessageFlow(BPMNNode source, BPMNNode target, SubProcess parentSubProcess, String label) {
		super(source, target, parentSubProcess);
		fillAttributes(label);
	}

	public MessageFlow(BPMNNode source, BPMNNode target, Swimlane parentSwimlane, String label) {
		super(source, target, parentSwimlane);
		fillAttributes(label);
	}

	/**
	 * 
	 */
	private void fillAttributes(String label) {
		getAttributeMap().put(AttributeMap.EDGESTART, ArrowType.ARROWTYPE_CIRCLE);
		getAttributeMap().put(AttributeMap.EDGESTARTFILLED, false);
		getAttributeMap().put(AttributeMap.EDGEEND, ArrowType.ARROWTYPE_SIMPLE);
		getAttributeMap().put(AttributeMap.EDGEENDFILLED, false);
		getAttributeMap().put(AttributeMap.LABEL, label);
		getAttributeMap().put(AttributeMap.SHOWLABEL, true);
		getAttributeMap().put(AttributeMap.DASHPATTERN, new float[] { (float)3.0, (float)3.0 });
	}

	public Swimlane getParentSwimlane() {
		if (getParent() != null) {
			if (getParent() instanceof Swimlane)
				return (Swimlane) getParent();
			else
				return null;
		}
		return null;
	}

	public SubProcess getParentSubProcess() {
		if (getParent() != null) {
			if (getParent() instanceof SubProcess)
				return (SubProcess) getParent();
			else
				return null;
		}
		return null;
	}

	public boolean equals(Object o) {
		return (o == this);
	}

	public IGraphElementDecoration getDecorator() {
		return decorator;
	}

	public void setDecorator(IGraphElementDecoration decorator) {
		this.decorator = decorator;
	}

	public void decorate(Graphics2D g2d, double x, double y, double width, double height) {
		if (decorator != null) {
			decorator.decorate(g2d, x, y, width, height);
		}
	}
}
