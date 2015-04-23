package models.graphbased.directed.bpmn.elements;

import java.awt.Graphics2D;

import models.graphbased.AttributeMap;
import models.graphbased.AttributeMap.ArrowType;
import models.graphbased.directed.bpmn.BPMNEdge;
import models.graphbased.directed.bpmn.BPMNNode;
import models.shapes.Decorated;

public class DataAssociation extends BPMNEdge<BPMNNode, BPMNNode> implements Decorated {

	private IGraphElementDecoration decorator = null;
	
	public DataAssociation(BPMNNode source, BPMNNode target, String label) {
		super(source, target);
		fillAttributes(label);
	}
	
	private void fillAttributes(String label) {
		getAttributeMap().put(AttributeMap.EDGEEND, ArrowType.ARROWTYPE_SIMPLE);
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
}
