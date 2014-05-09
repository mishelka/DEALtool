package gui.editor.visualization.graph;

import gui.editor.DomainModelEditor;
import gui.editor.visualization.graph.elements.GraphCell;
import gui.editor.visualization.graph.elements.Indicator;
import gui.model.domain.Term;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.List;
import java.util.Map;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.handler.mxCellMarker;

/**
 * Tieda zabezpe‹uje vstup pouì’vate¼a pomocou myäi.
 * @author D‡vid
 *
 */
public class GraphMouseListener extends mxCellMarker implements MouseListener, MouseMotionListener {

	/**
	 * serial versiou UID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * graf komponent
	 */
	private mxGraphComponent graphComponent;
	/**
	 * vizualiza‹nù panel
	 */
	private VisualizationPanel vizPanel;

	/**
	 * typ uzol
	 */
	private static final int TYPE_VERTEX = 0;
	/**
	 * typ hrana
	 */
	private static final int TYPE_EDGE = 1;
	/**
	 * inù typ
	 */
	private static final int TYPE_OTHER = 2;

	/**
	 * kliknut‡ bunka
	 */
	private GraphCell selectedCell;

	/**
	 * konätruktor, inicializ‡cia premennùch
	 * @param graphComponent
	 * @param vizPanel
	 * @param color
	 */
	public GraphMouseListener(mxGraphComponent graphComponent, VisualizationPanel vizPanel, Color color) {
		super(graphComponent, color);
		this.graphComponent = graphComponent;
		this.vizPanel = vizPanel;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 1) {
			handleOneClick(e);
		} else if (e.getClickCount() == 2) {
			handleDoubleClick(e);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		handlePopupMenu(e);
		if (vizPanel.getGraphVisualization().isStateSearch()) {
			vizPanel.getGraphVisualization().endSearchState();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		handlePopupMenu(e);
		reset();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		process(e);
	}

	/**
	 * nastavenie prieh¼adnosti bunky
	 * @param cell cie¼ova bunka
	 * @param hide zapnut/vypnut prieh¼adnost
	 * @param children prieh¼adnŽ aj dcŽrske bunky
	 */
	private void setOpacity(GraphCell cell, boolean hide, boolean children) {
		if (!hide) {
			cell.getCell().setStyle("opacity=100");
			if (cell.hasEdge())
				cell.getEdge().setStyle("opacity=100");
		} else {
			cell.getCell().setStyle("opacity=20");
			if (cell.hasEdge())
				cell.getEdge().setStyle("opacity=20");
		}

		cell.setHidden(hide);

		if (children) {
			Term t = GraphData.getInstance().getTermByCell(cell);
			for (int i = 0; i < t.getChildrenCount(); i++) {
				setOpacity(GraphData.getInstance().getVertexes().get(t.getChildAt(i)), hide, true);
			}
		}
	}

	/**
	 * handler pre kontextovŽ menu na pravŽ kliknutie myäi
	 * @param e mouse event
	 */
	private void handlePopupMenu(MouseEvent e) {
		if (e.isPopupTrigger()) {

			int type = getComponentType(e);

			if (type == TYPE_VERTEX) {
				for (final Map.Entry<Term, GraphCell> entry : GraphData.getInstance().getVertexes().entrySet()) {
					if (entry.getValue() == selectedCell) {
						JPopupMenu menu = new JPopupMenu("Show/Hide");
						JMenuItem item, item2;
						if (entry.getValue().isHidden()) {
							item = new JMenuItem("Show");
							item2 = new JMenuItem("Show All");
						} else {
							item = new JMenuItem("Hide");
							item2 = new JMenuItem("Hide All");
						}
						item.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent e) {
								graphComponent.getGraph().getModel().beginUpdate();
								try {
									setOpacity(entry.getValue(), !entry.getValue().isHidden(), false);

								} finally {
									graphComponent.getGraph().refresh();
									graphComponent.getGraph().getModel().endUpdate();
								}
							}
						});

						item2.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent e) {
								graphComponent.getGraph().getModel().beginUpdate();
								try {
									setOpacity(entry.getValue(), !entry.getValue().isHidden(), true);

								} finally {
									graphComponent.getGraph().refresh();
									graphComponent.getGraph().getModel().endUpdate();
								}

							}
						});
						menu.add(item);
						menu.add(item2);
						menu.show(e.getComponent(), e.getX(), e.getY());
					}
				}
			}

		}
	}

	/**
	 * handler pre dvojklik myäi
	 * @param e mouse event
	 */
	private void handleDoubleClick(MouseEvent e) {
		int type = getComponentType(e);

		if (type == TYPE_VERTEX) {
			for (Map.Entry<Term, GraphCell> entry : GraphData.getInstance().getVertexes().entrySet()) {
				if (entry.getValue() == selectedCell) {
					mxCell firstChild;
					if (entry.getKey().hasChildren()) {
						firstChild = GraphData.getInstance().getVertexes().get(entry.getKey().getChildAt(0)).getCell();
					} else {
						return;
					}
					showHide(entry.getKey().getChildren(), !firstChild.isVisible());
					GraphCell actCell = entry.getValue();
					if (!firstChild.isVisible()) {
						// UKAZ TROJUHOLNIK
						if (GraphData.getInstance().hasIndicator(actCell)) {
							graphComponent.getGraph().getModel().beginUpdate();
							try {
								GraphData.getInstance().getCellIndicator(actCell).getCell().setVisible(true);
							} finally {
								graphComponent.getGraph().refresh();
								graphComponent.getGraph().getModel().endUpdate();
							}
						} else {
							graphComponent.getGraph().getModel().beginUpdate();
							try {
								Indicator indi = new Indicator(entry);
								mxCell indicator = indi.getIndicatorAsCell(graphComponent);
								GraphCell gCell = new GraphCell(indicator);
								gCell.setIndicator(true);
								GraphData.getInstance().addIndicator(actCell, gCell);
							} finally {
								graphComponent.getGraph().refresh();
								graphComponent.getGraph().getModel().endUpdate();
							}
						}
					} else {
						// HIDE TROJUHOLNIK
						graphComponent.getGraph().getModel().beginUpdate();
						try {
							GraphData.getInstance().getCellIndicator(actCell).getCell().setVisible(false);
						} finally {
							graphComponent.getGraph().refresh();
							graphComponent.getGraph().getModel().endUpdate();
						}
					}
				}
			}
		}
	}

	/**
	 * Skrytie/odkrytie dcŽrskych prvkov
	 * @param childrens list prvkov
	 * @return skry - true, odkry - false
	 */
	private void showHide(List<Term> childrens, boolean visibility) {

		graphComponent.getGraph().getModel().beginUpdate();
		try {
			for (Term t : childrens) {
				GraphCell gCell = (GraphCell) GraphData.getInstance().getVertexes().get(t);
				mxCell mxCell = gCell.getCell();
				mxCell.setVisible(visibility);
				// sky vsetky child indikatory
				if (GraphData.getInstance().hasIndicator(gCell)) {
					GraphData.getInstance().getCellIndicator(gCell).getCell().setVisible(false);
				}
				if (t.hasChildren()) {
					showHide(t.getChildren(), visibility);
				}
			}
		} finally {
			graphComponent.getGraph().getModel().endUpdate();
			graphComponent.getGraph().refresh();
		}
	}

	/**
	 * handler pre klik lavŽho tla‹idla myäi
	 * @param e mouse event
	 */
	private void handleOneClick(MouseEvent e) {
		int type = getComponentType(e);

		if (type == TYPE_VERTEX) {
			// update lavy panel
			for (Map.Entry<Term, GraphCell> entry : GraphData.getInstance().getVertexes().entrySet()) {
				if (entry.getValue() == selectedCell) {
					DomainModelEditor.getInstance().showInTrees(entry.getKey());
				}
			}

		} else if (type == TYPE_EDGE) {
			//on edge click do nothing
		}
	}

	/**
	 * 
	 * @param e mouse event
	 * @return typ kliknutŽho komponentu
	 */
	private int getComponentType(MouseEvent e) {

		Object cell = graphComponent.getCellAt(e.getX(), e.getY());
		if (cell != null && cell instanceof mxCell) {
			if (((mxCell) cell).isVertex()) {
				selectedCell = GraphData.getInstance().getCellbyMxCell((mxCell) cell);
				return TYPE_VERTEX;
			} else if (((mxCell) cell).isEdge()) {
				return TYPE_EDGE;
			} else {
				return TYPE_OTHER;
			}
		}
		return TYPE_OTHER;
	}

}
