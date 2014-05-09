package gui.editor;

import gui.analyzer.util.JLabelFinder;
import gui.editor.DealFileChooser.DealFileChooserType;
import gui.editor.tabpane.VerticalTextIcon;
import gui.editor.tree.TreeCellRenderer;
import gui.editor.tree.TreeModel;
import gui.editor.tree.TreeNode;
import gui.editor.visualization.events.Visualization;
import gui.editor.visualization.graph.VisualizationPanel;
import gui.generator.GeneratorException;
import gui.generator.dsl.YajcoGenerator;
import gui.generator.itask.ITaskGenerator;
import gui.generator.ontology.OntologyTester;
import gui.generator.plaintext.PlainTextGenerator;
import gui.model.application.Application;
import gui.model.application.observable.ApplicationEvent;
import gui.model.application.observable.ApplicationEvent.ApplicationChangeState;
import gui.model.application.scenes.DialogScene;
import gui.model.application.scenes.Scene;
import gui.model.application.scenes.WindowScene;
import gui.model.domain.DomainModel;
import gui.model.domain.Term;
import gui.model.domain.relation.RelationType;
import gui.settings.Setting;
import gui.settings.Settings;
import gui.tools.Recorder;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.apache.commons.io.FilenameUtils;

import yajco.model.Language;

@SuppressWarnings({ "rawtypes", "serial" })
public class DomainModelEditor extends JFrame implements Observer {
	private static DomainModelEditor instance;
	private Settings settings;

	private static Application application = new Application();
	private static ArrayList<Window> windows = new ArrayList<Window>();

	private Component clickedComponent;
	private Color clickedComponentColor;
	private boolean clickedComponentOpaque;

	private DefaultMutableTreeNode clickedPopupNode;

	private YajcoGenerator yajcoGenerator;

	private static final String IMAGE_PATH = "resources/editor/";	

	/** Getter for singleton */
	public static DomainModelEditor getInstance() {
		if (instance == null) {
			DomainModelEditor.instance = new DomainModelEditor();
			instance.setVisible(true);
		}
		return instance;
	}

	/** default constructor */
	private DomainModelEditor() {
		settings = new Settings();
		UIManager.put("TabbedPane.textIconGap", new Integer(-8));
		initComponents();

		setTitle("DEAL (Domain Extraction ALgorithm) tool prototype");
		setIconImage(Toolkit.getDefaultToolkit().getImage(InputFileDialog.class.getResource("/gui/editor/resources/tree/model.png")));

		ToolTipManager.sharedInstance().registerComponent(domainJTree);
		domainJTree.setCellRenderer(new TreeCellRenderer());

		expandAll(domainJTree, true);
		
		yajcoGenerator = new YajcoGenerator();
		
		//setSize(1080, 716);
	}
	
	public void saveCurrentSetting() {
		Setting s = new Setting(extractFunctionalComponents.isSelected());
		settings.save(s);
	}
	
	public Setting getSetting() {
		return settings.load();
	}

	//<editor-fold defaultstate="collapsed" desc="Methods for domain model setup">
	/**
	 * The Observer method - if any scene is added, edited or removed, this
	 * method is called.
	 */
	@Override
	public void update(Observable application, Object event) {
		ApplicationEvent appEvt = (ApplicationEvent) event;

		if (appEvt.getChangeState() == ApplicationChangeState.ADDED) {
			addDomainModel(appEvt.getSourceScene());

			setupComponentTreeModel();
			
			updateVisualizationPanel();
		} else {
			// we're not testing remove/name_changed, because the
			// setupDomainTreeModel and setupComponetnTreeModel() will be called
			// to update
			// in the future this will be used to edit/save the existing model
		}
	}

	public void addDomainModel(Scene scene) {
		TreeModel newModel = new TreeModel(application);

		for (Scene s : application.getScenes()) {
			TreeModel toAdd = new TreeModel(s.getDomainModel());
			newModel.add(toAdd);
		}

		setupDomainTreeModel(newModel);
	}

	public void setupDomainTreeModel(TreeModel model) {
		domainJTree = new JTree(model);

		ToolTipManager.sharedInstance().registerComponent(domainJTree);
		domainJTree.setCellRenderer(new TreeCellRenderer());

		domainJTree
				.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
					public void valueChanged(
							javax.swing.event.TreeSelectionEvent evt) {
						onTreeValueChanged(evt);
					}
				});

		domainJTree.addMouseListener(new PopClickListener());

		domainJTree.setScrollsOnExpand(true);

		domainScrollPane.setViewportView(domainJTree);

		repaint();

		expandAll(domainJTree, true);
	}
	//</editor-fold>

	//<editor-fold defaultstate="collapsed" desc="Methods for component tree setup">
	/**
	 * Sets up the ComponentTreeModel based on the window that just opened.
	 */
	public void setupComponentTreeModel() {
		DefaultMutableTreeNode dmtn = new DefaultMutableTreeNode(
				"Component tree");

		for (Scene s : application.getScenes()) {
			if (s instanceof WindowScene || s instanceof DialogScene) {
				dmtn.add(setupPartOfTreeModelExtended((Window) s
						.getSceneContainer()));
			}
		}

		componentJTree = new JTree(new DefaultTreeModel(dmtn));

		parseTreeAndClearStrings(componentJTree.getModel());
		componentJTree.setScrollsOnExpand(true);

		componentJTree
				.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
					public void valueChanged(
							javax.swing.event.TreeSelectionEvent evt) {
						onTreeValueChanged(evt);
					}
				});

		expandAll(componentJTree, true);

		componentScrollPane.setViewportView(componentJTree);

		repaint();
	}
	
	private void updateVisualizationPanel() {
		visualizationPanel.update(showInfoTypesCheckBox.isSelected());
	}

	/**
	 * If only a part of a tree has changed / only one window from the list of
	 * windows changed -> then this method is called to add this part into the
	 * tree model.
	 */
	private DefaultMutableTreeNode setupPartOfTreeModelExtended(Window frame) {
		Component[] comp = frame.getComponents();
		DefaultMutableTreeNode top = new DefaultMutableTreeNode(frame) {
			private static final long serialVersionUID = 1L;

			@Override
			public String toString() {
				if (super.userObject instanceof Dialog) {
					return ((Dialog) super.userObject).getTitle();
				} else if (super.userObject instanceof Frame) {
					return ((Frame) super.userObject).getTitle();
				}
				return super.userObject.getClass().getSimpleName();
			}
		};
		helper(comp, top);
		return top;
	}

	private void helper(Component[] components, DefaultMutableTreeNode top) {
		if (components == null) {
			return;
		}
		for (int i = 0; i < components.length; i++) {
			if (components[i] instanceof Container) {
				DefaultMutableTreeNode cont = null;
				cont = new DefaultMutableTreeNode(components[i]) {
					private static final long serialVersionUID = 1L;

					@Override
					public String toString() {
						if (this.userObject instanceof JLabel) {
							JLabel comp = (JLabel) this.userObject;
							return comp.getText();
						}
						if (this.userObject instanceof JButton) {
							JButton comp = (JButton) this.userObject;
							return comp.getText();
						}
						return this.userObject.getClass().getSimpleName();
					}
				};
				top.add(cont);
				if (components[i] instanceof JMenu) {
					helper(((JMenu) components[i]).getMenuComponents(), cont);
				} else {
					helper(((Container) components[i]).getComponents(), cont);
				}
			}
		}
	}

	private void parseTreeAndClearStrings(javax.swing.tree.TreeModel treeModel) {
		Enumeration<?> nodes = ((DefaultMutableTreeNode) (treeModel.getRoot()))
				.breadthFirstEnumeration();

		while (nodes.hasMoreElements()) {
			Object obj = nodes.nextElement();
			if (obj instanceof DefaultMutableTreeNode) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) obj;
				if (node.getUserObject() instanceof Dialog) {
					Dialog dialog = (Dialog) node.getUserObject();
					if (!dialog.isVisible() && !dialog.isValid()) {
						node.removeFromParent();
					}
				}
			}
		}
	}
	//</editor-fold>

	//<editor-fold defaultstate="collapsed" desc="Getters and setters">
	public javax.swing.JTree getDomainTree() {
		return domainJTree;
	}

	public javax.swing.JTree getComponentTree() {
		return componentJTree;
	}

	public Application getApplication() {
		return application;
	}

	public List<DomainModel> getDomainModels() {
		return application.getDomainModels();
	}
	
	public List<Window> getApplicationWindows() {
		return windows;
	}
	
	public void addApplicationWindow(Window window) {
		if(!windows.contains(window)) {
			windows.add(window);
		}
	}
	//</editor-fold>

	//<editor-fold defaultstate="collapsed" desc="Mouse clicking events (on tree click, hide funcitonality)">
	private void onTreeValueChanged(javax.swing.event.TreeSelectionEvent evt) {
		DefaultMutableTreeNode obj = (DefaultMutableTreeNode) evt.getPath()
				.getLastPathComponent();
		Object clickedObject = obj.getUserObject();
		if(clickedObject != null) {
			if (clickedObject instanceof Term) {
				showInTrees((Term)clickedObject);
			} else if (clickedObject instanceof Component) {
				showInTrees((Component) clickedObject);
			}
		}
	}
	
	public void showInTrees(Component component) {
		unhighlightLastClickedComponent();
		showClickedComponent(component);
		Term targetTerm = ((gui.editor.tree.TreeModel) 
				domainJTree.getModel()).findTermForComponent(clickedComponent);
		showClickedTerm(targetTerm);
		updateInfoPanel(targetTerm, component);
	}
	
	public void showInTrees(Term term) {
		unhighlightLastClickedComponent();
		Object component = term.getComponent();
		showClickedComponent(component);
		showClickedTerm(term);
		updateInfoPanel(term, component);
	}
	
	private void showClickedComponent(Object component) {	
		if (component != null) {
			highlightComponentWithYellow(component);
			//TODO: ak je component null, tak unhighlight tree, to iste v termJTree
			showComponentInTrees(component);
		}
	}
	
	private void showClickedTerm(Term term) {
		showTermInTrees(term);
	}
	
	private void unhighlightLastClickedComponent() {
		if (clickedComponent != null) {
			clickedComponent.setBackground(clickedComponentColor);
			if (clickedComponent instanceof JComponent)
				((JComponent) clickedComponent)
						.setOpaque(clickedComponentOpaque);
		}
	}
	
	private void highlightComponentWithYellow(Object component) {
		if(component instanceof Component) {
			Component clickedComponent = (Component) component;
			this.clickedComponent = clickedComponent;
			clickedComponentColor = clickedComponent.getBackground();
			clickedComponent.setBackground(Color.YELLOW);
			if (clickedComponent instanceof JComponent) {
				JComponent jc = (JComponent) clickedComponent;
				clickedComponentOpaque = jc.isOpaque();
				jc.setOpaque(true);
			}
		}
	}

	private void onHidePopupMenuItemActionPerformed() {
		if (clickedPopupNode != null) {
			if (clickedPopupNode instanceof TreeNode) {
				TreeNode tn = (TreeNode) clickedPopupNode;
				tn.setHidden(!tn.isHidden());

				reloadTrees();
			}
		}
	}

	private void onHideAllPopupMenuItemActionPerformed() {
		if (clickedPopupNode != null) {
			if (clickedPopupNode instanceof TreeNode) {
				TreeNode tn = (TreeNode) clickedPopupNode;
				tn.hideSubtree();

				reloadTrees();
			}
		}
	}

	private void onUnhideAllPopupMenuItemActionPerformed() {
		if (clickedPopupNode != null) {
			if (clickedPopupNode instanceof TreeNode) {
				TreeNode tn = (TreeNode) clickedPopupNode;
				tn.unhideSubtree();

				reloadTrees();
			}
		}
	}

	private void reloadTrees() {
		DefaultTreeModel cmodel = (DefaultTreeModel) componentJTree.getModel();
		cmodel.reload();
		DefaultTreeModel dmodel = (DefaultTreeModel) domainJTree.getModel();
		dmodel.reload();

		expandAll(componentJTree, true);
		expandAll(domainJTree, true);
	}
	
	private void updateVisualization() {
		if(visualizationPanel != null && visualizationPanel.isInited()){
			visualizationPanel.actionPerformedInfoTypeCheckBox(showInfoTypesCheckBox.isSelected());
		}
	}
	//</editor-fold>

	//<editor-fold defaultstate="collapsed" desc="Recorder stuff">
	/** Thread for recording */
	private Thread recordingThread;
	/** Recorder instance - Recorder enables recording to physical memory. */
	private Recorder recorder;

	/**
	 * @param recorder
	 *            a Recorder instance responsible for recording to physical
	 *            memory.
	 */
	public void setRecorder(Recorder recorder) {
		this.recorder = recorder;
	}

	public Recorder getRecorder() {
		return recorder;
	}
	
	private void browseButtonActionPerformed(ActionEvent evt) {
		DealFileChooser chooser = new DealFileChooser("Record", DealFileChooserType.DEAL);
		int returnVal = chooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File f = chooser.getSelectedFile();
			if(f != null) {
				fileNameTextField.setText(f.getAbsolutePath());
			}
		}
	}

	private void recordButtonActionPerformed(ActionEvent evt) {
		if (!recorder.isRecording()) {
			String name = fileNameTextField.getText();
			try {
				if (name != null && !name.equals("")) {
					recorder.createNewFile(name);
				} else {
					recorder.createNewFile();
					fileNameTextField.setText(recorder.getFileName());
				}

				//My visualization - zaznamenava aj kliknutia v jframe pre vizualizaciu- TODO: opravit
			    Visualization visualizer = new Visualization();
				visualizer.startVisualization();	
				recorder.addListener(visualizer);
				
				recorder.setRecording(true);

				updateRecordPanel();

				System.out.println("\nSTARTING RECORDING\n");

				recordingThread = new RecordingThread();
				recordingThread.start();
			} catch (IOException ex) {
				System.out.println("CAN NOT CREATE NEW FILE: ");
				ex.printStackTrace();

				recorder.setRecording(false);
				recorder.closeWriter();
				
				updateRecordPanel();
			}
		}
	}
	
	private void updateRecordPanel() {
		boolean recording = recorder.isRecording();
		
		recordButton.setEnabled(!recording);
		stopButton.setEnabled(recording);
		fileNameTextField.setEnabled(!recording);
	}

	private void stopButtonActionPerformed(ActionEvent evt) {
		if (recorder.isRecording()) {
			recordingThread.interrupt();
			try {
				recordingThread.join();
			} catch (InterruptedException ex) {
				// could not stop
			}
			recordingProgressBar.setValue(0);
			
			System.out.println("\nSTOPPING RECORDING\n");
			fileNameTextField.setText("");

			recorder.setRecording(false);
			recorder.closeWriter();
			
			updateRecordPanel();

			recorder.writeResultToConsole();
		}
	}

	public void showInfoTypesCheckBoxActionPerformed(
			ActionEvent evt) {
		for (DomainModel domainModel : application.getDomainModels()) {
			domainModel.setShowComponentInfoTypes(showInfoTypesCheckBox
					.isSelected());
		}

		reloadTrees();
		
		updateVisualization();
	}
	//</editor-fold>

	//<editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {
		java.awt.GridBagConstraints gridBagConstraints;

		infoSplitPane = new javax.swing.JSplitPane();
		leftPanel = new javax.swing.JPanel();
		infoPanel = new javax.swing.JPanel();
		infoPanel.setBorder(new EmptyBorder(0, 5, 0, 5));
		domainInfoTitlePanel = new javax.swing.JPanel();
		domainInfoLabel = new javax.swing.JLabel();
		domainInfoSeparator = new javax.swing.JSeparator();
		nameLabel = new javax.swing.JLabel();
		nameField = new javax.swing.JTextArea();
		descriptionLabel = new javax.swing.JLabel();
		descriptionField = new javax.swing.JTextField();
		typeLabel = new javax.swing.JLabel();
		
		typeComboBox = new javax.swing.JComboBox<RelationType>(
				RelationType.values());
		typeComboBox.setEnabled(false);
		
		iconLabel = new javax.swing.JLabel();
		iconField = new javax.swing.JLabel();
		componentInfoTitlePanel = new javax.swing.JPanel();
		componentInfoLabel = new javax.swing.JLabel();
		componentInfoSeparator = new javax.swing.JSeparator();
		classLabel = new javax.swing.JLabel();
		classField = new javax.swing.JTextField();
		contentLabel = new javax.swing.JLabel();
		contentField = new javax.swing.JTextField();
		tooltipLabel = new javax.swing.JLabel();
		tooltipField = new javax.swing.JTextField();
		labelLabel = new javax.swing.JLabel();
		labelField = new javax.swing.JTextField();
		actionCommandLabel = new javax.swing.JLabel();
		actionCommandField = new javax.swing.JTextField();
		rightJTabbedPane = new javax.swing.JTabbedPane();
		modelSplitPane = new javax.swing.JSplitPane();
		domainTreePanel = new javax.swing.JPanel();
		domainLabel = new javax.swing.JLabel();
		domainScrollPane = new javax.swing.JScrollPane();
		domainJTree = new javax.swing.JTree();
		componentTreePanel = new javax.swing.JPanel();
		componentLabel = new javax.swing.JLabel();
		componentScrollPane = new javax.swing.JScrollPane();
		componentJTree = new javax.swing.JTree();
//		editorScrollPane = new javax.swing.JScrollPane();
//		editorTextArea = new javax.swing.JTextArea();
		menuBar = new javax.swing.JMenuBar();
		fileMenu = new javax.swing.JMenu();
		settingsMenu = new javax.swing.JMenu();
		//not used in this version yet
//		saveMenuItem = new javax.swing.JMenuItem();
//		openMenuItem = new javax.swing.JMenuItem();
		exitMenuItem = new javax.swing.JMenuItem();
		generateDSLMenuItem = new javax.swing.JMenuItem();
		generateITaskMenuItem = new javax.swing.JMenuItem();
		generateOntologyFromDomainModel = new javax.swing.JMenuItem();
		generatePlainTextMenuItem = new javax.swing.JMenuItem();
		findComponentByNameMenuItem = new javax.swing.JMenuItem();
		extractFunctionalComponents = new javax.swing.JCheckBoxMenuItem();
		
		recordButtonGroup = new javax.swing.ButtonGroup();

		nameField.setLineWrap(true);

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setTitle("DEAL");
		setBounds(0, 0, 1080, 716);
		setMinimumSize(new java.awt.Dimension(600, 500));

		infoSplitPane.setDividerLocation(250);
		infoSplitPane.setDividerSize(2);

		GridBagLayout gbl_infoPanel = new GridBagLayout();
		gbl_infoPanel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10, 0, 0 };
		gbl_infoPanel.columnWidths = new int[] { 105, 0, 0 };
		gbl_infoPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
		gbl_infoPanel.columnWeights = new double[] { 1.0, 1.0, 0.0 };
		infoPanel.setLayout(gbl_infoPanel);

		domainInfoTitlePanel.setLayout(new java.awt.GridBagLayout());

		domainInfoLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
		domainInfoLabel.setText("Term");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
		domainInfoTitlePanel.add(domainInfoLabel, gridBagConstraints);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 3;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		domainInfoTitlePanel.add(domainInfoSeparator, gridBagConstraints);

		gbc_domainInfoTitlePanel = new java.awt.GridBagConstraints();
		gbc_domainInfoTitlePanel.insets = new Insets(0, 0, 5, 0);
		gbc_domainInfoTitlePanel.gridx = 0;
		gbc_domainInfoTitlePanel.gridy = 0;
		gbc_domainInfoTitlePanel.gridwidth = 3;
		gbc_domainInfoTitlePanel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		infoPanel.add(domainInfoTitlePanel, gbc_domainInfoTitlePanel);

		nameLabel.setText("Name:");
		gridBagConstraints_1 = new java.awt.GridBagConstraints();
		gridBagConstraints_1.gridx = 0;
		gridBagConstraints_1.gridy = 1;
		gridBagConstraints_1.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
		gridBagConstraints_1.insets = new Insets(5, 0, 5, 5);
		infoPanel.add(nameLabel, gridBagConstraints_1);

		nameField.setColumns(10);
		nameField.setEditable(false);
		gridBagConstraints_2 = new java.awt.GridBagConstraints();
		gridBagConstraints_2.gridwidth = 2;
		gridBagConstraints_2.gridx = 1;
		gridBagConstraints_2.gridy = 1;
		gridBagConstraints_2.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints_2.anchor = java.awt.GridBagConstraints.BASELINE;
		gridBagConstraints_2.weightx = 1.0;
		gridBagConstraints_2.insets = new Insets(5, 0, 5, 0);
		infoPanel.add(nameField, gridBagConstraints_2);

		descriptionLabel.setText("Description:");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
		gridBagConstraints.insets = new Insets(5, 0, 5, 5);
		infoPanel.add(descriptionLabel, gridBagConstraints);

		descriptionField.setColumns(10);
		descriptionField.setEditable(false);
		gridBagConstraints_3 = new java.awt.GridBagConstraints();
		gridBagConstraints_3.gridwidth = 2;
		gridBagConstraints_3.gridx = 1;
		gridBagConstraints_3.gridy = 2;
		gridBagConstraints_3.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints_3.anchor = java.awt.GridBagConstraints.BASELINE;
		gridBagConstraints_3.weightx = 1.0;
		gridBagConstraints_3.insets = new Insets(5, 0, 5, 0);
		infoPanel.add(descriptionField, gridBagConstraints_3);

		typeLabel.setText("Type:");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
		gridBagConstraints.insets = new Insets(5, 0, 5, 5);
		infoPanel.add(typeLabel, gridBagConstraints);

		gridBagConstraints_4 = new java.awt.GridBagConstraints();
		gridBagConstraints_4.gridwidth = 2;
		gridBagConstraints_4.gridx = 1;
		gridBagConstraints_4.gridy = 3;
		gridBagConstraints_4.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints_4.anchor = java.awt.GridBagConstraints.BASELINE;
		gridBagConstraints_4.weightx = 1.0;
		gridBagConstraints_4.insets = new Insets(5, 0, 5, 0);
		infoPanel.add(typeComboBox, gridBagConstraints_4);

		iconLabel.setText("Icon:");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
		gridBagConstraints.insets = new Insets(5, 0, 5, 5);
		infoPanel.add(iconLabel, gridBagConstraints);

		iconField.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		iconField.setText("no icon");
		gridBagConstraints_5 = new java.awt.GridBagConstraints();
		gridBagConstraints_5.gridwidth = 2;
		gridBagConstraints_5.gridx = 1;
		gridBagConstraints_5.gridy = 4;
		gridBagConstraints_5.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints_5.anchor = java.awt.GridBagConstraints.BASELINE;
		gridBagConstraints_5.weightx = 1.0;
		gridBagConstraints_5.insets = new Insets(5, 0, 5, 0);
		infoPanel.add(iconField, gridBagConstraints_5);

		componentInfoTitlePanel.setLayout(new java.awt.GridBagLayout());

		componentInfoLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
		componentInfoLabel.setText("Component");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
		componentInfoTitlePanel.add(componentInfoLabel, gridBagConstraints);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 3;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		componentInfoTitlePanel.add(componentInfoSeparator, gridBagConstraints);

		gbc_componentInfoTitlePanel = new java.awt.GridBagConstraints();
		gbc_componentInfoTitlePanel.gridx = 0;
		gbc_componentInfoTitlePanel.gridy = 5;
		gbc_componentInfoTitlePanel.gridwidth = 3;
		gbc_componentInfoTitlePanel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gbc_componentInfoTitlePanel.insets = new Insets(10, 0, 5, 0);
		infoPanel.add(componentInfoTitlePanel, gbc_componentInfoTitlePanel);

		classLabel.setText("Class:");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 6;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
		gridBagConstraints.insets = new Insets(5, 0, 5, 5);
		infoPanel.add(classLabel, gridBagConstraints);

		classField.setColumns(10);
		classField.setEditable(false);
		gridBagConstraints_6 = new java.awt.GridBagConstraints();
		gridBagConstraints_6.gridwidth = 2;
		gridBagConstraints_6.gridx = 1;
		gridBagConstraints_6.gridy = 6;
		gridBagConstraints_6.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints_6.anchor = java.awt.GridBagConstraints.BASELINE;
		gridBagConstraints_6.weightx = 1.0;
		gridBagConstraints_6.insets = new Insets(5, 0, 5, 0);
		infoPanel.add(classField, gridBagConstraints_6);

		contentLabel.setText("Text/Content:");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 7;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
		gridBagConstraints.insets = new Insets(5, 0, 5, 5);
		infoPanel.add(contentLabel, gridBagConstraints);

		contentField.setColumns(10);
		contentField.setEditable(false);
		gridBagConstraints_7 = new java.awt.GridBagConstraints();
		gridBagConstraints_7.gridwidth = 2;
		gridBagConstraints_7.gridx = 1;
		gridBagConstraints_7.gridy = 7;
		gridBagConstraints_7.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints_7.anchor = java.awt.GridBagConstraints.BASELINE;
		gridBagConstraints_7.weightx = 1.0;
		gridBagConstraints_7.insets = new Insets(5, 0, 5, 0);
		infoPanel.add(contentField, gridBagConstraints_7);

		tooltipLabel.setText("Tooltip:");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 8;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
		gridBagConstraints.insets = new Insets(5, 0, 5, 5);
		infoPanel.add(tooltipLabel, gridBagConstraints);

		tooltipField.setColumns(10);
		tooltipField.setEditable(false);
		gridBagConstraints_8 = new java.awt.GridBagConstraints();
		gridBagConstraints_8.gridwidth = 2;
		gridBagConstraints_8.gridx = 1;
		gridBagConstraints_8.gridy = 8;
		gridBagConstraints_8.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints_8.anchor = java.awt.GridBagConstraints.BASELINE;
		gridBagConstraints_8.weightx = 1.0;
		gridBagConstraints_8.insets = new Insets(5, 0, 5, 0);
		infoPanel.add(tooltipField, gridBagConstraints_8);

		labelLabel.setText("Label:");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 9;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
		gridBagConstraints.insets = new Insets(5, 0, 5, 5);
		infoPanel.add(labelLabel, gridBagConstraints);

		labelField.setEditable(false);
		gridBagConstraints_9 = new java.awt.GridBagConstraints();
		gridBagConstraints_9.gridwidth = 2;
		gridBagConstraints_9.gridx = 1;
		gridBagConstraints_9.gridy = 9;
		gridBagConstraints_9.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints_9.anchor = java.awt.GridBagConstraints.BASELINE;
		gridBagConstraints_9.weightx = 1.0;
		gridBagConstraints_9.insets = new Insets(5, 0, 5, 0);
		infoPanel.add(labelField, gridBagConstraints_9);

		actionCommandLabel.setText("Action command:");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 10;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
		gridBagConstraints.insets = new Insets(5, 0, 5, 5);
		infoPanel.add(actionCommandLabel, gridBagConstraints);

		actionCommandField.setEditable(false);
		gridBagConstraints_10 = new java.awt.GridBagConstraints();
		gridBagConstraints_10.gridwidth = 2;
		gridBagConstraints_10.gridx = 1;
		gridBagConstraints_10.gridy = 10;
		gridBagConstraints_10.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints_10.anchor = java.awt.GridBagConstraints.BASELINE;
		gridBagConstraints_10.weightx = 1.0;
		gridBagConstraints_10.insets = new Insets(5, 0, 5, 0);
		infoPanel.add(actionCommandField, gridBagConstraints_10);

		componentInfoTypesTitlePanel = new JPanel();
		GridBagConstraints gbc_componentInfoTypesTitlePanel = new GridBagConstraints();
		gbc_componentInfoTypesTitlePanel.insets = new Insets(0, 0, 5, 0);
		gbc_componentInfoTypesTitlePanel.gridwidth = 3;
		gbc_componentInfoTypesTitlePanel.fill = GridBagConstraints.BOTH;
		gbc_componentInfoTypesTitlePanel.gridx = 0;
		gbc_componentInfoTypesTitlePanel.gridy = 12;
		infoPanel.add(componentInfoTypesTitlePanel,
				gbc_componentInfoTypesTitlePanel);
		GridBagLayout gbl_componentInfoTypesTitlePanel = new GridBagLayout();
		gbl_componentInfoTypesTitlePanel.columnWidths = new int[] { 0, 0, 0, 0,
				0 };
		gbl_componentInfoTypesTitlePanel.rowHeights = new int[] { 0, 0 };
		gbl_componentInfoTypesTitlePanel.columnWeights = new double[] { 0.0,
				0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_componentInfoTypesTitlePanel.rowWeights = new double[] { 0.0,
				Double.MIN_VALUE };
		componentInfoTypesTitlePanel
				.setLayout(gbl_componentInfoTypesTitlePanel);

		componentInfoTypesLabel = new JLabel();
		componentInfoTypesLabel.setText("Component Info Types");
		componentInfoTypesLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_componentInfoTypesLabel = new GridBagConstraints();
		gbc_componentInfoTypesLabel.anchor = GridBagConstraints.LINE_START;
		gbc_componentInfoTypesLabel.insets = new Insets(0, 0, 0, 5);
		gbc_componentInfoTypesLabel.gridx = 0;
		gbc_componentInfoTypesLabel.gridy = 0;
		componentInfoTypesTitlePanel.add(componentInfoTypesLabel,
				gbc_componentInfoTypesLabel);

		componentInfoTypesSeparator = new JSeparator();
		GridBagConstraints gbc_componentInfoTypesSeparator = new GridBagConstraints();
		gbc_componentInfoTypesSeparator.weightx = 1.0;
		gbc_componentInfoTypesSeparator.fill = GridBagConstraints.HORIZONTAL;
		gbc_componentInfoTypesSeparator.gridwidth = 3;
		gbc_componentInfoTypesSeparator.gridx = 1;
		gbc_componentInfoTypesSeparator.gridy = 0;
		componentInfoTypesTitlePanel.add(componentInfoTypesSeparator,
				gbc_componentInfoTypesSeparator);

		componentInfoTypeBlue = new JLabel("");
		componentInfoTypeBlue.setIcon(new ImageIcon(DomainModelEditor.class
				.getResource(IMAGE_PATH + "info_type_blue.png")));
		componentInfoTypeBlue.setBackground(Color.BLUE);
		GridBagConstraints gbc_componentInfoTypeBlue = new GridBagConstraints();
		gbc_componentInfoTypeBlue.fill = GridBagConstraints.VERTICAL;
		gbc_componentInfoTypeBlue.insets = new Insets(0, 0, 5, 5);
		gbc_componentInfoTypeBlue.gridx = 0;
		gbc_componentInfoTypeBlue.gridy = 13;
		infoPanel.add(componentInfoTypeBlue, gbc_componentInfoTypeBlue);

		componentInfoTypeBlueLabel = new JLabel(
				"Informative and Text Components");
		GridBagConstraints gbc_componentInfoTypeBlueLabel = new GridBagConstraints();
		gbc_componentInfoTypeBlueLabel.gridwidth = 2;
		gbc_componentInfoTypeBlueLabel.insets = new Insets(0, 0, 5, 0);
		gbc_componentInfoTypeBlueLabel.anchor = GridBagConstraints.WEST;
		gbc_componentInfoTypeBlueLabel.gridx = 1;
		gbc_componentInfoTypeBlueLabel.gridy = 13;
		infoPanel.add(componentInfoTypeBlueLabel,
				gbc_componentInfoTypeBlueLabel);

		componentInfoTypeRed = new JLabel("");
		componentInfoTypeRed.setIcon(new ImageIcon(DomainModelEditor.class
				.getResource(IMAGE_PATH + "info_type_red.png")));
		componentInfoTypeRed.setBackground(Color.BLUE);
		GridBagConstraints gbc_componentInfoTypeRed = new GridBagConstraints();
		gbc_componentInfoTypeRed.insets = new Insets(0, 0, 5, 5);
		gbc_componentInfoTypeRed.gridx = 0;
		gbc_componentInfoTypeRed.gridy = 14;
		infoPanel.add(componentInfoTypeRed, gbc_componentInfoTypeRed);

		componentInfoTypeRedLabel = new JLabel("Functional components");
		GridBagConstraints gbc_componentInfoTypeRedLabel = new GridBagConstraints();
		gbc_componentInfoTypeRedLabel.gridwidth = 2;
		gbc_componentInfoTypeRedLabel.anchor = GridBagConstraints.WEST;
		gbc_componentInfoTypeRedLabel.insets = new Insets(0, 0, 5, 0);
		gbc_componentInfoTypeRedLabel.gridx = 1;
		gbc_componentInfoTypeRedLabel.gridy = 14;
		infoPanel.add(componentInfoTypeRedLabel, gbc_componentInfoTypeRedLabel);

		componentInfoTypeGreen = new JLabel("");
		componentInfoTypeGreen.setIcon(new ImageIcon(DomainModelEditor.class
				.getResource(IMAGE_PATH + "info_type_green.png")));
		componentInfoTypeGreen.setBackground(Color.BLUE);
		GridBagConstraints gbc_componentInfoTypeGreen = new GridBagConstraints();
		gbc_componentInfoTypeGreen.insets = new Insets(0, 0, 5, 5);
		gbc_componentInfoTypeGreen.gridx = 0;
		gbc_componentInfoTypeGreen.gridy = 15;
		infoPanel.add(componentInfoTypeGreen, gbc_componentInfoTypeGreen);

		componentInfoTypeGreenLabel = new JLabel(
				"Graphically grouping components");
		GridBagConstraints gbc_componentInfoTypeGreenLabel = new GridBagConstraints();
		gbc_componentInfoTypeGreenLabel.gridwidth = 2;
		gbc_componentInfoTypeGreenLabel.anchor = GridBagConstraints.WEST;
		gbc_componentInfoTypeGreenLabel.insets = new Insets(0, 0, 5, 0);
		gbc_componentInfoTypeGreenLabel.gridx = 1;
		gbc_componentInfoTypeGreenLabel.gridy = 15;
		infoPanel.add(componentInfoTypeGreenLabel,
				gbc_componentInfoTypeGreenLabel);

		componentInfoTypeCyan = new JLabel("");
		componentInfoTypeCyan.setIcon(new ImageIcon(DomainModelEditor.class
				.getResource(IMAGE_PATH + "info_type_cyan.png")));
		componentInfoTypeCyan.setBackground(Color.BLUE);
		GridBagConstraints gbc_componentInfoTypeCyan = new GridBagConstraints();
		gbc_componentInfoTypeCyan.insets = new Insets(0, 0, 5, 5);
		gbc_componentInfoTypeCyan.gridx = 0;
		gbc_componentInfoTypeCyan.gridy = 16;
		infoPanel.add(componentInfoTypeCyan, gbc_componentInfoTypeCyan);

		componentInfoTypeCyanLabel = new JLabel("Logically grouping components");
		GridBagConstraints gbc_componentInfoTypeCyanLabel = new GridBagConstraints();
		gbc_componentInfoTypeCyanLabel.gridwidth = 2;
		gbc_componentInfoTypeCyanLabel.anchor = GridBagConstraints.WEST;
		gbc_componentInfoTypeCyanLabel.insets = new Insets(0, 0, 5, 0);
		gbc_componentInfoTypeCyanLabel.gridx = 1;
		gbc_componentInfoTypeCyanLabel.gridy = 16;
		infoPanel.add(componentInfoTypeCyanLabel,
				gbc_componentInfoTypeCyanLabel);

		componentInfoTypeYellow = new JLabel("");
		componentInfoTypeYellow.setIcon(new ImageIcon(DomainModelEditor.class
				.getResource(IMAGE_PATH + "info_type_yellow.png")));
		componentInfoTypeYellow.setBackground(Color.BLUE);
		GridBagConstraints gbc_componentInfoTypeYellow = new GridBagConstraints();
		gbc_componentInfoTypeYellow.insets = new Insets(0, 0, 5, 5);
		gbc_componentInfoTypeYellow.gridx = 0;
		gbc_componentInfoTypeYellow.gridy = 17;
		infoPanel.add(componentInfoTypeYellow, gbc_componentInfoTypeYellow);

		componentInfoTypeYellowLabel = new JLabel("Custom components");
		GridBagConstraints gbc_componentInfoTypeYellowLabel = new GridBagConstraints();
		gbc_componentInfoTypeYellowLabel.gridwidth = 2;
		gbc_componentInfoTypeYellowLabel.anchor = GridBagConstraints.WEST;
		gbc_componentInfoTypeYellowLabel.insets = new Insets(0, 0, 5, 0);
		gbc_componentInfoTypeYellowLabel.gridx = 1;
		gbc_componentInfoTypeYellowLabel.gridy = 17;
		infoPanel.add(componentInfoTypeYellowLabel,
				gbc_componentInfoTypeYellowLabel);

		componentInfoTypeWhite = new JLabel("");
		componentInfoTypeWhite.setIcon(new ImageIcon(DomainModelEditor.class
				.getResource(IMAGE_PATH + "info_type_white.png")));
		componentInfoTypeWhite.setBackground(Color.BLUE);
		GridBagConstraints gbc_componentInfoTypeWhite = new GridBagConstraints();
		gbc_componentInfoTypeWhite.insets = new Insets(0, 0, 5, 5);
		gbc_componentInfoTypeWhite.gridx = 0;
		gbc_componentInfoTypeWhite.gridy = 18;
		infoPanel.add(componentInfoTypeWhite, gbc_componentInfoTypeWhite);

		showInfoTypesPanel = new JPanel();
		showInfoTypesPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		GridBagConstraints gbc_showInfoTypesPanel = new GridBagConstraints();
		gbc_showInfoTypesPanel.insets = new Insets(0, 0, 5, 5);
		gbc_showInfoTypesPanel.fill = GridBagConstraints.BOTH;
		gbc_showInfoTypesPanel.gridx = 0;
		gbc_showInfoTypesPanel.gridy = 19;
		infoPanel.add(showInfoTypesPanel, gbc_showInfoTypesPanel);

		showInfoTypesCheckBox = new JCheckBox("");
		showInfoTypesCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				showInfoTypesCheckBoxActionPerformed(evt);
			}
		});
		showInfoTypesPanel.add(showInfoTypesCheckBox);

		componentInfoTypeWhiteLabel = new JLabel("Unknown");
		GridBagConstraints gbc_componentInfoTypeWhiteLabel = new GridBagConstraints();
		gbc_componentInfoTypeWhiteLabel.gridwidth = 2;
		gbc_componentInfoTypeWhiteLabel.insets = new Insets(0, 0, 5, 0);
		gbc_componentInfoTypeWhiteLabel.anchor = GridBagConstraints.WEST;
		gbc_componentInfoTypeWhiteLabel.gridx = 1;
		gbc_componentInfoTypeWhiteLabel.gridy = 18;
		infoPanel.add(componentInfoTypeWhiteLabel,
				gbc_componentInfoTypeWhiteLabel);

		lblShowInfoTypes = new JLabel("Show Info Types in Graphs");
		GridBagConstraints gbc_lblShowInfoTypes = new GridBagConstraints();
		gbc_lblShowInfoTypes.gridwidth = 2;
		gbc_lblShowInfoTypes.anchor = GridBagConstraints.WEST;
		gbc_lblShowInfoTypes.insets = new Insets(0, 0, 5, 0);
		gbc_lblShowInfoTypes.gridx = 1;
		gbc_lblShowInfoTypes.gridy = 19;
		infoPanel.add(lblShowInfoTypes, gbc_lblShowInfoTypes);

		recordingTitlePanel = new JPanel();
		GridBagConstraints gbc_recordingTitlePanel = new GridBagConstraints();
		gbc_recordingTitlePanel.gridwidth = 3;
		gbc_recordingTitlePanel.insets = new Insets(0, 0, 5, 0);
		gbc_recordingTitlePanel.fill = GridBagConstraints.BOTH;
		gbc_recordingTitlePanel.gridx = 0;
		gbc_recordingTitlePanel.gridy = 20;
		infoPanel.add(recordingTitlePanel, gbc_recordingTitlePanel);
		GridBagLayout gbl_recordingTitlePanel = new GridBagLayout();
		gbl_recordingTitlePanel.columnWeights = new double[] { 0.0, 0.0, 0.0,
				0.0 };
		gbl_recordingTitlePanel.rowWeights = new double[] { 0.0 };
		recordingTitlePanel.setLayout(gbl_recordingTitlePanel);

		recordingTitleLabel = new JLabel();
		recordingTitleLabel.setText("Recording");
		recordingTitleLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_recordingTitleLabel = new GridBagConstraints();
		gbc_recordingTitleLabel.anchor = GridBagConstraints.LINE_START;
		gbc_recordingTitleLabel.insets = new Insets(0, 0, 5, 5);
		gbc_recordingTitleLabel.gridx = 0;
		gbc_recordingTitleLabel.gridy = 0;
		recordingTitlePanel.add(recordingTitleLabel, gbc_recordingTitleLabel);

		recordingTitleSeparator = new JSeparator();
		GridBagConstraints gbc_recordingTitleSeparator = new GridBagConstraints();
		gbc_recordingTitleSeparator.insets = new Insets(0, 0, 5, 0);
		gbc_recordingTitleSeparator.weightx = 1.0;
		gbc_recordingTitleSeparator.fill = GridBagConstraints.HORIZONTAL;
		gbc_recordingTitleSeparator.gridwidth = 3;
		gbc_recordingTitleSeparator.gridx = 1;
		gbc_recordingTitleSeparator.gridy = 0;
		recordingTitlePanel.add(recordingTitleSeparator,
				gbc_recordingTitleSeparator);

		fineNameLabel = new JLabel();
		fineNameLabel.setText("File name:");
		GridBagConstraints gbc_fineNameLabel = new GridBagConstraints();
		gbc_fineNameLabel.anchor = GridBagConstraints.WEST;
		gbc_fineNameLabel.insets = new Insets(0, 0, 5, 5);
		gbc_fineNameLabel.gridx = 0;
		gbc_fineNameLabel.gridy = 21;
		infoPanel.add(fineNameLabel, gbc_fineNameLabel);

		fileNameTextField = new JTextField();
		GridBagConstraints gbc_fileNameTextField_1 = new GridBagConstraints();
		gbc_fileNameTextField_1.insets = new Insets(0, 0, 5, 5);
		gbc_fileNameTextField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_fileNameTextField_1.gridx = 1;
		gbc_fileNameTextField_1.gridy = 21;
		infoPanel.add(fileNameTextField, gbc_fileNameTextField_1);
		fileNameTextField.setColumns(10);

		browseButton = new JButton("");
		browseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				browseButtonActionPerformed(evt);
			}
		});
		browseButton.setToolTipText("Browse");
		browseButton.setIcon(new ImageIcon(DomainModelEditor.class
				.getResource(IMAGE_PATH + "fileBrowser.png")));
		GridBagConstraints gbc_browseButton_1 = new GridBagConstraints();
		gbc_browseButton_1.anchor = GridBagConstraints.EAST;
		gbc_browseButton_1.insets = new Insets(0, 0, 5, 0);
		gbc_browseButton_1.gridx = 2;
		gbc_browseButton_1.gridy = 21;
		infoPanel.add(browseButton, gbc_browseButton_1);

		recordSeparator = new JSeparator();
		GridBagConstraints gbc_recordSeparator = new GridBagConstraints();
		gbc_recordSeparator.gridwidth = 3;
		gbc_recordSeparator.insets = new Insets(0, 0, 5, 5);
		gbc_recordSeparator.gridx = 0;
		gbc_recordSeparator.gridy = 22;
		infoPanel.add(recordSeparator, gbc_recordSeparator);

		recordButtonPanel = new JPanel();
		GridBagConstraints gbc_recordButtonPanel = new GridBagConstraints();
		gbc_recordButtonPanel.anchor = GridBagConstraints.EAST;
		gbc_recordButtonPanel.gridwidth = 3;
		gbc_recordButtonPanel.insets = new Insets(0, 0, 5, 0);
		gbc_recordButtonPanel.fill = GridBagConstraints.VERTICAL;
		gbc_recordButtonPanel.gridx = 0;
		gbc_recordButtonPanel.gridy = 23;
		infoPanel.add(recordButtonPanel, gbc_recordButtonPanel);

		recordButton = new JButton("");
		recordButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				recordButtonActionPerformed(evt);
			}
		});
		recordButton.setToolTipText("Start recording");
		recordButton.setIcon(new ImageIcon(DomainModelEditor.class
				.getResource(IMAGE_PATH + "record.png")));
		recordButton.setDisabledIcon(new javax.swing.ImageIcon(getClass()
				.getResource(IMAGE_PATH + "record_disabled.png"))); // NOI18N
		recordButton.setDisabledSelectedIcon(new javax.swing.ImageIcon(
				getClass().getResource(IMAGE_PATH + "record_disabled.png"))); // NOI18N
		recordButton.setPressedIcon(new javax.swing.ImageIcon(getClass()
				.getResource(IMAGE_PATH + "record.png"))); // NOI18N
		recordButton.setRolloverIcon(new javax.swing.ImageIcon(getClass()
				.getResource(IMAGE_PATH + "record_hover.png"))); // NOI18N
		recordButton.setRolloverSelectedIcon(new javax.swing.ImageIcon(
				getClass().getResource(IMAGE_PATH + "record_hover.png"))); // NOI18N
		recordButton.setSelectedIcon(new javax.swing.ImageIcon(getClass()
				.getResource(IMAGE_PATH + "record.png"))); // NOI18N

		recordButtonPanel.add(recordButton);

		stopButton = new JButton("");
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				stopButtonActionPerformed(evt);
			}
		});
		stopButton.setToolTipText("Stop recording");
		stopButton.setIcon(new ImageIcon(DomainModelEditor.class
				.getResource(IMAGE_PATH + "stop.png")));
		stopButton.setDisabledIcon(new javax.swing.ImageIcon(getClass()
				.getResource(IMAGE_PATH + "stop_disabled.png"))); // NOI18N
		stopButton.setDisabledSelectedIcon(new javax.swing.ImageIcon(getClass()
				.getResource(IMAGE_PATH + "stop_disabled.png"))); // NOI18N
		stopButton.setEnabled(false);
		stopButton.setPressedIcon(new javax.swing.ImageIcon(getClass()
				.getResource(IMAGE_PATH + "stop.png"))); // NOI18N
		stopButton.setRolloverIcon(new javax.swing.ImageIcon(getClass()
				.getResource(IMAGE_PATH + "stop_hover.png"))); // NOI18N
		stopButton.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass()
				.getResource(IMAGE_PATH + "stop_hover.png"))); // NOI18N
		stopButton.setSelectedIcon(new javax.swing.ImageIcon(getClass()
				.getResource(IMAGE_PATH + "stop.png"))); // NOI18N

		recordButtonPanel.add(stopButton);

		recordButtonGroup.add(recordButton);
		recordButtonGroup.add(stopButton);

		recordingProgressBar = new JProgressBar();
		GridBagConstraints gbc_recordingProgressBar_1 = new GridBagConstraints();
		gbc_recordingProgressBar_1.gridwidth = 3;
		gbc_recordingProgressBar_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_recordingProgressBar_1.gridx = 0;
		gbc_recordingProgressBar_1.gridy = 24;
		infoPanel.add(recordingProgressBar, gbc_recordingProgressBar_1);

		infoSplitPane.setLeftComponent(leftPanel);
		leftPanel.setLayout(new GridLayout(0, 1, 0, 0));
		leftPanel.add(infoPanel);

		rightJTabbedPane.setTabPlacement(javax.swing.JTabbedPane.LEFT);

		modelSplitPane.setResizeWeight(0.5);

		domainTreePanel.setLayout(new javax.swing.BoxLayout(domainTreePanel,
				javax.swing.BoxLayout.Y_AXIS));

		domainLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
		domainLabel.setLabelFor(domainJTree);
		domainLabel.setText("Terms");
		domainLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2,
				2, 2));
		domainTreePanel.add(domainLabel);

		domainJTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode(
				"Domain model")));
		domainScrollPane.setViewportView(domainJTree);

		domainTreePanel.add(domainScrollPane);

		modelSplitPane.setRightComponent(domainTreePanel);

		componentTreePanel.setLayout(new javax.swing.BoxLayout(
				componentTreePanel, javax.swing.BoxLayout.Y_AXIS));

		componentLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
		componentLabel.setLabelFor(componentJTree);
		componentLabel.setText("Components");
		componentLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(2,
				2, 2, 2));
		componentTreePanel.add(componentLabel);

		componentJTree.setModel(new DefaultTreeModel(
				new DefaultMutableTreeNode("Component graph")));
		componentScrollPane.setViewportView(componentJTree);

		componentTreePanel.add(componentScrollPane);

		modelSplitPane.setLeftComponent(componentTreePanel);

		visualizationPanel = new VisualizationPanel();
		
		rightJTabbedPane.addTab("", new VerticalTextIcon(" Visualisation ", false),
				visualizationPanel, "Model");
		rightJTabbedPane.addTab("", new VerticalTextIcon(" Model ", false),
				modelSplitPane, "ComponentTree and TermTree");
		
//		rightJTabbedPane.addChangeListener(new ChangeListener() {
//			
//			@Override
//			public void stateChanged(ChangeEvent e) {
//				if(rightJTabbedPane.getSelectedIndex() == 1){
//					visualizationPanel.initializeView(showInfoTypesCheckBox.isSelected());
//				} else {
//					//clear view
//					visualizationPanel.removeAll();
//				}
//			}
//		});

//		editorTextArea.setColumns(20);
//		editorTextArea.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
//		editorTextArea.setRows(5);
//		editorTextArea
//				.setText("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n\t<domainModel>\n\t\t<struct>\n\t\t\t<and mandatory=\"true\" name=\"My Notepad\">\n\t\t\t\t<domain name=\"Cut daco\" description=\"This domain is for cutting something\"/>\n\t\t\t\t<domain name=\"Finder of Wizards\" description=\"This finds your favourite wizard\"/>\n\t\t\t\t<domain name=\"Undo step\" description=\"Returns back one step\"/>\n\t\t\t</and>\n\t\t</struct>\n\t</domainModel>");
//		editorScrollPane.setViewportView(editorTextArea);

//		rightJTabbedPane.addTab("", new VerticalTextIcon(" Editor ", false),
//				editorScrollPane, "Editor");

		infoSplitPane.setRightComponent(rightJTabbedPane);
		rightJTabbedPane.getAccessibleContext()
				.setAccessibleName("jTabbedPane");

		fileMenu.setText("File");
		settingsMenu.setText("Settings");
		
		
		generateDSLMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_D,
				java.awt.event.InputEvent.CTRL_MASK));
		generateDSLMenuItem.setText("Generate DSL");
		generateDSLMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				generateDSLMenuItemActionPerformed(evt);
			}
		});
		fileMenu.add(generateDSLMenuItem);
		
		
		generateITaskMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_I,
				java.awt.event.InputEvent.CTRL_MASK));
		generateITaskMenuItem.setText("Generate iTask code");
		generateITaskMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				generateITaskMenuItemActionPerformed(evt);
			}
		});
		fileMenu.add(generateITaskMenuItem);
		
		generateOntologyFromDomainModel.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_O,
				java.awt.event.InputEvent.CTRL_MASK));
		generateOntologyFromDomainModel.setText("Generate ontology");
		generateOntologyFromDomainModel.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				generateOntologyFromDomainModelItemActionPerformed(evt);
			}
		});
		fileMenu.add(generateOntologyFromDomainModel);
		
		generatePlainTextMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_T,
				java.awt.event.InputEvent.CTRL_MASK));
		generatePlainTextMenuItem.setText("Generate plain text");
		generatePlainTextMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				generatePlainTextMenuItemActionPerformed(evt);
			}
		});
		fileMenu.add(generatePlainTextMenuItem);
		
		findComponentByNameMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_F,
				java.awt.event.InputEvent.CTRL_MASK));
		findComponentByNameMenuItem.setText("Find component by name");
		findComponentByNameMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				findComponentByNameMenuItemActionPerformed(evt);
			}
		});
		fileMenu.add(findComponentByNameMenuItem);
		
		extractFunctionalComponents.setText("Extract functional components");
		extractFunctionalComponents.setSelected(getSetting().isExtractFunctionalComponents());
		extractFunctionalComponents.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				saveCurrentSetting();
			}
		});
		settingsMenu.add(extractFunctionalComponents);
//		saveMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
//				java.awt.event.KeyEvent.VK_S,
//				java.awt.event.InputEvent.CTRL_MASK));
//		saveMenuItem.setText("Save");
//		saveMenuItem.addActionListener(new java.awt.event.ActionListener() {
//			public void actionPerformed(ActionEvent evt) {
//				saveMenuItemActionPerformed(evt);
//			}
//		});
//		fileMenu.add(saveMenuItem);
//
//		openMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
//				java.awt.event.KeyEvent.VK_O,
//				java.awt.event.InputEvent.CTRL_MASK));
//		openMenuItem.setText("Open");
//		openMenuItem.addActionListener(new java.awt.event.ActionListener() {
//			public void actionPerformed(ActionEvent evt) {
//				openMenuItemActionPerformed(evt);
//			}
//		});
//		fileMenu.add(openMenuItem);

		exitMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_F4,
				java.awt.event.InputEvent.ALT_MASK));
		exitMenuItem.setText("Exit");
		exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				exitMenuItemActionPerformed(evt);
			}
		});
		fileMenu.add(exitMenuItem);

		menuBar.add(fileMenu);
		menuBar.add(settingsMenu);

		setJMenuBar(menuBar);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				infoSplitPane, javax.swing.GroupLayout.Alignment.TRAILING,
				javax.swing.GroupLayout.DEFAULT_SIZE, 850, Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				infoSplitPane, javax.swing.GroupLayout.Alignment.TRAILING));		
		pack();
	}//</editor-fold>

	//<editor-fold defaultstate="collapsed" desc="Save and open functionalities - NOT YET IMPLEMENTED">
	//not used in this version yet
//	private void saveMenuItemActionPerformed(ActionEvent evt) {
		// // xml writer test
		// currently not working
		// XmlDomainModelWriter w = new XmlDomainModelWriter(domainModels);
		// String s = w.writeToString();
		// if (s != null) {
		// editorTextArea.setText(s);
		// } else {
		// Logger.logError("There was an error when writing the model to xml");
		// }
//	}

	//not used in this version yet
//	private void openMenuItemActionPerformed(ActionEvent evt) {
		// // xml reader test
		// currently not working
		// domainModel.reset();
		//
		// XmlDomainModelReader r = new XmlDomainModelReader(domainModel);
		// try {
		// r.readFromString(editorTextArea.getText());
		// } catch (Exception e) {
		// Logger.logError(e);
		// }
//	}
	//</editor-fold>
	
	//<editor-fold defaultstate="collapsed" desc="Find functionality">
	public void findComponentByNameMenuItemActionPerformed(ActionEvent evt) {
		final FindDialog findDialog = new FindDialog(this, true);
		findDialog.addFindActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				findDialogActionPerformed(findDialog.getStringToFind());
			}
		});
		findDialog.setVisible(true);
	}
	
	private void findDialogActionPerformed(String stringToFind) {
		List<Term> termsWithName = findTermsWithName(stringToFind);
		List<Term> termsWithDescription = findTermsWithDescription(stringToFind);
		
		Term clickedTerm = null;
		if(termsWithName.size() > 0) {
			clickedTerm = termsWithName.get(0);
		} else if (termsWithDescription.size() > 0) {
			clickedTerm = termsWithDescription.get(0);
		}
		
		if(clickedTerm != null) {
			showInTrees(clickedTerm);
		}
	}
	
	private List<Term> findTermsWithName(String name) {
		List<Term> termsWithName = new ArrayList<Term>();
		for(DomainModel dm : application.getDomainModels()) {
			termsWithName.addAll(dm.findTermsByName(name));
		}
		return termsWithName;
	}
	
	private List<Term> findTermsWithDescription(String description) {
		List<Term> termsWithDescription = new ArrayList<Term>();
		for(DomainModel dm : application.getDomainModels()) {
			termsWithDescription.addAll(dm.findTermsByDescription(description));
		}
		return termsWithDescription;
	}
	//</editor-fold>
	
	private void exitMenuItemActionPerformed(ActionEvent evt) {
		System.exit(0);
	}
	
	//<editor-fold defaultstate="collapsed" desc="generator functionalities">
	private void generateITaskMenuItemActionPerformed(ActionEvent evt) {
		if(application.getDomainModelCount() >= 1) {
			DomainModel dm = application.getDomainModels().get(0);
			Language language = generateDSL(dm);
			
			if(language != null) {
				ITaskGenerator generator = new ITaskGenerator(language, dm);
				try {
					generator.generate();
				} catch (GeneratorException e) {
					e.printStackTrace();
				}
				
				invokeOpenDir(ITaskGenerator.ITASK_DIR);
			} else {
				JOptionPane.showMessageDialog(this, "The iTask code could not be generated, because DEAL could not create a DSL");
			}
		}
	}
	
	private void generateDSLMenuItemActionPerformed(ActionEvent evt) {
		for(DomainModel dm : application.getDomainModels()) {
			generateDSL(dm);
			
			String filePath = YajcoGenerator.DSL_DIR;
			invokeOpenDir(filePath);
			
//			int selection = JOptionPane.showConfirmDialog(this, "DSL pre jazyk rozhrania bol vygenerovany. Chcete otvorit adresar s DSL?", "Vygenerovany adresar", JOptionPane.YES_NO_OPTION);
//			if(selection == JOptionPane.OK_OPTION) {
//				invokeOpenDir("." + File.separatorChar + "src" + File.separatorChar + dsl.getName());
//			}
		}
	}
	
	private Language generateDSL(DomainModel domainModel) {
		Language language = yajcoGenerator.generateDSL(domainModel);
		return language;
	}

	public static final String OWL_DIR = "owl";
	
	private void generateOntologyFromDomainModelItemActionPerformed(java.awt.event.ActionEvent evt) {
		File saveFile = null;
		File owlDir = new File(OWL_DIR);
		if(!owlDir.exists()) {
			owlDir.mkdir();
		}
		DealFileChooser fc = new DealFileChooser(DealFileChooserType.OWL);
		fc.setCurrentDirectory(owlDir);
		int dialogValue = fc.showSaveDialog(this.getContentPane());
		
		if (dialogValue == JFileChooser.APPROVE_OPTION) {
			saveFile = fc.getSelectedFile();
		}
		
		if (saveFile!=null) {
			if (!FilenameUtils.getExtension(saveFile.getName()).equalsIgnoreCase(DealFileChooser.OWL_FILE_EXT))
				saveFile = new File(saveFile.getAbsolutePath()+"." + DealFileChooser.OWL_FILE_EXT);
			OntologyTester.generateOntology(DomainModelEditor.getInstance().getDomainModels(), saveFile);
			invokeOpenDir(saveFile.getParent());
		}
	}
	
	private void generatePlainTextMenuItemActionPerformed(ActionEvent evt) {
		if(application.getDomainModelCount() >= 1) {
			PlainTextGenerator generator = new PlainTextGenerator(application);
			try {
				generator.generate();
			} catch (GeneratorException e) {
				e.printStackTrace();
			}
				
			invokeOpenDir(PlainTextGenerator.PLAIN_TEXT_DIR);
		}
	}
	
	public void invokeOpenDir(String path) {
		if(System.getProperty("os.name").toLowerCase().contains("windows")) {
			try {
				Desktop.getDesktop().open(new File(path));
			} catch (IOException e) {
				//could not open the gererated directory
			}
		}
	}
	//</editor-fold>

	public void showTermInTrees(Term term) {
		showObjectInTree(term, domainJTree);
	}
	
	//<editor-fold defaultstate="collapsed" desc="On click update functionalities. On mouse click the info panl updates and the clicked component is highlighted in both trees and in the GUI with a yellow collor">
	/**
	 * Sets the cursor to highlight the given component both in the component and domain tree
	 * @param component the component to be highlighted (focused) in both trees
	 */
	public void showComponentInTrees(Object component) {
		showObjectInTree(component, componentJTree);
	}

	private void resetInfoPanel() {
		nameField.setText("");
		descriptionField.setText("");
		typeComboBox.setSelectedIndex(0);
		iconField.setText("no icon");
		iconField.setIcon(null);

		classField.setText("");
		contentField.setText("");
		tooltipField.setText("");
		labelField.setText("");
		actionCommandField.setText("");
	}

	public void updateInfoPanel(Term term, Object component) {
		resetInfoPanel();

		// update term info fields
		if (term != null) {
			nameField.setText(term.getName());
			descriptionField.setText(term.getDescription()); 
			typeComboBox.setSelectedItem(term.getRelation());

			Icon ii = term.getIcon();
			if (ii != null) {
				iconField.setText("");
				iconField.setIcon(ii);
			} else {
				iconField.setText("no icon");
				iconField.setIcon(null);
			}
		}

		// update component info fields
		updateInfoPanel(component);
	}

	public void updateInfoPanel(Object component) {
		if (component == null)
			return;

		String text = "";

		classField.setText(component.getClass().getName());

		// ///// action command text field
		if (component instanceof JMenu) {
			text = ((JMenu) component).getActionCommand();
		} else if (component instanceof AbstractButton) {
			text = ((AbstractButton) component).getActionCommand();
		} else
			text = "";
		actionCommandField.setText(text);

		// ///// text / content text field
		if (component instanceof JTextComponent) {
			text = ((JTextComponent) component).getText();
		} else if (component instanceof AbstractButton) {
			text = ((AbstractButton) component).getText();
		} else if (component instanceof JSpinner) {
			text = ((JSpinner) component).getValue().toString();
		} else if (component instanceof JMenu) {
			text = ((JMenu) component).getText();
		} else if (component instanceof JComboBox) {
			Object[] objects = ((JComboBox<?>) component).getSelectedObjects();
			text = "";
			for (int i = 0; i < objects.length; i++) {
				text = text + objects[i].toString();
				if (i != objects.length - 1) {
					text = text + ", ";
				}
			}
		} else if (component instanceof JList) {
			@SuppressWarnings("deprecation")
			Object[] objects = ((JList<?>) component).getSelectedValues();
			for (int i = 0; i < objects.length; i++) {
				text = text + objects[i].toString();
				if (i != objects.length - 1) {
					text = text + ", ";
				}
			}
		} else if (component instanceof JTabbedPane) {
			text = ((JTabbedPane) component).getSelectedIndex() + "";
		} else if (component instanceof JTree) {
			TreePath[] objects = ((JTree) component).getSelectionPaths();
			if (objects != null && objects.length != 0) {
				for (int i = 0; i < objects.length; i++) {
					text = text + objects[i].getLastPathComponent().toString();
					if (i != objects.length - 1) {
						text = text + ", ";
					}
				}
			} else
				text = "";
		} else
			text = "";
		contentField.setText(text);

		if (component instanceof JComponent) {
			JComponent jc = (JComponent) component;

			// /////tooltip text field
			tooltipField.setText(((JComponent) component).getToolTipText());

			// /////labelfor text field
			JLabel label = JLabelFinder.findLabelFor(jc);
			if (label != null) {
				text = label.getText();
			} else
				text = "";
			labelField.setText(text);
		}
	}

	// sets the cursor in the component tree to the current component - in the
	// componentJTree
	//object can be a term or a component
	//tree can be domainJTree or componentJTree
	private TreePath showObjectInTree(Object object, JTree tree) {
		Object root = tree.getModel().getRoot();
		Object[] treePath = createTreePathToObject(object, root);
		TreePath path = null;
		if (treePath.length != 0) {
			path = new TreePath(treePath);
			if (path != null && path.getPathCount() != 0) {
				tree.expandPath(path.getParentPath());
				tree.makeVisible(path);
				tree.scrollPathToVisible(path);
				tree.setSelectionPath(path);
			}
		}
		return path;
	}
	
	/*object can be term or component*/
	private Object[] createTreePathToObject(Object object, Object root) {
		List<Object> list = new ArrayList<Object>();
		if (root instanceof DefaultMutableTreeNode) {

			DefaultMutableTreeNode top = (DefaultMutableTreeNode) root;
			Enumeration<?> nodes = top.breadthFirstEnumeration();

			Object obj = null;
			while (nodes.hasMoreElements()) {
				if ((obj = nodes.nextElement()) != null) {
					if (obj instanceof DefaultMutableTreeNode) {
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) obj;
						Object userObject = node.getUserObject();
						if (userObject instanceof Term) {
							Term f = (Term) userObject;
							if(f.equals(object) || (f.getComponent() != null
									&& f.getComponent().equals(object))) {
								return node.getPath();
							}
						} else if (node.getUserObject() != null && node.getUserObject().equals(object)) {
							return node.getPath();
						}
					}
				}
			}

		} else {
			list.add(new DefaultMutableTreeNode("Window"));
		}

		Object[] emptyList = {};
		return emptyList;
	}

	public Component getClickedComponent() {
		return clickedComponent;
	}

	public void setClickedComponent(Component clickedComponent) {
		this.clickedComponent = clickedComponent;
	}

	public Color getClickedComponentColor() {
		return clickedComponentColor;
	}

	public void setClickedComponentColor(Color clickedComponentColor) {
		this.clickedComponentColor = clickedComponentColor;
	}

	public boolean isClickedComponentOpaque() {
		return clickedComponentOpaque;
	}

	public void setClickedComponentOpaque(boolean clickedComponentOpaque) {
		this.clickedComponentOpaque = clickedComponentOpaque;
	}
	//</editor-fold>

	//<editor-fold defaultstate="collapsed" desc="Utilities for tree expanding/collapsing">
	/**
	 * Expand/collapse utility method. If expand is true, expands all nodes in
	 * the tree. Otherwise collapses all nodes in the tree.
	 * 
	 * @param tree
	 *            The jtree to expand/collapse
	 * @param expand
	 *            If true, the method expands all nodes in the tree, otherwise
	 *            it collapses all nodes in the tree.
	 */
	public void expandAll(JTree tree, boolean expand) {
		javax.swing.tree.TreeNode root = (javax.swing.tree.TreeNode) tree
				.getModel().getRoot();

		// Traverse tree from root
		expandAll(tree, new TreePath(root), expand);
	}

	/**
	 * Helper method for the expandAll(JTree tree, boolean expand) method, free
	 * from root.
	 * 
	 * @param tree
	 *            The tree to expand/collapse
	 * @param parent
	 *            The parent node to expand/collapse
	 * @param expand
	 *            If true, the method expands all nodes in the subtree,
	 *            otherwise it collapses all nodes in the subtree.
	 */
	private void expandAll(JTree tree, TreePath parent, boolean expand) {
		// Traverse children
		javax.swing.tree.TreeNode node = (javax.swing.tree.TreeNode) parent
				.getLastPathComponent();
		if (node.getChildCount() >= 0) {
			for (Enumeration<?> e = node.children(); e.hasMoreElements();) {
				javax.swing.tree.TreeNode n = (javax.swing.tree.TreeNode) e
						.nextElement();
				TreePath path = parent.pathByAddingChild(n);
				expandAll(tree, path, expand);
			}
		}

		// Expansion or collapse must be done bottom-up
		if (expand) {
			tree.expandPath(parent);
		} else {
			tree.collapsePath(parent);
		}
	}
	//</editor-fold>
	
	public boolean extractFunctionalComponents() {
		return extractFunctionalComponents.isSelected();
	}
	
	//<editor-fold defaultstate="collapsed" desc="Component variables declaration">
	private javax.swing.JTextField actionCommandField;
	private javax.swing.JLabel actionCommandLabel;
	private javax.swing.JTextField classField;
	private javax.swing.JLabel classLabel;
	private javax.swing.JLabel componentInfoLabel;
	private javax.swing.JPanel componentInfoTitlePanel;
	private javax.swing.JSeparator componentInfoSeparator;
	private javax.swing.JLabel componentLabel;
	private javax.swing.JPanel componentTreePanel;
	private javax.swing.JScrollPane componentScrollPane;
	private javax.swing.JTree componentJTree;
	private javax.swing.JTextField contentField;
	private javax.swing.JLabel contentLabel;
	private javax.swing.JTextField descriptionField;
	private javax.swing.JLabel descriptionLabel;
//	private javax.swing.JScrollPane editorScrollPane;
//	private javax.swing.JTextArea editorTextArea;
	private javax.swing.JMenuItem exitMenuItem;
	private javax.swing.JLabel domainInfoLabel;
	private javax.swing.JPanel domainInfoTitlePanel;
	private javax.swing.JSeparator domainInfoSeparator;
	private javax.swing.JLabel domainLabel;
	private javax.swing.JPanel domainTreePanel;
	private javax.swing.JScrollPane domainScrollPane;
	private javax.swing.JTree domainJTree;
	private javax.swing.JMenu fileMenu;
	private javax.swing.JMenu settingsMenu;
	private javax.swing.JLabel iconField;
	private javax.swing.JLabel iconLabel;
	private javax.swing.JPanel leftPanel;
	private javax.swing.JPanel infoPanel;
	private javax.swing.JSplitPane infoSplitPane;
	private javax.swing.JTabbedPane rightJTabbedPane;
	private javax.swing.JTextField labelField;
	private javax.swing.JLabel labelLabel;
	private javax.swing.JMenuBar menuBar;
	private javax.swing.JSplitPane modelSplitPane;
	private javax.swing.JTextArea nameField;
	private javax.swing.JLabel nameLabel;
//	not used in this version yet
//	private javax.swing.JMenuItem openMenuItem;
//	private javax.swing.JMenuItem saveMenuItem;
	private javax.swing.JMenuItem generateDSLMenuItem;
	private javax.swing.JMenuItem generateITaskMenuItem;
	private javax.swing.JMenuItem generateOntologyFromDomainModel;
	private javax.swing.JMenuItem findComponentByNameMenuItem;
	private javax.swing.JTextField tooltipField;
	private javax.swing.JLabel tooltipLabel;
	private javax.swing.JComboBox<RelationType> typeComboBox;
	private javax.swing.JLabel typeLabel;
	private javax.swing.JMenuItem hidePopupMenuItem;
	private javax.swing.JMenuItem hideAllPopupMenuItem;
	private javax.swing.JMenuItem unhideAllPopupMenuItem;
	private javax.swing.JMenuItem generatePlainTextMenuItem;
	private javax.swing.JCheckBoxMenuItem extractFunctionalComponents;
	private GridBagConstraints gbc_domainInfoTitlePanel;
	private GridBagConstraints gbc_componentInfoTitlePanel;
	private GridBagConstraints gridBagConstraints_1;
	private javax.swing.JPanel componentInfoTypesTitlePanel;
	private javax.swing.JLabel componentInfoTypesLabel;
	private javax.swing.JSeparator componentInfoTypesSeparator;
	private javax.swing.JLabel componentInfoTypeBlue;
	private javax.swing.JLabel componentInfoTypeBlueLabel;
	private javax.swing.JLabel componentInfoTypeRed;
	private javax.swing.JLabel componentInfoTypeRedLabel;
	private javax.swing.JLabel componentInfoTypeGreen;
	private javax.swing.JLabel componentInfoTypeGreenLabel;
	private javax.swing.JLabel componentInfoTypeCyan;
	private javax.swing.JLabel componentInfoTypeCyanLabel;
	private javax.swing.JLabel componentInfoTypeYellow;
	private javax.swing.JLabel componentInfoTypeYellowLabel;
	private javax.swing.JLabel componentInfoTypeWhite;
	private javax.swing.JLabel componentInfoTypeWhiteLabel;
	private javax.swing.JPanel recordingTitlePanel;
	private javax.swing.JLabel recordingTitleLabel;
	private JSeparator recordingTitleSeparator;
	private javax.swing.JLabel fineNameLabel;
	private javax.swing.JTextField fileNameTextField;
	private javax.swing.JButton browseButton;
	private javax.swing.JProgressBar recordingProgressBar;
	private javax.swing.JPanel recordButtonPanel;
	private javax.swing.JButton recordButton;
	private javax.swing.JButton stopButton;
	private javax.swing.JCheckBox showInfoTypesCheckBox;
	private javax.swing.JPanel showInfoTypesPanel;
	private javax.swing.JLabel lblShowInfoTypes;
	private GridBagConstraints gridBagConstraints_2;
	private GridBagConstraints gridBagConstraints_3;
	private GridBagConstraints gridBagConstraints_4;
	private GridBagConstraints gridBagConstraints_5;
	private GridBagConstraints gridBagConstraints_6;
	private GridBagConstraints gridBagConstraints_7;
	private GridBagConstraints gridBagConstraints_8;
	private GridBagConstraints gridBagConstraints_9;
	private GridBagConstraints gridBagConstraints_10;
	private javax.swing.JSeparator recordSeparator;
	private javax.swing.ButtonGroup recordButtonGroup;
	private VisualizationPanel visualizationPanel;	
//</editor-fold>

	//<editor-fold  defaultstate="collapsed" desc="Private classes: PopupMenu, PopClickListener, RecordingThread, DealFileFilter">
	/**
	 * JPopMenu implementation.
	 */
	private class PopupMenu extends JPopupMenu {
		private static final long serialVersionUID = 3554250591171474489L;

		/* Static variables for PopupMenu labels. */
		public static final String HIDE_MENU_TEXT = "Hide";
		public static final String UNHIDE_MENU_TEXT = "Unhide";
		public static final String HIDE_ALL_MENU_TEXT = "Hide All";
		public static final String UNHIDE_ALL_MENU_TEXT = "Unhide All";

		public PopupMenu() {
			hidePopupMenuItem = new JMenuItem(HIDE_MENU_TEXT);
			hideAllPopupMenuItem = new JMenuItem(HIDE_ALL_MENU_TEXT);
			unhideAllPopupMenuItem = new JMenuItem(UNHIDE_ALL_MENU_TEXT);

			add(hidePopupMenuItem);
			add(hideAllPopupMenuItem);
			add(unhideAllPopupMenuItem);

			hidePopupMenuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					onHidePopupMenuItemActionPerformed();
				}
			});

			hideAllPopupMenuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					onHideAllPopupMenuItemActionPerformed();
				}
			});

			unhideAllPopupMenuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					onUnhideAllPopupMenuItemActionPerformed();
				}
			});
		}
	}

	class PopClickListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			if (e.isPopupTrigger())
				doPop(e);
		}

		public void mouseReleased(MouseEvent e) {
			if (e.isPopupTrigger())
				doPop(e);
		}

		private void doPop(MouseEvent e) {
			PopupMenu menu = new PopupMenu();
			TreePath tp = domainJTree.getPathForLocation(e.getX(), e.getY());
			if (e.getClickCount() == 1 && tp != null) {
				switch (e.getButton()) {
				case MouseEvent.BUTTON3: {
					Object o = tp.getLastPathComponent();
					if (o != null && o instanceof DefaultMutableTreeNode) {
						clickedPopupNode = (DefaultMutableTreeNode) o;
						if (clickedPopupNode instanceof TreeNode) {
							TreeNode tn = (TreeNode) clickedPopupNode;
							hidePopupMenuItem
									.setText(tn.isHidden() ? PopupMenu.UNHIDE_MENU_TEXT
											: PopupMenu.HIDE_MENU_TEXT);
							hideAllPopupMenuItem.setEnabled(tn
									.isAtLeastOneUnhidden());
							unhideAllPopupMenuItem.setEnabled(tn
									.isAtLeastOneHidden());
						}

						if (!clickedPopupNode.isRoot())
							menu.show(domainJTree, e.getX(), e.getY());
					}
				}
				}
			}
		}
	}

	/**
	 * Recording thread class.
	 */
	private class RecordingThread extends Thread {
		/**
		 * Recording flag.
		 * 
		 * @value true if recording is running, false otherwise.
		 */
		private boolean running = true;

		/**
		 * Stops the recording process.
		 */
		@Override
		public void interrupt() {
			running = false;
		}

		/**
		 * Runs the recording process.
		 */
		@Override
		public void run() {
			while (running) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException ex) {
					// couldnt sleep
				}
				for (int i = 0; i <= 100; i++) {
					recordingProgressBar.setValue(i);
					try {
						Thread.sleep(10);
					} catch (InterruptedException ex) {
						// couldnt sleep
					}
				}
			}
		}
	}

//</editor-fold>
}
