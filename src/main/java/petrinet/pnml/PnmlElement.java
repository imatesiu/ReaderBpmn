package petrinet.pnml;

import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;

/**
 * Basic PNML element. All PNML objects extend this class (either directly or
 * indirectly).
 * 
 * @author hverbeek
 */
public class PnmlElement {

	/**
	 * The PNML tag for this element.
	 */
	public String tag;

	public int lineNumber;

	/**
	 * Creates a fresh PNML element.
	 * 
	 * @param tag
	 */
	public PnmlElement(String tag) {
		this.tag = tag;
	}

	/**
	 * Imports the given element.
	 * 
	 * @param xpp
	 * @param pnml
	 */
	public void importElement(XmlPullParser xpp, Pnml pnml) {
		lineNumber = xpp.getLineNumber();
		/*
		 * Import all attributes of this element.
		 */
		importAttributes(xpp, pnml);
		/*
		 * Create afresh stack to keep track of start tags to match.
		 */
		ArrayList<String> stack = new ArrayList<String>();
		/*
		 * Add the current tag to this stack, as we still have to find the
		 * matching end tag.
		 */
		stack.add(tag);
		/*
		 * As long as the stack is not empty, we're still working on this
		 * object.
		 */
		while (!stack.isEmpty()) {
			/*
			 * Get next event.
			 */
			try {
				int eventType = xpp.next();
				if (eventType == XmlPullParser.END_DOCUMENT) {
					/*
					 * End of document. Should not happen.
					 */
					pnml.log(tag, xpp.getLineNumber(), "Found end of document");
					//System.err.println("Line " + xpp.getLineNumber() + ": Malformed PNML document: No </"+ tag + "> found.");
					//throw new Exception("Malformed PNML document: No </"+ tag + "> found.");
					return;
				} else if (eventType == XmlPullParser.START_TAG) {
					//pnml.logInfo("Tag " + tag, XLifecycleExtension.StandardModel.START, "Line " + xpp.getLineNumber());
					/*
					 * Start tag. Push it on the stack.
					 */
					stack.add(xpp.getName());
					/*
					 * If this tag is the second on the stack, then it is a
					 * direct child.
					 */
					if (stack.size() == 2) {
						/*
						 * For a direct child, check whether the tag is known.
						 * If so, take proper action. Note that this needs not
						 * to be done for other offspring.
						 */
						if (importElements(xpp, pnml)) {
							/*
							 * Known start tag. The end tag has been matched and
							 * can be popped from the stack.
							 */
							stack.remove(stack.size() - 1);
						}
					}
				} else if ((eventType == XmlPullParser.END_TAG)) {
					//pnml.logInfo("Tag " + tag, XLifecycleExtension.StandardModel.COMPLETE, "Line " + xpp.getLineNumber());
					/*
					 * End tag. Should be identical to top of the stack.
					 */
					if (xpp.getName().equals(stack.get(stack.size() - 1))) {
						/*
						 * Yes it is. Pop the stack.
						 */
						stack.remove(stack.size() - 1);
					} else {
						/*
						 * No it is not. XML violation.
						 */
						pnml.log(tag, xpp.getLineNumber(), "Found " + xpp.getName() + ", expected "
								+ stack.get(stack.size() - 1));
						return;
					}
				} else if (eventType == XmlPullParser.TEXT) {
					/*
					 * Plain text. Import it.
					 */
					//pnml.logInfo("Text", XLifecycleExtension.StandardModel.UNKNOWN, "Line " + xpp.getLineNumber(), xpp.getText());
					importText(xpp.getText(), pnml);
				}
			} catch (Exception ex) {
				pnml.log(tag, xpp.getLineNumber(), ex.getMessage());
				return;
			}
		}
		/*
		 * The element has been imported. Now is a good time to check its
		 * validity.
		 */
		checkValidity(pnml);
	}

	/**
	 * Exports the element.
	 * 
	 * @return
	 */
	public String exportElement(Pnml pnml) {
		/*
		 * Export all attributes of this element.
		 */
		String s = "<" + tag;
		s += exportAttributes(pnml);
		/*
		 * Export all child elements.
		 */
		String t = exportElements(pnml);
		if (t.equals("")) {
			/*
			 * No child elements, use combined start-end tag.
			 */
			s += "/>";
		} else {
			/*
			 * Child elements, use separated start and end tags.
			 */
			s += ">" + t + "</" + tag + ">";
		}
		return s;
	}

	/**
	 * Imports all standard attributes: None. If some subclass has attributes,
	 * this method needs to be overruled by it.
	 * 
	 * @param xpp
	 * @param pnml
	 */
	protected void importAttributes(XmlPullParser xpp, Pnml pnml) {
	}

	/**
	 * Exports all standard attributes: None. If some subclass has attributes,
	 * this method needs to be overruled by it.
	 * 
	 * @return
	 */
	protected String exportAttributes(Pnml pnml) {
		return "";
	}

	/**
	 * Imports all standard child elements: None. If some subclass has child
	 * elements, this method needs to be overruled by it.
	 * 
	 * @param xpp
	 * @param pnml
	 * @return
	 */
	protected boolean importElements(XmlPullParser xpp, Pnml pnml) {
		return false;
	}

	/**
	 * Exports all standard elements: None. If some subclass has child elements,
	 * this method needs to be overruled by it.
	 * 
	 * @return
	 */
	protected String exportElements(Pnml pnml) {
		return "";
	}

	/**
	 * Imports standard text: No action. If some subclass needs to import text,
	 * this method needs to be overruled by it.
	 * 
	 * @param text
	 * @param pnml
	 */
	protected void importText(String text, Pnml pnml) {
	}

	/**
	 * Default way to export some attribute.
	 * 
	 * @param tag
	 *            The attribute tag.
	 * @param value
	 *            The attribute value.
	 * @return
	 */
	protected String exportAttribute(String tag, String value, Pnml pnml) {
		return " " + tag + "=\"" + protectSpecialCharacters(value) + "\"";
	}

	/**
	 * Default check for validity: No action. If some subclass needs to check
	 * validity, this method needs to be overruled by it.
	 * 
	 * @param pnml
	 */
	protected void checkValidity(Pnml pnml) {
	}

	private static String protectSpecialCharacters(String originalUnprotectedString) {
	    if (originalUnprotectedString == null) {
	        return null;
	    }
	    boolean anyCharactersProtected = false;

	    StringBuffer stringBuffer = new StringBuffer();
	    for (int i = 0; i < originalUnprotectedString.length(); i++) {
	        char ch = originalUnprotectedString.charAt(i);

	        boolean controlCharacter = ch < 32;
	        boolean unicodeButNotAscii = ch > 126;
	        boolean characterWithSpecialMeaningInXML = ch == '<' || ch == '&' || ch == '>';

	        if (characterWithSpecialMeaningInXML || unicodeButNotAscii || controlCharacter) {
	            stringBuffer.append("&#" + (int) ch + ";");
	            anyCharactersProtected = true;
	        } else {
	            stringBuffer.append(ch);
	        }
	    }
	    if (anyCharactersProtected == false) {
	        return originalUnprotectedString;
	    }

	    return stringBuffer.toString();
	}

}
