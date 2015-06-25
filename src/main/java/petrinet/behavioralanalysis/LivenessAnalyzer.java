/***********************************************************
 * This software is part of the ProM package * http://www.processmining.org/ * *
 * Copyright (c) 2003-2008 TU/e Eindhoven * and is licensed under the * LGPL
 * License, Version 1.0 * by Eindhoven University of Technology * Department of
 * Information Systems * http://www.processmining.org * *
 ***********************************************************/

package petrinet.behavioralanalysis;

import models.graphbased.directed.petrinet.Petrinet;
import models.graphbased.directed.petrinet.PetrinetGraph;
import models.graphbased.directed.transitionsystem.ReachabilityGraph;
import models.semantics.petrinet.Marking;
import models.semantics.petrinet.PetrinetSemantics;
import models.semantics.petrinet.impl.PetrinetSemanticsFactory;
import petrinet.analysis.NonLiveTransitionsSet;

/**
 * Class to analyze whether a given net is live based on Murata, Tadao. Petri
 * Nets:Properties, Analysis, and Applications. Proceedings of the IEEE vol. 77,
 * No.4, April 1989 . If a net is a free-choice net, it is live iff every siphon
 * contains a marked traps. If it's not, construct a reachability graph. If it
 * has all transitions and every leaf is the same as root node, the net is live.
 * 
 * 
 * 
 * 
 */
public class LivenessAnalyzer extends AbstractLivenessAnalyzer {

	private Object[] finalizeResult( PetrinetGraph net, Marking state) {
		
		System.out.println("Liveness Analysis of " + net.getLabel());
		System.out.println("Non-Live Transitions of " + net.getLabel());

		if (nonLiveTrans == null) {
			// means that the net may not be live, reachability graph cannot be constructed, and non live transition cannot be determined
			nonLiveTrans = new NonLiveTransitionsSet();
		}

		
		return new Object[] { info, nonLiveTrans };

	}

	/**
	 * Variant of net and marking
	 */
	// variant with Petrinet and marking
	
	public Object[] analyzeLivenessPetriNet( Petrinet net, Marking state)
			 {
		
		TSGenerator tsg = new TSGenerator();
		Object[] result = tsg.calculateTS(net, state);
		analyzeLivenessPetriNetPrivate( net, state,(ReachabilityGraph) result[0], PetrinetSemanticsFactory
				.regularPetrinetSemantics(Petrinet.class));

		return finalizeResult( net, state);
	}

	

	

	/**
	 * Variant of net, marking, and semantics
	 */
	// variant with Petrinet and marking
	
	public Object[] analyzeLivenessPetriNet( Petrinet net, Marking state,
			PetrinetSemantics semantics)  {
		
		analyzeLivenessPetriNetPrivate( net, state,null, semantics);
		return finalizeResult( net, state);
	}




	/**
	 * Variant of net, marking, and reachability graph
	 */
	// variant with Petrinet and marking
	
	public Object[] analyzeLivenessPetriNet( Petrinet net, Marking state,
			ReachabilityGraph reachabilityGraph)  {
		analyzeLivenessPetriNetPrivate( net, state, reachabilityGraph, null);
		return finalizeResult( net, state);
	}



}
