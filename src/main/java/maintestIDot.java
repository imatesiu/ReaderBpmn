import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingConstants;

import models.connections.GraphLayoutConnection;
import models.graphbased.AttributeMap;
import models.graphbased.ViewSpecificAttributeMap;
import models.graphbased.directed.DirectedGraph;
import models.graphbased.directed.DirectedGraphEdge;
import models.graphbased.directed.DirectedGraphNode;
import models.graphbased.directed.petrinet.Petrinet;
import models.jgraph.CustomGraphModel;
import models.jgraph.CustomJGraph;
import models.jgraph.visualization.CustomJGraphPanel;
import petrinet.analysis.WorkflowNetUtils;
import petrinet.behavioralanalysis.woflan.Woflan;
import petrinet.behavioralanalysis.woflan.WoflanDiagnosis;
import petrinet.dot.DotImporting;
import petrinet.pep.PePImporting;

import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.JGraphLayout;
import com.jgraph.layout.hierarchical.JGraphHierarchicalLayout;

import framework.util.SimplePanel;
import framework.util.Util;
import framework.util.ui.scalableview.ScalableViewPanel;
import framework.util.ui.scalableview.interaction.ExportInteractionPanel;
import framework.util.ui.scalableview.interaction.ZoomInteractionPanel;


public class maintestIDot {
	public static void main(String[] args) {


		try {

			JFileChooser fc = new JFileChooser();
			fc.setDialogType(JFileChooser.OPEN_DIALOG);		
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

			int returnVal = fc.showOpenDialog(new JFrame("Load File"));

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();


					DotImporting doti = new DotImporting(file);
				
					Petrinet pn = doti.importPetriNet();
					
					
					boolean result = WorkflowNetUtils.isValidWFNet(pn);


					Woflan wolf = new Woflan();
					WoflanDiagnosis result2 = wolf.diagnose(pn);
					System.out.println();
					
				//	Util.toFile(file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("."))+".ll_net",pn.toPEP());
					
				//	Util.toFile(file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("."))+".html", result2.toHTMLString(false));
					
					/*String dot = graph.toDOT();
					Util.toFile("./esempi/bp.dot", dot);
					String  dot2png = "dot -q -Tpng ./esempi/bp.dot -o ./esempi/bp.png \n";
					System.out.println(dot2png);

					String dot =dot = pn.toDOT();
					Util.toFile("./esempi/pn.dot", dot);
					String  dot2png = "dot -q -Tpng ./esempi/pn.dot -o ./esempi/pn.png \n";
					System.out.println(dot2png);*/

					
					SimplePanel sp = new SimplePanel();
					sp.view(pn);
					
					

				}
			
			

		} catch (Exception e) {

			e.printStackTrace();
		}


	}
	



}
