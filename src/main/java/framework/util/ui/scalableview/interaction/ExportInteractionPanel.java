package framework.util.ui.scalableview.interaction;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

//import org.freehep.util.export.ExportDialog;
import org.freehep.graphicsbase.util.export.ExportDialog;

import framework.util.ui.scalableview.ScalableComponent;
import framework.util.ui.scalableview.ScalableViewPanel;



public class ExportInteractionPanel extends JPanel implements ViewInteractionPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1036741994786060955L;
	protected final ScalableViewPanel panel;
	private ScalableComponent scalable;
	private JButton exportButton;

	public ExportInteractionPanel(ScalableViewPanel panel) {
		this.panel = panel;
		double size[][] = { { 10, TableLayoutConstants.FILL, 10 }, { 10, TableLayoutConstants.FILL, 10 } };
		setLayout(new TableLayout(size));
		exportButton = new JButton("Export view...");
		exportButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				export();
			}
		});
		this.add(exportButton, "1, 1");
	}

	private void export() {
		ExportDialog export = new ExportDialog();
		export.showExportDialog(this, "Export view as ...", scalable.getComponent(), "View");
	}

	public void updated() {
		// TODO Auto-generated method stub

	}

	public String getPanelName() {
		return "Export";
	}

	public JComponent getComponent() {
		return this;
	}

	public void setScalableComponent(ScalableComponent scalable) {
		this.scalable = scalable;
	}

	public void setParent(ScalableViewPanel viewPanel) {
	}

	public double getHeightInView() {
		return 50;
	}

	public double getWidthInView() {
		return 100;
	}

	public void willChangeVisibility(boolean to) {
	}
}
