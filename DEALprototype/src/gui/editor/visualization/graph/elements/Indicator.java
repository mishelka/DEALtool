package gui.editor.visualization.graph.elements;

import gui.editor.visualization.graph.Constants;
import gui.model.domain.Term;

import java.util.Map;

import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;

/**
 * Indik‡tor
 * @author D‡vid
 *
 */
public class Indicator {

	/**
	 * bunka ku ktorej sa viaìe
	 */
	private mxCell cell;
	/**
	 * term
	 */
	private Term term;
	/**
	 * poz’cia a rozmery
	 */
	private double posX, posY, width, heigth;
	

	/**
	 * konätruktor
	 * @param entry vstupnŽ œdaje
	 */
	public Indicator(Map.Entry<Term, GraphCell> entry) {
		cell = (mxCell) entry.getValue().getCell();
		term = entry.getKey();
		posX = cell.getGeometry().getCenterX();
		posY = cell.getGeometry().getY() + cell.getGeometry().getHeight();
		calculateWidthHeight(term, 1);
		heigth = (Constants.VERTEX_HEIGHT / 3) * heigth;
		width = (Constants.VERTEX_WIDTH / 4) + (width * 2);
	}

	/**
	 * vypo‹’ta ä’rku a vùäku trojuholn’ka na z‡klade vstupnùch œdajov
	 * @param term rodi‹ovskù term
	 * @param th vùäka
	 */
	private void calculateWidthHeight(Term term, double th) {
		double tempWidth = 0, tempHeight = th;

		if (tempHeight > heigth)
			heigth = tempHeight;

		tempWidth = term.getChildrenCount();
		if (tempWidth > width)
			width = tempWidth;
		for (Term t : term.getChildren()) {
			if (t.hasChildren()) {
				tempHeight = tempHeight + 1;
				calculateWidthHeight(t, tempHeight);
			}
		}
	}

	/**
	 * 
	 * @param graphComponent graf kompnenent
	 * @return vr‡ti indik‡tor ako bunku typu mxCell
	 */
	public mxCell getIndicatorAsCell(mxGraphComponent graphComponent) {
		return (mxCell) graphComponent.getGraph().insertVertex(graphComponent.getGraph().getDefaultParent(), "-100", "", posX - width / 2, posY, width, heigth, "shape=" + mxConstants.SHAPE_TRIANGLE + ";direction=" + mxConstants.DIRECTION_NORTH);
	}
}
