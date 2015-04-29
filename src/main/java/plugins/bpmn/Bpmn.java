package plugins.bpmn;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import plugins.bpmn.Bpmn;
import models.graphbased.directed.bpmn.BPMNDiagram;
import models.graphbased.directed.bpmn.BPMNDiagramFactory;
import models.graphbased.directed.bpmn.BPMNNode;
import models.graphbased.directed.bpmn.elements.Swimlane;
import plugins.bpmn.diagram.BpmnDiagram;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;



public class Bpmn extends BpmnDefinitions {

	
	Collection<String> log;
	boolean hasErrors;
	boolean hasInfos;

	public Bpmn(File file) {
		super("definitions");
		log = new ArrayList<String>();
		
		try {
			InputStream targetStream = new FileInputStream(file);
			importBpmnFromStream(targetStream,file.getName(), file.length());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	public Collection<BPMNDiagram> BpmnextractDiagram(){
		Collection<BPMNDiagram> newDiagrams = new ArrayList<BPMNDiagram>();
		for(BpmnDiagram  diagram: this.getDiagrams()){
			BPMNDiagram newDiagram = BPMNDiagramFactory.newBPMNDiagram(diagram.name);
			Map<String, BPMNNode> id2node = new HashMap<String, BPMNNode>();
			Map<String, Swimlane> id2lane = new HashMap<String, Swimlane>();
			Collection<String> elements = diagram.getElements();
			this.unmarshall(newDiagram, elements, id2node, id2lane);
			newDiagrams.add(newDiagram);
		}
		return newDiagrams;
	}
	
	private void importBpmnFromStream( InputStream input, String filename, long fileSizeInBytes) 
			 {
		
		try{
		/*
		 * Get an XML pull parser.
		 */
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xpp = factory.newPullParser();
		/*
		 * Initialize the parser on the provided input.
		 */
		xpp.setInput(input, null);
		/*
		 * Get the first event type.
		 */
		int eventType = xpp.getEventType();
		/*
		 * Create a fresh PNML object.
		 */
		Bpmn bpmn = this;

		/*
		 * Skip whatever we find until we've found a start tag.
		 */
		while (eventType != XmlPullParser.START_TAG) {
			eventType = xpp.next();
		}
		/*
		 * Check whether start tag corresponds to PNML start tag.
		 */
		if (xpp.getName().equals(bpmn.tag)) {
			/*
			 * Yes it does. Import the PNML element.
			 */
			bpmn.importElement(xpp, bpmn);
		} else {
			/*
			 * No it does not. Return null to signal failure.
			 */
			bpmn.log(bpmn.tag, xpp.getLineNumber(), "Expected " + bpmn.tag + ", got " + xpp.getName());
		}
		if (bpmn.hasErrors()) {
			System.out.println("Log of BPMN import"+ bpmn.getLog());
			//return null;
		}
		
		}catch(Exception e){
			
		}
	//	return bpmn;
	}

	
	
	/**
	 * Creates and initializes a log to throw to the framework when importing
	 * the XPDL file fails.
	 */
	

	private String getLog() {
		// TODO Auto-generated method stub
		return log.toString();
	}


	/**
	 * Adds a log event to the current trace in the log.
	 * 
	 * @param context
	 *            Context of the message, typically the current PNML tag.
	 * @param lineNumber
	 *            Current line number.
	 * @param message
	 *            Error message.
	 */
	public void log(String context, int lineNumber, String message) {
		log.add(context+";"+lineNumber+";"+message);
		hasErrors = true;
		System.out.println(message);
	}



	/**
	 * Adds a new trace with the given name to the log. This trace is now
	 * current.
	 * 
	 * @param name
	 *            The give name.
	 */
	

	public boolean hasErrors() {
		return hasErrors;
	}

	public boolean hasInfos() {
		return hasInfos;
	}
}
