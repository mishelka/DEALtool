package gui.ui;

import gui.analyzer.util.PathFinder;
import gui.model.application.Application;
import gui.model.application.Scene;
import gui.model.application.WindowScene;
import gui.model.domain.DomainModel;
import gui.model.domain.Term;
import gui.model.domain.relation.RelationType;
import gui.ui.tabpane.VerticalTextIcon;
import gui.ui.tree.TreeCellRenderer;
import gui.ui.tree.TreeModel;
import gui.ui.tree.TreeNode;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.text.JTextComponent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

public class DomainModelEditor extends javax.swing.JFrame {
	private static final long serialVersionUID = 1L;
	private static DomainModelEditor instance;

	private static Application application = new Application();
	private static LinkedHashMap<Scene, DomainModel> domainModels = new LinkedHashMap<Scene, DomainModel>();

	private Component clickedComponent;
	private Color clickedComponentColor;
	private boolean clickedComponentOpaque;

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
		this(new DomainModel(""));
	}

	/** Constructor for a new domainModel (used only in the default constructor) */
	private DomainModelEditor(DomainModel domainModel) {
		UIManager.put("TabbedPane.textIconGap", new Integer(-8));
		initComponents();

		this.setTitle("DEAL (Domain Extraction ALgorithm) tool prototype");

		ToolTipManager.sharedInstance().registerComponent(domainJTree);
		domainJTree.setCellRenderer(new TreeCellRenderer());

		domainJTree
				.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
					public void valueChanged(
							javax.swing.event.TreeSelectionEvent evt) {
						componentTreeValueChanged(evt);
					}
				});

		expandAll(domainJTree, true);
	}

	/******************* Methods for domain model setup *************************/

	public void addDomainModel(Scene scene, DomainModel newDomainModel) {
		if (domainModels.values().size() == 0) {
			domainModels.put(scene, newDomainModel);
		} else {
			boolean pridany = false;
			for (Scene s : domainModels.keySet()) {
				if (s.getSceneContainer().equals(scene.getSceneContainer())) {
					domainModels.put(s, newDomainModel);
					pridany = true;
					break;
				}
			}
			
			if (!pridany) {
				domainModels.put(scene, newDomainModel);
			}
		}

		updateDomainTree();
	}

	public void updateDomainTree() {
		TreeModel newModel = new TreeModel(application);

		for (DomainModel dm : domainModels.values()) {
			// domainModel = dm;
			// if (domainModels.indexOf(domainModel) == 0) {
			// newModel = new TreeModel(domainModel);
			// } else {
			TreeModel toAdd = new TreeModel(dm);
			newModel.add(toAdd);
			// }
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
						componentTreeValueChanged(evt);
					}
				});

		domainJTree.setScrollsOnExpand(true);

		domainScrollPane.setViewportView(domainJTree);

		repaint();

		expandAll(domainJTree, true);
	}

	/******************* Methods for component tree setup ************************/

	/**
	 * Sets up the ComponentTreeModel based on the window that just opened.
	 */
	public void setupComponentTreeModel() {
		DefaultMutableTreeNode dmtn = new DefaultMutableTreeNode(
				"Component tree");

		for (WindowScene ws : application.getWindowScenes()) {
			dmtn.add(setupPartOfTreeModelExtended(ws.getSceneContainer()));
		}

		componentJTree = new JTree(dmtn);
		parseTreeAndClearStrings(componentJTree.getModel());
		componentJTree.setScrollsOnExpand(true);

		componentJTree
				.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
					public void valueChanged(
							javax.swing.event.TreeSelectionEvent evt) {
						componentTreeValueChanged(evt);
					}
				});

		// componentTree.addMouseListener(new MouseAdapter() {
		// public void mouseClicked(MouseEvent e) {
		// doMouseClicked(e);
		// }
		// });

		expandAll(componentJTree, true);

		componentScrollPane.setViewportView(componentJTree);

		repaint();
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

	/****************** getters and setters *************************************/

	public javax.swing.JTree getDomainTree() {
		return domainJTree;
	}

	public javax.swing.JTree getComponentTree() {
		return componentJTree;
	}

	public static Application getApplication() {
		return application;
	}

	public static HashMap<Scene, DomainModel> getDomainModels() {
		return domainModels;
	}

	/******************* Events for mouse clicking *******************************/

	// void doDomainTreeMouseClicked(MouseEvent e) {
	// if (clickedComponent != null) {
	// clickedComponent.setBackground(clickedComponentColor);
	// if (clickedComponent instanceof JComponent)
	// ((JComponent) clickedComponent)
	// .setOpaque(isClickedComponentOpaque());
	// }
	// TreePath tp = domainTree.getPathForLocation(e.getX(), e.getY());
	//
	// if (tp != null) {
	// Object o = tp.getLastPathComponent();
	// if (o instanceof TreeNode) {
	//
	// Object targetObject = ((Term) ((TreeNode) o).getUserObject())
	// .getComponent();
	//
	// if (targetObject != null && targetObject instanceof Component) {
	// setClickedComponent((Component) targetObject);
	// setClickedComponentColor(clickedComponent
	// .getBackground());
	// clickedComponent.setBackground(Color.YELLOW);
	// if (clickedComponent instanceof JComponent) {
	// JComponent jc = (JComponent) clickedComponent;
	// setClickedComponentOpaque(jc.isOpaque());
	// jc.setOpaque(true);
	// }
	// }
	// }
	// }
	//
	// showComponentInTree(e.getSource(), componentTree);
	// }

	private void componentTreeValueChanged(
			javax.swing.event.TreeSelectionEvent evt) {
		if (clickedComponent != null) {
			clickedComponent.setBackground(clickedComponentColor);
			if (clickedComponent instanceof JComponent)
				((JComponent) clickedComponent)
						.setOpaque(clickedComponentOpaque);
		}

		DefaultMutableTreeNode obj = (DefaultMutableTreeNode) evt.getPath()
				.getLastPathComponent();
		Object targetObject = obj.getUserObject();
		if (targetObject != null && targetObject instanceof Term) {
			targetObject = ((Term) targetObject).getComponent();
		}

		if (targetObject != null && targetObject instanceof Component) {
			clickedComponent = (Component) targetObject;
			clickedComponentColor = clickedComponent.getBackground();
			clickedComponent.setBackground(Color.YELLOW);
			if (clickedComponent instanceof JComponent) {
				JComponent jc = (JComponent) clickedComponent;
				clickedComponentOpaque = jc.isOpaque();
				jc.setOpaque(true);
			}
			showComponentInTrees(clickedComponent);
		}
	}

	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {
		java.awt.GridBagConstraints gridBagConstraints;

		infoSplitPane = new javax.swing.JSplitPane();
		infoPanel1 = new javax.swing.JPanel();
		infoPanel2 = new javax.swing.JPanel();
		domainInfoPanel = new javax.swing.JPanel();
		domainInfoLabel = new javax.swing.JLabel();
		domainInfoSeparator = new javax.swing.JSeparator();
		nameLabel = new javax.swing.JLabel();
		nameField = new javax.swing.JTextField();
		descriptionLabel = new javax.swing.JLabel();
		descriptionField = new javax.swing.JTextField();
		typeLabel = new javax.swing.JLabel();
		typeComboBox = new javax.swing.JComboBox(RelationType.values());
		iconLabel = new javax.swing.JLabel();
		iconField = new javax.swing.JLabel();
		componentInfoPanel = new javax.swing.JPanel();
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
		jTabbedPane = new javax.swing.JTabbedPane();
		modelSplitPane = new javax.swing.JSplitPane();
		domainPanel = new javax.swing.JPanel();
		domainLabel = new javax.swing.JLabel();
		domainScrollPane = new javax.swing.JScrollPane();
		domainJTree = new javax.swing.JTree();
		componentPanel = new javax.swing.JPanel();
		componentLabel = new javax.swing.JLabel();
		componentScrollPane = new javax.swing.JScrollPane();
		componentJTree = new javax.swing.JTree();
		editorScrollPane = new javax.swing.JScrollPane();
		editorTextArea = new javax.swing.JTextArea();
		menuBar = new javax.swing.JMenuBar();
		fileMenu = new javax.swing.JMenu();
		saveMenuItem = new javax.swing.JMenuItem();
		openMenuItem = new javax.swing.JMenuItem();
		exitMenuItem = new javax.swing.JMenuItem();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setTitle("DEAL");
		setBounds(0, 0, 600, 500);
		setMinimumSize(new java.awt.Dimension(600, 500));

		infoSplitPane.setDividerLocation(250);
		infoSplitPane.setDividerSize(2);

		infoPanel2.setLayout(new java.awt.GridBagLayout());

		domainInfoPanel.setLayout(new java.awt.GridBagLayout());

		domainInfoLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
		domainInfoLabel.setText("Term");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
		domainInfoPanel.add(domainInfoLabel, gridBagConstraints);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 3;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		domainInfoPanel.add(domainInfoSeparator, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		infoPanel2.add(domainInfoPanel, gridBagConstraints);

		nameLabel.setText("Name:");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridy = 1;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
		gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
		infoPanel2.add(nameLabel, gridBagConstraints);

		nameField.setColumns(10);
		nameField.setEditable(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
		infoPanel2.add(nameField, gridBagConstraints);

		descriptionLabel.setText("Description:");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
		gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
		infoPanel2.add(descriptionLabel, gridBagConstraints);

		descriptionField.setColumns(10);
		descriptionField.setEditable(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
		infoPanel2.add(descriptionField, gridBagConstraints);

		typeLabel.setText("Type:");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
		gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
		infoPanel2.add(typeLabel, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
		infoPanel2.add(typeComboBox, gridBagConstraints);

		iconLabel.setText("Icon:");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
		gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
		infoPanel2.add(iconLabel, gridBagConstraints);

		iconField.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		iconField.setText("no icon");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
		infoPanel2.add(iconField, gridBagConstraints);

		componentInfoPanel.setLayout(new java.awt.GridBagLayout());

		componentInfoLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
		componentInfoLabel.setText("Component");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
		componentInfoPanel.add(componentInfoLabel, gridBagConstraints);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 3;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		componentInfoPanel.add(componentInfoSeparator, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
		infoPanel2.add(componentInfoPanel, gridBagConstraints);

		classLabel.setText("Class:");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 6;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
		gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
		infoPanel2.add(classLabel, gridBagConstraints);

		classField.setColumns(10);
		classField.setEditable(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 6;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
		infoPanel2.add(classField, gridBagConstraints);

		contentLabel.setText("Text/Content:");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 7;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
		gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
		infoPanel2.add(contentLabel, gridBagConstraints);

		contentField.setColumns(10);
		contentField.setEditable(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 7;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
		infoPanel2.add(contentField, gridBagConstraints);

		tooltipLabel.setText("Tooltip:");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 8;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
		gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
		infoPanel2.add(tooltipLabel, gridBagConstraints);

		tooltipField.setColumns(10);
		tooltipField.setEditable(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 8;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
		infoPanel2.add(tooltipField, gridBagConstraints);

		labelLabel.setText("Label:");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 9;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
		gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
		infoPanel2.add(labelLabel, gridBagConstraints);

		labelField.setEditable(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 9;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
		infoPanel2.add(labelField, gridBagConstraints);

		actionCommandLabel.setText("Action command:");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 10;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
		gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
		infoPanel2.add(actionCommandLabel, gridBagConstraints);

		actionCommandField.setEditable(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 10;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
		infoPanel2.add(actionCommandField, gridBagConstraints);

		javax.swing.GroupLayout infoPanel1Layout = new javax.swing.GroupLayout(
				infoPanel1);
		infoPanel1.setLayout(infoPanel1Layout);
		infoPanel1Layout.setHorizontalGroup(infoPanel1Layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						infoPanel1Layout
								.createSequentialGroup()
								.addContainerGap()
								.addComponent(infoPanel2,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										239, Short.MAX_VALUE)));
		infoPanel1Layout.setVerticalGroup(infoPanel1Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				infoPanel1Layout
						.createSequentialGroup()
						.addContainerGap()
						.addComponent(infoPanel2,
								javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(400, Short.MAX_VALUE)));

		infoSplitPane.setLeftComponent(infoPanel1);

		jTabbedPane.setTabPlacement(javax.swing.JTabbedPane.LEFT);

		modelSplitPane.setResizeWeight(0.5);

		domainPanel.setLayout(new javax.swing.BoxLayout(domainPanel,
				javax.swing.BoxLayout.Y_AXIS));

		domainLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
		domainLabel.setLabelFor(domainJTree);
		domainLabel.setText("Terms");
		domainLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2,
				2, 2));
		domainPanel.add(domainLabel);

		domainJTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode(
				"Domain model")));
		domainScrollPane.setViewportView(domainJTree);

		domainPanel.add(domainScrollPane);

		modelSplitPane.setLeftComponent(domainPanel);

		componentPanel.setLayout(new javax.swing.BoxLayout(componentPanel,
				javax.swing.BoxLayout.Y_AXIS));

		componentLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
		componentLabel.setLabelFor(componentJTree);
		componentLabel.setText("Components");
		componentLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(2,
				2, 2, 2));
		componentPanel.add(componentLabel);

		componentJTree.setModel(new DefaultTreeModel(
				new DefaultMutableTreeNode("Component graph")));
		componentScrollPane.setViewportView(componentJTree);

		componentPanel.add(componentScrollPane);

		modelSplitPane.setRightComponent(componentPanel);

		jTabbedPane.addTab("", new VerticalTextIcon(" Model ", false),
				modelSplitPane, "Model");

		editorTextArea.setColumns(20);
		editorTextArea.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
		editorTextArea.setRows(5);
		editorTextArea
				.setText("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n\t<domainModel>\n\t\t<struct>\n\t\t\t<and mandatory=\"true\" name=\"My Notepad\">\n\t\t\t\t<domain name=\"Cut daco\" description=\"This domain is for cutting something\"/>\n\t\t\t\t<domain name=\"Finder of Wizards\" description=\"This finds your favourite wizard\"/>\n\t\t\t\t<domain name=\"Undo step\" description=\"Returns back one step\"/>\n\t\t\t</and>\n\t\t</struct>\n\t</domainModel>");
		editorScrollPane.setViewportView(editorTextArea);

		jTabbedPane.addTab("", new VerticalTextIcon(" Editor ", false),
				editorScrollPane, "Editor");

		infoSplitPane.setRightComponent(jTabbedPane);
		jTabbedPane.getAccessibleContext().setAccessibleName("jTabbedPane");

		fileMenu.setText("File");

		saveMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_S,
				java.awt.event.InputEvent.CTRL_MASK));
		saveMenuItem.setText("Save");
		saveMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				saveMenuItemActionPerformed(evt);
			}
		});
		fileMenu.add(saveMenuItem);

		openMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_O,
				java.awt.event.InputEvent.CTRL_MASK));
		openMenuItem.setText("Open");
		openMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				openMenuItemActionPerformed(evt);
			}
		});
		fileMenu.add(openMenuItem);

		exitMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_F4,
				java.awt.event.InputEvent.ALT_MASK));
		exitMenuItem.setText("Exit");
		exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				exitMenuItemActionPerformed(evt);
			}
		});
		fileMenu.add(exitMenuItem);

		menuBar.add(fileMenu);

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
	}// </editor-fold>

	private void saveMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		// // xml writer test
		// currently not working
		// XmlDomainModelWriter w = new XmlDomainModelWriter(domainModels);
		// String s = w.writeToString();
		// if (s != null) {
		// editorTextArea.setText(s);
		// } else {
		// Logger.logError("There was an error when writing the model to xml");
		// }
	}

	private void openMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
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
	}

	private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		System.exit(0);
	}

	// sets the cursor in the component tree to the current component - in both
	// jTrees
	public void showComponentInTrees(Object source) {
		showComponentInTree(source, componentJTree);
		showComponentInTree(source, domainJTree);
		// TODO: doplnit aby sa to zobrazilo aj v infopaneli
		updateInfoPanel(source);
	}

	public void updateInfoPanel(Object source) {
		Object root = domainJTree.getModel().getRoot();
		Object[] treePath = createTreePathToComponent(source, root);

		if (treePath != null && treePath.length > 0) {
			Object last = treePath[treePath.length - 1];
			if (last instanceof TreeNode) {
				TreeNode tn = (TreeNode) last;
				Object userObject = tn.getUserObject();
				if (userObject instanceof Term) {
					Term f = (Term) tn.getUserObject();

					nameField.setText(f.getName());
					descriptionField.setText(f.getDescription());
					typeComboBox.setSelectedItem(f.getRelation());

					Icon ii = f.getIcon();
					if (ii != null) {
						iconField.setText("");
						iconField.setIcon(ii);
					} else {
						iconField.setText("no icon");
						iconField.setIcon(null);
					}

					Object c = f.getComponent();
					String text = "";

					classField.setText(c.getClass().getName());

					// ///// action command text field
					if (c instanceof JMenu) {
						text = ((JMenu) c).getActionCommand();
					} else if (c instanceof AbstractButton) {
						text = ((AbstractButton) c).getActionCommand();
					} else
						text = "";
					actionCommandField.setText(text);

					// ///// text / content text field
					if (c instanceof JTextComponent) {
						text = ((JTextComponent) c).getText();
					} else if (c instanceof AbstractButton) {
						text = ((AbstractButton) c).getText();
					} else if (c instanceof JSpinner) {
						text = ((JSpinner) c).getValue().toString();
					} else if (c instanceof JMenu) {
						text = ((JMenu) c).getText();
					} else if (c instanceof JComboBox) {
						Object[] objects = ((JComboBox) c).getSelectedObjects();
						text = "";
						for (int i = 0; i < objects.length; i++) {
							text = text + objects[i].toString();
							if (i != objects.length - 1) {
								text = text + ", ";
							}
						}
					} else if (c instanceof JList) {
						Object[] objects = ((JList) c).getSelectedValues();
						for (int i = 0; i < objects.length; i++) {
							text = text + objects[i].toString();
							if (i != objects.length - 1) {
								text = text + ", ";
							}
						}
					} else if (c instanceof JTabbedPane) {
						text = ((JTabbedPane) c).getSelectedIndex() + "";
					} else if (c instanceof JTree) {
						TreePath[] objects = ((JTree) c).getSelectionPaths();
						if (objects != null && objects.length != 0) {
							for (int i = 0; i < objects.length; i++) {
								text = text
										+ objects[i].getLastPathComponent()
												.toString();
								if (i != objects.length - 1) {
									text = text + ", ";
								}
							}
						} else
							text = "";
					} else
						text = "";
					contentField.setText(text);

					if (c instanceof JComponent) {
						JComponent jc = (JComponent) c;

						// /////tooltip text field
						tooltipField.setText(((JComponent) c).getToolTipText());

						// /////labelfor text field
						PathFinder apf = PathFinder.getInstance();
						JLabel label = apf.findLabelFor(jc);
						if (label != null) {
							text = label.getText();
						} else
							text = "";
						labelField.setText(text);
					}
				}
			}
		}
	}

	// sets the cursor in the component tree to the current component - in the
	// componentJTree
	private void showComponentInTree(Object source, JTree tree) {
		Object root = tree.getModel().getRoot();
		Object[] treePath = createTreePathToComponent(source, root);
		if (treePath.length != 0) {
			TreePath path = new TreePath(treePath);
			if (path != null && path.getPathCount() != 0) {

				tree.expandPath(path.getParentPath());
				tree.makeVisible(path);
				tree.scrollPathToVisible(path);
				tree.setSelectionPath(path);
			}
		}
	}

	private Object[] createTreePathToComponent(Object source, Object root) {
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
							if (f.getComponent() != null
									&& f.getComponent().equals(source))
								return node.getPath();
						} else if (node.getUserObject().equals(source)) {
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

	/******************* Utilities for tree expanding/collapsing *****************/

	// If expand is true, expands all nodes in the tree.
	// Otherwise, collapses all nodes in the tree.
	public void expandAll(JTree tree, boolean expand) {
		javax.swing.tree.TreeNode root = (javax.swing.tree.TreeNode) tree
				.getModel().getRoot();

		// Traverse tree from root
		expandAll(tree, new TreePath(root), expand);
	}

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

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JTextField actionCommandField;
	private javax.swing.JLabel actionCommandLabel;
	private javax.swing.JTextField classField;
	private javax.swing.JLabel classLabel;
	private javax.swing.JLabel componentInfoLabel;
	private javax.swing.JPanel componentInfoPanel;
	private javax.swing.JSeparator componentInfoSeparator;
	private javax.swing.JLabel componentLabel;
	private javax.swing.JPanel componentPanel;
	private javax.swing.JScrollPane componentScrollPane;
	private javax.swing.JTree componentJTree;
	private javax.swing.JTextField contentField;
	private javax.swing.JLabel contentLabel;
	private javax.swing.JTextField descriptionField;
	private javax.swing.JLabel descriptionLabel;
	private javax.swing.JScrollPane editorScrollPane;
	private javax.swing.JTextArea editorTextArea;
	private javax.swing.JMenuItem exitMenuItem;
	private javax.swing.JLabel domainInfoLabel;
	private javax.swing.JPanel domainInfoPanel;
	private javax.swing.JSeparator domainInfoSeparator;
	private javax.swing.JLabel domainLabel;
	private javax.swing.JPanel domainPanel;
	private javax.swing.JScrollPane domainScrollPane;
	private javax.swing.JTree domainJTree;
	private javax.swing.JMenu fileMenu;
	private javax.swing.JLabel iconField;
	private javax.swing.JLabel iconLabel;
	private javax.swing.JPanel infoPanel1;
	private javax.swing.JPanel infoPanel2;
	private javax.swing.JSplitPane infoSplitPane;
	private javax.swing.JTabbedPane jTabbedPane;
	private javax.swing.JTextField labelField;
	private javax.swing.JLabel labelLabel;
	private javax.swing.JMenuBar menuBar;
	private javax.swing.JSplitPane modelSplitPane;
	private javax.swing.JTextField nameField;
	private javax.swing.JLabel nameLabel;
	private javax.swing.JMenuItem openMenuItem;
	private javax.swing.JMenuItem saveMenuItem;
	private javax.swing.JTextField tooltipField;
	private javax.swing.JLabel tooltipLabel;
	private javax.swing.JComboBox typeComboBox;
	private javax.swing.JLabel typeLabel;
	// End of variables declaration//GEN-END:variables
}
