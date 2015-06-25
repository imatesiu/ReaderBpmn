package plugin.sim;

import java.io.File;
import java.util.Collection;

import models.graphbased.directed.bpmn.BPMNDiagram;
import models.graphbased.directed.bpmn.BPMNNode;
import models.graphbased.directed.petrinet.Petrinet;
import models.graphbased.directed.transitionsystem.CoverabilityGraph;
import models.graphbased.directed.transitionsystem.Transition;
import models.semantics.petrinet.Marking;
import petrinet.behavioralanalysis.CGGenerator;
import petrinet.behavioralanalysis.woflan.Woflan;
import petrinet.behavioralanalysis.woflan.WoflanDiagnosis;
import plugins.bpmn.Bpmn;
import plugins.bpmn.trasform.BpmnToPetriNet;

public class IAllPath {


	private Collection<Collection<Transition>> cct=null;
	private BPMNDiagram BPMNdiagram=null;
	private Petrinet pn=null;
	private Marking marking;
	private CoverabilityGraph covGraph=null;
	private Collection<Collection<BPMNNode>> ccbp;
	private DepthFirstSearchLearnPad dpslp;

	public IAllPath(File fileinput, boolean subexplorer) {

		this.ccbp=Calculate(fileinput,subexplorer);
	}


	private Collection<Collection<BPMNNode>> Calculate(File fileinput, boolean subexplorer){
		try {
			Bpmn bpmn = new Bpmn(fileinput);


			BPMNdiagram = 	bpmn.BpmnextractDiagram().iterator().next();

			BpmnToPetriNet btpn = new BpmnToPetriNet(BPMNdiagram,subexplorer);
			pn = (Petrinet) btpn.getPetriNet();
			marking = btpn.getMarking();


			Marking marking = btpn.getMarking();

			Woflan wolf = new Woflan();
			WoflanDiagnosis result2 = wolf.diagnose(pn);
			System.out.println(result2);
			if(result2.isSound()){



				CGGenerator cg = new CGGenerator();
				Object[] result = cg.petriNetToCoverabilityGraph(pn,marking);
				covGraph = (CoverabilityGraph) result[0];




				dpslp = new DepthFirstSearchLearnPad(covGraph, btpn.getMap());

				cct = dpslp.getCCT();

				return dpslp.getCCBP();
			} 
			return null;
		} catch (Exception e) {
			return null;
		}
	}


	public BPMNDiagram getBPMNdiagram() {
		return BPMNdiagram;
	}


	public Petrinet getPn() {
		return pn;
	}


	public CoverabilityGraph getCovGraph() {
		return covGraph;
	}


	public Collection<Collection<Transition>> getCct() {
		return cct;
	}


	public Collection<Collection<BPMNNode>> getCcbp() {
		return ccbp;
	}


	public DepthFirstSearchLearnPad getDpslp() {
		return dpslp;
	}




}
