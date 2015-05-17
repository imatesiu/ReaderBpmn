package petrinet.pnml;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import framework.util.Pair;
import models.connections.GraphLayoutConnection;
import models.graphbased.AbstractGraphElement;
import models.graphbased.directed.petrinet.PetrinetEdge;
import models.graphbased.directed.petrinet.PetrinetGraph;
import models.graphbased.directed.petrinet.PetrinetNode;
import models.graphbased.directed.petrinet.elements.Arc;
import models.graphbased.directed.petrinet.elements.ExpandableSubNet;
import models.graphbased.directed.petrinet.elements.InhibitorArc;
import models.graphbased.directed.petrinet.elements.Place;
import models.graphbased.directed.petrinet.elements.ResetArc;
import models.graphbased.directed.petrinet.elements.Transition;
import models.semantics.petrinet.Marking;
import org.xmlpull.v1.XmlPullParser;

/**
 * Basic PNML page object.
 * 
 * @author hverbeek
 */
public class PnmlPage extends PnmlNode {

	/**
	 * PNML page tag.
	 */
	public final static String TAG = "page";

	/**
	 * Node elements.
	 */
	private List<PnmlNode> nodeList;
	/**
	 * Arc elements.
	 */
	private List<PnmlArc> arcList;

	private ExpandableSubNet subNet;

	/**
	 * Create a fresh PNML page object.
	 */
	public PnmlPage() {
		super(TAG);
		nodeList = new ArrayList<PnmlNode>();
		arcList = new ArrayList<PnmlArc>();
		subNet = null;
	}

	public PnmlPage(String tag) {
		super(tag);
		nodeList = new ArrayList<PnmlNode>();
		arcList = new ArrayList<PnmlArc>();
		subNet = null;
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
			/*
			 * Start tag corresponds to a known child element of a PNML node.
			 */
			return true;
		}
		if (xpp.getName().equals(PnmlPlace.TAG)) {
			/*
			 * Found place start tag. Create a place object and import place
			 * element.
			 */
			PnmlPlace place = new PnmlPlace();
			place.importElement(xpp, pnml);
			nodeList.add(place);
			return true;
		}
		if (xpp.getName().equals(PnmlTransition.TAG)) {
			/*
			 * Found transition start tag. Create a transition object and import
			 * transition element.
			 */
			PnmlTransition transition = new PnmlTransition();
			transition.importElement(xpp, pnml);
			nodeList.add(transition);
			return true;
		}
		if (xpp.getName().equals(PnmlReferencePlace.TAG)) {
			/*
			 * Found referencePlace start tag. Create a referencePlace object
			 * and import referencePlace element.
			 */
			PnmlReferencePlace referencePlace = new PnmlReferencePlace();
			referencePlace.importElement(xpp, pnml);
			nodeList.add(referencePlace);
			return true;
		}
		if (xpp.getName().equals(PnmlReferenceTransition.TAG)) {
			/*
			 * Found referenceTransition start tag. Create a referenceTransition
			 * object and import referenceTransition element.
			 */
			PnmlReferenceTransition referenceTransition = new PnmlReferenceTransition();
			referenceTransition.importElement(xpp, pnml);
			nodeList.add(referenceTransition);
			return true;
		}
		if (xpp.getName().equals(PnmlPage.TAG)) {
			/*
			 * Found page start tag. Create a page object and import page
			 * element.
			 */
			PnmlPage page = new PnmlPage();
			page.importElement(xpp, pnml);
			nodeList.add(page);
			return true;
		}
		if (xpp.getName().equals(PnmlArc.TAG)) {
			/*
			 * Found arc start tag. Create an arc object and import arc element.
			 */
			PnmlArc arc = new PnmlArc();
			arc.importElement(xpp, pnml);
			arcList.add(arc);
			return true;
		}
		/*
		 * Unknown start tag.
		 */
		return false;
	}

	/**
	 * Exports all child elements.
	 */
	protected String exportElements(Pnml pnml) {
		String s = super.exportElements(pnml);
		for (PnmlNode node : nodeList) {
			if (node instanceof PnmlPlace) {
				s += ((PnmlPlace) node).exportElement(pnml);
			} else if (node instanceof PnmlTransition) {
				s += ((PnmlTransition) node).exportElement(pnml);
			} else if (node instanceof PnmlReferencePlace) {
				s += ((PnmlReferencePlace) node).exportElement(pnml);
			} else if (node instanceof PnmlReferenceTransition) {
				s += ((PnmlReferenceTransition) node).exportElement(pnml);
			} else if (node instanceof PnmlPage) {
				s += ((PnmlPage) node).exportElement(pnml);
			}
		}
		for (PnmlArc arc : arcList) {
			s += arc.exportElement(pnml);
		}
		return s;
	}

	/**
	 * Converts this page to a Petri net.
	 * 
	 * @param net
	 *            Where to store the net.
	 * @param subNet
	 *            Where to store the sub net.
	 * @param marking
	 *            Where to store the initial marking.
	 * @param placeMap
	 *            Places found so far.
	 * @param transitionMap
	 *            Transitions found so far.
	 */
	public void convertToNet(PetrinetGraph net, ExpandableSubNet subNet, int pageCtr, Marking marking,
			Map<String, Place> placeMap, Map<String, Transition> transitionMap,
			Map<String, PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> edgeMap,
			Point2D.Double displacement, boolean createGroup, GraphLayoutConnection layout) {
		convertGroupsToNet(net, subNet, "Page " + pageCtr, marking, placeMap, transitionMap, displacement, createGroup,
				layout);
		convertNodesToNet(net, subNet, marking, placeMap, transitionMap, displacement, layout);
		int size = 0;
		int newSize = placeMap.size() + transitionMap.size();
		while (size != newSize) {
			convertRefNodesToNet(net, subNet, marking, placeMap, transitionMap);
			size = newSize;
			newSize = placeMap.size() + transitionMap.size();
		}
		convertArcsToNet(net, subNet, marking, placeMap, transitionMap, edgeMap, displacement, layout);
	}

	/**
	 * Gets the bounding box for all nodes on this page.
	 * 
	 * @return The bounding box for all nodes on this page.
	 */
	public Pair<Point2D.Double, Point2D.Double> getPageBoundingBox() {
		Point2D.Double luc = new Point2D.Double(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
		Point2D.Double rbc = new Point2D.Double(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
		try {
			for (PnmlNode node : nodeList) {
				if ((node instanceof PnmlPage) || (node instanceof PnmlPlace) || (node instanceof PnmlTransition)) {
					Pair<Point2D.Double, Point2D.Double> boundingBox = node.getBoundingBox();
					if (boundingBox.getFirst().x < luc.x) {
						luc.x = boundingBox.getFirst().x;
					}
					if (boundingBox.getFirst().y < luc.y) {
						luc.y = boundingBox.getFirst().y;
					}
					if (boundingBox.getSecond().x > rbc.x) {
						rbc.x = boundingBox.getSecond().x;
					}
					if (boundingBox.getSecond().y > rbc.y) {
						rbc.y = boundingBox.getSecond().y;
					}
				}
			}
		} catch (Exception ex) {
		}
		return new Pair<Point2D.Double, Point2D.Double>(luc, rbc);
	}

	/**
	 * Gets the displacement for nodes/arcs on the given page, given the
	 * displacement for the page itself.
	 * 
	 * @param displacement
	 *            The displacement for the given page.
	 * @param page
	 *            The given page.
	 * @return The displacement for any node/arc in the given page.
	 */
	private Point2D.Double getDisplacement(Point2D.Double displacement, PnmlPage page) {
		/*
		 * Start with the same displacement as the parent.
		 */
		Point2D.Double newDisplacement = new Point2D.Double(displacement.x, displacement.y);
		try {
			/*
			 * Add the position of this page to the displacement.
			 */
			Point2D.Double position = page.graphics.getBoundingBox().getFirst();
			if (position.x < Double.POSITIVE_INFINITY) {
				newDisplacement.x += position.x;
			}
			if (position.y < Double.POSITIVE_INFINITY) {
				newDisplacement.y += position.y;
			}
			/*
			 * Forget about the empty page area at the upper-left area.
			 */
			Pair<Point2D.Double, Point2D.Double> boundingBox = page.getPageBoundingBox();
			position = boundingBox.getFirst();
			if (position.x < Double.POSITIVE_INFINITY) {
				newDisplacement.x -= position.x;
			}
			if (position.y < Double.POSITIVE_INFINITY) {
				newDisplacement.y -= position.y;
			}
		} catch (Exception ex) {
		}
		//		System.err.println((name != null && name.text != null ? name.text.getText() : id) + ": " + newDisplacement);
		return newDisplacement;
	}

	private void convertGroupsToNet(PetrinetGraph net, ExpandableSubNet subNet, String label, Marking marking,
			Map<String, Place> placeMap, Map<String, Transition> transitionMap, Point2D.Double displacement,
			boolean createGroup, GraphLayoutConnection layout) {
		if ((name != null) && (name.text != null)) {
			label = name.text.getText();
		}
		if (createGroup) {
			/*
			 * Create a fresh sub net for this page.
			 */
			this.subNet = net.addGroup(label, subNet);
		}
		if (subNet == null) {
			/*
			 * Root page. Expand it.
			 */
			//			this.subNet.expand();
		}
		if (this.subNet != null) {
			/*
			 * Set optional graphics.
			 */
			super.convertToNet(subNet, this.subNet, displacement, layout);

			int ctr = 1;
			for (PnmlNode node : nodeList) {
				if (node instanceof PnmlPage) {
					((PnmlPage) node).convertGroupsToNet(net, this.subNet, (label + " " + ctr++), marking, placeMap,
							transitionMap, getDisplacement(displacement, (PnmlPage) node), true, layout);
				}
			}
		}
	}

	private void convertNodesToNet(PetrinetGraph net, ExpandableSubNet subNet, Marking marking,
			Map<String, Place> placeMap, Map<String, Transition> transitionMap, Point2D.Double displacement,
			GraphLayoutConnection layout) {
		for (PnmlNode node : nodeList) {
			if (node instanceof PnmlPlace) {
				((PnmlPlace) node).convertToNet(net, this.subNet, marking, placeMap, displacement, layout);
			} else if (node instanceof PnmlTransition) {
				((PnmlTransition) node).convertToNet(net, this.subNet, transitionMap, displacement, layout);
			} else if (node instanceof PnmlPage) {
				((PnmlPage) node).convertNodesToNet(net, subNet, marking, placeMap, transitionMap,
						getDisplacement(displacement, (PnmlPage) node), layout);
			}
		}
	}

	private void convertRefNodesToNet(PetrinetGraph net, ExpandableSubNet subNet, Marking marking,
			Map<String, Place> placeMap, Map<String, Transition> transitionMap) {
		for (PnmlNode node : nodeList) {
			if (node instanceof PnmlReferencePlace) {
				((PnmlReferencePlace) node).convertToNet(net, this.subNet, placeMap);
			} else if (node instanceof PnmlReferenceTransition) {
				((PnmlReferenceTransition) node).convertToNet(net, this.subNet, transitionMap);
			} else if (node instanceof PnmlPage) {
				((PnmlPage) node).convertRefNodesToNet(net, subNet, marking, placeMap, transitionMap);
			}
		}
	}

	private void convertArcsToNet(PetrinetGraph net, ExpandableSubNet subNet, Marking marking,
			Map<String, Place> placeMap, Map<String, Transition> transitionMap,
			Map<String, PetrinetEdge<?, ?>> edgeMap, Point2D.Double displacement, GraphLayoutConnection layout) {
		for (PnmlArc arc : arcList) {
			arc.convertToNet(net, this.subNet, placeMap, transitionMap, edgeMap, displacement, layout);
		}
		for (PnmlNode node : nodeList) {
			if (node instanceof PnmlPage) {
				((PnmlPage) node).convertArcsToNet(net, subNet, marking, placeMap, transitionMap, edgeMap,
						getDisplacement(displacement, (PnmlPage) node), layout);
			}
		}
	}

	private class PageMap {
		Map<ExpandableSubNet, PnmlPage> pageMap;
		PnmlPage nullPage;

		public PageMap() {
			pageMap = new HashMap<ExpandableSubNet, PnmlPage>();
			nullPage = null;
		}

		public void put(ExpandableSubNet group, PnmlPage page) {
			if (group != null) {
				pageMap.put(group, page);
			} else {
				nullPage = page;
			}
		}

		public PnmlPage get(ExpandableSubNet group) {
			return group != null ? pageMap.get(group) : nullPage;
		}
	}

	public PnmlPage convertFromNet(PetrinetGraph net, Marking marking, ExpandableSubNet parent,
			ExpandableSubNet element, Map<Pair<AbstractGraphElement, ExpandableSubNet>, String> idMap,
			GraphLayoutConnection layout) {
		PageMap pageMap = new PageMap();
		pageMap.put(element, this);
		convertGroupsFromNet(net, marking, parent, element, idMap, pageMap, layout);
		convertNodesFromNet(net, marking, parent, element, idMap, pageMap, layout);
		convertArcsFromNet(net, marking, parent, element, idMap, pageMap, layout);

		return this;
	}

	private PnmlPage convertGroupsFromNet(PetrinetGraph net, Marking marking, ExpandableSubNet parent,
			ExpandableSubNet element, Map<Pair<AbstractGraphElement, ExpandableSubNet>, String> idMap, PageMap pageMap,
			GraphLayoutConnection layout) {
		super.convertFromNet(parent, element, idMap, layout);
		nodeList = new ArrayList<PnmlNode>();
		arcList = new ArrayList<PnmlArc>();
		subNet = parent;

		for (ExpandableSubNet group : net.getGroups()) {
			if (group.getParent() == element) {
				PnmlPage page = new PnmlPage();
				pageMap.put(group, page);
				pageMap.get(element).nodeList.add(page);
				page.convertGroupsFromNet(net, marking, element, group, idMap, pageMap, layout);
			}
		}
		return this;
	}

	private void convertNodesFromNet(PetrinetGraph net, Marking marking, ExpandableSubNet parent,
			ExpandableSubNet element, Map<Pair<AbstractGraphElement, ExpandableSubNet>, String> idMap, PageMap pageMap,
			GraphLayoutConnection layout) {
		for (Place place : net.getPlaces()) {
			pageMap.get(place.getParent()).nodeList.add(new PnmlPlace().convertFromNet(marking, place.getParent(),
					place, idMap, layout));
		}
		for (Transition transition : net.getTransitions()) {
			pageMap.get(transition.getParent()).nodeList.add(new PnmlTransition().convertFromNet(net,
					transition.getParent(), transition, idMap, layout));
		}
	}

	private void convertArcsFromNet(PetrinetGraph net, Marking marking, ExpandableSubNet parent,
			ExpandableSubNet element, Map<Pair<AbstractGraphElement, ExpandableSubNet>, String> idMap, PageMap pageMap,
			GraphLayoutConnection layout) {
		for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : net.getEdges()) {
			if (edge instanceof Arc || edge instanceof ResetArc || edge instanceof InhibitorArc) {
				pageMap.get(edge.getParent()).arcList.add(new PnmlArc().convertFromNet(element, edge, this, idMap,
						layout));
			}
		}
	}

	public PnmlReferencePlace convertFromNet(ExpandableSubNet parent, Place place,
			Map<Pair<AbstractGraphElement, ExpandableSubNet>, String> idMap, GraphLayoutConnection layout) {
		PnmlReferencePlace refPlace = new PnmlReferencePlace().convertFromNet(parent, place, idMap, layout);
		nodeList.add(refPlace);
		return refPlace;
	}

	public PnmlReferenceTransition convertFromNet(ExpandableSubNet parent, Transition transition,
			Map<Pair<AbstractGraphElement, ExpandableSubNet>, String> idMap, GraphLayoutConnection layout) {
		PnmlReferenceTransition refTransition = new PnmlReferenceTransition().convertFromNet(parent, transition, idMap,
				layout);
		nodeList.add(refTransition);
		return refTransition;
	}
}
