package gui.editor.visualization.graph.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

/**
 * Načítavací panel
 * @author Dávid
 *
 */
public class GuiProgress implements IGui{
	
	/**
	 * rodičovský komponent
	 */
	private JPanel parentPanel;
	/**
	 * komponent pre všetky prvky
	 */
	private JPanel progressBarPanel;

	/**
	 * konštruktor
	 * @param parentPanel rodičovský komponenet
	 */
	public GuiProgress(JPanel parentPanel) {
		this.parentPanel = parentPanel;
		initialize();
	}

	@Override
	public void show() {
		parentPanel.add(progressBarPanel);		
	}

	@Override
	public void destroy() {
		parentPanel.remove(progressBarPanel);
	}

	/**
	 * inicializácia komponentov
	 */
	private void initialize() {
		
		JLabel pleaseWaitLabel = new JLabel("Please wait...");
		pleaseWaitLabel.setHorizontalAlignment(JLabel.CENTER);

		JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		progressBar.setStringPainted(true);
		progressBar.setPreferredSize(new Dimension(400, 40));

		progressBarPanel = new JPanel();
		progressBarPanel.setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		progressBarPanel.add(progressBar, gbc);

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 1;

		progressBarPanel.add(pleaseWaitLabel, gbc);
	}

}
