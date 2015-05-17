package petrinet.pnml.exporting;

import java.io.File;
import java.io.IOException;

import models.semantics.petrinet.Marking;

import models.graphbased.directed.petrinet.Petrinet;

import petrinet.pnml.Pnml;


public class PnmlExportNetToPNML extends PnmlExportNet {

	
	public void exportPetriNetToPNMLFile( Petrinet net, File file, Marking mark) throws IOException {
		exportPetriNetToPNMLOrEPNMLFile(mark, net, file, Pnml.PnmlType.PNML);
	}


}
