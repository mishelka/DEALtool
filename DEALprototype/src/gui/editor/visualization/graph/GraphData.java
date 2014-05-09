package gui.editor.visualization.graph;

import gui.editor.visualization.graph.elements.GraphCell;
import gui.model.domain.Term;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

import com.mxgraph.model.mxCell;

/**
 * Trieda obsahuje v�etky �daje pou�it� v grafe
 * @author D�vid
 *
 */
public class GraphData {

	/**
	 * in�tancia
	 */
	private static GraphData instance;

	/**
	 * tabu�ka mapuje term na graph cell objekt
	 */
	private Hashtable<Term, GraphCell> vertexes;
	/**
	 * tabu�ka indik�torov
	 */
	private Hashtable<GraphCell, GraphCell> indicators;

	/**
	 * kon�truktor, inicializ�cia tabuliek
	 */
	private GraphData() {
		vertexes = new Hashtable<Term, GraphCell>();
		indicators = new Hashtable<GraphCell, GraphCell>();
	}

	/**
	 * Singleton
	 * @return in�tancia triedy
	 */
	public static GraphData getInstance() {
		if (instance == null) {
			instance = new GraphData();
		}
		return instance;
	}

	/**
	 * vlo� z�znam do tabu�ky uzlov
	 * @param key term
	 * @param value objekt triedy graphCell
	 */
	public void addVertex(Term key, GraphCell value) {
		vertexes.put(key, value);
	}

	/**
	 * vlo� z�znam do tabu�ky indik�torov
	 * @param key uzol ku ktor�mu indik�tor patr�
	 * @param value indik�tor
	 */
	public void addIndicator(GraphCell key, GraphCell value) {
		indicators.put(key, value);
	}

	/**
	 * 
	 * @return tabu�ka uzlov
	 */
	public Hashtable<Term, GraphCell> getVertexes() {
		return vertexes;
	}

	/**
	 * 
	 * @return tabu�ka indik�torv
	 */
	public Hashtable<GraphCell, GraphCell> getIndicators() {
		return indicators;
	}

	/**
	 * zist� �i uzol m� indik�tor
	 * @param cell uzol
	 * @return true ak m�, inak false
	 */
	public boolean hasIndicator(GraphCell cell) {
		Enumeration<GraphCell> enumKey = indicators.keys();
		while (enumKey.hasMoreElements()) {
			if (enumKey.nextElement() == cell)
				return true;
		}
		return false;
	}

	/**
	 *
	 * @param cell uzol
	 * @return indik�tor pre zvolen� uzol
	 */
	public GraphCell getCellIndicator(GraphCell cell) {
		Enumeration<GraphCell> enumKey = indicators.keys();
		while (enumKey.hasMoreElements()) {
			if (enumKey.nextElement() == cell)
				return indicators.get(cell);
		}
		return null;
	}

	/**
	 * 
	 * @param cell mxCell objekt
	 * @return GraphCell objekto pod�a mxCell
	 */
	public GraphCell getCellbyMxCell(mxCell cell) {
		for (Map.Entry<Term, GraphCell> entry : vertexes.entrySet()) {
			if (entry.getValue().getCell() == cell) {
				return entry.getValue();
			}
		}
		return null;
	}

	/**
	 * 
	 * @param cell GraphCell objekt
	 * @return term na z�klade GraphCell
	 */
	public Term getTermByCell(GraphCell cell) {
		for (Map.Entry<Term, GraphCell> entry : vertexes.entrySet()) {
			if (entry.getValue() == cell) {
				return entry.getKey();
			}
		}
		return null;
	}

	/**
	 * 
	 * @param cell mxCell objekt
	 * @return term pod�a mxCell objektu
	 */
	public Term getTermByMxCell(mxCell cell) {
		for (Map.Entry<Term, GraphCell> entry : vertexes.entrySet()) {
			if (entry.getValue().getCell() == cell) {
				return entry.getKey();
			}
		}
		return null;
	}
	
	/**
	 * vyma�e tabu�ku indik�torov
	 */
	public void resetIndicators(){
		if(indicators.size() > 0){
			indicators.clear();
		}
	}
	
}