package petrinet.pnml.importing;

import java.io.InputStream;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;



import models.graphbased.directed.petrinet.Petrinet;
import models.graphbased.directed.petrinet.PetrinetGraph;
import models.graphbased.directed.petrinet.impl.PetrinetFactory;
import models.semantics.petrinet.Marking;
import models.utils.Pair;
import petrinet.pnml.Pnml;


public class PnmlImportNet  {

	protected FileFilter getFileFilter() {
		return new FileNameExtensionFilter("PNML files", "pnml");
	}

	public Pair<PetrinetGraph,Marking> importFromStream( InputStream input, String filename, long fileSizeInBytes)
			throws Exception {
		PnmlImportUtils utils = new PnmlImportUtils();
		Pnml pnml = utils.importPnmlFromStream( input, filename, fileSizeInBytes);
		if (pnml == null) {
			/*
			 * No PNML found in file. Fail.
			 */
			return null;
		}
		/*
		 * PNML file has been imported. Now we need to convert the contents to a
		 * regular Petri net.
		 */
		PetrinetGraph net = PetrinetFactory.newPetrinet(pnml.getLabel() + " (imported from " + filename + ")");

		utils.connectNet(pnml, net);
		
		return new Pair<PetrinetGraph, Marking>(utils.getNet(), utils.getMarking());
	}
}
