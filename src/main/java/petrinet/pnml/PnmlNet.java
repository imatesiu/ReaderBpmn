package petrinet.pnml;

import java.awt.geom.Point2D;
import java.net.URI;
import java.net.URISyntaxException;
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
import models.graphbased.directed.petrinet.elements.ExpandableSubNet;
import models.graphbased.directed.petrinet.elements.Place;
import models.graphbased.directed.petrinet.elements.Transition;
import models.semantics.petrinet.Marking;
import org.xmlpull.v1.XmlPullParser;

/**
 * Basic PNML net object.
 * 
 * @author hverbeek
 * 
 */
public class PnmlNet extends PnmlBasicObject {

	/**
	 * PNML net tag.
	 */
	public final static String TAG = "net";

	/**
	 * Id attribute.
	 */
	private String id;
	/**
	 * Type attribute, determines whether the type of the PNML object is PNML or
	 * EPNML.
	 */
	private URI type;
	/**
	 * Page elements. For EPNML, a dummy page will be created.
	 */
	private List<PnmlPage> pageList;

	/**
	 * Creates a fresh PNML net object.
	 */
	public PnmlNet() {
		super(TAG);
		id = null;
		type = null;
		pageList = new ArrayList<PnmlPage>();
	}

	/**
	 * Imports all known attributes (id and type).
	 */
	protected void importAttributes(XmlPullParser xpp, Pnml pnml) {
		super.importAttributes(xpp, pnml);
		importId(xpp, pnml);
		importType(xpp, pnml);
	}

	/**
	 * Exports all known attributes (id and type).
	 */
	protected String exportAttributes(Pnml pnml) {
		return super.exportAttributes(pnml) + exportId(pnml) + exportType(pnml);
	}

	/**
	 * Imports the id attribute.
	 * 
	 * @param xpp
	 * @param pnml
	 */
	private void importId(XmlPullParser xpp, Pnml pnml) {
		String value = xpp.getAttributeValue(null, "id");
		if (value != null) {
			id = value;
		}
		
	}

	/**
	 * Exports the id attribute.
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
	 * Imports the type attribute.
	 * 
	 * @param xpp
	 * @param pnml
	 */
	private void importType(XmlPullParser xpp, Pnml pnml) {
		String value = xpp.getAttributeValue(null, "type");
		if (value != null) {
			type = URI.create(value);
			if (value.equals("http://www.yasper.org/specs/epnml-1.1")
					|| value.equals("http://www.petriweb.org/specs/epnml11")
					|| value.equals("http://www.informatik.hu-berlin.de/top/pntd/ptNetb")
					|| value.equals("http://www.informatik.hu-berlin.de/top/pnml/basicPNML.rng")
					|| value.equals("PTNet")) {
				/*
				 * URI suggests that this is an EPNML file. At least, the top
				 * page is missing in these PNML variants.
				 */
				pnml.setType(Pnml.PnmlType.EPNML);
				/*
				 * Create a dummy page.
				 */
				pageList.add(new PnmlPage());
			} else if (value.equals("oWFNet")) {
				pnml.setType(Pnml.PnmlType.LOLA);
			} else {
				/*
				 * URI suggests that this is a PNML file.
				 */
				pnml.setType(Pnml.PnmlType.PNML);
			}
		}
	}

	/**
	 * Exports the type attribute.
	 * 
	 * @return
	 */
	private String exportType(Pnml pnml) {
		switch (pnml.getType()) {
			case PNML :
				return exportAttribute("type", "http://www.pnml.org/version-2009/grammar/pnmlcoremodel", pnml);
			case LOLA :
				return exportAttribute("type", "oWFNet", pnml);
			case EPNML :
				return exportAttribute("type", "http://www.yasper.org/specs/epnml-1.1", pnml);
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
		switch (pnml.getType()) {
			case PNML :
				// Fall through
			case LOLA :
				if (xpp.getName().equals(PnmlPage.TAG)) {
					/*
					 * Found page start tag. Create a page object and import the
					 * page element.
					 */
					PnmlPage page = new PnmlPage();
					pageList.add(page);
					page.importElement(xpp, pnml);
					return true;
				}
				break;
			case EPNML :
				if (pageList.get(0).importElements(xpp, pnml)) {
					/*
					 * Found net start tag and imported it as a child of the
					 * dummy page.
					 */
					return true;
				}
		}
		/*
		 * Start tag unknown. End tag needs to be dealt with.
		 */
		return false;
	}

	/**
	 * Exports all pages.
	 */
	protected String exportElements(Pnml pnml) {
		String s = super.exportElements(pnml);
		switch (pnml.getType()) {
			case PNML :
				// Fall through
			case LOLA :
				for (PnmlPage page : pageList) {
					s += page.exportElement(pnml);
				}
				break;
			case EPNML :
				for (PnmlPage page : pageList) {
					s += page.exportElements(pnml);
				}
				break;
		}
		return s;
	}

	/**
	 * Check the validity of this net. It should have an id and a type.
	 */
	protected void checkValidity(Pnml pnml) {
		super.checkValidity(pnml);
		if ((id == null) || (type == null)) {
			pnml.log(tag, lineNumber, "Expected id and type");
		}
	}

	/**
	 * Convert this net to a Petri net.
	 * 
	 * @param net
	 *            Where to store the net.
	 * @param marking
	 *            Where to store the initial marking.
	 * @param placeMap
	 *            Places found so far.
	 * @param transitionMap
	 *            Transitions found so far.
	 */
	public void convertToNet(PetrinetGraph net, Marking marking, Map<String, Place> placeMap,
			Map<String, Transition> transitionMap,
			Map<String, PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> edgeMap,
			GraphLayoutConnection layout) {
		int ctr = 1;
		for (PnmlPage page : pageList) {
			/*
			 * Convert this page to the fresh sub net.
			 */
			page.convertToNet(net, null, ctr, marking, placeMap, transitionMap, edgeMap, new Point2D.Double(0.0, 0.0),
					pageList.size() > 1, layout);
		}
	}

	public PnmlNet convertFromNet(PetrinetGraph net, Marking marking,
			Map<Pair<AbstractGraphElement, ExpandableSubNet>, String> idMap, int id, GraphLayoutConnection layout) {
		Map<String, AbstractGraphElement> map = new HashMap<String, AbstractGraphElement>();
		return convertFromNet(net, marking, idMap, id, map, layout);
	}

	public PnmlNet convertFromNet(PetrinetGraph net, Marking marking,
			Map<Pair<AbstractGraphElement, ExpandableSubNet>, String> idMap, int id,
			Map<String, AbstractGraphElement> map, GraphLayoutConnection layout) {
		super.convertFromNet(net.getLabel());
		this.id = "net" + id;
		try {
			type = new URI("http://www.pnml.org/version-2009/grammar/pnmlcoremodel");
		} catch (URISyntaxException e) {
			System.err.println("URI http://www.pnml.org/version-2009/grammar/pnmlcoremodel unknown");
			type = null;
		}
		pageList = new ArrayList<PnmlPage>();
		pageList.add(new PnmlPage().convertFromNet(net, marking, null, null, idMap, layout));
		for (Pair<AbstractGraphElement, ExpandableSubNet> object : idMap.keySet()) {
			map.put(idMap.get(object), object.getFirst());
		}
		return this;
	}

}
