package models.semantics.petrinet.impl;


import models.graphbased.directed.petrinet.Petrinet;


import models.semantics.petrinet.PetrinetSemantics;


public class PetrinetSemanticsFactory {

	private PetrinetSemanticsFactory() {

	}

	public static PetrinetSemantics regularPetrinetSemantics(Class<? extends Petrinet> net) {
		return new PetrinetSemanticsImpl();
	}

	public static PetrinetSemantics elementaryPetrinetSemantics(Class<? extends Petrinet> net) {
		return new ElementaryPetrinetSemanticsImpl();
	}

	
}
