package petrinet.pnml;

/**
 * Basic PNML text object.
 * 
 * @author hverbeek
 */
public class PnmlText extends PnmlElement {

	/**
	 * PNML text tag.
	 */
	public final static String TAG = "text";

	/**
	 * Text.
	 */
	private String text;

	/**
	 * Creates a fresh text object.
	 */
	public PnmlText() {
		super(TAG);
		text = "";
	}

	public PnmlText(String text) {
		super(TAG);
		this.text = text;
	}

	/**
	 * Imports the text.
	 */
	protected void importText(String text, Pnml pnml) {
		this.text = (this.text + text).trim().replaceAll("&gt;", ">").replaceAll("&lt;", "<");
	}

	/**
	 * Exports the text.
	 */
	protected String exportElements(Pnml pnml) {
		return text.replaceAll(">", "&gt;").replaceAll("<", "&lt;");
	}

	/**
	 * Gets the text.
	 * 
	 * @return
	 */
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
