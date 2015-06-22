package models.graphbased.directed.petrinet.impl;

import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingConstants;









import models.graphbased.AttributeMap;
import models.graphbased.directed.petrinet.Petrinet;
import models.graphbased.directed.petrinet.PetrinetEdge;
import models.graphbased.directed.petrinet.PetrinetNode;
import models.graphbased.directed.petrinet.elements.Place;
import models.graphbased.directed.petrinet.elements.Transition;
import models.semantics.petrinet.Marking;


public class PetrinetImpl extends AbstractResetInhibitorNet implements Petrinet {

	public PetrinetImpl(String label) {
		super(false, false);
		getAttributeMap().put(AttributeMap.PREF_ORIENTATION, SwingConstants.WEST);
		getAttributeMap().put(AttributeMap.LABEL, label);
	}

	@Override
	protected PetrinetImpl getEmptyClone() {
		return new PetrinetImpl(getLabel());
	}

	public String toDOT(/*Marking m*/) {
		String result = "digraph G {\n";
		result += "graph [fontname=\"Helvetica\" fontsize=\"10\" nodesep=\"0.35\" ranksep=\"0.25 equally\"];\n";
		result += "node [fontname=\"Helvetica\" fontsize=\"10\" fixedsize=\"true\" style=\"filled\" fillcolor=\"white\" penwidth=\"2\"];\n";
		result += "edge [fontname=\"Helvetica\" fontsize=\"10\" arrowhead=\"normal\" color=\"black\"];\n";
		result += "\n";
		result += "node [shape=\"circle\"];\n";

		for (Place p : this.getPlaces()) {
			Integer n = 0; // m.occurrences(p);
			String label = ((n == 0) || (n == null)) ? p.getLabel().replace("\n", "").replace("\r", "") : p.getLabel().replace("\n", "").replace("\r", "") + "[" + n.toString() + "]"; 
			result += String.format("\tn%s[label=\"%s\" width=\".3\" height=\".3\"];\n", p.getId().toString().replace("-", "").replace("node ", "").replace("-", ""), label);
		}

		result += "\n";
		result += "node [shape=\"box\"];\n";

		for (Transition t : this.getTransitions()) {
			String fillColor = true ? " fillcolor=\"#9ACD32\"" : "";
			if (t.isInvisible())
				result += String.format("\tn%s[label=\"\" width=\".3\""+fillColor+" height=\".1\"];\n", t.getId().toString().replace("-", "").replace("node ", "").replace("-", ""));
			else 
				result += String.format("\tn%s[label=\"%s\" width=\".3\""+fillColor+" height=\".3\"];\n", t.getId().toString().replace("-", "").replace("node ", "").replace("-", ""), t.getLabel().replace("\n", "").replace("\r", ""));
		}

		result += "\n";
		for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> f: this.getEdges()) {
			result += String.format("\tn%s->n%s;\n", f.getSource().getId().toString().replace("-", "").replace("node ", "").replace("-", ""), f.getTarget().getId().toString().replace("-", "").replace("node ", "").replace("-", ""));
		}
		result += "}\n";

		return result;
	}

	public String toPEP(Marking m){
		String result ="PEP\nPetriBox\nFORMAT_N2\n";
		result += "PL\n";
		int count = 1;
		Map<PetrinetNode,Integer> posPeP = new HashMap<PetrinetNode,Integer>();
		for (Place p : this.getPlaces()) {

			if(m.contains(p)){
				result +="\""+p.getLabel()+"\"M1\n";
			}
			else{
				result +="\""+p.getLabel()+"\"\n";
			}
			posPeP.put(p, count);
			count++;
		}
		result += "TR\n";
		count = 1;
		for (Transition t : this.getTransitions()) {
			result +="\""+t.getLabel()+"\"\n";
			posPeP.put(t, count);
			count++;
		}
		result += "TP\n";
		String result2 = "PT\n";
		for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> f: this.getEdges()) {
			if((f.getSource() instanceof Transition)&&(f.getTarget() instanceof Place)){
				int t = posPeP.get(f.getSource());
				int p =  posPeP.get(f.getTarget());
				result +=t+"<"+p+"\n";
			}
			if((f.getSource() instanceof Place)&&(f.getTarget() instanceof Transition)){
				int p = posPeP.get(f.getSource());
				int t =  posPeP.get(f.getTarget());
				result2 +=p+">"+t+"\n";
			}	
		}

		result += result2;

		return result;
	}

}
