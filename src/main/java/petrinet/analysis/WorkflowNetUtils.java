package petrinet.analysis;

import java.util.SortedSet;
import java.util.TreeSet;

import models.graphbased.directed.analysis.ShortestPathFactory;
import models.graphbased.directed.analysis.ShortestPathInfo;
import models.graphbased.directed.petrinet.Petrinet;
import models.graphbased.directed.petrinet.PetrinetEdge;
import models.graphbased.directed.petrinet.PetrinetNode;
import models.graphbased.directed.petrinet.elements.Place;
import models.graphbased.directed.petrinet.elements.Transition;
import models.graphbased.directed.petrinet.impl.PetrinetFactory;
import models.semantics.petrinet.Marking;

public class WorkflowNetUtils {

	private WorkflowNetUtils() {
	}

	/**
	 * This method checks whether:
	 * 
	 * 1) There is exactly one input place,
	 * 
	 * 2) there is exactly one output place,
	 * 
	 * 3) all nodes are on a path from the input place to the output place.
	 * 
	 * @param net
	 *            the net to be checked for being a valid WF net
	 * @return true if the net is a valid WF net (Has nothing to do with
	 *         soundness)
	 */
	public static boolean isValidWFNet(Petrinet net) {
		Place in = null;
		Place out = null;
		for (Place p : net.getPlaces()) {
			if (net.getInEdges(p).isEmpty()) {
				if (in != null) {
					// 2 input places
					return false;
				} else {
					in = p;
				}
			}
			if (net.getOutEdges(p).isEmpty()) {
				if (out != null) {
					// two output places
					return false;
				} else {
					out = p;
				}
			}
		}
		if ((in == null) || (out == null)) {
			// input or output place missing.
			return false;
		}
		ShortestPathInfo<PetrinetNode, PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> map = ShortestPathFactory
				.calculateAllShortestDistanceDijkstra(net);
		for (PetrinetNode n : net.getNodes()) {
			if ((n instanceof Place) || (n instanceof Transition)) {
				if ((map.getShortestPathLength(in, n) == ShortestPathInfo.NOPATH)
						|| (map.getShortestPathLength(n, out) == ShortestPathInfo.NOPATH)) {
					// node n is not reachable from in, or out is not reachable
					// from n
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Returns the input place of this net. Note that it is assumed that
	 * isValidWFNet(net)==true. If not, the result is unpredictable.
	 * 
	 * @param net
	 *            the net of which the input place is requested
	 * @return the input place if isValidWFNet(net)==true. Garbage otherwise
	 *         (potentially null)
	 */
	public static Place getInputPlace(Petrinet net) {
		for (Place p : net.getPlaces()) {
			if (net.getInEdges(p).isEmpty()) {
				return p;
			}
		}
		return null;
	}

	/**
	 * Returns the output place of this net. Note that it is assumed that
	 * isValidWFNet(net)==true. If not, the result is unpredictable.
	 * 
	 * @param net
	 *            the net of which the output place is requested
	 * @return the output place if isValidWFNet(net)==true. Garbage otherwise
	 *         (potentially null)
	 */
	public static Place getOutputPlace(Petrinet net) {
		for (Place p : net.getPlaces()) {
			if (net.getOutEdges(p).isEmpty()) {
				return p;
			}
		}
		return null;
	}

	/**
	 * Returns the set of source places of this net.
	 * 
	 * @param net
	 *            the net of which the set of source places is requested
	 * @return the set of source places.
	 */
	public static SortedSet<Place> getSourcePlaces(Petrinet net) {
		SortedSet<Place> places = new TreeSet<Place>();
		for (Place p : net.getPlaces()) {
			if (net.getInEdges(p).isEmpty()) {
				places.add(p);
			}
		}
		return places;
	}

	/**
	 * Returns the set of sink places of this net.
	 * 
	 * @param net
	 *            the net of which the set of sink places is requested
	 * @return the set of sink places.
	 */
	public static SortedSet<Place> getSinkPlaces(Petrinet net) {
		SortedSet<Place> places = new TreeSet<Place>();
		for (Place p : net.getPlaces()) {
			if (net.getOutEdges(p).isEmpty()) {
				places.add(p);
			}
		}
		return places;
	}

	/**
	 * Returns the set of unconnected nodes of this net.
	 * 
	 * @param net
	 *            the net of which the set of unconnected nodes is requested
	 * @return the set of unconnected nodes.
	 */
	public static SortedSet<PetrinetNode> getUnconnectedNodes(Petrinet net) {
		SortedSet<PetrinetNode> nodes = new TreeSet<PetrinetNode>();
		Place in = getInputPlace(net);
		Place out = getOutputPlace(net);
		ShortestPathInfo<PetrinetNode, PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> map = ShortestPathFactory
				.calculateAllShortestDistanceDijkstra(net);
		for (PetrinetNode n : net.getNodes()) {
			if ((n instanceof Place) || (n instanceof Transition)) {
				if ((map.getShortestPathLength(in, n) == ShortestPathInfo.NOPATH)
						|| (map.getShortestPathLength(n, out) == ShortestPathInfo.NOPATH)) {
					// node n is not reachable from in, or out is not reachable
					// from n
					nodes.add(n);
				}
			}
		}
		return nodes;
	}

	public static Object[] shortCircuit( Petrinet net) {
		Petrinet shortCNet = PetrinetFactory.clonePetrinet(net);
		Transition shortCTransition = shortCNet.addTransition("_t_");
		Marking initialMarking = new Marking();
		Marking finalMarking = new Marking();
		for (Place place : getSourcePlaces(shortCNet)) {
			shortCNet.addArc(shortCTransition, place);
			initialMarking.add(place);
		}
		for (Place place : getSinkPlaces(shortCNet)) {
			shortCNet.addArc(place, shortCTransition);
			finalMarking.add(place);
		}

		return new Object[] { shortCNet, initialMarking, finalMarking };
	}
}
