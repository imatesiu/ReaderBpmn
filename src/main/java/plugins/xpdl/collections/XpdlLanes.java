package plugins.xpdl.collections;

import java.util.Map;

import models.graphbased.directed.bpmn.BPMNDiagram;
import models.graphbased.directed.bpmn.BPMNNode;
import plugins.xpdl.idname.XpdlLane;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="Lanes"> <xsd:annotation>
 *         <xsd:documentation>BPMN</xsd:documentation> </xsd:annotation>
 *         <xsd:complexType> <xsd:sequence> <xsd:element ref="xpdl:Lane"
 *         minOccurs="0" maxOccurs="unbounded"/> <xsd:any namespace="##other"
 *         processContents="lax" minOccurs="0" maxOccurs="unbounded"/>
 *         </xsd:sequence> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:complexType> </xsd:element>
 */
public class XpdlLanes extends XpdlCollections<XpdlLane> {

	public XpdlLanes(String tag) {
		super(tag);
	}

	public XpdlLane create() {
		return new XpdlLane("Lane");
	}

	public void convertToBpmn(BPMNDiagram bpmn, Map<String, BPMNNode> id2node) {
		if (!list.isEmpty()) {
			for (XpdlLane lane : list) {
				lane.convertToBpmn(bpmn, id2node);
			}
		}
	}
}
