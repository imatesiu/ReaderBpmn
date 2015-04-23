package plugins.bpmn;

import models.graphbased.directed.bpmn.elements.Event.EventType;

public class BpmnStartEvent extends BpmnEvent {

	
	public BpmnStartEvent(String tag) {
		super(tag, EventType.START);
	}
}
