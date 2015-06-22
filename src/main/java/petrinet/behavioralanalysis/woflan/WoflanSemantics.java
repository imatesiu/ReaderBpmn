package petrinet.behavioralanalysis.woflan;

import java.util.Collection;
import java.util.HashSet;

import models.graphbased.directed.petrinet.Petrinet;
import models.graphbased.directed.petrinet.elements.Transition;
import models.semantics.ExecutionInformation;
import models.semantics.IllegalTransitionException;
import models.semantics.Semantics;
import models.semantics.petrinet.CTMarking;
import models.semantics.petrinet.Marking;
import models.semantics.petrinet.PetrinetSemantics;
import models.semantics.petrinet.impl.PetrinetSemanticsFactory;

public class WoflanSemantics implements PetrinetSemantics {

	/**
	 * 
	 */
	private static final long serialVersionUID = -160069264973479583L;
	
	Marking marking;
	Semantics<Marking, Transition> semantics = PetrinetSemanticsFactory.regularPetrinetSemantics(Petrinet.class);;

	public void setCurrentState(Marking currentState) {
		semantics.setCurrentState(currentState);
	}

	public Marking getCurrentState() {
		return semantics.getCurrentState();
	}

	public Collection<Transition> getExecutableTransitions() {
		Marking marking = semantics.getCurrentState();
		if (marking instanceof CTMarking) {
			CTMarking ctMarking = (CTMarking) marking;
			if (ctMarking.hasOmegaPlace()) {
				/*
				 * No transition enabled if omega marking.
				 */
				return new HashSet<Transition>();
			}
		}
		return semantics.getExecutableTransitions();
	}

	public ExecutionInformation executeExecutableTransition(Transition toExecute) throws IllegalTransitionException {
		return semantics.executeExecutableTransition(toExecute);
	}

	public void initialize(Collection<Transition> transitions, Marking initialState) {
		semantics.initialize(transitions, initialState);
	}

}
