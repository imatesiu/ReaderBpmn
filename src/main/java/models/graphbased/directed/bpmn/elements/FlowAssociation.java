

package models.graphbased.directed.bpmn.elements;

import java.awt.Graphics2D;

import models.graphbased.AttributeMap;
import models.graphbased.AttributeMap.ArrowType;
import models.graphbased.directed.bpmn.BPMNEdge;
import models.graphbased.directed.bpmn.BPMNNode;
import models.shapes.Decorated;

public class FlowAssociation extends BPMNEdge<BPMNNode, BPMNNode> implements Decorated{

	private IGraphElementDecoration decorator = null;

	public FlowAssociation(BPMNNode source, BPMNNode target) {
		super(source, target);
		fillAttributes();
		
	}
	public FlowAssociation(BPMNNode source, BPMNNode target, SubProcess parent) {
		super(source, target, parent);
		fillAttributes();
		
	}
	public FlowAssociation(BPMNNode source, BPMNNode target, Swimlane parent) {
		super(source, target, parent);
		fillAttributes();
		
	}
	
	
	private void fillAttributes() {
		float[] v = new float[2];
        v[0] = 1;
        v[1] = 3;
		getAttributeMap().put(AttributeMap.EDGEEND, ArrowType.ARROWTYPE_NONE);
		getAttributeMap().put(AttributeMap.DASHPATTERN, v );
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

	
	public void decorate(Graphics2D g2d, double x, double y, double width,
			double height) {
		if (decorator != null) {
			decorator.decorate(g2d, x, y, width, height);
		}
		
	}

}
