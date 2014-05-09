package gui.editor.visualization.graph;

import gui.editor.visualization.graph.elements.GraphCell;
import gui.model.domain.Term;

import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

/**
 * Trieda rozäiruje zakladny mxGraph komponent kniznice
 * @author David
 *
 */
public class DEALGraph extends mxGraph {

	/**
	 * konätruktor
	 * @param stylesheet ätùl
	 */
	public DEALGraph(mxStylesheet stylesheet) {
		super(stylesheet);
	}

	/**
	 * vracia tooltip pre zvolenu bunku
	 */
	@Override
	public String getToolTipForCell(Object cell) {
		mxCell mxCell = (mxCell) cell;
		GraphCell gCell = GraphData.getInstance().getCellbyMxCell(mxCell);
		Term term = GraphData.getInstance().getTermByCell(gCell);
		String retValue = "";
		try {
			if (!gCell.isIndicator()) {
				retValue = "<html><b>" + (term.getName() != null ? term.getName() : "") + "</b><br>" + (term.getDescription() != null ? term.getDescription() : "") + "</html>";
			} 
		} catch (NullPointerException e) {
			// ak to nie je cell ale edge alebo indikator co sa nenachadza v
			// tabulke
			return retValue;
		}
		return retValue;
	}
	
}
