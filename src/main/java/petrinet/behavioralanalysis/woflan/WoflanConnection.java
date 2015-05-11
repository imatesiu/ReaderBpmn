package petrinet.behavioralanalysis.woflan;

import java.util.Collection;

import framework.connections.impl.AbstractConnection;
import models.graphbased.directed.petrinet.PetrinetGraph;

/**
 * WoflanConnection
 * 
 * Connects a Petri net to its diagnostics and the assumptions made to obtain
 * these diagnostics.
 * 
 * @author HVERBEEK
 * 
 */
public class WoflanConnection extends AbstractConnection {
	public final static String NET = "Net";
	public final static String DIAGNOSIS = "Diagnosis";

	/**
	 * The assumptions made.
	 */
	private final Collection<WoflanState> assumptions;

	public WoflanConnection(PetrinetGraph net, WoflanDiagnosis diagnosis, Collection<WoflanState> assumptions) {
		super("Woflan Diagnosis of " + net.getLabel());
		this.assumptions = assumptions;
		put(NET, net);
		put(DIAGNOSIS, diagnosis);
	}

	/**
	 * Gets the assumptions made.
	 * 
	 * @return The assumptions made.
	 */
	public Collection<WoflanState> getAssumptions() {
		return assumptions;
	}
}