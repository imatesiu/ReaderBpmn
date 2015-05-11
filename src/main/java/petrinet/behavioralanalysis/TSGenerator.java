package petrinet.behavioralanalysis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;


import framework.connections.ConnectionCannotBeObtained;

import models.graphbased.directed.petrinet.Petrinet;
import models.graphbased.directed.petrinet.PetrinetGraph;

import models.graphbased.directed.petrinet.ResetNet;
import petrinet.analysis.NetAnalysisInformation;
import petrinet.analysis.NetAnalysisInformation.UnDetBool;
import petrinet.analysis.ReachabilitySet;
import models.graphbased.directed.petrinet.elements.Transition;
import models.graphbased.directed.transitionsystem.AcceptStateSet;
import models.graphbased.directed.transitionsystem.CoverabilityGraph;
import models.graphbased.directed.transitionsystem.ReachabilityGraph;
import models.graphbased.directed.transitionsystem.StartStateSet;
import models.graphbased.directed.transitionsystem.State;
import models.semantics.IllegalTransitionException;
import models.semantics.Semantics;
import models.semantics.petrinet.CTMarking;

import models.semantics.petrinet.Marking;
import models.semantics.petrinet.PetrinetSemantics;

import models.semantics.petrinet.impl.PetrinetSemanticsFactory;



public class TSGenerator {

	private static final int MAXSTATES = 25000;

	
	
	public Object[] calculateTS(Petrinet net, Marking state) throws ConnectionCannotBeObtained {
		return calculateTS( net, state, PetrinetSemanticsFactory.regularPetrinetSemantics(Petrinet.class));
	}

	
	
	
	
	public Object[] calculateTS(Petrinet net, Marking state, CoverabilityGraph graph)
			throws ConnectionCannotBeObtained {
		return build( net, state, graph);
	}

	
	public Object[] calculateTS(ResetNet net, Marking state, CoverabilityGraph graph)
			throws ConnectionCannotBeObtained {
		return build( net, state, graph);
	}

	

	public Object[] calculateTS(Petrinet net, Marking state, PetrinetSemantics semantics)
			 {
		semantics.initialize(net.getTransitions(), new Marking(state));
		return build( net, state, semantics, null);
	}

	
	
	private Object[] build(PetrinetGraph net, Marking initial,
			CoverabilityGraph coverabilityGraph) throws ConnectionCannotBeObtained {

		
		PetrinetSemantics sem=null;
		sem.initialize(net.getTransitions(), initial);
		return build( net, initial, sem, coverabilityGraph);
	}

	private Object[] build(PetrinetGraph net, Marking initial,
			Semantics<Marking, Transition> semantics, CoverabilityGraph coverabilityGraph)
			 {
		
		ReachabilityGraph ts = null;
		NetAnalysisInformation.BOUNDEDNESS info = null;

		

		if ((info != null) && info.getValue().equals(UnDetBool.FALSE)) {
			// This net has been shows to be unbounded on this marking
			System.out.println("The given net is unbounded on the given initial marking, no Statespace is constructed.");
			
			// unreachable statement, but safe.
			return null;
		}
		// boolean bounded = (info != null);

		// if (coverabilityGraph == null && !bounded) {
		// // If boundedness is unknown, try to construct a coverability graph
		// coverabilityGraph =
		// context.tryToFindOrConstructFirstObject(CoverabilityGraph.class,
		// CoverabilityGraphConnection.class,
		// CoverabilityGraphConnection.STATEPACE, net, initial, semantics);
		// }

		if (coverabilityGraph != null) {// && !bounded) {
			if (!BoundednessAnalyzer.isBounded(coverabilityGraph)) {
				// This net has been shows to be unbounded on this marking
				System.out.println("The given net is unbounded on the given initial marking, no Statespace is constructed.");
				
				// unreachable statement, but safe.
				return null;
			}
			// clone the graph and return
			Map<CTMarking, Marking> mapping = new HashMap<CTMarking, Marking>();

			ts = new ReachabilityGraph("StateSpace of " + net.getLabel());
			for (Object o : coverabilityGraph.getStates()) {
				CTMarking m = (CTMarking) o;
				Marking tsm = new Marking(m);
				ts.addState(tsm);
				mapping.put(m, tsm);
			}
			for (models.graphbased.directed.transitionsystem.Transition e : coverabilityGraph
					.getEdges()) {
				Marking source = mapping.get(e.getSource().getIdentifier());
				Marking target = mapping.get(e.getTarget().getIdentifier());
				ts.addTransition(source, target, e.getIdentifier());
			}

		}

		StartStateSet startStates = new StartStateSet();
		startStates.add(initial);

		if (ts == null) {
			ts = doBreadthFirst( net.getLabel(), initial, semantics, MAXSTATES);
		}
		if (ts == null) {
			// Problem with the reachability graph.
			System.out.println("Problem with the reachability graph.");
			return null;
		}

		AcceptStateSet acceptingStates = new AcceptStateSet();
		for (State state : ts.getNodes()) {
			if (ts.getOutEdges(state).isEmpty()) {
				acceptingStates.add(state.getIdentifier());
			}
		}

		Marking[] markings = ts.getStates().toArray(new Marking[0]);
		ReachabilitySet rs = new ReachabilitySet(markings);

		

		System.out.println("Reachability graph of " + net.getLabel());
		System.out.println("Reachability set of " + net.getLabel());
		System.out.println("Initial states of " + ts.getLabel());
		System.out.println("Accepting states of " + ts.getLabel());

		System.out.println("Statespace size: " + ts.getStates().size() + " states and " + ts.getEdges().size()
				+ " transitions.");

		return new Object[] { ts, rs, startStates, acceptingStates };
	}

	private ReachabilityGraph doBreadthFirst(String label, Marking state,
			Semantics<Marking, Transition> semantics, int max) {
		ReachabilityGraph ts = new ReachabilityGraph("StateSpace of " + label);
		ts.addState(state);
		Queue<Marking> newStates = new LinkedList<Marking>();
		newStates.add(state);
		do {
			newStates.addAll(extend(ts, newStates.poll(), semantics));
		} while (!newStates.isEmpty() && (ts.getStates().size() < max));
		if (!newStates.isEmpty()) {
			// This net has been shows to be unbounded on this marking
			System.out.println("The behaviour of the given net is has over " + max + " states. Aborting...");
			
			return null;
		}
		return ts;

	}

	private Set<Marking> extend(ReachabilityGraph ts, Marking state, Semantics<Marking, Transition> semantics
			) {
		Set<Marking> newStates = new HashSet<Marking>();
		semantics.setCurrentState(state);
		for (Transition t : semantics.getExecutableTransitions()) {
			semantics.setCurrentState(state);
			try {
				/*
				 * [HV] The local variable info is never read
				 * ExecutionInformation info =
				 */semantics.executeExecutableTransition(t);
				// System.out.println(info.toString());
			} catch (IllegalTransitionException e) {
				System.out.println(e);
				assert (false);
			}
			Marking newState = semantics.getCurrentState();

			if (ts.addState(newState)) {
				newStates.add(newState);
				int size = ts.getEdges().size();
				if (size % 1000 == 0) {
					System.out.println("Statespace size: " + ts.getStates().size() + " states and " + ts.getEdges().size()
							+ " transitions.");
				}
			}
			ts.addTransition(state, newState, t);
			semantics.setCurrentState(state);
		}
		return newStates;

	}
}
