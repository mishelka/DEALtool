package gui.editor.visualization.graph.gui;

import gui.editor.visualization.graph.VisualizationPanel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import com.mxgraph.swing.mxGraphComponent;

/**
 * Controller component
 * @author David
 *
 */
public class GuiControlPanel implements IGui, ActionListener {

	/**
	 * Graph component
	 */
	private mxGraphComponent graphComponent;
	/**
	 * Visualization panel
	 */
	private VisualizationPanel parentPanel;
	/**
	 * Component for all control items
	 */
	private JPanel contentPanel;
	/**
	 * Actual value of zoom
	 */
	private double zoomPercentage;
	/**
	 * Actual text for displaying zoom
	 */
	private JLabel zoomPercentageLabel;
	/**
	 * Control items
	 */
	private JPanel searchPanel, zoomPanel, settingsPanel, exportPanel;
	/**
	 * If control panel is hidden, isHidden is set to true.
	 */
	private boolean isHidden;
	/**
	 * Button for hiding the control panel
	 */
	private JButton hideButton;
	/**
	 * Input field for searching
	 */
	private JTextField input;

	/**
	 * Constructor
	 * @param parentPanel parent component
	 * @param graphComponent graph component
	 */
	public GuiControlPanel(VisualizationPanel parentPanel, mxGraphComponent graphComponent) {
		this.parentPanel = parentPanel;
		this.graphComponent = graphComponent;
		this.isHidden = false;
		initialize();
	}

	@Override
	public void show() {
		parentPanel.add(contentPanel);
	}

	@Override
	public void destroy() {
		parentPanel.remove(contentPanel);
	}

	/**
	 * Initializes all components
	 */
	private void initialize() {

		contentPanel = new JPanel();

		// INIT HIDE BUTTON
		hideButton = new JButton("Hide control panel");
		hideButton.setActionCommand("hide");
		hideButton.addActionListener(this);

		// INIT CHILD PANELS
		initZoomPanel();
		initSearchPanel();
		initExportPanel();
		initSettingPanel();

		contentPanel.setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.VERTICAL;
		contentPanel.add(settingsPanel, gbc);

		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.VERTICAL;
		contentPanel.add(searchPanel, gbc);

		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.VERTICAL;
		contentPanel.add(zoomPanel, gbc);

		gbc.gridx = 3;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.VERTICAL;
		contentPanel.add(exportPanel, gbc);

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 4;
		gbc.anchor = GridBagConstraints.FIRST_LINE_END;
		contentPanel.add(hideButton, gbc);
	}

	/**
	 * Initializes the export panel (for saving the generated graph into a file)
	 */
	private void initExportPanel() {
		exportPanel = new JPanel();
		exportPanel.setLayout(new GridBagLayout());
		TitledBorder ztitle = BorderFactory.createTitledBorder("File export");
		exportPanel.setBorder(ztitle);

		JButton exportButton = new JButton("Export");
		exportButton.setActionCommand("export");
		exportButton.addActionListener(this);
		exportPanel.add(exportButton);
	}

	/**
	 * Initializes components for visualization setup
	 */
	private void initSettingPanel() {

		JLabel routingLabel = new JLabel("Routes to stacks");
		JLabel minStackLabel = new JLabel("Min.childs to create stack");

		String[] routingStrings = { "Top", "Left" };
		JComboBox<String> routingBox = new JComboBox<String>(routingStrings);
		routingBox.setSelectedIndex(0);
		routingBox.addActionListener(this);
		routingBox.setActionCommand("routing");

		String[] minStackStrings = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" };
		JComboBox<String> minStackBox = new JComboBox<String>(minStackStrings);
		minStackBox.setSelectedIndex(parentPanel.getGraphVisualization().getLayout().getMinLeafsToStack());
		minStackBox.addActionListener(this);
		minStackBox.setActionCommand("minStack");

		JCheckBox editMode = new JCheckBox("Edit Mode");
		editMode.setSelected(false);
		parentPanel.getGraphVisualization().setEditMode(false);
		editMode.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					parentPanel.getGraphVisualization().setEditMode(true);
				} else {
					parentPanel.getGraphVisualization().setEditMode(false);
				}
			}
		});

		settingsPanel = new JPanel();
		settingsPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		TitledBorder ztitle = BorderFactory.createTitledBorder("Settings");
		settingsPanel.setBorder(ztitle);

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.ipadx = 10;
		settingsPanel.add(routingLabel, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		settingsPanel.add(routingBox, gbc);

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.NONE;
		settingsPanel.add(minStackLabel, gbc);

		gbc.gridx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridy = 1;
		settingsPanel.add(minStackBox, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.CENTER;
		settingsPanel.add(editMode, gbc);

	}

	/**
	 * Initializes components for zooming
	 */
	private void initZoomPanel() {

		zoomPercentage = 100.0;
		zoomPercentageLabel = new JLabel("%");
		updateZoomPercentageLabel();

		JButton zoomIn = new JButton("+");
		zoomIn.setActionCommand("zoomIn");
		zoomIn.addActionListener(this);

		JButton zoomOut = new JButton("-");
		zoomOut.setActionCommand("zoomOut");
		zoomOut.addActionListener(this);

		zoomPanel = new JPanel();
		zoomPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		TitledBorder ztitle = BorderFactory.createTitledBorder("Zoom");
		zoomPanel.setBorder(ztitle);

		gbc.gridx = 0;
		gbc.gridy = 1;
		zoomPanel.add(zoomIn, gbc);

		gbc.gridx = 1;
		gbc.gridy = 1;
		zoomPanel.add(zoomOut, gbc);

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.ipady = 15;
		zoomPanel.add(zoomPercentageLabel, gbc);
	}

	/**
	 * Initializes components for searching
	 */
	private void initSearchPanel() {
		JButton search = new JButton("Find");
		search.setActionCommand("search");
		search.addActionListener(this);

		input = new JTextField();
		input.setColumns(10);

		searchPanel = new JPanel();
		searchPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		TitledBorder stitle = BorderFactory.createTitledBorder("Search Term");
		searchPanel.setBorder(stitle);

		gbc.gridx = 0;
		gbc.gridy = 0;
		searchPanel.add(input, gbc);

		gbc.gridx = 1;
		gbc.gridy = 0;
		searchPanel.add(search, gbc);
	}

	/**
	 * Changes the values of the zooming components
	 */
	private void updateZoomPercentageLabel() {
		String zoom = Double.toString(zoomPercentage);
		zoom = zoom.split("\\.")[0];
		zoomPercentageLabel.setText(zoom + "%");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("zoomIn")) {
			graphComponent.zoomIn();
			zoomPercentage *= graphComponent.getZoomFactor();
			updateZoomPercentageLabel();
			return;
		}

		if (e.getActionCommand().equals("zoomOut")) {
			graphComponent.zoomOut();
			zoomPercentage *= (1 / graphComponent.getZoomFactor());
			updateZoomPercentageLabel();
			return;
		}

		if (e.getActionCommand().equals("routing")) {
			JComboBox<?> cb = (JComboBox<?>) e.getSource();
			if (cb.getSelectedIndex() == 0) {
				// top
				parentPanel.getGraphVisualization().getLayout().setRoutingTop();
			} else if (cb.getSelectedIndex() == 1) {
				// left
				parentPanel.getGraphVisualization().getLayout().setRoutingLeft();
			}
			parentPanel.updateUI();
			return;
		}

		if (e.getActionCommand().equals("minStack")) {
			JComboBox<?> cb = (JComboBox<?>) e.getSource();
			parentPanel.getGraphVisualization().getLayout().setMinLeafsToStack(cb.getSelectedIndex());
			parentPanel.updateUI();
			return;
		}

		if (e.getActionCommand().equals("search")) {

			String searchText = input.getText();
			if (!searchText.equalsIgnoreCase("")) {
				parentPanel.getGraphVisualization().startSearchState(searchText);
				input.setText("");
			} else {
				parentPanel.getGraphVisualization().endSearchState();
			}
			return;
		}

		if (e.getActionCommand().equals("export")) {
			JFileChooser chooser = new JFileChooser();
			chooser.setSelectedFile(new File("graph.png"));
			int returnVal = chooser.showSaveDialog(parentPanel);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				parentPanel.makeGraphImage(chooser.getSelectedFile());
			}
			return;
		}

		if (e.getActionCommand().equals("hide")) {
			zoomPanel.setVisible(isHidden);
			settingsPanel.setVisible(isHidden);
			exportPanel.setVisible(isHidden);
			searchPanel.setVisible(isHidden);
			isHidden = !isHidden;
			if (isHidden)
				hideButton.setText("Show control panel");
			else
				hideButton.setText("Hide control panel");
			parentPanel.updateUI();
			return;
		}

	}

}
