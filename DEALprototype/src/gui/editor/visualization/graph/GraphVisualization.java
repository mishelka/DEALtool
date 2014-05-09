package gui.editor.visualization.graph;

import gui.editor.DomainModelEditor;
import gui.editor.tree.TreeNode;
import gui.editor.visualization.graph.elements.GraphCell;
import gui.model.domain.ComponentInfoType;
import gui.model.domain.Term;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxPerimeter;
import com.mxgraph.view.mxStylesheet;

/**
 * Trieda ktor‡ vytv‡ra vizualiza‹nù graf
 * @author D‡vid
 *
 */
public class GraphVisualization {

	/**
	 * graf
	 */
	private DEALGraph graph;
	/**
	 * id bunky
	 */
	private int id;
	/**
	 * automatickŽ rozmiestnenie prvkov
	 */
	private DEALLayout layout;
	/**
	 * je h¼adanie zapnutŽ
	 */
	private boolean stateSearch;

	/**
	 * list hlavnùch termov
	 */
	private ArrayList<Term> rootTerms = new ArrayList<Term>();

	/**
	 * konätruktor, inicializ‡cia a rozmiestnenie prvkov
	 */
	public GraphVisualization() {
		graph = new DEALGraph(getStylesheet());
		graph.setLabelsClipped(false);
		id = -1; // cisluje od 0, na zaciatku je id++ (dajak to nefunguje) bug v
					// kniznici?
		insertData();
		layoutGraph();
		stateSearch = false;
	}

	/**
	 * 
	 * @return graf
	 */
	public DEALGraph getGraph() {
		return graph;
	}

	/**
	 * 
	 * @return ätùl uzlov a hr‡n
	 */
	public mxStylesheet getStylesheet() {
		Map<String, Object> vertexStyle = new Hashtable<String, Object>();
		vertexStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
		vertexStyle.put(mxConstants.STYLE_PERIMETER, mxPerimeter.RectanglePerimeter);
		vertexStyle.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_MIDDLE);
		vertexStyle.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_CENTER);
		vertexStyle.put(mxConstants.STYLE_FILLCOLOR, "#A0A0A0");
		vertexStyle.put(mxConstants.STYLE_STROKECOLOR, "#4D4D4D");
		vertexStyle.put(mxConstants.STYLE_GRADIENTCOLOR, "#9A9A9A");
		vertexStyle.put(mxConstants.STYLE_GRADIENT_DIRECTION, mxConstants.DIRECTION_NORTH);
		vertexStyle.put(mxConstants.STYLE_FONTCOLOR, "#151515");

		Map<String, Object> edgeStyle = new Hashtable<String, Object>();
		edgeStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CONNECTOR);
		edgeStyle.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_OVAL);
		edgeStyle.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_MIDDLE);
		edgeStyle.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_CENTER);
		edgeStyle.put(mxConstants.STYLE_STROKECOLOR, "#212121");
		edgeStyle.put(mxConstants.STYLE_FONTCOLOR, "#151515");

		mxStylesheet stylesheet = new mxStylesheet();
		stylesheet.setDefaultVertexStyle(vertexStyle);
		stylesheet.setDefaultEdgeStyle(edgeStyle);
		return stylesheet;
	}

	/**
	 * nastavenie editovacieho m—du
	 * @param value true - ano, inak false
	 */
	public void setEditMode(boolean value) {
		graph.setCellsMovable(value);
		graph.setCellsResizable(value);
		graph.setCellsEditable(value);
	}

	/**
	 * vloì’ uzly a hrany do grafu
	 */
	private void insertData() {
		graph.getModel().beginUpdate();
		try {
			makeGraph();
		} finally {
			graph.getModel().endUpdate();
		}
	}

	/**
	 * rozmiestni uzly na zobrazovaciu plochu
	 */
	public void layoutGraph() {
		layout = new DEALLayout(graph, this);
		Object cell = graph.getDefaultParent();
		graph.getModel().beginUpdate();
		try {
			layout.execute(cell, rootTerms, 4, false);
			checkHidden();
		} finally {
			graph.getModel().endUpdate();
			graph.refresh();
		}
	}

	/**
	 * rozmiestni uzly na zobrazovaciu plochu
	 * @param minstack minimalny po‹et prvkov ktore majœ vytv‡raé z‡sobn’k
	 * @param leftRouting ¼avŽ cesty do uzlov
	 */
	public void layoutGraph(int minstack, boolean leftRouting) {
		Object cell = graph.getDefaultParent();
		graph.getModel().beginUpdate();
		try {
			layout.execute(cell, rootTerms, minstack, leftRouting);
		} finally {
			graph.getModel().endUpdate();
			graph.refresh();
		}
	}

	/**
	 * 
	 * @return objekt pre automatickŽ rozmiestnenie prvkov
	 */
	public DEALLayout getLayout() {
		return layout;
	}

	/**
	 * vytvor’ graf
	 */
	private void makeGraph() {
		TreeModel model = DomainModelEditor.getInstance().getDomainTree().getModel();
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) model.getRoot();
		Object root = insertVertex(rootNode.toString(), null);
		mxCell rootCell = (mxCell) root;
		rootCell.setVisible(false);
		makeGraphChilds(rootNode, root, true);
	}

	/**
	 * vloì’ uzol do grafu
	 * @param name n‡zov
	 * @param t objet term
	 * @return mxCell objekt
	 */
	private Object insertVertex(String name, Term t) {
		id++;
		Object vertx = graph.createVertex(graph.getDefaultParent(), Integer.toString(id), clipLabel(name), 0, 0, Constants.VERTEX_WIDTH, Constants.VERTEX_HEIGHT, null);
		return graph.addCell(vertx);
	}

	/**
	 * vloì’ prepojenie medzi uzlami do grafu
	 * @param from rodi‹ovskù prvok
	 * @param to cie¼ovù prvok
	 * @return objekt mxCell hrana
	 */
	private Object insertEdge(Object from, Object to) {
		return graph.insertEdge(graph.getDefaultParent(), null, "", from, to);
	}

	/**
	 * inicializ‡cia œdajov grafu
	 * @param parent rodi‹ovskù prvok
	 * @param parentGraphNode rodi‹ovskù uzol
	 * @param bRoot root
	 */
	private void makeGraphChilds(DefaultMutableTreeNode parent, Object parentGraphNode, boolean bRoot) {
		int iChildren = parent.getChildCount();
		if (bRoot) {
			for (int i = 0; i < parent.getChildCount(); i++) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent.getChildAt(i);
				rootTerms.add((Term) node.getUserObject());
			}
		}
		for (int i = 0; i < iChildren; i++) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent.getChildAt(i);
			Object obj = node.getUserObject();
			Object childGraphNode = insertVertex(node.toString(), (Term) obj);

			if (!bRoot) {
				Object edge = insertEdge(parentGraphNode, childGraphNode);
				GraphCell gCell = new GraphCell((mxCell) childGraphNode, (mxCell) edge);
				gCell.setHidden(((TreeNode) parent.getChildAt(i)).isHidden());
				GraphData.getInstance().addVertex((Term) obj, gCell);
			} else {
				GraphCell gCell = new GraphCell((mxCell) childGraphNode, null);
				gCell.setHidden(((TreeNode) parent.getChildAt(i)).isHidden());
				GraphData.getInstance().addVertex((Term) obj, gCell);
			}
			makeGraphChilds(node, childGraphNode, false);
		}
	}

	/**
	 * nastavi prieh¼adnosé skrytùm prvkom na 20%
	 */
	private void checkHidden() {
		for (Map.Entry<Term, GraphCell> entry : GraphData.getInstance().getVertexes().entrySet()) {
			if (entry.getValue().isHidden()) {
				entry.getValue().getCell().setStyle("opacity=20");
				if (entry.getValue().hasEdge()) {
					entry.getValue().getEdge().setStyle("opacity=20");
				}
			}
		}
	}

	/**
	 * zobraz’ typ komponentu ako pozadie uzlov
	 * @param bShow zobrazié
	 */
	public void updateGraphVertexColor(boolean bShow) {
		graph.getModel().beginUpdate();
		try {
			Enumeration<Term> enumKey = GraphData.getInstance().getVertexes().keys();
			while (enumKey.hasMoreElements()) {
				Term t = enumKey.nextElement();
				Object cell = GraphData.getInstance().getVertexes().get(t);
				GraphCell mxCell = (GraphCell) cell;
				if (bShow) {
					ComponentInfoType infoType = t.getComponentInfoType();
					if (infoType != null)
						switch (infoType) {
						case TEXTUAL:
						case DESCRIPTIVE:
							mxCell.getCell().setStyle("fillColor=#0600ff;gradientColor=#2865ff");
							break;
						case FUNCTIONAL:
							mxCell.getCell().setStyle("fillColor=#d91717;gradientColor=#ff2929");
							break;
						case CONTAINERS:
							mxCell.getCell().setStyle("fillColor=#00b500;gradientColor=#2ad72e");
							break;
						case LOGICALLY_GROUPING:
							mxCell.getCell().setStyle("fillColor=#2bbab8;gradientColor=#37efed");
							break;
						case CUSTOM:
							mxCell.getCell().setStyle("fillColor=#efdc06;gradientColor=#fcea4e");
							break;
						case UNKNOWN:
							mxCell.getCell().setStyle("fillColor=#ffffff;gradientColor=#b0a9a9");
							break;
						default:
							mxCell.getCell().setStyle("fillColor=#ffffff;gradientColor=#b0a9a9");
							break;
						}
					else
						mxCell.getCell().setStyle("fillColor=#ffffff;gradientColor=#b0a9a9");
				} else {
					mxCell.getCell().setStyle(null);
				}

			}
		} finally {
			graph.getModel().endUpdate();
			graph.refresh();
		}
	}

	/**
	 * ukon‹’ h¼adanie
	 */
	public void endSearchState() {
		stateSearch = false;
		graph.getModel().beginUpdate();
		try {
			for (final Map.Entry<Term, GraphCell> entry : GraphData.getInstance().getVertexes().entrySet()) {
				if (!entry.getValue().isHidden()) {
					entry.getValue().getCell().setStyle("opacity=100");
					if (entry.getValue().hasEdge())
						entry.getValue().getEdge().setStyle("opacity=100");
				}
			}
		} finally {
			graph.getModel().endUpdate();
			graph.refresh();
		}
	}

	/**
	 * za‹ne h¼adanie
	 * @param searchText vloìenù text
	 */
	public void startSearchState(String searchText) {
		for (Map.Entry<Term, GraphCell> entry : GraphData.getInstance().getVertexes().entrySet()) {
			graph.getModel().beginUpdate();
			try {
				String termName = entry.getKey().getName();

				if (termName != null) {
					if (!termName.equalsIgnoreCase(searchText)) {
						stateSearch = true;
						entry.getValue().getCell().setStyle("opacity=20");
						if (entry.getValue().hasEdge())
							entry.getValue().getEdge().setStyle("opacity=20");
					} else {
						entry.getValue().getCell().setStyle("opacity=100");
						if (entry.getValue().hasEdge())
							entry.getValue().getEdge().setStyle("opacity=100");
					}
				} else {
					// uzly co nemaju name (prazdne)
					entry.getValue().getCell().setStyle("opacity=20");
					if (entry.getValue().hasEdge())
						entry.getValue().getEdge().setStyle("opacity=20");
				}
			} finally {
				graph.getModel().endUpdate();
				graph.refresh();
			}
		}
	}

	/**
	 * 
	 * @return je graf v stave h¼adania
	 */
	public boolean isStateSearch() {
		return stateSearch;
	}

	/**
	 * osekne text v uzle
	 * @param label vstupnù text
	 * @return osekanù text
	 */
	public String clipLabel(String label) {
		if (label != null)
			if (label.length() > 6) {
				label = label.substring(0, 6);
				label = label + "...";
				return label;
			} else {
				return label;
			}
		return null;
	}



}
