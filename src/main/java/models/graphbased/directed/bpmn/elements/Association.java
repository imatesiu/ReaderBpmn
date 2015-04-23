package models.graphbased.directed.bpmn.elements;

import java.awt.Graphics2D;

import models.graphbased.AttributeMap;
import models.graphbased.AttributeMap.ArrowType;
import models.graphbased.directed.bpmn.BPMNEdge;
import models.graphbased.directed.bpmn.BPMNNode;
import models.shapes.Decorated;
import plugins.bpmn.BpmnAssociation.AssociationDirection;

public class Association extends BPMNEdge<BPMNNode, BPMNNode> implements Decorated {
	
	private IGraphElementDecoration decorator = null;

	private AssociationDirection direction;
	
	public Association(BPMNNode source, BPMNNode target, AssociationDirection direction) {
		super(source, target);
		this.direction = direction;
		fillAttributes();
	}

	private void fillAttributes() {
		switch(direction) {
			case BOTH: 
				getAttributeMap().put(AttributeMap.EDGESTART, ArrowType.ARROWTYPE_SIMPLE);
				//$FALL-THROUGH$
			case ONE:
				getAttributeMap().put(AttributeMap.EDGESTART, ArrowType.ARROWTYPE_SIMPLE);
				//$FALL-THROUGH$
			default :
				break;
		}
		getAttributeMap().put(AttributeMap.EDGESTARTFILLED, false);
		getAttributeMap().put(AttributeMap.EDGEENDFILLED, false);	
		getAttributeMap().put(AttributeMap.DASHPATTERN, new float[] { (float)2.0, (float)2.0 });
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
	
	public AssociationDirection getDirection() {
		return direction;
	}
	
	public void setDirection(AssociationDirection direction) {
		this.direction = direction;
	}
}
