package plugin.sim;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import models.graphbased.directed.bpmn.BPMNNode;
import models.graphbased.directed.petrinet.PetrinetNode;
import models.graphbased.directed.transitionsystem.State;
import models.graphbased.directed.transitionsystem.Transition;
import models.graphbased.directed.transitionsystem.TransitionSystemImpl;


public class DepthFirstSearchLearnPad {
	private TransitionSystemImpl ts;

	private Collection<Collection<Transition>> cct;

	private Collection<Collection<BPMNNode>> ccbp;


	private Map<PetrinetNode,BPMNNode> mapBPPN;

	public DepthFirstSearchLearnPad(TransitionSystemImpl input, Map<PetrinetNode,BPMNNode> mapBP){
		this.ts = input;

		this.mapBPPN=mapBP;


		cct =generateTrace();
	}

	public Collection<Collection<Transition>> getCCT(){
		return cct;
	}

	public Collection<Collection<BPMNNode>> getCCBP(){
		return ccbp;
	}

	private  Collection<Collection<Transition>> generateTrace(){
		Set<State> nodes = ts.getNodes();
		State first = nodes.iterator().next();
		cct = new ArrayList<Collection<Transition>>();
		ccbp = new ArrayList<Collection<BPMNNode>>();

		if(first.getGraph().getInEdges(first).isEmpty()){
			for(Transition edge :first.getGraph().getOutEdges(first)){
				ArrayList<Transition> ct = new ArrayList<Transition>();

				ArrayList<BPMNNode> cbp = new ArrayList<BPMNNode>();
				ccbp.add(cbp);
				cct.add(ct);
				addBPV(edge,cbp);
				ct.add(edge);
				State node = edge.getTarget();
				search(node,ct,cbp);
			}

		}


		return cct;
	}

	public void addBPV(Transition edge, ArrayList<BPMNNode> cbp){

		if(mapBPPN.containsKey(edge.getIdentifier())){
			BPMNNode valueb = mapBPPN.get(edge.getIdentifier());
			cbp.add(valueb);
		}

	}

	public void search(State node, ArrayList<Transition> at, ArrayList<BPMNNode> cbp){
		int index = 0;
		ArrayList<Transition> at1 = (ArrayList<Transition>) at.clone();
		ArrayList<BPMNNode> cbp1 = (ArrayList<BPMNNode>) cbp.clone();
		for(Transition edge :node.getGraph().getOutEdges(node)){
			if(index==0){
				if(!at.contains(edge)){
					at.add(edge);
					addBPV(edge,cbp);
					State nodetarget = edge.getTarget();
					search(nodetarget,at,cbp);
				}
			}else{

				if(!at1.contains(edge)){
					cct.add(at1);
					ccbp.add(cbp1);
					addBPV(edge,cbp1);
					at1.add(edge);
					State nodetarget = edge.getTarget();
					search(nodetarget,at1,cbp1);
				}
			}
			index++;
		}
	}


	public String toStringCT() {
		String strings = "";
		for(Collection<Transition> ct :cct){
			strings += ct.toString()+"\n";
		}

		return strings;
	}

	@Override
	public String toString() {
		String strings = "";
		for(Collection<BPMNNode> cp :ccbp){
			strings += cp.toString()+"\n";
		}

		return strings;
	}



}
