/***********************************************************
 * This software is part of the ProM package * http://www.processmining.org/ * *
 * Copyright (c) 2003-2008 TU/e Eindhoven * and is licensed under the * LGPL
 * License, Version 1.0 * by Eindhoven University of Technology * Department of
 * Information Systems * http://www.processmining.org * *
 ***********************************************************/

package petrinet.behavioralanalysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import framework.connections.ConnectionCannotBeObtained;



import models.graphbased.directed.petrinet.Petrinet;
import models.graphbased.directed.petrinet.PetrinetGraph;
import petrinet.analysis.CoverabilitySet;
import models.graphbased.directed.petrinet.elements.Place;
import models.graphbased.directed.petrinet.elements.Transition;
import models.graphbased.directed.transitionsystem.AcceptStateSet;
import models.graphbased.directed.transitionsystem.CoverabilityGraph;
import models.graphbased.directed.transitionsystem.StartStateSet;
import models.graphbased.directed.transitionsystem.State;
import models.graphbased.directed.transitionsystem.TransitionSystem;
import models.graphbased.directed.transitionsystem.TransitionSystemFactory;
import models.graphbased.directed.utils.Node;
import models.semantics.IllegalTransitionException;
import models.semantics.Semantics;
import models.semantics.petrinet.CTMarking;

import models.semantics.petrinet.Marking;
import models.semantics.petrinet.PetrinetSemantics;
import models.semantics.petrinet.impl.PetrinetSemanticsFactory;

/**
 * This class represents a plugin to transform net into coverability graph
 * 
 * 
 * 
 * 
 */
public class CGGenerator {

	// variant with Petrinet and marking
	public   Object[] petriNetToCoverabilityGraph( Petrinet net, Marking state)
			 {
		return petrinetetToCoverabilityGraph( net, state, PetrinetSemanticsFactory
				.regularPetrinetSemantics(Petrinet.class));
	}

	// variant with InhibitorNet and marking
	


	// variant with PetriNet, marking, and semantic
	
	public  Object[] petrinetetToCoverabilityGraph( Petrinet net, Marking state,
			PetrinetSemantics semantics)  {
		return buildAndConnect( net, state, semantics);
	}

	// variant with InhibitorNet, marking, and semantic
	
	

	// variant wiithout context
	public static CoverabilityGraph getCoverabilityGraph(Petrinet net, Marking initial,
			Semantics<Marking, Transition> semantics) {
		CGGenerator generator = new CGGenerator();
		CoverabilityGraph ts = generator.doBreadthFirst( net.getLabel(), new CTMarking(initial), semantics);
		return ts;
	}

	

	/**
	 * Method to build a coverability graph, as well as make its connection
	 * 
	 * 
	 *            Context of the net
	 * 
	 *            net to be analyzed
	 * 
	 *            Initial state (initial marking)
	 * 
	 *            Semantic of this net
	 * 
	 *         AcceptStateSet
	 * 
	 */
	private Object[] buildAndConnect( PetrinetGraph net, Marking initial,
			Semantics<Marking, Transition> semantics)  {
		semantics.initialize(net.getTransitions(), initial);
		CoverabilityGraph ts = doBreadthFirst( net.getLabel(), new CTMarking(initial), semantics);

		StartStateSet startStates = new StartStateSet();
		startStates.add(new CTMarking(initial));

		AcceptStateSet acceptingStates = new AcceptStateSet();
		for (State state : ts.getNodes()) {
			if (ts.getOutEdges(state).isEmpty()) {
				acceptingStates.add(state.getIdentifier());
			}
		}

		CTMarking[] markings = ts.getStates().toArray(new CTMarking[0]);
		CoverabilitySet cs = new CoverabilitySet(markings);
	

		System.out.println("Coverability graph of " + net.getLabel());
		System.out.println("Coverability set of " + net.getLabel());
		System.out.println("Initial states of " + ts.getLabel());
		System.out.println("Accepting states of " + ts.getLabel());

		System.out.println("Coverability Graph size: " + ts.getStates().size() + " states and " + ts.getEdges().size()
				+ " transitions.");

		return new Object[] { ts, cs, startStates, acceptingStates };
	}

	/**
	 * Build a coverability graph from initial state with breadth-first approach
	 * 
	 * 
	 *            context of the net
	 * 
	 *            label of the net
	 * 
	 *            Initial state (initial marking)
	 * 
	 *            semantics obtained from initial state
	 * 
	 */
	public CoverabilityGraph doBreadthFirst( String label, CTMarking state,
			Semantics<Marking, Transition> semantics) {

		// work using tree and transition system in parallel
		Node<CTMarking> root = new Node<CTMarking>();
		// create a tree to describe the coverability tree

		root.setData(new CTMarking(state));
		root.setParent(null);

		// build transitionSystem based on the root node
		CoverabilityGraph ts = TransitionSystemFactory.newCoverabilityGraph("Coverability Graph of " + label);
		ts.addState(state);

		// expands all
		Queue<Node<CTMarking>> expandedNodes = new LinkedList<Node<CTMarking>>();
		expandedNodes.add(root);

		// if CG is used as an intermediary result, no need to have context as a parameter
		// therefore the context should be null
		// checking context inside of extend methods
		do {
			expandedNodes.addAll(extend(expandedNodes.poll(), semantics,  ts));
		} while (!expandedNodes.isEmpty());
		return ts;
	}

	/**
	 * Extend an input state to get all of its children by executing available
	 * transition. Omega notation is added as needed
	 * 
	 * 
	 *            state to be extended, represented in form of element of a tree
	 * 
	 *            semantic of current net
	 * 
	 *            Context of the net
	 * 
	 *            transition system associated with the tree
	 * 
	 */
	private Collection<? extends Node<CTMarking>> extend(Node<CTMarking> root,
			Semantics<Marking, Transition> semantics,  TransitionSystem ts) {
		// init
		Marking rootState = root.getData();
		semantics.setCurrentState(rootState);

		List<Node<CTMarking>> needToBeExpanded = new ArrayList<Node<CTMarking>>();
		// this is the variable to be returned
		//		if (context != null)
		//			System.out.println("Current root = " + root.getData().toString());

		// execute transitions
		for (Transition t : semantics.getExecutableTransitions()) {
			//			if (context != null)
			//				System.out.println("Transition going to be executed : " + t.getLabel());
			semantics.setCurrentState(rootState);
			try {
				/*
				 * [HV] The local variable info is never read
				 * ExecutionInformation info =
				 */semantics.executeExecutableTransition(t);
				//				if (context != null)
				//					System.out.println(info.toString());
			} catch (IllegalTransitionException e) {
				e.printStackTrace();
				assert (false);
			}
			//			if (context != null)
			//				System.out.println("After execution= " + semantics.getCurrentState().toString());

			// convert current state to CTMarking
			// change the place in all the nodes of the tree with omega
			// representation
			CTMarking currStateCTMark = new CTMarking(semantics.getCurrentState());
			if (root.getData().hasOmegaPlace()) {
				currStateCTMark = currStateCTMark.transformToOmega(root.getData().getOmegaPlaces());
			}

			// currStateCTMark node
			Node<CTMarking> currStateCTMarkNode = new Node<CTMarking>();

			// is newState marking identical to a marking on the path from the
			// root?
			CTMarking lessOrEqualMarking = null;
			//			if (context != null)
			//				System.out.println("currStateCTMark = " + currStateCTMark.toString());
			//			if (context != null)
			//				System.out.println("root = " + root.getData().toString());

			for (State node : ts.getNodes()) {
				if (node.getIdentifier().equals(currStateCTMark)) {
					lessOrEqualMarking = currStateCTMark;
				}
			}
			if (lessOrEqualMarking == null) {
				lessOrEqualMarking = getIdenticalOrCoverable(currStateCTMark, root);
			}
			if (lessOrEqualMarking != null) {

				//				if (context != null)
				//					System.out.println("less or equal is found");
				//				if (context != null)
				//					System.out.println(lessOrEqualMarking.toString());

				// check if the state is the same
				if (!lessOrEqualMarking.equals(currStateCTMark)) {
					// if not the same, check in case there are places that
					// needs to be changed into omega
					//					if (context != null)
					//						System.out.println("Equal node is not found. Coverable node is found");

					// The places that need to be marked as omega, are those places that
					// occur more often in currStateCTMark, than in lessOrEqualMarking
					CTMarking temp = new CTMarking(currStateCTMark);
					temp.removeAll(lessOrEqualMarking);

					Set<Place> listToBeChanged = temp.baseSet();

					// list of places need to be transformed into omega
					currStateCTMark = currStateCTMark.transformToOmega(listToBeChanged);

					// set the node
					currStateCTMarkNode.setData(currStateCTMark);
					currStateCTMarkNode.setParent(root);

					root.addChild(currStateCTMarkNode); // insert the new node
					// to root

					// update transition system
					ts.addState(currStateCTMark);
					int size = ts.getStates().size();
					if (size % 1000 == 0) {
						System.out.println("Coverability Graph size: " + size + " states and " + ts.getEdges().size()
								+ " transitions.");
					}
					// BVD:new Marking(currStateCTMark));
					ts.addTransition(rootState, currStateCTMark, t);
					// BVD: new Marking(currStateCTMarkNode.getData()),
					//					if (context != null)
					//						System.out.println("Added Child (also need to be expanded): "
					//								+ currStateCTMarkNode.getData().toString());

					// node to be expanded
					needToBeExpanded.add(currStateCTMarkNode);
				} else { // exactly the same node is found
					//					if (context != null)
					//						System.out.println("Equal node is found");
					// just set the node and add
					// set the node
					currStateCTMarkNode.setData(lessOrEqualMarking);
					currStateCTMarkNode.setParent(root);

					root.addChild(currStateCTMarkNode); // insert the new node
					// to root

					// update transition system
					ts.addState(currStateCTMark);
					// BVD:new Marking(currStateCTMark));
					ts.addTransition(rootState, currStateCTMark, t);
					// BVD: new Marking(currStateCTMarkNode.getData()),

					//					if (context != null)
					//						System.out.println("Added Child : " + lessOrEqualMarking.toString());
				}
				//				if (context != null)
				//					System.out.println("root after = " + root.toString());
			} else {
				// set the node
				currStateCTMarkNode.setData(currStateCTMark);
				currStateCTMarkNode.setParent(root);

				root.addChild(currStateCTMarkNode); // insert the new node to
				// root

				// update transition system
				ts.addState(currStateCTMark);
				int size = ts.getStates().size();
				if (size % 1000 == 0) {
					System.out.println("Coverability Graph size: " + size + " states and " + ts.getEdges().size()
							+ " transitions.");
				}
				// BVD:new Marking(currStateCTMark));
				ts.addTransition(rootState, currStateCTMark, t);
				// BVD: new Marking(currStateCTMarkNode.getData()),

				// node only need to be expanded
				needToBeExpanded.add(currStateCTMarkNode);

				//				if (context != null)
				//					System.out.println("Added Child (also need to be expanded): " + currStateCTMarkNode.getData().toString(),
				//							MessageLevel.DEBUG);
			}
			//			if (context != null)
			//				System.out.println("---------------");
		}
		return needToBeExpanded;
	}

	/**
	 * Return an identical node or a node coverable by newState node. The node
	 * should be on the top of tree hierarchy. Return null if there is no
	 * identical node nor coverable node.
	 * 
	 * 
	 * 
	 *            node which represent the parent of the newState node
	 * 
	 */
	private CTMarking getIdenticalOrCoverable(CTMarking newState, Node<CTMarking> referenceNode) {
		if (referenceNode.getParent() != null) { // checking not against root
			// node
			if (referenceNode.getData().isLessOrEqual(newState)) {
				return referenceNode.getData();
			} else {
				return getIdenticalOrCoverable(newState, referenceNode.getParent());
			}
		} else { // now checking against the root node
			if (referenceNode.getData().isLessOrEqual(newState)) {
				return referenceNode.getData();
			} else {
				return null;
			}
		}
	}
}