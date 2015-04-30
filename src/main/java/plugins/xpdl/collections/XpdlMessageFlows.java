package plugins.xpdl.collections;

import java.util.Map;

import models.graphbased.directed.bpmn.BPMNDiagram;
import models.graphbased.directed.bpmn.BPMNNode;
import plugins.xpdl.idname.XpdlMessageFlow;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="MessageFlows"> <xsd:annotation>
 *         <xsd:documentation>BPMN</xsd:documentation> </xsd:annotation>
 *         <xsd:complexType> <xsd:sequence minOccurs="0" maxOccurs="unbounded">
 *         <xsd:element ref="xpdl:MessageFlow"/> <xsd:any namespace="##other"
 *         processContents="lax" minOccurs="0" maxOccurs="unbounded"/>
 *         </xsd:sequence> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:complexType> </xsd:element>
 */
public class XpdlMessageFlows extends XpdlCollections<XpdlMessageFlow> {

	public XpdlMessageFlows(String tag) {
		super(tag);
	}

	public XpdlMessageFlow create() {
		return new XpdlMessageFlow("MessageFlow");
	}
	
	public void convertToBpmn(BPMNDiagram bpmn, Map<String, BPMNNode> id2node) {
		for (XpdlMessageFlow messageFlow: list) {
			messageFlow.convertToBpmn(bpmn, id2node);
		}
	}
}
