

import java.io.File;
import java.util.Collection;
import java.util.List;

import models.graphbased.directed.bpmn.BPMNDiagram;
import models.graphbased.directed.petrinet.Petrinet;

import org.jbpt.algo.graph.DirectedGraphAlgorithms;
import org.jbpt.petri.Flow;
import org.jbpt.petri.Node;
import org.jbpt.petri.NetSystem;
import org.jbpt.petri.PetriNet;
import org.jbpt.petri.unfolding.CompletePrefixUnfolding;
import org.jbpt.petri.unfolding.CompletePrefixUnfoldingSetup;
import org.jbpt.petri.unfolding.Event;
import org.jbpt.petri.unfolding.OccurrenceNet;
import org.jbpt.petri.unfolding.order.AdequateOrderType;
import org.jbpt.utils.IOUtils;







import framework.util.SimplePanel;
import framework.util.Util;



import plugins.bpmn.Bpmn;
import plugins.bpmn.trasform.BpmnToPetriNet;

public class main {

	public static void main(String[] args) {
		
		if(args.length<0 || args[0]==null){
			System.out.println("Non del file non trovato");
		}else{
			try {
				
				
				File file = new File(args[0]);
				Bpmn bpmn = new Bpmn(file);

				Collection<BPMNDiagram> BPMNdiagrams = 	bpmn.BpmnextractDiagram();
				for(BPMNDiagram graph : BPMNdiagrams){
					BpmnToPetriNet btpn = new BpmnToPetriNet(graph);
					//Petrinet pn = (Petrinet) btpn.getPetriNet();
					
					SimplePanel sp = new SimplePanel();
					sp.view(graph);
					//sp.view(pn);
					

					

				BPMNtoNetSystem fpn = new	BPMNtoNetSystem(graph);
				PetriNet jpn = fpn.getPN();
				NetSystem sys = new NetSystem(jpn);
				
				sys.loadNaturalMarking();
				
				boolean serializeModels=true;
				String name="testpn";
				if (serializeModels) IOUtils.toFile("./"+name+"/original.dot", sys.toDOT());
				String dot2png="";
				if (serializeModels) Util.executeCommand("/usr/local/bin/dot -q -Tpng ./"+name+"/original.dot -o ./"+name+"/original.png\n");
				System.out.println(dot2png);
				
				// check if net is cyclic
				DirectedGraphAlgorithms<Flow,Node> dga = new DirectedGraphAlgorithms<>();
				String result = dga.isCyclic(sys) ? "cyclic\n" : "acyclic\n"; 
				
				CompletePrefixUnfoldingSetup setup = new CompletePrefixUnfoldingSetup();
				setup.ADEQUATE_ORDER = AdequateOrderType.ESPARZA_FOR_SAFE_SYSTEMS;
				
				CompletePrefixUnfolding unfolding = new CompletePrefixUnfolding(sys,setup);
				OccurrenceNet occurrenceNet = (OccurrenceNet) unfolding.getOccurrenceNet();
				List<Event> log = unfolding.getLog();
				if (serializeModels) IOUtils.toFile("./"+name+"/occurrenceNet.dot", occurrenceNet.toDOT());
				dot2png="";
				if (serializeModels) Util.executeCommand("/usr/local/bin/dot -q -Tpng ./"+name+"/occurrenceNet.dot -o ./"+name+"/occurrenceNet.png\n");
				System.out.println(dot2png);
				
				
				Petrinet pn1 = JbptConversion.convert(unfolding);
				
				sp.view(pn1);
					
				}

			} catch (Exception e) {
				
				e.printStackTrace();
			}
		}

	}
	
	

	

}
