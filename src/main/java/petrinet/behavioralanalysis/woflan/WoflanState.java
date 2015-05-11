package petrinet.behavioralanalysis.woflan;

/**
 * This holds the current state of the diagnosis. Furthermore, a subset of these
 * be used as assumptions (BOUNDED, NOTBOUNDED, ...) for the diagnosis.
 * 
 * @author HVERBEEK
 * 
 */
public enum WoflanState {

	/**
	 * The short-circuited net is bounded.
	 */
	BOUNDED,

	/**
	 * The short-circuited net is bounded but contains dead transitions.
	 */
	DEAD,

	/**
	 * Diagnosis is done.
	 */
	DONE,

	/**
	 * The short-circuited net is not S-coverable but extended free-choice.
	 */
	FREECHOICE,

	/**
	 * Diagnosis is to start.
	 */
	INIT,

	/**
	 * The short-circuited net is bounded, contains no dead transitions, and is
	 * live.
	 */
	LIVE,

	/**
	 * The short-circuited net is S-coverable and not extended free-choice.
	 */
	NONFREECHOICE,

	/**
	 * The short-circuited net is not covered by positive place invariants.
	 */
	NOPCOVER,

	/**
	 * The short-circuited net is not S-coverable.
	 */
	NOSCOVER,

	/**
	 * The short-circuited net is unbounded.
	 */
	NOTBOUNDED,

	/**
	 * The short-circuited net is bounded and contains no dead transitions.
	 */
	NOTDEAD,

	/**
	 * The short-circuited net is bounded, contains no dead transitions, but is
	 * not live.
	 */
	NOTLIVE,

	/**
	 * The short-circuited net is not S-coverable, not extended free-choice, and
	 * contains PT-handles.
	 */
	NOTWELLPTHANDLED,

	/**
	 * The short-circuited net is not S-coverable, not extended free-choice, and
	 * is not well-handled.
	 */
	NOTWELLTPHANDLED,

	/**
	 * The net is not a WF-net.
	 */
	NOWFNET,

	/**
	 * The short-circuited net is not S-coverable but is covered by positive
	 * place invariants.
	 */
	PCOVER,

	/**
	 * The short-circuited net is S-coverable.
	 */
	SCOVER,

	/**
	 * The net is a sound WF-net.
	 */
	SOUND,

	/**
	 * The short-circuited net is not S-coverable, not extended free-choice, and
	 * contains not PT-handles.
	 */
	WELLPTHANDLED,

	/**
	 * The short-circuited net is not S-coverable, not extended free-choice, but
	 * well-handled.
	 */
	WELLTPHANDLED,

	/**
	 * The net is a WF-net.
	 */
	WFNET
}
