package petrinet.behavioralanalysis.woflan;

import java.util.Collection;
import java.util.HashSet;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;





import framework.util.collection.MultiSet;
import models.graphbased.directed.petrinet.Petrinet;
import models.graphbased.directed.petrinet.PetrinetNode;
import petrinet.analysis.DeadTransitionsSet;
import petrinet.analysis.NonExtendedFreeChoiceClustersSet;
import petrinet.analysis.NonLiveSequences;
import petrinet.analysis.NonLiveTransitionsSet;
import petrinet.analysis.NotPCoveredNodesSet;
import petrinet.analysis.NotSCoveredNodesSet;
import petrinet.analysis.PTHandles;
import petrinet.analysis.PlaceInvariantSet;
import petrinet.analysis.SComponentSet;
import petrinet.analysis.TPHandles;
import petrinet.analysis.UnboundedPlacesSet;
import petrinet.analysis.UnboundedSequences;
import petrinet.analysis.WorkflowNetUtils;
import petrinet.behavioralanalysis.AbstractLivenessAnalyzer;
import petrinet.behavioralanalysis.BoundednessAnalyzer;
import petrinet.behavioralanalysis.DeadTransitionAnalyzer;
import petrinet.behavioralanalysis.LivenessAnalyzer;
import petrinet.structuralanalysis.FreeChoiceAnalyzer;
import petrinet.structuralanalysis.PTHandlesGenerator;
import petrinet.structuralanalysis.SComponentGenerator;
import petrinet.structuralanalysis.TPHandlesGenerator;
import petrinet.structuralanalysis.invariants.PlaceInvariantCalculator;
import models.graphbased.directed.petrinet.elements.Place;
import models.semantics.petrinet.Marking;
import models.semantics.petrinet.PetrinetSemantics;
import models.semantics.petrinet.impl.PetrinetSemanticsFactory;

/**
 * Woflan
 * 
 * Woflan diagnosis for a Petri net.
 * 
 * @author HVERBEEK
 * 
 */	
public class Woflan {

	
	/**
	 * The current diagnostic results.
	 */
	private WoflanDiagnosis diagnosis;

	/**
	 * The current state of the diagnosis: to start.
	 */
	private WoflanState state = WoflanState.INIT;

	private Collection<WoflanState> assumptions = new HashSet<WoflanState>();

	/**
	 * The initial marking of the short-circuited net. Note that the net and the
	 * short-circuited net are kept by the diagnosis.
	 * 
	 * Invariant: marking == null || marking == diagnosis.shortCNet
	 */
	private Marking initialMarking;
	private Marking finalMarking;

	/**
	 * Public constructor of Woflan
	 */
	public Woflan() {
		initialMarking = null;
		finalMarking = null;
	}

	/**
	 * The Woflan plug-in using no assumptions.
	 * 
	 * 
	 *            The context of this plug-in.
	 * 
	 *            The net to diagnose.
	 * @return The diagnosis of the net.
	 * @throws ExecutionException
	 * @throws InterruptedException
	 * @throws CancellationException
	 * @throws Exception
	 */
	
	
	public WoflanDiagnosis diagnose( Petrinet net) throws Exception {
		return diagnose( net, new HashSet<WoflanState>());
	}

	/**
	 * The Woflan plug-in using assumptions.
	 * 
	 * 
	 *            The context of this plug-in.
	 * 
	 *            The net to diagnose.
	 * 
	 *            The assumptions to make.
	 * @return The diagnosis of the net, given the assumptions.
	 * @throws CancellationException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	//TODO: BVD: A plugin variant cannot accept parameters using generics. At runtime,
	// these generics are lost, i.e. every Collection would be OK as a parameter, such as, 
	// for example a Marking, which is a MultiSet<Place> which extends Collection<Place> 
	// A Special wrapping object has to be defined for Collection<WoflanState>	
	//	
	//	
	public WoflanDiagnosis diagnose( Petrinet net, Collection<WoflanState> assumptions)
			 {
		
		
		/**
		 * Store the assumptions for future use.
		 */
		this.assumptions = assumptions;
		/**
		 * Initialize the diagnosis.
		 */
		diagnosis = null;
		
		
		if (diagnosis == null) {
			/**
			 * No, it does not exist. create a new one.
			 */
			diagnosis = new WoflanDiagnosis(net);
			/**
			 * Do the diagnosis.
			 */
			while (state != WoflanState.DONE) {
				state = diagnose(state);
			}
		
				System.out.println("Woflan Diagnosis of net " + net.getLabel());
			
			System.out.println(diagnosis.toString());
			//context.addConnection(new WoflanConnection(net, diagnosis, assumptions));
		}
		return diagnosis;
	}

	/**
	 * Takes a next diagnosis step.
	 * 
	 * 
	 *            The current state.
	 * @return The next state.
	 * @throws ExecutionException
	 * @throws InterruptedException
	 * @throws CancellationException
	 * @throws Exception
	 */
	public WoflanState diagnose(WoflanState state)  {
		switch (state) {
			case BOUNDED : {
				System.out.println("Net is bounded.");
				state = diagnoseDeadness();
				break;
			}
			case FREECHOICE : {
				System.out.println("Net is free-choice.");
				state = diagnosePCover();
				break;
			}
			case INIT : {
				System.out.println("Start of diagnosis.");
				state = diagnoseWFNet();
				break;
			}
			case LIVE : {
				System.out.println("Net is live.");
				state = WoflanState.SOUND;
				break;
			}
			case NONFREECHOICE : {
				System.out.println("Net is not free-choice.");
				state = diagnosePTHandles();
				break;
			}
			case NOPCOVER : {
				System.out.println("Net is not covered by P-invariants.");
				state = diagnoseBoundedness();
				break;
			}
			case NOSCOVER : {
				System.out.println("Net is not covered by S-components.");
				state = diagnoseFC();
				break;
			}
			case NOTDEAD : {
				System.out.println("Net contains no dead transitions.");
				state = diagnoseLiveness();
				break;
			}
			case NOTWELLPTHANDLED : {
				System.out.println("Net contains PT-handles.");
				state = diagnoseTPHandles();
				break;
			}
			case NOTWELLTPHANDLED : {
				System.out.println("Net contains TP-handles.");
				state = diagnosePCover();
				break;
			}
			case PCOVER : {
				System.out.println("Net is covered by P-invariants.");
				state = WoflanState.BOUNDED;
				break;
			}
			case SCOVER : {
				System.out.println("Net is covered by S-components.");
				state = WoflanState.BOUNDED;
				break;
			}
			case SOUND : {
				System.out.println("Net is sound.");
				state = WoflanState.DONE;
				diagnosis.setSound();
				break;
			}
			case WELLPTHANDLED  : {
				System.out.println("Net contains no PT-handles.");
				state = diagnoseTPHandles();
				break;
			}
			case WELLTPHANDLED : {
				System.out.println("Net contains no TP-handles.");
				state = diagnosePCover();
				break;
			}
			case WFNET : {
				System.out.println("Net is a WF-net.");
				state = diagnoseSCover();
				break;
			}
			default : {
				System.out.println("----");
				state = diagnoseTPHandles();
				state = WoflanState.DONE;
				break;
			}
		}
		return state;
	}

	/**
	 * Returns a copy of the net which has an extra transition with (1) all sink
	 * places as inputs and (2) all source places as output. If such a copy
	 * cannot be found, it is created and returned. Otherwise, an existing copy
	 * is returned.
	 */
	private void shortCircuit() {
		diagnosis.shortCNet = null;
		initialMarking = null;
		finalMarking = null;
		
		/**
		 * If marking != null, the diagnosis.shortCNet != null.
		 */
		if (initialMarking == null) {
			/**
			 * No existing copy found. Create a copy.
			 */
			Object[] objects = WorkflowNetUtils.shortCircuit( diagnosis.net);
			diagnosis.shortCNet = (Petrinet) objects[0];
			initialMarking = (Marking) objects[1];
			finalMarking = (Marking) objects[2];
			/**
			 * Add the copy to the object pool.
			 */
		//	context.getProvidedObjectManager().createProvidedObject(
		//			"Short-circuited net of " + diagnosis.net.getLabel(), diagnosis.shortCNet, context);
			/**
			 * Add connection from the original net to this copy to the
			 * connection pool. As a result, this copy might be found next time.
			 */
		//	context.addConnection(new ShortCircuitedNetConnection(diagnosis.net, diagnosis.shortCNet, finalMarking));
		//	context.getProvidedObjectManager().createProvidedObject(
		//			"Initial marking of " + diagnosis.shortCNet.getLabel(), initialMarking, context);
		//	context.addConnection(new InitialMarkingConnection(diagnosis.shortCNet, initialMarking));
		}
	}

	/**
	 * Checks whether the net is a WF-net, using the assumptions.
	 * 
	 * @return WFNET if the net is a WF-net, NOWFNET if the net is not a WF-net,
	 *         DONE otherwise.
	 * @throws Exception
	 */
	private WoflanState diagnoseWFNet() {
		/**
		 * First check the assumptions.
		 */
		if (assumptions.contains(WoflanState.WFNET)) {
			return WoflanState.WFNET;
		} else if (assumptions.contains(WoflanState.NOWFNET)) {
			return WoflanState.NOWFNET;
		}
		/**
		 * No assumptions on this. Proceed.
		 */
		WoflanState state = WoflanState.DONE;
		if (WorkflowNetUtils.isValidWFNet(diagnosis.net)) {
			/**
			 * The net is a workflow net. Short-circuit it.
			 */
			shortCircuit();
			state = WoflanState.WFNET;
		} else {
			/**
			 * The net is not a workflow net. Diagnose the reason.
			 */
			SortedSet<Place> sourcePlaces = WorkflowNetUtils.getSourcePlaces(diagnosis.net);
			if (sourcePlaces.size() != 1) {
				diagnosis.setSourcePlaces( sourcePlaces);
			}
			SortedSet<Place> sinkPlaces = WorkflowNetUtils.getSinkPlaces(diagnosis.net);
			if (sinkPlaces.size() != 1) {
				diagnosis.setSinkPlaces( sinkPlaces);
			}
			if ((sourcePlaces.size() == 1) && (sinkPlaces.size() == 1)) {
				SortedSet<PetrinetNode> nodes = WorkflowNetUtils.getUnconnectedNodes(diagnosis.net);
				if (nodes.size() != 0) {
					diagnosis.setUnconnectedNodes( nodes);
				}
			}
			state = WoflanState.NOWFNET;
		}
		return state;
	}

	private WoflanState diagnoseSCover()  {
		/**
		 * Check assumptions.
		 */
		if (assumptions.contains(WoflanState.SCOVER)) {
			return WoflanState.SCOVER;
		} else if (assumptions.contains(WoflanState.NOSCOVER)) {
			return WoflanState.NOSCOVER;
		}
		WoflanState state = WoflanState.DONE;
	
		NotSCoveredNodesSet nodesSet = null;
		SComponentGenerator scg = new SComponentGenerator();
		
		SComponentSet sComponents = scg.calculateSComponentsPetriNet(diagnosis.shortCNet);
		
		
		if (nodesSet == null) {
			/**
			 * No it is not. Compute it now.
			 */
			SortedSet<PetrinetNode> coveredNodes = new TreeSet<PetrinetNode>();
			for (SortedSet<PetrinetNode> nodes : sComponents) {
				coveredNodes.addAll(nodes);
			}
			SortedSet<PetrinetNode> uncoveredNodes = new TreeSet<PetrinetNode>(diagnosis.shortCNet.getPlaces());
			uncoveredNodes.addAll(diagnosis.shortCNet.getTransitions());
			uncoveredNodes.removeAll(coveredNodes);
			nodesSet = new NotSCoveredNodesSet();
			nodesSet.add(uncoveredNodes);
			
		}
		if (nodesSet.isEmpty() || nodesSet.iterator().next().isEmpty()) {
			state = WoflanState.SCOVER;
		} else {
			state = WoflanState.NOSCOVER;
			diagnosis.setNotSCoveredNodes(nodesSet);
		}
		return state;
	}

	private WoflanState diagnosePCover()  {
		/**
		 * Check assumptions.
		 */
		if (assumptions.contains(WoflanState.PCOVER)) {
			return WoflanState.PCOVER;
		} else if (assumptions.contains(WoflanState.NOPCOVER)) {
			return WoflanState.NOPCOVER;
		}
		WoflanState state = WoflanState.DONE;
		

		NotPCoveredNodesSet uncoveredNodes = null;
		PlaceInvariantCalculator pic = new PlaceInvariantCalculator();
		
		PlaceInvariantSet pInvariantSet = pic.calculate(diagnosis.shortCNet);
		
		if (uncoveredNodes == null) {
			SortedSet<PetrinetNode> coveredNodes = new TreeSet<PetrinetNode>();
			for (MultiSet<Place> pInvariant : pInvariantSet) {
				coveredNodes.addAll(pInvariant.baseSet());
			}
			SortedSet<Place> uncoveredPlaces = new TreeSet<Place>(diagnosis.shortCNet.getPlaces());
			uncoveredPlaces.removeAll(coveredNodes);
			uncoveredNodes = new NotPCoveredNodesSet();
			uncoveredNodes.add(uncoveredPlaces);
			
		}
		if (uncoveredNodes.isEmpty() || (uncoveredNodes.iterator().next().size() == 0)) {
			state = WoflanState.PCOVER;
		} else {
			diagnosis.setNotPCoveredNodes(uncoveredNodes);
			state = WoflanState.NOPCOVER;
		}
		return state;
	}

	private WoflanState diagnoseDeadness()  {
		/**
		 * Check assumptions.
		 */
		if (assumptions.contains(WoflanState.DEAD)) {
			return WoflanState.DEAD;
		} else if (assumptions.contains(WoflanState.NOTDEAD)) {
			return WoflanState.NOTDEAD;
		}
		WoflanState state = WoflanState.DONE;
	
		DeadTransitionsSet deadTransitionsSet = DeadTransitionAnalyzer.analyzeDeadTransitionPetriNet(diagnosis.shortCNet,initialMarking);
		
		if (deadTransitionsSet.isEmpty() || deadTransitionsSet.iterator().next().isEmpty()) {
			state = WoflanState.NOTDEAD;
		} else {
			state = WoflanState.DEAD;
			diagnosis.setDeadTransitions(deadTransitionsSet);
		}
		return state;
	}

	private WoflanState diagnoseLiveness()  {
		/**
		 * Check assumptions.
		 */
		if (assumptions.contains(WoflanState.LIVE)) {
			return WoflanState.LIVE;
		} else if (assumptions.contains(WoflanState.NOTLIVE)) {
			return WoflanState.NOTLIVE;
		}
		WoflanState state = WoflanState.DONE;
		
		LivenessAnalyzer la = new LivenessAnalyzer();
		Object[] result = la.analyzeLivenessPetriNet(diagnosis.shortCNet, initialMarking,(PetrinetSemantics)diagnosis.semantics);
		
		NonLiveTransitionsSet nonLiveTransitionsSet = ( NonLiveTransitionsSet)result[1];
				//new NonLiveTransitionsSet() ;
		/**
		 * Try whether the set of non-live transitions has already been
		 * computed.
		 */
	

		if (nonLiveTransitionsSet.isEmpty() || nonLiveTransitionsSet.iterator().next().isEmpty()) {
			state = WoflanState.LIVE;
		} else {
			state = WoflanState.NOTLIVE;
			NonLiveSequences nonLiveSequences = new NonLiveSequences();
			/**
			 * Get the non-live sequences.
			 */
			

			diagnosis.setNotLiveTransitions(nonLiveTransitionsSet, nonLiveSequences);
		}
		return state;
	}

	private WoflanState diagnoseBoundedness()  {
		/**
		 * Check assumptions.
		 */
		if (assumptions.contains(WoflanState.BOUNDED)) {
			return WoflanState.BOUNDED;
		} else if (assumptions.contains(WoflanState.NOTBOUNDED)) {
			return WoflanState.NOTBOUNDED;
		}
		WoflanState state = WoflanState.DONE;
		
		BoundednessAnalyzer ba = new BoundednessAnalyzer(diagnosis.shortCNet, initialMarking);
		UnboundedPlacesSet unboundedPlacesSet = ba.getUnbountedPlacesSet();


		if (unboundedPlacesSet.isEmpty() || unboundedPlacesSet.iterator().next().isEmpty()) {
			state = WoflanState.BOUNDED;
		} else {
			state = WoflanState.NOTBOUNDED;
			UnboundedSequences unboundedSequences = new UnboundedSequences();
			/**
			 * Get the unbounded sequences.
			 */
			
			diagnosis.setUnboundedPlaces(unboundedPlacesSet, unboundedSequences);
		}
		return state;
	}

	private WoflanState diagnoseFC()  {
		/**
		 * Check assumptions.
		 */
		if (assumptions.contains(WoflanState.FREECHOICE)) {
			return WoflanState.FREECHOICE;
		} else if (assumptions.contains(WoflanState.NONFREECHOICE)) {
			return WoflanState.NONFREECHOICE;
		}
		WoflanState state = WoflanState.DONE;
		FreeChoiceAnalyzer fca = new FreeChoiceAnalyzer();
		Object[] result = fca.analyzeFCAndEFCProperty(diagnosis.shortCNet);
		NonExtendedFreeChoiceClustersSet clusterSet =  (NonExtendedFreeChoiceClustersSet) result[3];
		/**
		 * Get the non-extended-free-choice clusters.
		 */
		

		if (clusterSet.isEmpty()) {
			state = WoflanState.FREECHOICE;
		} else {
			state = WoflanState.NONFREECHOICE;
			diagnosis.setNotFreeChoice(clusterSet);
		}
		return state;
	}

	private WoflanState diagnosePTHandles()  {
		/**
		 * Check assumptions.
		 */
		if (assumptions.contains(WoflanState.WELLPTHANDLED)) {
			return WoflanState.WELLPTHANDLED;
		} else if (assumptions.contains(WoflanState.NOTWELLPTHANDLED)) {
			return WoflanState.NOTWELLPTHANDLED;
		}
		WoflanState state = WoflanState.DONE;
		PTHandlesGenerator pthg = new PTHandlesGenerator(diagnosis.shortCNet);
		PTHandles handles = pthg.analyzePTHandles();
				

		if (handles.isEmpty()) {
			state = WoflanState.WELLPTHANDLED;
		} else {
			state = WoflanState.NOTWELLPTHANDLED;
			diagnosis.setPTHandles(handles);
		}
		return state;
	}

	private WoflanState diagnoseTPHandles()  {
		/**
		 * Check assumptions.
		 */
		if (assumptions.contains(WoflanState.WELLTPHANDLED)) {
			return WoflanState.WELLTPHANDLED;
		} else if (assumptions.contains(WoflanState.NOTWELLTPHANDLED)) {
			return WoflanState.NOTWELLTPHANDLED;
		}
		WoflanState state = WoflanState.DONE;
		TPHandlesGenerator tphg=null;
		if(diagnosis.shortCNet!=null)
		 tphg = new TPHandlesGenerator(diagnosis.shortCNet);
		else
		 tphg = new TPHandlesGenerator(diagnosis.net);	
		TPHandles handles = tphg.analyzeTPHandles();

		if (handles.isEmpty()) {
			state = WoflanState.WELLTPHANDLED;
		} else {
			state = WoflanState.NOTWELLTPHANDLED;
			diagnosis.setTPHandles(handles);
		}
		return state;
	}
	
	public Marking getInitialMarking(){
		return initialMarking;
	}
}
