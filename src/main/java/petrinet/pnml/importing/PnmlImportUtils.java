package petrinet.pnml.importing;

import java.io.InputStream;



import models.connections.GraphLayoutConnection;

import models.graphbased.directed.petrinet.PetrinetGraph;
import models.semantics.petrinet.Marking;
import petrinet.pnml.Pnml;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class PnmlImportUtils {
	
	private PetrinetGraph net;
	private Marking m;
	
	
	public Pnml importPnmlFromStream( InputStream input, String filename, long fileSizeInBytes)
			throws Exception {
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
		Pnml pnml = new Pnml();

		/*
		 * Skip whatever we find until we've found a start tag.
		 */
		while (eventType != XmlPullParser.START_TAG) {
			eventType = xpp.next();
		}
		/*
		 * Check whether start tag corresponds to PNML start tag.
		 */
		if (xpp.getName().equals(Pnml.TAG)) {
			/*
			 * Yes it does. Import the PNML element.
			 */
			pnml.importElement(xpp, pnml);
		} else {
			/*
			 * No it does not. Return null to signal failure.
			 */
			pnml.log(Pnml.TAG, xpp.getLineNumber(), "Expected pnml");
		}
		if (pnml.hasErrors()) {
			System.out.println("Log of PNML import "+ pnml.getLog());
			return null;
		}
		return pnml;
	}
	
	public void connectNet( Pnml pnml, PetrinetGraph net) {
		/*
		 * Create a fresh marking.
		 */
		Marking marking = new Marking();

		GraphLayoutConnection layout = new GraphLayoutConnection(net);
		/*
		 * Initialize the Petri net and marking from the PNML element.
		 */
		pnml.convertToNet(net, marking, layout);

		/*
		 * Add a connection from the Petri net to the marking.
		 */
		

		/*
		 * Set the label of the Petri net.
		 */
		
		/*
		 * Return the net and the marking.
		 */
		
		this.net = net;
		this.m = marking;
		
	}

	public PetrinetGraph getNet() {
		return net;
	}

	public Marking getMarking() {
		return m;
	}

	

}
