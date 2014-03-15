package gui.editor.visualization.events;

import gui.model.application.events.UiEvent;
import gui.model.application.events.UiEventSequence;
import gui.tools.listener.UiEventListener;

import javax.swing.BorderFactory;
import javax.swing.CellRendererPane;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.border.BevelBorder;

import org.w3c.dom.events.UIEvent;

import com.mxgraph.canvas.mxICanvas;
import com.mxgraph.canvas.mxImageCanvas;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.handler.mxRubberband;
import com.mxgraph.swing.view.mxInteractiveCanvas;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;

public class Visualization extends JFrame implements UiEventListener {
	private Object group1;
	private mxGraph graph;
	private UIEvent uievnt;
	private UiEvent uient;

	public Visualization() {
		super("Visualization");

		// Demonstrates the use of a Swing component for rendering vertices.
		// Note: Use the heavyweight feature to allow for event handling in
		// the Swing component that is used for rendering the vertex.

		mxGraph graph = new mxGraph() {

			public void drawState(mxICanvas canvas, mxCellState state,
					boolean drawLabel) {
				String label = (drawLabel) ? state.getLabel() : "";

				// Indirection for wrapped swing canvas inside image canvas
				// (used for creating
				// the preview image when cells are dragged)
				if (getModel().isVertex(state.getCell())
						&& canvas instanceof mxImageCanvas
						&& ((mxImageCanvas) canvas).getGraphicsCanvas() instanceof SwingCanvas) {
					((SwingCanvas) ((mxImageCanvas) canvas).getGraphicsCanvas())
							.drawVertex(state, label);
				}
				// Redirection of drawing vertices in SwingCanvas
				else if (getModel().isVertex(state.getCell())
						&& canvas instanceof SwingCanvas) {
					((SwingCanvas) canvas).drawVertex(state, label);
				} else {
					super.drawState(canvas, state, drawLabel);
				}
			}
		};

		// createGraphObject(graph);
		if (uievnt != null) {
			createVisualizedAction(graph);
		}
		mxGraphComponent graphComponent = new mxGraphComponent(graph) {

			public mxInteractiveCanvas createCanvas() {
				return new SwingCanvas(this);
			}
		};

		getContentPane().add(graphComponent);

		// Adds rubberband selection
		new mxRubberband(graphComponent);

		JButton b = new JButton("Click me");
		this.add(b);
	}

	public class SwingCanvas extends mxInteractiveCanvas {
		protected CellRendererPane rendererPane = new CellRendererPane();
		protected JLabel vertexRenderer = new JLabel();
		protected mxGraphComponent graphComponent;

		public SwingCanvas(mxGraphComponent graphComponent) {
			this.graphComponent = graphComponent;
			vertexRenderer.setBorder(BorderFactory
					.createBevelBorder(BevelBorder.RAISED));
			vertexRenderer.setHorizontalAlignment(JLabel.CENTER);
			vertexRenderer.setBackground(graphComponent.getBackground()
					.darker());
			vertexRenderer.setOpaque(true);
		}

		public void drawVertex(mxCellState state, String label) {
			vertexRenderer.setText(label);
			// TODO: Configure other properties...

			rendererPane.paintComponent(g, vertexRenderer, graphComponent,
					(int) state.getX() + translate.x, (int) state.getY()
							+ translate.y, (int) state.getWidth(),
					(int) state.getHeight(), true);
		}

	}

	public void startVisualization() {
		Visualization frame = new Visualization();
		frame.setSize(400, 320);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	private void createVisualizedAction(mxGraph graph) {

		uient.getCause();
		System.out.println(uient.getCause());

	}

	// private Object insertVertex(String name) {
	// id++;
	// System.out.println("id = " +id + " name = " + name);
	// return graph.insertVertex(graph.getDefaultParent(), Integer.toString(id),
	// name, 0, 0, GraphHelper.VERTEX_WIDTH, GraphHelper.VERTEX_HEIGHT);
	// }

	private void insertEdge(Object from, Object to) {
		graph.insertEdge(graph.getDefaultParent(), null, "", from, to);
	}

	// private void makeGraphChilds(DefaultMutableTreeNode parent, Object
	// parentGraphNode) {
	// int iChildren = parent.getChildCount();
	//
	// DefaultMutableTreeNode node = (DefaultMutableTreeNode)
	// parent.getChildAt(i);
	// Object childGraphNode = insertVertex(node.toString());
	// insertEdge(parentGraphNode, childGraphNode);
	// makeGraphChilds(node, childGraphNode);
	//
	// }

	public void setEditable(boolean value) {
		graph.setCellsResizable(value);
		graph.setCellsEditable(value);
		graph.setCellsMovable(value);
	}

	private void insertData(mxGraph graph) {
		graph.getModel().beginUpdate();
		try {
			createVisualizedAction(graph);
		} finally {
			graph.getModel().endUpdate();
		}
	}

	// public void createGraphObject(mxGraph graph){
	// Object parent = graph.getDefaultParent();
	//
	// graph.getModel().beginUpdate();
	// try
	// {
	//
	//
	// Object v1 = graph.insertVertex(parent, null, "Lol", 20, 20, 80,30);
	// Object v2 = graph.insertVertex(parent, null, "Dva!", 240, 150,80, 30);
	//
	//
	// Object[] objField = {v1,v2};
	//
	//
	//
	// graph.insertEdge(parent, null, "Edge", v1, v2);
	// }
	// finally
	// {
	// graph.getModel().endUpdate();
	// }
	//
	//
	// }

	public void updateGraph(mxGraph graph) {
		graph.getModel().beginUpdate();

	}

	public void getEventSeqeuenceName() {
		String prnt;

		UiEvent uiEvent = new UiEvent();
		prnt = uiEvent.toString();
		System.out.println(prnt + "     nta");
	}

	private void foldRepeatingSequences(mxGraph graph, Object[] objVertAdd,
			Object objVertFold) {

		graph.groupCells(objVertFold, 1, objVertAdd);
		Object[] objField2 = { objVertFold };
		graph.foldCells(true, true, objField2);

	}

	@Override
	public void uiEventRecorded(UiEvent uiEvent, UiEventSequence sequence) {
//		this.uiEvSeq = sequence;
//		insertEvent(uiEvent);
	}

}
