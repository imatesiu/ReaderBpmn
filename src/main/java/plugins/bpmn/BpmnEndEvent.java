package plugins.bpmn;

import models.graphbased.directed.bpmn.elements.Event.EventType;

public class BpmnEndEvent extends BpmnEvent {

	public BpmnEndEvent(String tag) {
		super(tag, EventType.END);
	}
}
