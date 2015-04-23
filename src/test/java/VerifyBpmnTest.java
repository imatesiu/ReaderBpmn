import static org.junit.Assert.*;

import java.io.File;
import java.util.Collection;

import models.graphbased.directed.bpmn.BPMNDiagram;

import org.junit.Test;

import plugins.bpmn.Bpmn;


public class VerifyBpmnTest {

	@Test
	public void test() {
		//fail("Not yet implemented");
		assertEquals(0, 0);
		
try {
			
			//File file = new File("esempi/AB_CC_Compatto.2.bpmn");
			File file = new File("esempi/cc2.bpmn");
			Bpmn bpmn = new Bpmn(file);
			
			Collection<BPMNDiagram> BPMNdiagrams = 	bpmn.BpmnextractDiagram();
			for(BPMNDiagram bpmnd : BPMNdiagrams){
			System.out.println(bpmnd);
			}
			
		} catch (Exception e) {
			
			
			fail("Not good "+e.getMessage());
		}
	}

}
