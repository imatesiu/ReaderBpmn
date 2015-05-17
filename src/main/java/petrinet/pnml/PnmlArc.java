package petrinet.pnml;

import java.awt.geom.Point2D;
import java.util.Map;

import org.jgraph.graph.GraphConstants;
import framework.util.Pair;
import models.connections.GraphLayoutConnection;
import models.graphbased.AbstractGraphElement;
import models.graphbased.AttributeMap;
import models.graphbased.directed.DirectedGraphElement;
import models.graphbased.directed.petrinet.PetrinetEdge;
import models.graphbased.directed.petrinet.PetrinetGraph;
import models.graphbased.directed.petrinet.PetrinetNode;

import models.graphbased.directed.petrinet.ResetNet;
import models.graphbased.directed.petrinet.elements.Arc;
import models.graphbased.directed.petrinet.elements.ExpandableSubNet;
import models.graphbased.directed.petrinet.elements.InhibitorArc;
import models.graphbased.directed.petrinet.elements.Place;
import models.graphbased.directed.petrinet.elements.ResetArc;
import models.graphbased.directed.petrinet.elements.Transition;
import petrinet.pnml.extensions.PnmlArcType;
import petrinet.pnml.extensions.PnmlInscription;
import petrinet.pnml.graphics.PnmlArcGraphics;
import org.xmlpull.v1.XmlPullParser;

/**
 * Basic PNML arc object.
 * 
 * @author hverbeek
 */
public class PnmlArc extends PnmlBasicObject {

	/**
	 * PNML arc tag.
	 */
	public final static String TAG = "arc";

	/**
	 * Id attribute.
	 */
	private String id;
	/**
	 * Source attribute.
	 */
	private String source;
	/**
	 * Target attribute.
	 */
	private String target;
	/**
	 * Inscription element.
	 */
	private PnmlInscription inscription;
	/**
	 * (Arc)Type element.
	 */
	private PnmlArcType arcType;
	/**
	 * Graphics element.
	 */
	private PnmlArcGraphics graphics;

	/**
	 * Creates a fresh PNML arc.
	 */
	public PnmlArc() {
		super(TAG);
		id = null;
		source = null;
		target = null;
		inscription = null;
		arcType = null;
		graphics = null;
	}

	/**
	 * Imports all known attributes.
	 */
	protected void importAttributes(XmlPullParser xpp, Pnml pnml) {
		/*
		 * Import known basic object attributes.
		 */
		super.importAttributes(xpp, pnml);
		/*
		 * Import id attribute.
		 */
		importId(xpp, pnml);
		/*
		 * Import source attribute.
		 */
		importSource(xpp, pnml);
		/*
		 * Import target attribute.
		 */
		importTarget(xpp, pnml);
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes(Pnml pnml) {
		return super.exportAttributes(pnml) + exportId(pnml) + exportSource(pnml) + exportTarget(pnml);
	}

	private void importId(XmlPullParser xpp, Pnml pnml) {
		String value = xpp.getAttributeValue(null, "id");
		if (value != null) {
			id = value;
		}
	}

	/**
	 * Imports source attribute.
	 * 
	 * @param xpp
	 * @param pnml
	 */
	private void importSource(XmlPullParser xpp, Pnml pnml) {
		String value = xpp.getAttributeValue(null, "source");
		if (value != null) {
			source = value;
		}
	}

	private String exportId(Pnml pnml) {
		if (id != null) {
			return exportAttribute("id", id, pnml);
		}
		return "";
	}

	/**
	 * Exports source attribute.
	 * 
	 * @return
	 */
	private String exportSource(Pnml pnml) {
		if (source != null) {
			return exportAttribute("source", source, pnml);
		}
		return "";
	}

	/**
	 * Imports target attribute.
	 * 
	 * @param xpp
	 * @param pnml
	 */
	private void importTarget(XmlPullParser xpp, Pnml pnml) {
		String value = xpp.getAttributeValue(null, "target");
		if (value != null) {
			target = value;
		}
	}

	/**
	 * Exports target attribute.
	 * 
	 * @return
	 */
	private String exportTarget(Pnml pnml) {
		if (target != null) {
			return exportAttribute("target", target, pnml);
		}
		return "";
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
			 * Start tag corresponds to a known child element of a PNML basic
			 * object.
			 */
			return true;
		}
		if (xpp.getName().equals(PnmlInscription.TAG)) {
			/*
			 * Inscription element. create inscription object and import
			 * inscription element.
			 */
			inscription = new PnmlInscription();
			inscription.importElement(xpp, pnml);
			return true;
		}
		if (xpp.getName().equals(PnmlArcGraphics.TAG)) {
			/*
			 * Graphics element. create graphics object and import graphics
			 * element.
			 */
			graphics = new PnmlArcGraphics();
			graphics.importElement(xpp, pnml);
			return true;
		}
		String arcTypeTag = "";
		switch (pnml.getType()) {
			case PNML :
				// Fall through
			case LOLA :
				arcTypeTag = PnmlArcType.TAG;
				break;
			case EPNML :
				arcTypeTag = PnmlArcType.EPNML_TAG;
				break;
		}
		if (xpp.getName().equals(arcTypeTag)) {
			/*
			 * ArcType element (PNML) or type element (EPNML). create arc type
			 * object and import arc type element.
			 */
			arcType = new PnmlArcType(arcTypeTag);
			arcType.importElement(xpp, pnml);
			return true;
		}
		return false;
	}

	/**
	 * Exports all child elements.
	 */
	protected String exportElements(Pnml pnml) {
		String s = super.exportElements(pnml);
		if (inscription != null) {
			s += inscription.exportElement(pnml);
		}
		if (graphics != null) {
			s += graphics.exportElement(pnml);
		}
		if (arcType != null) {
			s += arcType.exportElement(pnml);
		}
		return s;
	}

	/**
	 * Checks validity. Should have a source and a target.
	 */
	protected void checkValidity(Pnml pnml) {
		super.checkValidity(pnml);
		if ((id == null) || (source == null) || (target == null)) {
			pnml.log(tag, lineNumber, "Expected id, source, and target");
		}
	}

	/**
	 * Converts this PNML arc to a regular Petri net arc.
	 * 
	 * @param net
	 *            The net to add the arc to.
	 * @param subNet
	 *            The sub net to add the arc to.
	 * @param placeMap
	 *            The places found so far.
	 * @param transitionMap
	 *            The transitions found so far.
	 */
	public void convertToNet(PetrinetGraph net, ExpandableSubNet subNet, Map<String, Place> placeMap,
			Map<String, Transition> transitionMap,
			Map<String, PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> edgeMap,
			Point2D.Double displacement, GraphLayoutConnection layout) {
		int weight = 1;
		if ((arcType == null) || arcType.isNormal()) {
			/*
			 * Get arc weight.
			 */
			if (inscription != null) {
				weight = inscription.getInscription();
			}
		}

		/*
		 * Create arc (if source and target can be found).
		 */
		PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> arc = null;
		if (placeMap.containsKey(source) && transitionMap.containsKey(target)) {
			ExpandableSubNet sourceSubNet = placeMap.get(source).getParent();
			ExpandableSubNet targetSubNet = transitionMap.get(target).getParent();
			if ((arcType == null) || arcType.isNormal()) {
				arc = net.addArc(placeMap.get(source), transitionMap.get(target), weight,
						firstCommonAncestor(sourceSubNet, targetSubNet));
			} 
		} else if (transitionMap.containsKey(source) && placeMap.containsKey(target)) {
			ExpandableSubNet sourceSubNet = transitionMap.get(source).getParent();
			ExpandableSubNet targetSubNet = placeMap.get(target).getParent();
			if ((arcType == null) || arcType.isNormal()) {
				arc = net.addArc(transitionMap.get(source), placeMap.get(target), weight,
						firstCommonAncestor(sourceSubNet, targetSubNet));
			} 
		}
		/*
		 * If arc created, set graphics and inscription.
		 */
		if (arc != null) {
			edgeMap.put(id, arc);
			arc.getAttributeMap().put(AttributeMap.STYLE, GraphConstants.STYLE_ORTHOGONAL);
			super.convertToNet(arc);
			if (graphics != null) {
				graphics.convertToNet(subNet, arc, displacement, layout);
			}
			if (inscription != null) {
				inscription.convertToNet(arc);
			}
		}
	}

	private ExpandableSubNet firstCommonAncestor(ExpandableSubNet subNet1, ExpandableSubNet subNet2) {
		int depth1 = 0;
		int depth2 = 0;
		for (ExpandableSubNet subNet = subNet1; subNet != null; subNet = subNet.getParent()) {
			depth1++;
		}
		for (ExpandableSubNet subNet = subNet2; subNet != null; subNet = subNet.getParent()) {
			depth2++;
		}
		while (depth1 > depth2) {
			subNet1 = subNet1.getParent();
			depth1--;
		}
		while (depth1 < depth2) {
			subNet2 = subNet2.getParent();
			depth2--;
		}
		while ((subNet1 != null) && (subNet1 != subNet2)) {
			subNet1 = subNet1.getParent();
			subNet2 = subNet2.getParent();
		}
		return subNet1;
	}

	public PnmlArc convertFromNet(ExpandableSubNet parent,
			PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge, PnmlPage page,
			Map<Pair<AbstractGraphElement, ExpandableSubNet>, String> idMap, GraphLayoutConnection layout) {
		super.convertFromNet(edge.getLabel());
		id = "arc" + idMap.size();
		idMap.put(new Pair<AbstractGraphElement, ExpandableSubNet>(edge, parent), id);
		PetrinetNode sourceNode = edge.getSource();
		PetrinetNode targetNode = edge.getTarget();
		if (!idMap.containsKey(new Pair<DirectedGraphElement, ExpandableSubNet>(sourceNode, parent))) {
			/*
			 * Source is in different subnet than the edge. Need to create a
			 * reference to the source in the edge's subnet (page) and use that
			 * reference as source.
			 */
			if (sourceNode instanceof Place) {
				page.convertFromNet(parent, (Place) sourceNode, idMap, layout);
			} else if (sourceNode instanceof Transition) {
				page.convertFromNet(parent, (Transition) sourceNode, idMap, layout);
			}
		}
		source = idMap.get(new Pair<DirectedGraphElement, ExpandableSubNet>(sourceNode, parent));
		if (!idMap.containsKey(new Pair<DirectedGraphElement, ExpandableSubNet>(targetNode, parent))) {
			/*
			 * Target is in different subnet than the edge. Need to create a
			 * reference to the target in the edge's subnet (page) and use that
			 * reference as target.
			 */
			if (targetNode instanceof Place) {
				page.convertFromNet(parent, (Place) targetNode, idMap, layout);
			} else if (targetNode instanceof Transition) {
				page.convertFromNet(parent, (Transition) targetNode, idMap, layout);
			}
		}
		target = idMap.get(new Pair<DirectedGraphElement, ExpandableSubNet>(targetNode, parent));
		inscription = new PnmlInscription().convertFromNet(edge);
		if (edge instanceof Arc) {
			arcType = new PnmlArcType(PnmlArcType.TAG);
			arcType.setNormal();
		} else if (edge instanceof ResetArc) {
			arcType = new PnmlArcType(PnmlArcType.TAG);
			arcType.setReset();
		} else if (edge instanceof InhibitorArc) {
			arcType = new PnmlArcType(PnmlArcType.TAG);
			arcType.setInhibitor();
		}
		graphics = new PnmlArcGraphics().convertFromNet(parent, edge, layout);
		return this;
	}
}
