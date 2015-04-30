package plugins.xpdl.export;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;


import models.graphbased.directed.bpmn.BPMNDiagram;
import plugins.xpdl.Xpdl;
import plugins.xpdl.converter.BPMN2XPDLConversion;


public class XPDLExport {

	public void export( BPMNDiagram bpmn, File file) throws IOException {

		BPMN2XPDLConversion xpdlConversion = new BPMN2XPDLConversion(bpmn);
		Xpdl xpdl = xpdlConversion.convert2XPDL();

		String text = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + xpdl.exportElement();
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
		bw.write(text);
		bw.close();
	}

	
	

	public void exportCLI( BPMNDiagram bpmn, File file) throws IOException {

		BPMN2XPDLConversion xpdlConversion = new BPMN2XPDLConversion(bpmn);
		Xpdl xpdl = xpdlConversion.convert2XPDL_noLayout();

		String text = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + xpdl.exportElement();
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
		bw.write(text);
		bw.close();
	}
}
