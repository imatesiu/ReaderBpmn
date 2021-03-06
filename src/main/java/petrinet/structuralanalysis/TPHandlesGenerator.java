/***********************************************************
 * This software is part of the ProM package * http://www.processmining.org/ * *
 * Copyright (c) 2003-2008 TU/e Eindhoven * and is licensed under the * LGPL
 * License, Version 1.0 * by Eindhoven University of Technology * Department of
 * Information Systems * http://www.processmining.org * *
 ***********************************************************/

package petrinet.structuralanalysis;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;


import framework.util.collection.ComparablePair;

import models.graphbased.directed.petrinet.Petrinet;
import models.graphbased.directed.petrinet.PetrinetEdge;
import models.graphbased.directed.petrinet.PetrinetGraph;
import models.graphbased.directed.petrinet.PetrinetNode;

import models.graphbased.directed.petrinet.ResetNet;
import petrinet.analysis.TPHandles;
import models.graphbased.directed.petrinet.elements.Place;
import models.graphbased.directed.petrinet.elements.Transition;
import models.graphbased.directed.utils.Node;

/**
 * Class to identify TP Handles from a given net(if there is any) TP Handle
 * refers to J. Esparza and M. Silva. Circuits, Handles, Bridges and Nets. In G.
 * Rozenberg, editor, Advances in Petri Nets 1990, volume 483 of Lecture Notes
 * in Computer Science, pages 210 - 242. Springer - Verlag, Berlin, 1990
 * 
 * @author arya
 * @email arya.adriansyah@gmail.com
 * @version Dec 8, 2008
 */

public class TPHandlesGenerator {

	Petrinet net = null;
	public TPHandlesGenerator(Petrinet net){
		this.net= net;
	}
	
	public TPHandles analyzeTPHandles()  {
		return analyzeTPHandlesMain( this.net);
	}
	
	public TPHandles analyzeTPHandles( Petrinet net) throws Exception {
		return analyzeTPHandlesMain( net);
	}

	
	
	


	/**
	 * Main method to analyze T-P Handle (with context)
	 * 
	 * 
	 *            context of the net
	 * 
	 *            net to be analyzed
	 * @return TPHandles T-P Handle in the net
	 * @throws Exception
	 */
	public TPHandles analyzeTPHandlesMain( PetrinetGraph net) {
		// call main method
		TPHandles tpHandles = identifyTPHandles(net);

		// add connection for siphon marking
		System.out.println("T-P Handles of " + net.getLabel());
		//context.addConnection(new TPHandleConnection(net, tpHandles));

		return tpHandles;
	}

	/**
	 * Main method to identify T-P Handles in a net. TPHandles are in form of
	 * pair of Transition-Place
	 * 
	 * 
	 *            net to be analyzed
	 * @return TPHandles T-P Handle in the net
	 */
	public TPHandles identifyTPHandles(PetrinetGraph net) {
		// result
		TPHandles result = new TPHandles();

		// temp variables
		Set<Transition> transitionToBeCheckedAsStart = new HashSet<Transition>();
		Set<Place> placesToBeCheckedAsSink = new HashSet<Place>();

		// identify all transitions which has outgoing arcs
		for (Transition trans : net.getTransitions()) {
			Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> edges = net.getOutEdges(trans);
			if (edges != null) {
				// check outgoing arcs
				if (edges.size() > 1) {
					// potentially is a start place
					transitionToBeCheckedAsStart.add(trans);
				}
			}
		}

		// check all places with more than 1 input arcs
		for (Place place : net.getPlaces()) {
			Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> edges = net.getInEdges(place);
			if (edges != null) {
				// check outgoing arcs
				if (edges.size() > 1) {
					// potentially is a sink transition
					placesToBeCheckedAsSink.add(place);
				}
			}
		}

		// generate available path from placeToBeCheckedAsStart to
		// transitionToBeCheckedAsSink
		for (Transition transition : transitionToBeCheckedAsStart) {
			for (Place place : placesToBeCheckedAsSink) {
				// check path, if there is more than 1 path
				if (pathsMoreThan1(transition, place, net)) {
					result.add(new ComparablePair<Transition, Place>(transition, place));
				}
			}
		}

		return result;
	}

	/**
	 * Return true if there are more than one paths to go from node1 to node2.
	 * Paths must not intersect each other. This method is similar with the one
	 * implemented in PTHandlesGenerator class
	 * 
	 * 
	 * 
	 * 
	 *            net to be analyzed
	 * @return boolean true if adjacent path from node1 to node2 is more than 1,
	 *         false if not
	 */
	public boolean pathsMoreThan1(PetrinetNode node1, PetrinetNode node2, PetrinetGraph net) {
		// use tree to trace previous node
		Node<PetrinetNode> root = new Node<PetrinetNode>();
		root.setParent(null);
		root.setData(node1);

		Queue<Node<PetrinetNode>> nodeToBeExpanded = new LinkedList<Node<PetrinetNode>>();
		nodeToBeExpanded.add(root);

		// to store path
		List<Set<PetrinetNode>> listOfPath = new LinkedList<Set<PetrinetNode>>();

		// expand node1

		while (!nodeToBeExpanded.isEmpty()) {
			Node<PetrinetNode> checkedNode = nodeToBeExpanded.poll();
			Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> edges = net
					.getOutEdges(checkedNode.getData());
			for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : edges) {
				PetrinetNode targetNode = edge.getTarget();
				if (targetNode.equals(node2)) {
					// add path to setOfPath
					Set<PetrinetNode> retrievedPath = retrievePath(checkedNode);
					retrievedPath.remove(node1);
					listOfPath.add(retrievedPath);
				} else {
					// check if this node already exist
					if (!isAlreadyExplored(checkedNode, targetNode)) {
						// expand node
						// create new node
						Node<PetrinetNode> newNode = new Node<PetrinetNode>();
						newNode.setData(targetNode);
						newNode.setParent(checkedNode);

						// add it to queue
						nodeToBeExpanded.add(newNode);
					}
				}
			}
		}

		// check if there are at least two paths which do not have any
		// intersection
		for (int i = 0; i < listOfPath.size() - 1; i++) {
			for (int j = i + 1; j < listOfPath.size(); j++) {
				// find intersection
				Set<PetrinetNode> intersection1 = new HashSet<PetrinetNode>(listOfPath.get(i));

				// if there is no intersection, at least 2 paths are founded
				if (!intersection1.removeAll(listOfPath.get(j))) {
					return true;
				}
				;
			}
		}
		return false;
	}

	/**
	 * Retrieve paths from leaf to root of a tree
	 * 
	 * 
	 * @return boolean
	 */
	private Set<PetrinetNode> retrievePath(Node<PetrinetNode> checkedNode) {
		Set<PetrinetNode> result = new HashSet<PetrinetNode>();
		Node<PetrinetNode> tempCheckedNode = checkedNode;

		while (tempCheckedNode != null) {
			result.add(tempCheckedNode.getData());
			tempCheckedNode = tempCheckedNode.getParent();
		}
		return result;
	}

	/**
	 * return true if targetNode exists in checkedNode or its ancestors
	 * 
	 * 
	 * 
	 * @return boolean
	 */
	private boolean isAlreadyExplored(Node<PetrinetNode> checkedNode, PetrinetNode targetNode) {
		if (checkedNode.getData().equals(targetNode)) {
			return true;
		} else {
			if (checkedNode.getParent() == null) {
				return false;
			} else {
				return isAlreadyExplored(checkedNode.getParent(), targetNode);
			}
		}
	}

}
