package plugins.xpdl;


import plugins.xpdl.idname.XpdlPackageType;

public class Xpdl extends XpdlPackageType {

	

	boolean hasErrors;
	boolean hasInfos;

	public Xpdl() {
		super("Package");

		initializeLog();
	}

	/**
	 * Creates and initializes a log to throw to the framework when importing
	 * the XPDL file fails.
	 */
	private void initializeLog() {
		

		log("<preamble>");

		hasErrors = false;
	}

	

	/**
	 * Adds a log event to the current trace in the log.
	 * 
	 * @param context
	 *            Context of the message, typically the current PNML tag.
	 * @param lineNumber
	 *            Current line number.
	 * @param message
	 *            Error message.
	 */
	public void log(String context, int lineNumber, String message) {
		System.out.println("context "+context+"lineNumber "+lineNumber+"message "+message);
		hasErrors = true;
	}

	/**
	 * Adds a log event to the current trace in the log.
	 * 
	 * @param context
	 *            Context of the message, typically the current PNML tag.
	 * @param lineNumber
	 *            Current line number.
	 * @param message
	 *            Error message.
	 */
	public void logInfo(String context, int lineNumber, String message) {
		System.out.println("context "+context+"lineNumber "+lineNumber+"message "+message);
		hasInfos = true;
	}

	/**
	 * Adds a new trace with the given name to the log. This trace is now
	 * current.
	 * 
	 * @param name
	 *            The give name.
	 */
	public void log(String name) {
		
	}

	public boolean hasErrors() {
		return hasErrors;
	}

	public boolean hasInfos() {
		return hasInfos;
	}
}
