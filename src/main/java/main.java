import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import plugin.sim.IAllPath;
import framework.util.SimplePanel;

public class main {

	public static void main(String[] args) {
		try {

			JFileChooser fc = new JFileChooser("/Users/isiu/github/learnpadworkspace/ReaderBpmn/esempi");
			fc.setDialogType(JFileChooser.OPEN_DIALOG);		
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

			int returnVal = fc.showOpenDialog(new JFrame("Load File"));

			if (returnVal == JFileChooser.APPROVE_OPTION) {

				SimplePanel sp = new SimplePanel();
				File file = fc.getSelectedFile();
				IAllPath ai =new IAllPath(file, false);
				System.out.println();
				sp.view(ai.getBPMNdiagram());
				sp.view(ai.getPn());
				sp.view(ai.getCovGraph());
				System.out.println(ai.getDpslp());
				System.out.println();
				System.out.println();
				System.out.println();
				System.out.println(ai.getCcbp());

				
				
				

			}
		} catch (Exception e) {

			e.printStackTrace();
		}


	}
	
	

	

}
