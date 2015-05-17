package petrinet.pnml.exporting;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;


import models.connections.GraphLayoutConnection;


import models.graphbased.directed.petrinet.Petrinet;
import models.graphbased.directed.petrinet.PetrinetGraph;
import models.semantics.petrinet.Marking;
import petrinet.pnml.Pnml;

public class PnmlExportNet {

	protected void exportPetriNetToPNMLOrEPNMLFile(Marking marking, Petrinet net, File file, Pnml.PnmlType type)
			throws IOException {
		
		
		GraphLayoutConnection layout  = new GraphLayoutConnection(net);;
		
		HashMap<PetrinetGraph, Marking> markedNets = new HashMap<PetrinetGraph, Marking>();
		markedNets.put(net, marking);
		Pnml pnml = new Pnml().convertFromNet(markedNets, layout);
		pnml.setType(type);
		String text = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" + pnml.exportElement(pnml);

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
		bw.write(text);
		bw.close();
	}

	

}
