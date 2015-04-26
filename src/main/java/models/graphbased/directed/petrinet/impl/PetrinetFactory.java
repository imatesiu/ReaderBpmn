package models.graphbased.directed.petrinet.impl;

import java.util.Map;

import models.graphbased.directed.DirectedGraphElement;

import models.graphbased.directed.petrinet.Petrinet;
import models.graphbased.directed.petrinet.ResetNet;

public class PetrinetFactory {

	private PetrinetFactory() {

	}

	public static Petrinet newPetrinet(String label) {
		return new PetrinetImpl(label);
	}

	

	public static Petrinet clonePetrinet(Petrinet net) {
		PetrinetImpl newNet = new PetrinetImpl(net.getLabel());
		newNet.cloneFrom(net);
		return newNet;
	}

	public static Petrinet clonePetrinet(Petrinet net, Map<DirectedGraphElement, DirectedGraphElement> map) {
		PetrinetImpl newNet = new PetrinetImpl(net.getLabel());
		map.putAll(newNet.cloneFrom(net));
		return newNet;
	}

	

	
}
