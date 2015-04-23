package models.graphbased.directed.bpmn.elements;

import java.awt.Graphics2D;

import models.graphbased.AttributeMap;
import models.graphbased.AttributeMap.ArrowType;
import models.graphbased.directed.ContainableDirectedGraphElement;
import models.graphbased.directed.ContainingDirectedGraphNode;
import models.graphbased.directed.bpmn.BPMNEdge;
import models.graphbased.directed.bpmn.BPMNNode;
import models.shapes.Decorated;

public class Flow extends BPMNEdge<BPMNNode, BPMNNode> implements Decorated {
	
	private IGraphElementDecoration decorator = null;
	
	private String conditionExpression;
	
	private String textAnnotation;

	public Flow(BPMNNode source, BPMNNode target, String label) {
		super(source, target);
		fillAttributes(label);
	}

	@Deprecated
	public Flow(BPMNNode source, BPMNNode target, SubProcess parentSubProcess, String label) {
		super(source, target, parentSubProcess);
		fillAttributes(label);
	}

	@Deprecated
	public Flow(BPMNNode source, BPMNNode target, Swimlane parentSwimlane, String label) {
		super(source, target, parentSwimlane);
		fillAttributes(label);
	}


	private void fillAttributes(String label) {
		getAttributeMap().put(AttributeMap.EDGEEND, ArrowType.ARROWTYPE_CLASSIC);
		getAttributeMap().put(AttributeMap.EDGEENDFILLED, true);
		if (label == null) {
			getAttributeMap().put(AttributeMap.SHOWLABEL, false);
		} else {
			getAttributeMap().put(AttributeMap.LABEL, label);
			getAttributeMap().put(AttributeMap.SHOWLABEL, true);
		}
	}

	@Deprecated
	public Swimlane getParentSwimlane() {
		if (getParent() != null) {
			if (getParent() instanceof Swimlane)
				return (Swimlane) getParent();
			else
				return null;
		}
		return null;
	}
	
	@Deprecated
	public Swimlane getParentPool() {
		ContainingDirectedGraphNode parent = getParent();
		while (parent != null) {
			if ((parent instanceof Swimlane) 
					&& ((Swimlane)parent).getSwimlaneType().equals(SwimlaneType.POOL)) {
				return (Swimlane) parent;
			}
			else {
				if(parent instanceof ContainableDirectedGraphElement) {
					parent = ((ContainableDirectedGraphElement)parent).getParent();
				} else {
					return null;
				}
			}
		}
		return null;
	}

	@Deprecated
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
	
	public void setConditionExpression(String conditionExpression) {
		this.conditionExpression = conditionExpression;
	}
	
	public String getConditionExpression() {
		return conditionExpression;
	}
	
	public String getTextAnnotation() {
		return textAnnotation;
	}

	public void setTextAnnotation(String textAnnotation) {
		this.textAnnotation = textAnnotation;
	}
}
