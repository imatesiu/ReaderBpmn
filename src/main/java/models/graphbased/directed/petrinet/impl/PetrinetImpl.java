package models.graphbased.directed.petrinet.impl;

import javax.swing.SwingConstants;


import models.graphbased.AttributeMap;
import models.graphbased.directed.petrinet.Petrinet;


public class PetrinetImpl extends AbstractResetInhibitorNet implements Petrinet {

	public PetrinetImpl(String label) {
		super(false, false);
		getAttributeMap().put(AttributeMap.PREF_ORIENTATION, SwingConstants.WEST);
		getAttributeMap().put(AttributeMap.LABEL, label);
	}

	@Override
	protected PetrinetImpl getEmptyClone() {
		return new PetrinetImpl(getLabel());
	}

}
