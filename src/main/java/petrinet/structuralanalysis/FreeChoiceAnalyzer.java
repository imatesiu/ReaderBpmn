/***********************************************************
 * This software is part of the ProM package * http://www.processmining.org/ * *
 * Copyright (c) 2003-2008 TU/e Eindhoven * and is licensed under the * LGPL
 * License, Version 1.0 * by Eindhoven University of Technology * Department of
 * Information Systems * http://www.processmining.org * *
 ***********************************************************/

package petrinet.structuralanalysis;

import java.util.Collection;
import java.util.HashSet;
import java.util.SortedSet;
import java.util.TreeSet;



import models.graphbased.directed.petrinet.Petrinet;
import models.graphbased.directed.petrinet.PetrinetEdge;
import models.graphbased.directed.petrinet.PetrinetGraph;
import models.graphbased.directed.petrinet.PetrinetNode;

import petrinet.analysis.NetAnalysisInformation;
import petrinet.analysis.NonExtendedFreeChoiceClustersSet;
import petrinet.analysis.NonFreeChoiceClustersSet;
import petrinet.analysis.NetAnalysisInformation.UnDetBool;
import models.graphbased.directed.petrinet.elements.Arc;
import models.graphbased.directed.petrinet.elements.Place;
import models.graphbased.directed.petrinet.elements.Transition;

/**
 * This class analyze whether a given net is a free choice-net, extended free
 * choice net, or not both Based on Murata, Tadao, Fellow, Lee. Petri Nets:
 * Properties, Analysis, and Applications.
 * 
 * @author arya
 * @email arya.adriansyah@gmail.com
 * @version Dec 6, 2008
 */
public class FreeChoiceAnalyzer {

	public Object[] analyzeFCAndEFCProperty( Petrinet net) throws Exception {
		return analyzeFCAndEFCPropertyPriv( net);
	}



	/**
	 * Main method to analyze Free-Choice and Extended Free-Choice property
	 * 
	 * @param context
	 *            context of the net
	 * @param net
	 *            net to be analyzed
	 * @return An array of objects is returned. The first object is of type
	 *         NetAnalysisInformation and contains information whether the net
	 *         is free choice and/or extended free choice. The second object is
	 *         of type NonFreeChoiceClustersSet and contains the non-free-choice
	 *         clusters in the net. The third object has type
	 *         NonExtendedFreeChoiceClustersSet and contains the
	 *         non-extended-free-choice clusters.
	 * @throws Exception
	 */
	private Object[] analyzeFCAndEFCPropertyPriv( PetrinetGraph net) throws Exception {
		// call main method
		NonFreeChoiceClustersSet nonFCClusters = getNFCClusters(net);
		NonExtendedFreeChoiceClustersSet nonXFCClusters = getNXFCClusters(net);

		// add connection
		NetAnalysisInformation.FREECHOICE fCRes = new NetAnalysisInformation.FREECHOICE();
		fCRes.setValue(nonFCClusters.isEmpty() ? UnDetBool.TRUE : UnDetBool.FALSE);
		
		NetAnalysisInformation.EXTFREECHOICE extFCRes = new NetAnalysisInformation.EXTFREECHOICE();
		extFCRes.setValue(nonXFCClusters.isEmpty() ? UnDetBool.TRUE : UnDetBool.FALSE);
		
		return new Object[] { fCRes, extFCRes, nonFCClusters, nonXFCClusters };
	}

	/**
	 * Main method to determine whether a given net is an extended free choice
	 * or not. Extended petri net is an ordinary petrinet so that (P1* intersect
	 * P2*) != null => p1* = p2* for all p1 and p2.
	 * 
	 * @param net
	 *            net to be analyzed
	 * @return the set of non-extended-free-choice clusters.
	 */
	public static NonExtendedFreeChoiceClustersSet getNXFCClusters(PetrinetGraph net) {
		NonFreeChoiceClustersSet clusters = buildClusters(net);
		NonExtendedFreeChoiceClustersSet nonXFCClusters = new NonExtendedFreeChoiceClustersSet();

		for (SortedSet<PetrinetNode> cluster : clusters) {
			if (!isXFC(cluster, net)) {
				nonXFCClusters.add(cluster);
			}
		}

		return nonXFCClusters;
	}

	/**
	 * Main method to determine whether a given net is free choice or not. Free
	 * choice net is an ordinary net such that every arc from a place is either
	 * a unique outgoing arc or a unique incoming arc to a transition
	 * 
	 * @param net
	 *            net to be analyzed
	 * @return The set of non-free-choice clusters
	 */
	public static NonFreeChoiceClustersSet getNFCClusters(PetrinetGraph net) {
		NonFreeChoiceClustersSet clusters = buildClusters(net);
		NonFreeChoiceClustersSet nonFCClusters = new NonFreeChoiceClustersSet();

		for (SortedSet<PetrinetNode> cluster : clusters) {
			if (!isFC(cluster, net)) {
				nonFCClusters.add(cluster);
			}
		}

		return nonFCClusters;
	}

	/**
	 * Determines whether a given cluster is extended free-choice.
	 * 
	 * @param cluster
	 *            The given cluster
	 * @param net
	 *            The corresponding net
	 * @return Whether the cluster is extended free-choice
	 */
	private static boolean isXFC(Collection<PetrinetNode> cluster, PetrinetGraph net) {
		HashSet<Place> places = new HashSet<Place>();
		HashSet<Transition> transitions = new HashSet<Transition>();
		Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> edges;
		for (PetrinetNode node : cluster) {
			if (node instanceof Place) {
				places.add((Place) node);
			} else {
				transitions.add((Transition) node);
			}
		}
		/**
		 * Every place should have arcs to every transition.
		 */
		for (Place place : places) {
			edges = net.getOutEdges(place);
			if (edges.size() != transitions.size()) {
				return false;
			}
		}
		/**
		 * The weights of the incoming edges to every transition should be
		 * identical.
		 */
		for (Transition transition : transitions) {
			edges = net.getInEdges(transition);
			int weight = ((Arc) edges.iterator().next()).getWeight();
			for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : edges) {
				if (((Arc) edge).getWeight() != weight) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Determines whether a given cluster is free-choice.
	 * 
	 * @param cluster
	 *            The given cluster
	 * @param net
	 *            The corresponding net
	 * @return Whether the cluster is free-choice
	 */
	private static boolean isFC(Collection<PetrinetNode> cluster, PetrinetGraph net) {
		int nofPlaces = 0, nofTransitions = 0;
		Place place = null;
		for (PetrinetNode node : cluster) {
			if (node instanceof Place) {
				nofPlaces++;
				place = (Place) node;
			} else {
				nofTransitions++;
			}
		}
		if ((nofPlaces > 1) && (nofTransitions > 1)) {
			return false;
		}
		if (nofPlaces == 1) {
			Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> edges;
			edges = net.getOutEdges(place);
			if (!edges.isEmpty()) {
				int weight = ((Arc) edges.iterator().next()).getWeight();
				for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : edges) {
					if (((Arc) edge).getWeight() != weight) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * Build the set of all cluster for the given net.
	 * 
	 * @param net
	 *            The given net.
	 * @return The set of all clusters.
	 */
	private static NonFreeChoiceClustersSet buildClusters(PetrinetGraph net) {
		NonFreeChoiceClustersSet clusters = new NonFreeChoiceClustersSet();
		Collection<PetrinetNode> clusteredNodes = new HashSet<PetrinetNode>();
		for (Place place : net.getPlaces()) {
			if (!clusteredNodes.contains(place)) {
				TreeSet<PetrinetNode> cluster = new TreeSet<PetrinetNode>();
				buildCluster(place, cluster, net);
				clusteredNodes.addAll(cluster);
				clusters.add(cluster);
			}
		}
		return clusters;
	}

	/**
	 * Builds the cluster for the given node in the given net.
	 * 
	 * @param node
	 *            The given node.
	 * @param cluster
	 *            The resulting cluster.
	 * @param net
	 *            The given net.
	 */
	private static void buildCluster(PetrinetNode node, Collection<PetrinetNode> cluster, PetrinetGraph net) {
		if (!cluster.contains(node)) {
			cluster.add(node);
			Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> edges;
			if (node instanceof Place) {
				edges = net.getOutEdges(node);
			} else {
				edges = net.getInEdges(node);
			}
			for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : edges) {
				PetrinetNode otherNode;
				if (node instanceof Place) {
					otherNode = edge.getTarget();
				} else {
					otherNode = edge.getSource();
				}
				buildCluster(otherNode, cluster, net);
			}
		}
	}
}
