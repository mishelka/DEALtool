package gui.editor.visualization.graph;

import gui.editor.visualization.graph.elements.GraphCell;
import gui.model.domain.Term;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

import com.mxgraph.model.mxCell;

/**
 * Trieda obsahuje väetky œdaje pouìitŽ v grafe
 * @author D‡vid
 *
 */
public class GraphData {

	/**
	 * inätancia
	 */
	private static GraphData instance;

	/**
	 * tabu¼ka mapuje term na graph cell objekt
	 */
	private Hashtable<Term, GraphCell> vertexes;
	/**
	 * tabu¼ka indik‡torov
	 */
	private Hashtable<GraphCell, GraphCell> indicators;

	/**
	 * konätruktor, inicializ‡cia tabuliek
	 */
	private GraphData() {
		vertexes = new Hashtable<Term, GraphCell>();
		indicators = new Hashtable<GraphCell, GraphCell>();
	}

	/**
	 * Singleton
	 * @return inätancia triedy
	 */
	public static GraphData getInstance() {
		if (instance == null) {
			instance = new GraphData();
		}
		return instance;
	}

	/**
	 * vloì’ z‡znam do tabu¼ky uzlov
	 * @param key term
	 * @param value objekt triedy graphCell
	 */
	public void addVertex(Term key, GraphCell value) {
		vertexes.put(key, value);
	}

	/**
	 * vloì’ z‡znam do tabu¼ky indik‡torov
	 * @param key uzol ku ktorŽmu indik‡tor patrù
	 * @param value indik‡tor
	 */
	public void addIndicator(GraphCell key, GraphCell value) {
		indicators.put(key, value);
	}

	/**
	 * 
	 * @return tabu¼ka uzlov
	 */
	public Hashtable<Term, GraphCell> getVertexes() {
		return vertexes;
	}

	/**
	 * 
	 * @return tabu¼ka indik‡torv
	 */
	public Hashtable<GraphCell, GraphCell> getIndicators() {
		return indicators;
	}

	/**
	 * zist’ ‹i uzol m‡ indik‡tor
	 * @param cell uzol
	 * @return true ak m‡, inak false
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
	 * @return indik‡tor pre zvolenù uzol
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
	 * @return GraphCell objekto pod¼a mxCell
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
	 * @return term na z‡klade GraphCell
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
	 * @return term pod¼a mxCell objektu
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
	 * vymaìe tabu¼ku indik‡torov
	 */
	public void resetIndicators(){
		if(indicators.size() > 0){
			indicators.clear();
		}
	}
	
}