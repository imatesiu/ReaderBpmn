package petrinet.pnml;

import java.awt.geom.Point2D;
import java.util.Map;

import models.utils.Pair;
import models.connections.GraphLayoutConnection;
import models.graphbased.AbstractGraphElement;
import models.graphbased.directed.petrinet.PetrinetNode;
import models.graphbased.directed.petrinet.elements.ExpandableSubNet;
import petrinet.pnml.graphics.PnmlNodeGraphics;
import org.xmlpull.v1.XmlPullParser;

/**
 * Basic PNML node object.
 * 
 * @author hverbeek
 */
public abstract class PnmlNode extends PnmlBasicObject {

	/**
	 * Id attribute.
	 */
	protected String id;
	/**
	 * Graphics element.
	 */
	protected PnmlNodeGraphics graphics;

	/**
	 * Creates a fresh PNML node.
	 * 
	 * @param tag
	 */
	public PnmlNode(String tag) {
		super(tag);
		id = null;
		graphics = null;
	}

	/**
	 * Imports all known attributes.
	 */
	protected void importAttributes(XmlPullParser xpp, Pnml pnml) {
		/*
		 * Import all basic object attributes.
		 */
		super.importAttributes(xpp, pnml);
		/*
		 * Import id attribute.
		 */
		importId(xpp);
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes(Pnml pnml) {
		return super.exportAttributes(pnml) + exportId(pnml);
	}

	/**
	 * Imports id attribute.
	 * 
	 * @param xpp
	 */
	private void importId(XmlPullParser xpp) {
		String value = xpp.getAttributeValue(null, "id");
		if (value != null) {
			id = value;
		}
	}

	/**
	 * Exports id attribute.
	 * 
	 * @return
	 */
	private String exportId(Pnml pnml) {
		if (id != null) {
			return exportAttribute("id", id, pnml);
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
		if (xpp.getName().equals(PnmlNodeGraphics.TAG)) {
			/*
			 * Graphics element. Create a graphics object and import graphics
			 * element.
			 */
			graphics = new PnmlNodeGraphics();
			graphics.importElement(xpp, pnml);
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
		/*
		 * Export basic node child elements.
		 */
		String s = super.exportElements(pnml);
		/*
		 * Export graphics element.
		 */
		if (graphics != null) {
			s += graphics.exportElement(pnml);
		}
		return s;
	}

	/**
	 * Checks the validity of this node. Should have an id attribute.
	 */
	protected void checkValidity(Pnml pnml) {
		super.checkValidity(pnml);
		if (id == null) {
			pnml.log(tag, lineNumber, "Expected id");
		}
	}

	public PnmlNodeGraphics getGraphics() {
		return graphics;
	}

	/**
	 * Gets the bounding box for this node.
	 * 
	 * @return The bounding box for this node.
	 */
	public Pair<Point2D.Double, Point2D.Double> getBoundingBox() {
		try {
			return graphics.getBoundingBox();
		} catch (Exception ex) {
		}
		return new Pair<Point2D.Double, Point2D.Double>(new Point2D.Double(Double.POSITIVE_INFINITY,
				Double.POSITIVE_INFINITY), new Point2D.Double(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY));
	}

	protected void convertToNet(ExpandableSubNet subNet, PetrinetNode node, Point2D.Double displacement, GraphLayoutConnection layout) {
		if (graphics != null) {
			super.convertToNet(node);
			graphics.convertToNet(subNet, node, displacement, layout);
		}
	}

	public PnmlNode convertFromNet(ExpandableSubNet parent, AbstractGraphElement element,
			Map<Pair<AbstractGraphElement, ExpandableSubNet>, String> idMap, GraphLayoutConnection layout) {
		super.convertFromNet(element != null ? element.getLabel() : "");
		PnmlNode node = null;
		//id = "node" + idMap.size(); Replaced as WoPeD does not handle these ids correctly (token game fails).
		id = "n" + idMap.size();
		idMap.put(new Pair<AbstractGraphElement, ExpandableSubNet>(element, parent), id);
		if (element != null) {
			graphics = new PnmlNodeGraphics().convertFromNet(parent, element, layout);
		}
		if ((id != null) || (graphics != null)) {
			node = this;
		}
		return node;
	}
}
