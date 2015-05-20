package petrinet.pnml;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




import models.connections.GraphLayoutConnection;
import models.graphbased.AbstractGraphElement;
import models.graphbased.directed.petrinet.PetrinetEdge;
import models.graphbased.directed.petrinet.PetrinetGraph;
import models.graphbased.directed.petrinet.PetrinetNode;
import models.graphbased.directed.petrinet.elements.ExpandableSubNet;
import models.graphbased.directed.petrinet.elements.Place;
import models.graphbased.directed.petrinet.elements.Transition;
import models.semantics.petrinet.Marking;
import models.utils.Pair;

import org.xmlpull.v1.XmlPullParser;

/**
 * Basic (E)PNML object. Allows import, export, and conversion to a Petri net.
 * 
 * @author hverbeek
 * 
 */
public class Pnml extends PnmlElement {

	/**
	 * PNML tag.
	 */
	public final static String TAG = "pnml";

	public final static int PHASE_NODES = 0;
	public final static int PHASE_DEREFNODES = 1;
	public final static int PHASE_REFNODES = 2;
	public final static int PHASE_ARCS = 3;

	/**
	 * Type of PNML: either PNML or EPNML. In EPNML, there are no pages, and
	 * some other subtle differences. The contained URI determines whether the
	 * type is PNML or EPNML.
	 */
	public enum PnmlType {
		PNML, EPNML, LOLA;
	}

	private PnmlType type;

	/**
	 * net elements.
	 */
	private List<PnmlNet> netList;
	
	

	private String log = new String();
	

	boolean hasErrors;

	/**
	 * Creates a fresh PNML object, given the type.
	 * 
	 * @param type
	 *            Either PNML or EPNML.
	 */
	public Pnml(PnmlType type) {
		super(TAG);
		this.type = type;
		netList = new ArrayList<PnmlNet>();
	
	}

	/**
	 * Creates a fresh default PNML object, that is, a PNML object of type PNML.
	 */
	public Pnml() {
		super(TAG);
		type = PnmlType.PNML;
		netList = new ArrayList<PnmlNet>();
		

		
	}

	
	/**
	 * Creates and initializes a log to throw to the framework when importing
	 * the PNML file fails. In this log, every net will have its own trace. The
	 * first net is preceded by a preamble.
	 */
	

	public String getLog() {
		return log;
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
		log +=context+ " Line "+message+ lineNumber;
		hasErrors = true;
	}

	

	public boolean hasErrors() {
		return hasErrors;
	}

	/**
	 * Set the type of this PNML object.
	 * 
	 * @param type
	 *            Either PNML or EPNML.
	 */
	public void setType(PnmlType type) {
		this.type = type;
	}

	/**
	 * Gets the type.
	 * 
	 * @return Either PNML or EPNML.
	 */
	public PnmlType getType() {
		return type;
	}

	/**
	 * Checks whether the current start tag is known. If known, it imports the
	 * corresponding child element and returns true. Otherwise, it returns
	 * false.
	 * 
	 * @return Whether the start tag was known.
	 */
	protected boolean importElements(XmlPullParser xpp, Pnml pnml) {
		if (super.importElements(xpp, pnml)) {
			return true;
		}
		if (xpp.getName().equals(PnmlNet.TAG)) {
			/*
			 * Found net element. Create a net object and import the net
			 * element.
			 */
			PnmlNet net = new PnmlNet();
			net.importElement(xpp, pnml);
			netList.add(net);
			return true;
		}
		
		
		return false;
	}

	/**
	 * Exports the child elements to String.
	 */
	protected String exportElements(Pnml pnml) {
		String s = super.exportElements(pnml);
		for (PnmlNet net : netList) {
			s += net.exportElement(pnml);
		}
		
		
		return s;
	}

	/**
	 * Converts the PNML object to a Petri net and initial marking.
	 * 
	 * @param net
	 *            Where to store the Petri net in (should be a fresh net).
	 * @param marking
	 *            Where to store the initial marking in (should be a fresh
	 *            marking).
	 */
	public void convertToNet(PetrinetGraph net, Marking marking, GraphLayoutConnection layout) {
		
			if (!netList.isEmpty()) {
				Map<String, Place> placeMap = new HashMap<String, Place>();
				Map<String, Transition> transitionMap = new HashMap<String, Transition>();
				Map<String, PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> edgeMap = new HashMap<String, PetrinetEdge<?, ?>>();
				netList.get(0).convertToNet(net, marking, placeMap, transitionMap, edgeMap, layout);
				setLayout(net, layout);
				
			}
		
	}

	public void setLayout(PetrinetGraph net, GraphLayoutConnection layout) {
		boolean doLayout = false;
		/*
		 * If any node has no position, then we need to layout the graph.
		 */
		for (PetrinetNode node : net.getNodes()) {
			if (layout.getPosition(node) == null) {
				doLayout = true;
			}
		}
		if (!doLayout) {
			/*
			 * All nodes have position (10.0,10.0) (which is the default
			 * position) we need to layout as well.
			 */
			doLayout = true;
			for (PetrinetNode node : net.getNodes()) {
				Point2D position = layout.getPosition(node);
				if ((position.getX() != 10.0) || (position.getY() != 10.0)) {
					doLayout = false;
				}
			}
		}
		layout.setLayedOut(!doLayout);
	}

	public String getLabel() {
		 if (!netList.isEmpty()) {
			return netList.get(0).getName("Unlabeled net");
		}
		return "Empty net";
	}

	public Pnml convertFromNet(Map<PetrinetGraph, Marking> markedNets, GraphLayoutConnection layout) {
		netList = new ArrayList<PnmlNet>();
		int netCtr = 1;
		Map<Pair<AbstractGraphElement, ExpandableSubNet>, String> map = new HashMap<Pair<AbstractGraphElement, ExpandableSubNet>, String>();
		for (PetrinetGraph net : markedNets.keySet()) {

			netList.add(new PnmlNet().convertFromNet(net, markedNets.get(net), map, netCtr++, layout));
		}
		Map<AbstractGraphElement, String> idMap = new HashMap<AbstractGraphElement, String>();
		for (Pair<AbstractGraphElement, ExpandableSubNet> pair : map.keySet()) {
			idMap.put(pair.getFirst(), map.get(pair));
		}
		
		return this;
	}

	public Pnml convertFromNet(PetrinetGraph net, Marking marking, GraphLayoutConnection layout) {
		Map<String, AbstractGraphElement> idMap = new HashMap<String, AbstractGraphElement>();
		return convertFromNet(net, marking, idMap, layout);
	}

	public Pnml convertFromNet(PetrinetGraph net, Marking marking, Map<String, AbstractGraphElement> idMap,
			GraphLayoutConnection layout) {
		
			Map<Pair<AbstractGraphElement, ExpandableSubNet>, String> map = new HashMap<Pair<AbstractGraphElement, ExpandableSubNet>, String>();
			netList.add(new PnmlNet().convertFromNet(net, marking, map, 1, idMap, layout));
			Map<AbstractGraphElement, String> map2 = new HashMap<AbstractGraphElement, String>();
			for (Pair<AbstractGraphElement, ExpandableSubNet> pair : map.keySet()) {
				map2.put(pair.getFirst(), map.get(pair));
			}
			
		
		return this;
	}/**/
}
