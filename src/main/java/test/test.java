package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collection;

import models.graphbased.directed.bpmn.BPMNDiagram;
import plugins.bpmn.Bpmn;

public class test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		try {
			
			//File file = new File("esempi/AB_CC_Compatto.2.bpmn");
			File file = new File("esempi/cc2.bpmn");
			Bpmn bpmn = new Bpmn(file);
			
			Collection<BPMNDiagram> BPMNdiagrams = 	bpmn.BpmnextractDiagram();
			for(BPMNDiagram bpmnd : BPMNdiagrams){
			System.out.println(bpmnd);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
