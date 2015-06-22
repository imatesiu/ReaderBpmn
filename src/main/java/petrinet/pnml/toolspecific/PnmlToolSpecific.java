package petrinet.pnml.toolspecific;

import petrinet.pnml.Pnml;
import petrinet.pnml.PnmlElement;
import org.xmlpull.v1.XmlPullParser;

public class PnmlToolSpecific extends PnmlElement {

	public final static String TAG = "toolspecific";

	private String tool;
	private String version;

	public PnmlToolSpecific() {
		super(TAG);
		tool = null;
		version = null;
	}

	protected void importAttributes(XmlPullParser xpp, Pnml pnml) {
		super.importAttributes(xpp, pnml);
		importTool(xpp);
		importVersion(xpp);
	}

	protected String exportAttributes(Pnml pnml) {
		return super.exportAttributes(pnml) + exportTool(pnml) + exportVersion(pnml);
	}

	private void importTool(XmlPullParser xpp) {
		String value = xpp.getAttributeValue(null, "tool");
		if (value != null) {
			tool = value;
		}
	}

	private String exportTool(Pnml pnml) {
		if (tool != null) {
			return exportAttribute("tool", tool, pnml);
		}
		return "";
	}

	private void importVersion(XmlPullParser xpp) {
		String value = xpp.getAttributeValue(null, "version");
		if (value != null) {
			version = value;
		}
	}

	private String exportVersion(Pnml pnml) {
		if (version != null) {
			return exportAttribute("version", version, pnml);
		}
		return "";
	}

	protected void checkValidity(Pnml pnml) {
		super.checkValidity(pnml);
		if ((tool == null) || (version == null)) {
			pnml.log(tag, lineNumber, "Expected tool and version");
		}
	}
}
