package eclipse.bpmn;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import models.graphbased.directed.bpmn.BPMNDiagram;
import models.graphbased.directed.bpmn.BPMNDiagramImpl;
import models.graphbased.directed.bpmn.BPMNNode;
import models.graphbased.directed.bpmn.elements.Activity;
import models.graphbased.directed.bpmn.elements.Event;
import models.graphbased.directed.bpmn.elements.Event.EventTrigger;
import models.graphbased.directed.bpmn.elements.Event.EventType;
import models.graphbased.directed.bpmn.elements.Event.EventUse;
import models.graphbased.directed.bpmn.elements.Gateway;
import models.graphbased.directed.bpmn.elements.Gateway.GatewayType;
import models.graphbased.directed.bpmn.elements.SubProcess;
import models.graphbased.directed.bpmn.elements.Swimlane;
import models.graphbased.directed.bpmn.elements.SwimlaneType;

import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.bpmn2.util.Bpmn2ResourceFactoryImpl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.IOWrappedException;
import org.eclipse.emf.ecore.xmi.XMLResource;

public class eBPMNReader {

	private static org.apache.log4j.Logger log = org.apache.log4j.Logger
			.getLogger(eBPMNReader.class);
	private Definitions diagram;

	public eBPMNReader(File file) {

		try {
			this.diagram = readJavaURIModel(file.toURI().toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error(e);
		}
	}

	public Definitions readStringModel(String theBPMNString) throws IOException {

		// create a temp file
		File temp = File.createTempFile("tempfile", ".tmp");
		temp.deleteOnExit();
		// write it
		BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
		bw.write(theBPMNString);
		bw.close();

		return readFileModel(temp.getAbsolutePath());

	}

	public Definitions readURIModel(URI uri) throws IOException {

		// URI uri = URI.createURI("SampleProcess.bpmn");
		Bpmn2ResourceFactoryImpl resFactory = new Bpmn2ResourceFactoryImpl();
		Resource resource = resFactory.createResource(uri);

		// We need this option because all object references in the file are
		// "by ID"
		// instead of the document reference "URI#fragment" form.
		HashMap<Object, Object> options = new HashMap<Object, Object>();
		options.put(XMLResource.OPTION_DEFER_IDREF_RESOLUTION, true);

		try {
			// Load the resource
			resource.load(options);
		} catch (IOWrappedException e) {

			log.error("\nModel involved in the exception:\n" + uri.toString()
					+ " " + e.getMessage());

			// e.printStackTrace();
		}
		// This is the root element of the XML document
		Definitions d = getDefinitions(resource);

		return d;

	}

	public Definitions readJavaURIModel(String theBPMNURIjava)
			throws IOException {

		URI uri = URI.createURI(theBPMNURIjava);

		return readURIModel(uri);

	}

	public Definitions readFileModel(String theBPMNFile) throws IOException {

		URI uri = URI.createFileURI(theBPMNFile);

		return readURIModel(uri);

	}

	public BPMNDiagram  getBPMNDiagram() {
		String label = "Unlabeled";
		if(diagram.getName()!=null)
			label = diagram.getName();
		BPMNDiagram bpmn = new BPMNDiagramImpl(label);
		Map<FlowElement, BPMNNode> mapEvsB = new HashMap<FlowElement, BPMNNode>();
		for (RootElement rootElement : diagram.getRootElements()) {
			if (rootElement instanceof Process) {
				Process process = (Process) rootElement;
				// System.out.format("Found a process: %s\n",
				// process.getName());
				Swimlane lane = null;
						//bpmn.addSwimlane(process.getName(), null, SwimlaneType.LANE);

				String IDProcess = process.getId();
				for (FlowElement fe : process.getFlowElements()) {
					if (fe instanceof org.eclipse.bpmn2.SubProcess) {
						org.eclipse.bpmn2.SubProcess sub = (org.eclipse.bpmn2.SubProcess) fe;
						SubProcess subn = bpmn.addSubProcess(fe.getName(),
								false, false, false, false, true, lane);
						mapEvsB.put(sub, subn);
					} else if (fe instanceof org.eclipse.bpmn2.Activity) {
						org.eclipse.bpmn2.Activity a = (org.eclipse.bpmn2.Activity) fe;
						Activity ac = bpmn.addActivity(a.getName(), false,
								false, false, false, false, lane);
						mapEvsB.put(a, ac);

					} else if (fe instanceof org.eclipse.bpmn2.StartEvent) {
						org.eclipse.bpmn2.StartEvent se = (org.eclipse.bpmn2.StartEvent) fe;
						Event sec = bpmn.addEvent(se.getName(),
								EventType.START, EventTrigger.NONE,
								EventUse.CATCH, lane, false, null);
						mapEvsB.put(se, sec);

					} else if (fe instanceof org.eclipse.bpmn2.EndEvent) {
						org.eclipse.bpmn2.EndEvent ee = (org.eclipse.bpmn2.EndEvent) fe;
						Event sec = bpmn.addEvent(ee.getName(), EventType.END,
								EventTrigger.NONE, EventUse.CATCH, lane, false,
								null);
						mapEvsB.put(ee, sec);

					} else if (fe instanceof org.eclipse.bpmn2.BoundaryEvent) {
						org.eclipse.bpmn2.BoundaryEvent ee = (org.eclipse.bpmn2.BoundaryEvent) fe;
						org.eclipse.bpmn2.Activity a = ee.getAttachedToRef();
						BPMNNode ref = mapEvsB.get(a);
						Event sec = bpmn.addEvent(ee.getName(),
								EventType.INTERMEDIATE, EventTrigger.ERROR,
								EventUse.CATCH, lane, false, (Activity) ref);
						mapEvsB.put(ee, sec);

					} else if (fe instanceof org.eclipse.bpmn2.IntermediateCatchEvent) {
						org.eclipse.bpmn2.IntermediateCatchEvent ee = (org.eclipse.bpmn2.IntermediateCatchEvent) fe;
						Event sec = bpmn.addEvent(ee.getName(),
								EventType.INTERMEDIATE, EventTrigger.ERROR,
								EventUse.CATCH, lane, false, null);
						mapEvsB.put(ee, sec);

					} else if (fe instanceof org.eclipse.bpmn2.IntermediateThrowEvent) {
						org.eclipse.bpmn2.IntermediateThrowEvent ee = (org.eclipse.bpmn2.IntermediateThrowEvent) fe;
						Event sec = bpmn.addEvent(ee.getName(),
								EventType.INTERMEDIATE, EventTrigger.NONE,
								EventUse.THROW, lane, false, null);
						mapEvsB.put(ee, sec);

					} else if (fe instanceof org.eclipse.bpmn2.ExclusiveGateway) {
						org.eclipse.bpmn2.ExclusiveGateway ee = (org.eclipse.bpmn2.ExclusiveGateway) fe;

						Gateway sec = bpmn.addGateway(ee.getName(),
								GatewayType.DATABASED, lane);
						mapEvsB.put(ee, sec);

					} else if (fe instanceof org.eclipse.bpmn2.InclusiveGateway) {
						org.eclipse.bpmn2.InclusiveGateway ee = (org.eclipse.bpmn2.InclusiveGateway) fe;

						Gateway sec = bpmn.addGateway(ee.getName(),
								GatewayType.INCLUSIVE, lane);
						mapEvsB.put(ee, sec);

					} else if (fe instanceof org.eclipse.bpmn2.EventBasedGateway) {
						org.eclipse.bpmn2.EventBasedGateway ee = (org.eclipse.bpmn2.EventBasedGateway) fe;

						Gateway sec = bpmn.addGateway(ee.getName(),
								GatewayType.EVENTBASED, lane);
						mapEvsB.put(ee, sec);

					} else if (fe instanceof org.eclipse.bpmn2.ParallelGateway) {
						org.eclipse.bpmn2.ParallelGateway ee = (org.eclipse.bpmn2.ParallelGateway) fe;

						Gateway sec = bpmn.addGateway(ee.getName(),
								GatewayType.PARALLEL, lane);
						mapEvsB.put(ee, sec);

					} 

				}
			}
		}
		for (RootElement rootElement : diagram.getRootElements()) {
			if (rootElement instanceof Process) {
				Process process = (Process) rootElement;
				for (FlowElement fe : process.getFlowElements()) {

					if (fe instanceof org.eclipse.bpmn2.SequenceFlow) {
						org.eclipse.bpmn2.SequenceFlow ee = (org.eclipse.bpmn2.SequenceFlow) fe;
						BPMNNode target = mapEvsB.get(ee.getTargetRef());
						BPMNNode source = mapEvsB.get(ee.getSourceRef());
						if(target!=null && source!=null)
							bpmn.addFlow(source, target, ee.getName()); // lane);
						else
							System.out.println("");

					} else if (fe instanceof org.eclipse.bpmn2.MessageFlow) {
						org.eclipse.bpmn2.MessageFlow ee = (org.eclipse.bpmn2.MessageFlow) fe;
						BPMNNode target = mapEvsB.get(ee.getTargetRef());
						BPMNNode source = mapEvsB.get(ee.getSourceRef());
						if(target!=null && source!=null)
							bpmn.addMessageFlow(source, target, ee.getName()); // lane);

					}
				}
			}
		}
		return bpmn;
	}

	private static Definitions getDefinitions(Resource resource) {
		if (resource != null
				&& !resource.getContents().isEmpty()
				&& !((EObject) resource.getContents().get(0)).eContents()
				.isEmpty()) {
			// Search for a Definitions object in this Resource
			for (Object e : resource.getContents()) {
				for (Object o : ((EObject) e).eContents()) {
					if (o instanceof Definitions)
						return (Definitions) o;
				}
			}
		}
		return null;
	}

}
