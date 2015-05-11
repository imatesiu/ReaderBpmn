package petrinet.behavioralanalysis;

import java.util.Collection;
import java.util.HashSet;
import java.util.SortedSet;
import java.util.TreeSet;


import framework.util.collection.MultiSet;
import framework.util.collection.TreeMultiSet;

import models.graphbased.directed.analysis.ComponentFactory;
import models.graphbased.directed.petrinet.PetrinetGraph;
import models.graphbased.directed.petrinet.PetrinetNode;
import petrinet.analysis.NetAnalysisInformation;
import petrinet.analysis.NonLiveSequences;
import petrinet.analysis.NonLiveTransitionsSet;
import petrinet.analysis.SiphonSet;
import petrinet.analysis.TrapSet;
import petrinet.analysis.NetAnalysisInformation.UnDetBool;
import models.graphbased.directed.petrinet.elements.Place;
import models.graphbased.directed.petrinet.elements.Transition;
import models.graphbased.directed.transitionsystem.ReachabilityGraph;
import models.graphbased.directed.transitionsystem.State;
import models.semantics.Semantics;
import models.semantics.petrinet.Marking;
import petrinet.structuralanalysis.FreeChoiceAnalyzer;

public class AbstractLivenessAnalyzer {

	protected Semantics<Marking, Transition> semantics;
	protected ReachabilityGraph reachabilityGraph;
	protected Marking[] finalMarkings;

	protected NetAnalysisInformation.LIVENESS info;
	protected NonLiveTransitionsSet nonLiveTrans;
	protected NonLiveSequences nonLiveSequences;

	/**
	 * Method to analyze liveness of any kind of petri net given a net, a
	 * marking, a semantic, and reachability graph
	 * 
	 * @param context
	 *            context of the net
	 * @param net
	 *            net to be analyzed
	 * @param state
	 *            initial state (initial marking)
	 * @param reachabilityGraph
	 *            reachability graph of this net and marking
	 * @param semantics
	 *            semantics of this net
	 * @return Nothing, but this method sets three fields: 1. The first object
	 *         is of type NetAnalysisInformation and contains whether the given
	 *         combination of net, marking, and semantics is live. 2. The second
	 *         object is of type NonLiveTransitionsSet and contains a set which
	 *         is either empty (if the net is live) or which contains the set of
	 *         non-live transitions as its only element (if the net is non
	 *         live). 3. The third object is of type NonLiveSequences and
	 *         contaisn the set of non-live sequences.
	 * @throws Exception
	 */
	protected void analyzeLivenessPetriNetPrivate( PetrinetGraph net, Marking state,
			ReachabilityGraph reachabilityGraph, Semantics<Marking, Transition> semantics, Marking... finalMarkings)
			{
		this.reachabilityGraph = reachabilityGraph;
		this.semantics = semantics;
		this.finalMarkings = finalMarkings;
		assert ((reachabilityGraph == null) || (semantics == null));

		// check connectivity between marking and net
		
		// check connectivity between reachability graph and the net
		

		// check for available freechoice property
		boolean isFreeChoice = false;
		boolean infoFound = false;
		
			
		

		if (!infoFound) {
			// check free-choice property first
			isFreeChoice = FreeChoiceAnalyzer.getNXFCClusters(net).isEmpty();
		}

		// check in NetAnalysisInformation about Free Choice property
		if (isFreeChoice) {
			// continue with liveness identification for Free Choice Net
			SiphonSet siphonSet = null;
			

			TrapSet trapSet = null;
			

			if ((siphonSet != null) && (trapSet != null)) {

				// analyze using siphon and traps
				info = analyzeLivenessOnFreeChoiceNet(state, siphonSet, trapSet);
				if (info.getValue().equals(UnDetBool.TRUE)) {
					// add connection

					nonLiveTrans = new NonLiveTransitionsSet();
					nonLiveSequences = new NonLiveSequences();

					return;
				}
				// Else fall through: the system is not live, and we need to compute the non-live
				// transitions.
			}
		}

		// if an execution reaches this part, liveness needs to be decided using reachability graph
		// find or create a new reachability graph
		if (reachabilityGraph == null) {
			try {
				
				if (reachabilityGraph == null) {
					System.out.println("Liveness cannot be determined due to infinite state space");
				}
				analyzeLivenessOnNonFreeChoicePetriNet(net, state, reachabilityGraph, finalMarkings);
			
				// if the reachability graph cannot be constructed, liveness can not be decided
				info = new NetAnalysisInformation.LIVENESS();
				info.setValue(UnDetBool.UNDETERMINED);
				System.out.println("Statespace construction failed. Maximum number of states reached");
			} catch (Exception exc) {
				System.out.println("Exception happened. Maximum number of states reached");
			}
		}

	}

	/**
	 * Method to analyze Liveness, given that the net is free-choice. If all
	 * siphon contains marked traps, the net is live
	 * 
	 * @param state
	 *            initial marking
	 * @param siphonSet
	 *            set of siphon
	 * @param trapSet
	 *            set of trap
	 * @return An array of two objects. The first object is of type
	 *         NetAnalysisInformation and contains whether the given net (given
	 *         its siphons and traps) is live. The second object is of type
	 *         NonLiveTransitionsSet and contains a set which is either empty
	 *         (if the net is live) or which contains the set of non-live
	 *         transitions as its only element (if the net is non live).
	 */
	protected NetAnalysisInformation.LIVENESS analyzeLivenessOnFreeChoiceNet(Marking state, SiphonSet siphonSet,
			TrapSet trapSet) {
		// result variable
		NetAnalysisInformation.LIVENESS result = new NetAnalysisInformation.LIVENESS();

		boolean hasMarkedTrapInsideSiphon;
		// check if all siphons contains a marked trap
		for (SortedSet<Place> siphon : siphonSet) {
			hasMarkedTrapInsideSiphon = false;
			// check which traps are inside the siphon
			trapCheck: for (SortedSet<Place> trap : trapSet) {
				if (siphon.containsAll(trap)) {
					// check whether it has marking on it
					SortedSet<Place> tempTrap = new TreeSet<Place>(trap);
					if (tempTrap.removeAll(state.baseSet())) {
						hasMarkedTrapInsideSiphon = true;
						break trapCheck;
					}
				}
			}
			// if there is no traps which correspond to a siphon, return false
			if (!hasMarkedTrapInsideSiphon) {
				// if there is no intersection between state and tempTramp, fail marking test 
				result.setValue(UnDetBool.FALSE);
				return result;
			}
		}
		// manage to get through all testing, this net is live
		result.setValue(UnDetBool.TRUE);
		return result;
	}

	protected void analyzeLivenessOnNonFreeChoicePetriNet(PetrinetGraph net, Marking marking,
			ReachabilityGraph reachabilityGraph, Marking... allowedFinalMarkings) {
		info = new NetAnalysisInformation.LIVENESS();
		nonLiveTrans = getNonLiveTransitions(net, marking, reachabilityGraph);
		if (nonLiveTrans.isEmpty()) {
			info.setValue(UnDetBool.TRUE);
			nonLiveSequences = new NonLiveSequences();
		} else {
			info.setValue(UnDetBool.FALSE);
			nonLiveSequences = getNonLiveSequences(net, marking, reachabilityGraph, allowedFinalMarkings);
		}
	}

	protected NonLiveTransitionsSet getNonLiveTransitions(PetrinetGraph net, Marking marking,
			ReachabilityGraph reachabilityGraph) {
		/**
		 * Get all strongly connected components.
		 */
		Collection<Collection<State>> components = ComponentFactory.componentize(reachabilityGraph);

		/**
		 * non live transitions are also the transitions that does not appear in
		 * reachability graph see Murata's paper, Petri Nets: Properties,
		 * Analysis, and Applications, Figure 17(f) in which t1 can not fire
		 * 
		 * Get the terminal components and identify transitions that never
		 * happen even once
		 */
		Collection<Collection<State>> terminalComponents = new HashSet<Collection<State>>();
		Collection<Transition> deadTransitions = new TreeSet<Transition>(net.getTransitions());

		for (Collection<State> component : components) {
			if (ComponentFactory.isTerminal(reachabilityGraph, component)) {
				terminalComponents.add(component);
			}
			// remove transitions that are never visited in reachability graph 
			for (State state : component) {
				Collection<models.graphbased.directed.transitionsystem.Transition> edges = reachabilityGraph
						.getOutEdges(state);
				for (models.graphbased.directed.transitionsystem.Transition edge : edges) {
					// remove every transition that appears
					deadTransitions.remove(edge.getIdentifier());
				}
			}
		}

		// At this point, deadTransitions will only consist transitions that are never visited in reachability graph

		/**
		 * A transition is not live iff it is dead in any terminal component. or
		 * if there is transition that can never fires
		 */
		TreeSet<Transition> nonLiveTransitions = new TreeSet<Transition>();
		nonLiveTransitions.addAll(deadTransitions); // this adds the transitions that never be visited in reachability graph 

		// now, time to check transitions that are dead and can be identified from terminal components

		for (Collection<State> component : terminalComponents) {
			deadTransitions = new TreeSet<Transition>(net.getTransitions());
			for (State state : component) {
				Collection<models.graphbased.directed.transitionsystem.Transition> edges = reachabilityGraph
						.getOutEdges(state);
				for (models.graphbased.directed.transitionsystem.Transition edge : edges) {
					if (component.contains(edge.getTarget())) {
						deadTransitions.remove(edge.getIdentifier());
					}
				}
			}
			nonLiveTransitions.addAll(deadTransitions);
		}

		NonLiveTransitionsSet nonLiveTransitionsSet = new NonLiveTransitionsSet();
		if (nonLiveTransitions.size() > 0) {
			nonLiveTransitionsSet.add(nonLiveTransitions);
		}
		// end of modification
		return nonLiveTransitionsSet;
	}

	protected NonLiveSequences getNonLiveSequences(PetrinetGraph net, Marking marking,
			ReachabilityGraph reachabilityGraph, Marking... allowedFinalMarkings) {

		NonLiveSequences sequences = new NonLiveSequences();
		Collection<State> greenStates = new HashSet<State>();
		Collection<State> yellowStates = new HashSet<State>();
		Collection<State> redStates = new HashSet<State>();

		/**
		 * First, all states are colored red.
		 */
		redStates.addAll(reachabilityGraph.getNodes());
		/**
		 * Second, all states from which a final state can be reached are
		 * colored green.
		 */
		State initialState = reachabilityGraph.getNode(marking);
		for (Marking finalMarking : allowedFinalMarkings) {
			State finalState = reachabilityGraph.getNode(finalMarking);
			if (finalState != null) {
				colorBackwards(reachabilityGraph, finalState, greenStates, redStates, initialState);
			}
		}
		/**
		 * Third, color all green states from which a red state can be reached
		 * yellow.
		 */
		if (!greenStates.isEmpty()) {
			for (models.graphbased.directed.transitionsystem.Transition edge : reachabilityGraph
					.getEdges()) {
				if (redStates.contains(edge.getTarget())) {
					colorBackwards(reachabilityGraph, edge.getSource(), yellowStates, greenStates, initialState);
				}
			}
		}

		/**
		 * Colored the reachability graph. Now find edges with a yellow source
		 * and a red target. Such edges disable the option to reach the final
		 * state, and should hence non be taken.
		 */
		if (yellowStates.isEmpty()) {
			/**
			 * No yellow states, hence no green state, hence final state not
			 * reachable. Put all transitions in the sequence to visualize this.
			 */
			MultiSet<PetrinetNode> sequence = new TreeMultiSet<PetrinetNode>();
			sequence.addAll(net.getTransitions());
			sequences.add(sequence);
		} else {
			for (models.graphbased.directed.transitionsystem.Transition edge : reachabilityGraph
					.getEdges()) {
				if (yellowStates.contains(edge.getSource()) && redStates.contains(edge.getTarget())) {
					MultiSet<PetrinetNode> sequence = new TreeMultiSet<PetrinetNode>();
					sequence.addAll((Marking) edge.getSource().getIdentifier());
					sequence.add((Transition) edge.getIdentifier());
					sequences.add(sequence);
				}
			}
		}

		return sequences;
	}

	protected void colorBackwards(ReachabilityGraph reachabilityGraph, State state, Collection<State> newCollection,
			Collection<State> oldCollection, State initialState) {
		if (oldCollection.contains(state)) {
			oldCollection.remove(state);
			newCollection.add(state);
			if (state != initialState) {
				for (models.graphbased.directed.transitionsystem.Transition incoming : reachabilityGraph
						.getInEdges(state)) {
					colorBackwards(reachabilityGraph, incoming.getSource(), newCollection, oldCollection, initialState);
				}
			}
		}
	}
}
