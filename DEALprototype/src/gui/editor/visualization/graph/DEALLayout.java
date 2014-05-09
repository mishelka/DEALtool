package gui.editor.visualization.graph;

import gui.editor.visualization.graph.elements.GraphCell;
import gui.editor.visualization.graph.elements.Stack;
import gui.model.domain.Term;
import gui.model.domain.relation.RelationType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxGraph;

/**
 * trieda zabezpecuje automaticke rozmiestnenie prvkov na plochu
 * @author David
 *
 */
public class DEALLayout extends mxGraphLayout {

	/**
	 * minimalny pocet listov ktore sa budu skladat do zasobnika
	 */
	private int minLeafsToStack;
	/**
	 * medzera v px medzi urovnami
	 */
	private double offsetY = 100;
	/**
	 * medzera v px medzi prvkami
	 */
	private double offsetX = 20;
	/**
	 * medzera pri prepojeniach
	 */
	private double edgeOffset = 10;
	/**
	 * prepina‹ laveho zobrazenia ciest do zasobnikov
	 */
	private boolean leftRouting;
	/**
	 * rodicovsky term
	 */
	private ArrayList<Term> root;
	/**
	 * rodicovsky objekt
	 */
	private Object parent;
	/**
	 * objekt vizualizacie
	 */
	private GraphVisualization visualization;

	/**
	 * medzera pri prepojeniach
	 */
	private double edgeStyleOffset = 10;

	/**
	 * konätrukotr
	 * @param graph graf
	 * @param visualization vizualizacia
	 */
	public DEALLayout(mxGraph graph, GraphVisualization visualization) {
		super(graph);
		this.visualization = visualization;
	}

	/**
	 * hlavna metoda ktora zabezpecuje rozmiestnenie prvkov
	 * @param parent rodi‹
	 * @param root rodi‹ovskù term
	 * @param minLeafs minimalny pocet listov ktore sa zobrazuju do zasobnika
	 * @param leftRouting lave cesty do zasobnikov
	 */
	public void execute(Object parent, ArrayList<Term> root, int minLeafs, boolean leftRouting) {
		super.execute(parent);
		mxIGraphModel model = graph.getModel();
		model.beginUpdate();
		this.parent = parent;
		this.root = root;
		double graphWidth = 0;
		minLeafsToStack = minLeafs;
		this.leftRouting = leftRouting;
		try {
			for (int i = 0; i < root.size(); i++) {
				GraphCell rootCell = GraphData.getInstance().getVertexes().get(root.get(i));
				rootCell.getCell().getGeometry().setY(20);
				rootCell.getCell().getGeometry().setX(offsetX + graphWidth);
				graphWidth += layoutChildren(root.get(i));

				centerParents(root.get(i));

				for (int j = 0; j < root.get(i).getChildrenCount(); j++) {
					GraphCell gCell = GraphData.getInstance().getVertexes().get(root.get(i).getChildAt(j));
					if (leftRouting)
						routeEdgesLeft(gCell, root.get(i).getRelation());
					else
						routeEdgesTop(gCell, root.get(i).getRelation());
				}
			}

		} finally {
			model.endUpdate();
		}

	}

	/**
	 * pomocna metoda pre cetrovanie prvkov pod¼a dcŽrskych prvkov
	 * @param parentTerm rodi‹ovsky term
	 */
	private void centerParents(Term parentTerm) {
		GraphCell parentCell = GraphData.getInstance().getVertexes().get(parentTerm);
		double startPos = 0;
		double endPos = 0;
		GraphCell childCell = null;
		for (int i = 0; i < parentTerm.getChildrenCount(); i++) {
			if (!parentTerm.getChildAt(i).isLeaf()) {
				centerParents(parentTerm.getChildAt(i));
			}
			childCell = GraphData.getInstance().getVertexes().get(parentTerm.getChildAt(i));
			if (i == 0) {
				startPos = childCell.getCell().getGeometry().getX();
				endPos = childCell.getCell().getGeometry().getX() + Constants.VERTEX_WIDTH;
			} else {
				if (childCell.getCell().getGeometry().getX() < startPos) {
					startPos = childCell.getCell().getGeometry().getX();
				}
				if (childCell.getCell().getGeometry().getX() + Constants.VERTEX_WIDTH > endPos) {
					endPos = childCell.getCell().getGeometry().getX() + Constants.VERTEX_WIDTH;
				}
			}
		}

		if (parentTerm.getChildrenCount() == 1) {
			parentCell.getCell().getGeometry().setX(GraphData.getInstance().getVertexes().get(parentTerm.getChildAt(0)).getCell().getGeometry().getX());
			return;
		}

		if (endPos - startPos > 0)
			parentCell.getCell().getGeometry().setX(startPos + (endPos - startPos) / 2 - Constants.VERTEX_WIDTH / 2);
	}

	/**
	 * rozmiestnenie dcerskych prvkov rodi‹a na plochu
	 * @param parentTerm rodi‹ovskù prvok
	 * @return ä’rku dcŽrskych prvkov v px
	 */
	private double layoutChildren(Term parentTerm) {
		double width = 0;

		// zisti kolko listov
		int leafs = 0;
		for (int i = 0; i < parentTerm.getChildrenCount(); i++) {
			if (parentTerm.getChildAt(i).isLeaf()) {
				leafs++;
			}
		}

		// zisti ci treba stacky a popripade ich vytvor
		GraphCell parentCell = GraphData.getInstance().getVertexes().get(parentTerm);
		int numOfStacks = 0;
		ArrayList<Stack> stacks = null;
		if (leafs >= minLeafsToStack) {
			if (leafs < 10 && leafs > 1) {
				int size = leafs / 2;
				if (leafs % 2 == 1)
					size += 1;
				stacks = new ArrayList<Stack>();
				Stack s1 = new Stack(parentCell.getCell().getGeometry().getX(), parentCell.getCell().getGeometry().getY() + Constants.VERTEX_HEIGHT + offsetY, true);
				Stack s2 = new Stack(parentCell.getCell().getGeometry().getX() + Constants.VERTEX_WIDTH + offsetX, parentCell.getCell().getGeometry().getY() + Constants.VERTEX_HEIGHT + offsetY, true);
				s1.setMaxElements(size);
				s2.setMaxElements(size);
				stacks.add(s1);
				stacks.add(s2);
				numOfStacks = 2;
				width += 2 * (Constants.VERTEX_WIDTH + offsetX);
			} else {
				numOfStacks = leafs / Stack.DEFAULT_MAX_ELEMENTS;
				if (leafs % Stack.DEFAULT_MAX_ELEMENTS > 0)
					numOfStacks++;
				stacks = new ArrayList<Stack>();
				for (int i = 0; i < numOfStacks; i++) {
					stacks.add(new Stack(parentCell.getCell().getGeometry().getX() + i * (Constants.VERTEX_WIDTH + offsetX), parentCell.getCell().getGeometry().getY() + Constants.VERTEX_HEIGHT + offsetY, false));
					width += Constants.VERTEX_WIDTH + offsetX;
				}
			}
		}

		// STACKY a LEAFY
		double xPos = parentCell.getCell().getGeometry().getX();
		GraphCell gCell = null;
		for (int i = 0; i < parentTerm.getChildrenCount(); i++) {
			gCell = GraphData.getInstance().getVertexes().get(parentTerm.getChildAt(i));
			if (numOfStacks > 0 && parentTerm.getChildAt(i).isLeaf()) {
				// najdi stack a buchni prvok do neho
				for (int j = 0; j < stacks.size(); j++) {
					if (!stacks.get(j).isFull()) {
						stacks.get(j).addCell(gCell.getCell());
						gCell.setStack(stacks.get(j));
						break;
					}
				}
				xPos = gCell.getCell().getGeometry().getX() + Constants.VERTEX_WIDTH + offsetX;
			} else if (numOfStacks == 0 && parentTerm.getChildAt(i).isLeaf()) {
				// je leaf
				gCell.getCell().getGeometry().setX(xPos);
				gCell.getCell().getGeometry().setY(parentCell.getCell().getGeometry().getY() + Constants.VERTEX_HEIGHT + offsetY);
				xPos = gCell.getCell().getGeometry().getX() + Constants.VERTEX_WIDTH + offsetX;
				width += Constants.VERTEX_WIDTH + offsetX;
			}

		}

		// OSTATNE PRVKY CO MAJU CHILDY
		for (int i = 0; i < parentTerm.getChildrenCount(); i++) {
			gCell = GraphData.getInstance().getVertexes().get(parentTerm.getChildAt(i));
			if (!parentTerm.getChildAt(i).isLeaf()) {
				gCell.getCell().getGeometry().setX(xPos);
				gCell.getCell().getGeometry().setY(parentCell.getCell().getGeometry().getY() + Constants.VERTEX_HEIGHT + offsetY);
				double childWidth = layoutChildren(GraphData.getInstance().getTermByCell(gCell));
				width += childWidth;

				// nastavenie dalsej x pozicie prvka (vlavo)
				xPos = gCell.getCell().getGeometry().getX() + childWidth;
			}
		}

		return width;
	}

	/**
	 * nastavenie ciest zhora
	 * @param cell bunka
	 * @param relation typ vztahu
	 */
	private void routeEdgesTop(GraphCell cell, RelationType relation) {

		if (cell.isInStack()) {
			if (cell.getStack().getCellPosition(cell.getCell()) > 0) {
				cell.getEdge().setVisible(false);
				return;
			}
		}
		Term term = GraphData.getInstance().getTermByCell(cell);
		if (relation == RelationType.MUTUALLY_EXCLUSIVE) {
			List<mxPoint> newPoints = new ArrayList<mxPoint>(7);
			newPoints.add(new mxPoint(cell.getEdge().getSource().getGeometry().getCenterX(), cell.getEdge().getSource().getGeometry().getY() + Constants.VERTEX_HEIGHT));
			newPoints.add(new mxPoint(cell.getEdge().getSource().getGeometry().getCenterX(), cell.getEdge().getSource().getGeometry().getY() + Constants.VERTEX_HEIGHT + offsetY / 2));
			newPoints.add(new mxPoint(cell.getEdge().getTarget().getGeometry().getCenterX(), cell.getEdge().getSource().getGeometry().getY() + Constants.VERTEX_HEIGHT + offsetY / 2));
			newPoints.add(new mxPoint(cell.getEdge().getTarget().getGeometry().getCenterX(), cell.getEdge().getSource().getGeometry().getY() + Constants.VERTEX_HEIGHT + offsetY / 2 + edgeStyleOffset));
			newPoints.add(new mxPoint(cell.getEdge().getSource().getGeometry().getCenterX(), cell.getEdge().getSource().getGeometry().getY() + Constants.VERTEX_HEIGHT + offsetY / 2 + edgeStyleOffset));
			newPoints.add(new mxPoint(cell.getEdge().getTarget().getGeometry().getCenterX(), cell.getEdge().getSource().getGeometry().getY() + Constants.VERTEX_HEIGHT + offsetY / 2 + edgeStyleOffset));
			newPoints.add(new mxPoint(cell.getEdge().getTarget().getGeometry().getCenterX(), cell.getEdge().getTarget().getGeometry().getY()));
			setEdgePoints((Object) cell.getEdge(), newPoints);
			cell.getEdge().setStyle("endArrow=none");
		} else if (relation == RelationType.MUTUALLY_NOT_EXCLUSIVE) {
			List<mxPoint> newPoints = new ArrayList<mxPoint>();
			newPoints.add(new mxPoint(cell.getEdge().getSource().getGeometry().getCenterX(), cell.getEdge().getSource().getGeometry().getY() + Constants.VERTEX_HEIGHT));
			newPoints.add(new mxPoint(cell.getEdge().getSource().getGeometry().getCenterX(), cell.getEdge().getSource().getGeometry().getY() + Constants.VERTEX_HEIGHT + offsetY / 2));
			newPoints.add(new mxPoint(cell.getEdge().getTarget().getGeometry().getCenterX(), cell.getEdge().getSource().getGeometry().getY() + Constants.VERTEX_HEIGHT + offsetY / 2));
			for (int i = 1; i <= edgeOffset; i++) {
				newPoints.add(new mxPoint(cell.getEdge().getTarget().getGeometry().getCenterX(), cell.getEdge().getSource().getGeometry().getY() + Constants.VERTEX_HEIGHT + offsetY / 2 + i));
				newPoints.add(new mxPoint(cell.getEdge().getSource().getGeometry().getCenterX(), cell.getEdge().getSource().getGeometry().getY() + Constants.VERTEX_HEIGHT + offsetY / 2 + i));
				newPoints.add(new mxPoint(cell.getEdge().getTarget().getGeometry().getCenterX(), cell.getEdge().getSource().getGeometry().getY() + Constants.VERTEX_HEIGHT + offsetY / 2 + i));
			}
			newPoints.add(new mxPoint(cell.getEdge().getTarget().getGeometry().getCenterX(), cell.getEdge().getTarget().getGeometry().getY()));
			setEdgePoints((Object) cell.getEdge(), newPoints);
			cell.getEdge().setStyle("endArrow=none");
		} else {
			List<mxPoint> newPoints = new ArrayList<mxPoint>(4);
			newPoints.add(new mxPoint(cell.getEdge().getSource().getGeometry().getCenterX(), cell.getEdge().getSource().getGeometry().getY() + Constants.VERTEX_HEIGHT));
			newPoints.add(new mxPoint(cell.getEdge().getSource().getGeometry().getCenterX(), cell.getEdge().getSource().getGeometry().getY() + Constants.VERTEX_HEIGHT + offsetY / 2));
			newPoints.add(new mxPoint(cell.getEdge().getTarget().getGeometry().getCenterX(), cell.getEdge().getSource().getGeometry().getY() + Constants.VERTEX_HEIGHT + offsetY / 2));
			newPoints.add(new mxPoint(cell.getEdge().getTarget().getGeometry().getCenterX(), cell.getEdge().getTarget().getGeometry().getY()));
			setEdgePoints((Object) cell.getEdge(), newPoints);
		}

		if (!term.isLeaf()) {
			for (int i = 0; i < term.getChildrenCount(); i++) {
				routeEdgesTop(GraphData.getInstance().getVertexes().get(term.getChildAt(i)), term.getRelation());
			}
		}
	}

	/**
	 * nastavenie ciest z¼ava
	 * @param cell bunka
	 * @param relation typ vzéahu
	 */
	private void routeEdgesLeft(GraphCell cell, RelationType relation) {
		if (cell.isInStack()) {
			cell.getEdge().setVisible(true);
			if (relation == RelationType.AND) {
				cell.getEdge().setStyle("endArrow=" + mxConstants.ARROW_OVAL);
			} else {
				cell.getEdge().setStyle("endArrow=none");
			}
			List<mxPoint> newPoints = new ArrayList<mxPoint>();
			if (relation == RelationType.AND) {
				newPoints.add(new mxPoint(cell.getEdge().getSource().getGeometry().getCenterX(), cell.getEdge().getSource().getGeometry().getY() + Constants.VERTEX_HEIGHT));
				newPoints.add(new mxPoint(cell.getEdge().getSource().getGeometry().getCenterX(), cell.getEdge().getSource().getGeometry().getY() + Constants.VERTEX_HEIGHT + offsetY - 2 * edgeOffset));
				newPoints.add(new mxPoint(cell.getEdge().getTarget().getGeometry().getX() - edgeOffset, cell.getEdge().getSource().getGeometry().getY() + Constants.VERTEX_HEIGHT + offsetY - 2 * edgeOffset));
				newPoints.add(new mxPoint(cell.getEdge().getTarget().getGeometry().getX() - edgeOffset, cell.getEdge().getTarget().getGeometry().getY() + Constants.VERTEX_HEIGHT / 2));
				newPoints.add(new mxPoint(cell.getEdge().getTarget().getGeometry().getX(), cell.getEdge().getTarget().getGeometry().getY() + Constants.VERTEX_HEIGHT / 2));
			} else if (relation == RelationType.MUTUALLY_EXCLUSIVE) {
				newPoints.add(new mxPoint(cell.getEdge().getSource().getGeometry().getCenterX(), cell.getEdge().getSource().getGeometry().getY() + Constants.VERTEX_HEIGHT));
				newPoints.add(new mxPoint(cell.getEdge().getSource().getGeometry().getCenterX(), cell.getEdge().getSource().getGeometry().getY() + Constants.VERTEX_HEIGHT + offsetY - 2 * edgeOffset - edgeStyleOffset));
				newPoints.add(new mxPoint(cell.getEdge().getTarget().getGeometry().getX() - edgeOffset, cell.getEdge().getSource().getGeometry().getY() + Constants.VERTEX_HEIGHT + offsetY - 2 * edgeOffset - edgeStyleOffset));

				newPoints.add(new mxPoint(cell.getEdge().getTarget().getGeometry().getX() - edgeOffset, cell.getEdge().getSource().getGeometry().getY() + Constants.VERTEX_HEIGHT + offsetY - 2 * edgeOffset));
				newPoints.add(new mxPoint(cell.getEdge().getSource().getGeometry().getCenterX(), cell.getEdge().getSource().getGeometry().getY() + Constants.VERTEX_HEIGHT + offsetY - 2 * edgeOffset));
				newPoints.add(new mxPoint(cell.getEdge().getTarget().getGeometry().getX() - edgeOffset, cell.getEdge().getSource().getGeometry().getY() + Constants.VERTEX_HEIGHT + offsetY - 2 * edgeOffset));

				newPoints.add(new mxPoint(cell.getEdge().getTarget().getGeometry().getX() - edgeOffset, cell.getEdge().getTarget().getGeometry().getY() + Constants.VERTEX_HEIGHT / 2));
				newPoints.add(new mxPoint(cell.getEdge().getTarget().getGeometry().getX(), cell.getEdge().getTarget().getGeometry().getY() + Constants.VERTEX_HEIGHT / 2));
			} else if (relation == RelationType.MUTUALLY_NOT_EXCLUSIVE) {
				newPoints.add(new mxPoint(cell.getEdge().getSource().getGeometry().getCenterX(), cell.getEdge().getSource().getGeometry().getY() + Constants.VERTEX_HEIGHT));
				for (int i = (int) edgeOffset; i > 0; i--) {
					newPoints.add(new mxPoint(cell.getEdge().getSource().getGeometry().getCenterX(), cell.getEdge().getSource().getGeometry().getY() + Constants.VERTEX_HEIGHT + offsetY - 2 * edgeOffset - i));
					newPoints.add(new mxPoint(cell.getEdge().getTarget().getGeometry().getX() - edgeOffset, cell.getEdge().getSource().getGeometry().getY() + Constants.VERTEX_HEIGHT + offsetY - 2 * edgeOffset - i));
				}
				newPoints.add(new mxPoint(cell.getEdge().getTarget().getGeometry().getX() - edgeOffset, cell.getEdge().getSource().getGeometry().getY() + Constants.VERTEX_HEIGHT + offsetY - 2 * edgeOffset));
				newPoints.add(new mxPoint(cell.getEdge().getSource().getGeometry().getCenterX(), cell.getEdge().getSource().getGeometry().getY() + Constants.VERTEX_HEIGHT + offsetY - 2 * edgeOffset));
				newPoints.add(new mxPoint(cell.getEdge().getTarget().getGeometry().getX() - edgeOffset, cell.getEdge().getSource().getGeometry().getY() + Constants.VERTEX_HEIGHT + offsetY - 2 * edgeOffset));

				newPoints.add(new mxPoint(cell.getEdge().getTarget().getGeometry().getX() - edgeOffset, cell.getEdge().getTarget().getGeometry().getY() + Constants.VERTEX_HEIGHT / 2));
				newPoints.add(new mxPoint(cell.getEdge().getTarget().getGeometry().getX(), cell.getEdge().getTarget().getGeometry().getY() + Constants.VERTEX_HEIGHT / 2));
			}
			setEdgePoints((Object) cell.getEdge(), newPoints);

		} else {
			List<mxPoint> newPoints = new ArrayList<mxPoint>();
			if (relation == RelationType.AND) {
				newPoints.add(new mxPoint(cell.getEdge().getSource().getGeometry().getCenterX(), cell.getEdge().getSource().getGeometry().getY() + Constants.VERTEX_HEIGHT));
				newPoints.add(new mxPoint(cell.getEdge().getSource().getGeometry().getCenterX(), cell.getEdge().getSource().getGeometry().getY() + Constants.VERTEX_HEIGHT + offsetY - 2 * edgeOffset));
				newPoints.add(new mxPoint(cell.getEdge().getTarget().getGeometry().getCenterX(), cell.getEdge().getSource().getGeometry().getY() + Constants.VERTEX_HEIGHT + offsetY - 2 * edgeOffset));
				newPoints.add(new mxPoint(cell.getEdge().getTarget().getGeometry().getCenterX(), cell.getEdge().getTarget().getGeometry().getY()));
			} else if (relation == RelationType.MUTUALLY_EXCLUSIVE) {
				newPoints.add(new mxPoint(cell.getEdge().getSource().getGeometry().getCenterX(), cell.getEdge().getSource().getGeometry().getY() + Constants.VERTEX_HEIGHT));
				newPoints.add(new mxPoint(cell.getEdge().getSource().getGeometry().getCenterX(), cell.getEdge().getSource().getGeometry().getY() + Constants.VERTEX_HEIGHT + offsetY - 2 * edgeOffset - edgeStyleOffset));
				newPoints.add(new mxPoint(cell.getEdge().getTarget().getGeometry().getCenterX(), cell.getEdge().getSource().getGeometry().getY() + Constants.VERTEX_HEIGHT + offsetY - 2 * edgeOffset - edgeStyleOffset));
				newPoints.add(new mxPoint(cell.getEdge().getTarget().getGeometry().getCenterX(), cell.getEdge().getSource().getGeometry().getY() + Constants.VERTEX_HEIGHT + offsetY - 2 * edgeOffset));
				newPoints.add(new mxPoint(cell.getEdge().getSource().getGeometry().getCenterX(), cell.getEdge().getSource().getGeometry().getY() + Constants.VERTEX_HEIGHT + offsetY - 2 * edgeOffset));
				newPoints.add(new mxPoint(cell.getEdge().getTarget().getGeometry().getCenterX(), cell.getEdge().getSource().getGeometry().getY() + Constants.VERTEX_HEIGHT + offsetY - 2 * edgeOffset));
				newPoints.add(new mxPoint(cell.getEdge().getTarget().getGeometry().getCenterX(), cell.getEdge().getTarget().getGeometry().getY()));
			} else if (relation == RelationType.MUTUALLY_NOT_EXCLUSIVE) {
				newPoints.add(new mxPoint(cell.getEdge().getSource().getGeometry().getCenterX(), cell.getEdge().getSource().getGeometry().getY() + Constants.VERTEX_HEIGHT));
				for(int i=(int)edgeStyleOffset;i>0;i--){
					newPoints.add(new mxPoint(cell.getEdge().getSource().getGeometry().getCenterX(), cell.getEdge().getSource().getGeometry().getY() + Constants.VERTEX_HEIGHT + offsetY - 2 * edgeOffset - i));
					newPoints.add(new mxPoint(cell.getEdge().getTarget().getGeometry().getCenterX(), cell.getEdge().getSource().getGeometry().getY() + Constants.VERTEX_HEIGHT + offsetY - 2 * edgeOffset - i));	
				}
				newPoints.add(new mxPoint(cell.getEdge().getTarget().getGeometry().getCenterX(), cell.getEdge().getSource().getGeometry().getY() + Constants.VERTEX_HEIGHT + offsetY - 2 * edgeOffset));
				newPoints.add(new mxPoint(cell.getEdge().getSource().getGeometry().getCenterX(), cell.getEdge().getSource().getGeometry().getY() + Constants.VERTEX_HEIGHT + offsetY - 2 * edgeOffset));
				newPoints.add(new mxPoint(cell.getEdge().getTarget().getGeometry().getCenterX(), cell.getEdge().getSource().getGeometry().getY() + Constants.VERTEX_HEIGHT + offsetY - 2 * edgeOffset));
				newPoints.add(new mxPoint(cell.getEdge().getTarget().getGeometry().getCenterX(), cell.getEdge().getTarget().getGeometry().getY()));
			}

			setEdgePoints((Object) cell.getEdge(), newPoints);
		}

		Term term = GraphData.getInstance().getTermByCell(cell);
		if (!term.isLeaf()) {
			for (int i = 0; i < term.getChildrenCount(); i++) {
				routeEdgesLeft(GraphData.getInstance().getVertexes().get(term.getChildAt(i)), term.getRelation());
			}
		}

	}

	/**
	 * nastavenie cesty do zasobnikov zhora
	 */
	public void setRoutingTop() {
		leftRouting = false;
		mxIGraphModel model = graph.getModel();
		model.beginUpdate();
		try {
			for (int i = 0; i < root.size(); i++) {
				for (int j = 0; j < root.get(i).getChildrenCount(); j++) {
					GraphCell gCell = GraphData.getInstance().getVertexes().get(root.get(i).getChildAt(j));
					routeEdgesTop(gCell, GraphData.getInstance().getTermByCell(gCell).getRelation());
				}
			}
		} finally {
			model.endUpdate();
			graph.refresh();
		}
	}

	/**
	 * nastavenie cesty do zasobnikov z¼ava
	 */
	public void setRoutingLeft() {
		leftRouting = true;
		mxIGraphModel model = graph.getModel();
		model.beginUpdate();
		try {
			for (int i = 0; i < root.size(); i++) {
				for (int j = 0; j < root.get(i).getChildrenCount(); j++) {
					GraphCell gCell = GraphData.getInstance().getVertexes().get(root.get(i).getChildAt(j));
					routeEdgesLeft(gCell, GraphData.getInstance().getTermByCell(gCell).getRelation());
				}
			}
		} finally {
			model.endUpdate();
			graph.refresh();
		}
	}

	/**
	 * 
	 * @return pocet prvkov ktore maju vytvarat zasobnik
	 */
	public int getMinLeafsToStack() {
		return minLeafsToStack;
	}

	/**
	 * nastavenie po‹tu minimalnych prvkov kedy sa ma vytvraraé zasobn’k
	 * @param minLeafs min po‹et prvkov
	 */
	public void setMinLeafsToStack(int minLeafs) {
		minLeafsToStack = minLeafs;
		resetLayoutPositions();
		visualization.layoutGraph(minLeafsToStack, leftRouting);
	}

	/**
	 * vrati vizualizaciu do p™vodnŽho stavu
	 */
	private void resetLayoutPositions() {
		for (Map.Entry<Term, GraphCell> entry : GraphData.getInstance().getVertexes().entrySet()) {
			mxIGraphModel model = graph.getModel();
			model.beginUpdate();
			try {
				entry.getValue().reset();
			} finally {
				model.endUpdate();
			}
		}
		GraphData.getInstance().resetIndicators();
	}

}
