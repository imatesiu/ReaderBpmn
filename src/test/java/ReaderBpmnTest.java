import static org.junit.Assert.*;

import java.io.File;
import java.util.Collection;

import models.graphbased.directed.bpmn.BPMNDiagram;

import org.junit.Test;

import plugins.bpmn.Bpmn;


public class ReaderBpmnTest {

	@Test
	public void test() {
		//fail("Not yet implemented");
		assertEquals(0, 0);
		
try {
			
			//File file = new File("esempi/AB_CC_Compatto.2.bpmn");
			File file = new File("esempi/cc2.bpmn");
			Bpmn bpmn = new Bpmn(file);
			
			Collection<BPMNDiagram> BPMNdiagrams = 	bpmn.BpmnextractDiagram();
			System.out.println("Number of Process: "+BPMNdiagrams.size());
			for(BPMNDiagram bpmnd : BPMNdiagrams){
				int numActivities =bpmnd.getActivities().size();
				System.out.println("Number of Activities: "+numActivities);
				int numEvents = bpmnd.getEvents().size();
				System.out.println("Number of Events: "+numEvents);
				int numGateways =bpmnd.getGateways().size();
				System.out.println("Number of Gateways: "+numGateways);
				int numSubProcesses =bpmnd.getSubProcesses().size();
				System.out.println("Number of SubProcesses: "+numSubProcesses);
				int numPools =bpmnd.getPools().size();
				
				System.out.println("Number of Pools: "+numPools);
				int numDataObjects = bpmnd.getDataObjects().size();
				System.out.println("Number of DataObjects: "+numDataObjects);
				
				
				int sum = numActivities + numEvents +numGateways;
				sum +=numPools +numSubProcesses +numDataObjects ;
				assertTrue("Test PASS!", sum>0);
				System.out.println();
			}
			
		} catch (Exception e) {
			
			
			fail("Not good "+e.getMessage());
		}
	}

}
