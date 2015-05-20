package petrinet.dot;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import models.graphbased.directed.petrinet.Petrinet;
import models.graphbased.directed.petrinet.PetrinetNode;
import models.graphbased.directed.petrinet.elements.Place;
import models.graphbased.directed.petrinet.elements.Transition;
import models.graphbased.directed.petrinet.impl.PetrinetFactory;

public class DotImporting {
	private File dotfile;
	public DotImporting(File pfile) {
		dotfile = pfile;

	}

	public Petrinet importPetriNet(){
		String name = dotfile.getName().substring(0, dotfile.getName().lastIndexOf("."));
		Petrinet net  = PetrinetFactory.newPetrinet("importing dot "+name);
		Map<String,PetrinetNode> posPT = new HashMap<String,PetrinetNode>();

		try {
			List<String> lines = Files.readAllLines(dotfile.toPath(), StandardCharsets.UTF_8);
			String mode = "";
			for(String line : lines){
				if(line.contains("node")){
					if(line.contains("circle")){
						mode="circle";
						continue;
					}
					if(line.contains("box")){
						mode="box";	
						continue;
					}
							
				}else
					if(line.contains("->")){
						mode="arc";
						String nodesource = line.substring(0, line.lastIndexOf(" -> "));
						String nodetarget =  line.substring(line.lastIndexOf(" -> ")+4).replace(";", "");
						if(posPT.containsKey(nodetarget) && posPT.containsKey(nodesource)){
							PetrinetNode source = posPT.get(nodesource);
							PetrinetNode target = posPT.get(nodetarget);
							if(source instanceof Place && target instanceof Transition){
								net.addArc((Place)source, (Transition)target);
							}else
								net.addArc((Transition)source, (Place)target);
						}
					}
				switch (mode) {
				case "circle":{
					String node = line.substring(0, line.indexOf(" "));
					String label ="";
					if(line.contains("\"")){
						label = line.substring(line.indexOf("\"")+1, line.lastIndexOf("\""));
					}
					Place p =net.addPlace(label);
					posPT.put(node, p);
					break;
				}
				case "box":{
					String node = line.substring(0, line.indexOf(" "));
					String label ="";
					if(line.contains("\"")){
						label = line.substring(line.indexOf("\"")+1, line.lastIndexOf("\""));
					}
					Transition t =net.addTransition(label);
					posPT.put(node, t);
					break;
				}
				default:
					break;
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
