/***********************************************************
 * This software is part of the ProM package * http://www.processmining.org/ * *
 * Copyright (c) 2003-2008 TU/e Eindhoven * and is licensed under the * LGPL
 * License, Version 1.0 * by Eindhoven University of Technology * Department of
 * Information Systems * http://www.processmining.org * *
 ***********************************************************/

package petrinet.behavioralanalysis;

import framework.connections.ConnectionCannotBeObtained;
import models.graphbased.directed.petrinet.Petrinet;
import models.graphbased.directed.petrinet.PetrinetGraph;
import models.graphbased.directed.petrinet.ResetNet;
import petrinet.analysis.NetAnalysisInformation;
import petrinet.analysis.NonLiveSequences;
import petrinet.analysis.NonLiveTransitionsSet;
import models.graphbased.directed.transitionsystem.ReachabilityGraph;
import models.semantics.petrinet.Marking;
import models.semantics.petrinet.PetrinetSemantics;
import models.semantics.petrinet.impl.PetrinetSemanticsFactory;

/**
 * Class to analyze whether a given net is live based on Murata, Tadao. Petri
 * Nets:Properties, Analysis, and Applications. Proceedings of the IEEE vol. 77,
 * No.4, April 1989 . If a net is a free-choice net, it is live iff every siphon
 * contains a marked traps. If it's not, construct a reachability graph. If it
 * has all transitions and every leaf is the same as root node, the net is live.
 * 
 * @author arya
 * @email arya.adriansyah@gmail.com
 * @version Dec 10, 2008
 */

public class LivenessAnalyzerWithSequence extends AbstractLivenessAnalyzer {

	private Object[] finalizeResult( PetrinetGraph net, Marking state,
			Marking[] allowedFinalMarkings) {
		
		System.out.println("Liveness Analysis of " + net.getLabel());
		System.out.println("Non-Live Transitions of " + net.getLabel());
		System.out.println("Non-Live Sequences of " + net.getLabel());

		
		return new Object[] { info, nonLiveTrans, nonLiveSequences };

	}

	/**
	 * Variant of net and marking
	 */
	// variant with Petrinet and marking
	
	public Object[] analyzeLivenessPetriNet( Petrinet net, Marking state, Marking[] finalMarkings)
			throws ConnectionCannotBeObtained {
		analyzeLivenessPetriNetPrivate(net, state, null, PetrinetSemanticsFactory
				.regularPetrinetSemantics(Petrinet.class), finalMarkings);
		return finalizeResult(net, state, finalMarkings);
	}

 

	/**
	 * Variant of net, marking, and semantics
	 */
	// variant with Petrinet and marking
	
	public Object[] analyzeLivenessPetriNet( Petrinet net, Marking state,
			PetrinetSemantics semantics, Marking[] finalMarkings) throws ConnectionCannotBeObtained {
		analyzeLivenessPetriNetPrivate(net, state, null, semantics, finalMarkings);
		return finalizeResult(net, state, finalMarkings);
	}
 

	// variant with ResetInhibitorNet and marking
	 

	/**
	 * Variant of net, marking, and reachability graph
	 */
	// variant with Petrinet and marking
	
	public Object[] analyzeLivenessPetriNet( Petrinet net, Marking state,
			ReachabilityGraph reachabilityGraph, Marking[] finalMarkings) throws ConnectionCannotBeObtained {
		analyzeLivenessPetriNetPrivate(net, state, reachabilityGraph, null, finalMarkings);
		return finalizeResult(net, state, finalMarkings);
	}

 

	// variant with ResetNet and marking
	
	public Object[] analyzeLivenessPetriNet( ResetNet net, Marking state,
			ReachabilityGraph reachabilityGraph, Marking[] finalMarkings) throws ConnectionCannotBeObtained {
		analyzeLivenessPetriNetPrivate(net, state, reachabilityGraph, null, finalMarkings);
		return finalizeResult(net, state, finalMarkings);
	}
 

}
