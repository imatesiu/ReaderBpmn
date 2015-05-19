package petrinet.pep;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.graphbased.directed.petrinet.Petrinet;
import models.graphbased.directed.petrinet.PetrinetNode;
import models.graphbased.directed.petrinet.elements.ExpandableSubNet;
import models.graphbased.directed.petrinet.elements.Place;
import models.graphbased.directed.petrinet.elements.Transition;
import models.graphbased.directed.petrinet.impl.PetrinetFactory;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class PePImporting {

	File pepfile;

	public PePImporting(File pfile) {
		pepfile = pfile;

	}

	public Petrinet importPetriNet(){
		String name = pepfile.getName().substring(0, pepfile.getName().lastIndexOf("."));
		Petrinet net  = PetrinetFactory.newPetrinet("importing pep "+name);
		Map<Integer,Place> posPL = new HashMap<Integer,Place>();
		Map<Integer,Transition> posTR = new HashMap<Integer,Transition>();
		
		try {
			List<String> lines = Files.readAllLines(pepfile.toPath(), StandardCharsets.UTF_8);
			String mode = "";
			int countplace = 1;
			int counttran = 1;
			for(String line : lines){

				switch (line) {
				case "PL":{
					mode = "PL";
					break;
				}
				case "TR":{
					mode = "TR";
					break;
				}
				case "TP":{
					mode = "TP";
					break;
				}
				case "PT":{
					mode = "PT";
					break;
				}

				default:
					break;
				}
				if(line.contains("M1")){
					line = line.substring(0, line.lastIndexOf("M1"));
				}
				if(line.length()>2){
					switch (mode) {
					case "PL":{
						String label = line.replace("\"", "");
						Place p =net.addPlace(label);
						posPL.put(countplace, p);
						countplace++;
						break;
					}
					case "TR":{
						String label = line.replace("\"", "");
						Transition t =net.addTransition(label);
						posTR.put(counttran, t);
						counttran++;
						break;
					}
					case "TP":{
						String t = line.substring(0, line.lastIndexOf("<"));
						int post = Integer.parseInt(t);
						
						String p = line.substring(line.lastIndexOf("<")+1);
						int posp = Integer.parseInt(p);
						
						Transition tt = posTR.get(post);
						Place pp =  posPL.get(posp);
						
						net.addArc(tt, pp);
						break;
					}
					case "PT":{
						String p = line.substring(0, line.lastIndexOf(">"));
						int posp = Integer.parseInt(p);
						
						String t = line.substring(line.lastIndexOf(">")+1);
						int post = Integer.parseInt(t);
						
						Place pp =  posPL.get(posp);
						Transition tt = posTR.get(post);
						
						net.addArc(pp,tt);
						break;
					}

					default:
						break;
					}

				}

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}


		return net;
	}


}
