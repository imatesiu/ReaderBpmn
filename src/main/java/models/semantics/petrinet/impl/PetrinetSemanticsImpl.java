package models.semantics.petrinet.impl;


import models.graphbased.directed.petrinet.elements.Transition;
import models.semantics.ExecutionInformation;
import models.semantics.petrinet.Marking;
import models.semantics.petrinet.PetrinetExecutionInformation;
import models.semantics.petrinet.PetrinetSemantics;


class PetrinetSemanticsImpl extends AbstractResetInhibitorNetSemantics implements PetrinetSemantics {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1863753685175892937L;

	public PetrinetSemanticsImpl() {
	}

	public ExecutionInformation executeTransition(Transition toExecute) {
		Marking required = getRequired(toExecute);
		Marking removed = state.minus(required);
		Marking produced = getProduced(toExecute);
		state.addAll(produced);

		return new PetrinetExecutionInformation(required, removed, produced, toExecute);
	}
}
