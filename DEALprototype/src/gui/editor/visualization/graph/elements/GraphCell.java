package gui.editor.visualization.graph.elements;

import com.mxgraph.model.mxCell;

/**
 * òdajov‡ bunka grafu vizualiz‡ce
 * @author D‡vid
 *
 */
public class GraphCell {
	/**
	 * je indik‡tor
	 */
	private boolean indicator = false, hidden;
	/**
	 * objekty uzla a hrany
	 */
	private mxCell cell, edge;
	/**
	 * z‡sobn’k v ktorom sa uzol nach‡dza
	 */
	private Stack stack;

	/**
	 * konätruktor
	 * @param cell uzol
	 * @param edge hrana
	 */
	public GraphCell(mxCell cell, mxCell edge) {
		this.cell = cell;
		this.edge = edge;
	}
	
	/**
	 * konätruktor
	 * @param cell uzol
	 */
	public GraphCell(mxCell cell) {
		this(cell, null);
	}

	/**
	 * 
	 * @return je indik‡tor
	 */
	public boolean isIndicator() {
		return indicator;
	}

	/**
	 * nastavenie indik‡tora
	 * @param indicator true ak ano, inak false
	 */
	public void setIndicator(boolean indicator) {
		this.indicator = indicator;
	}

	/**
	 * skrytie uzla
	 * @param hidden true ak ‡no, inak nie
	 */
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	/**
	 * 
	 * @return je skrytù
	 */
	public boolean isHidden() {
		return hidden;
	}

	/**
	 * 
	 * @return objekt uzla mxCell
	 */
	public mxCell getCell() {
		return cell;
	}

	/**
	 * nastavenie uzla
	 * @param cell uzol
	 */
	public void setCell(mxCell cell) {
		this.cell = cell;
	}

	/**
	 * 
	 * @return hrana
	 */
	public mxCell getEdge() {
		return edge;
	}

	/**
	 * nastavenie hrany
	 * @param edge hrana
	 */
	public void setEdge(mxCell edge) {
		this.edge = edge;
	}
	
	/**
	 * 
	 * @return true ak m‡ hranu inak false
	 */
	public boolean hasEdge(){
		return edge != null ? true : false; 
	}
	
	/**
	 * nastavenie z‡sobn’ka
	 * @param stack z‡sobn’k
	 */
	public void setStack(Stack stack){
		this.stack = stack;
	}
	
	/**
	 * 
	 * @return true ak sa nach‡dza v z‡sobn’ku, inak false
	 */
	public boolean isInStack(){
		return stack != null ? true : false;
	}
	
	/**
	 * 
	 * @return objekt z‡sobn’ka
	 */
	public Stack getStack(){
		return stack;
	}
	
	/**
	 * vr‡ti nastavenia uzla do p™vodnŽho stavu
	 */
	public void reset(){
		stack = null;
		if(cell != null){
			cell.getGeometry().setX(0);
			cell.getGeometry().setY(0);
		}
		if(hasEdge()){
			edge.getGeometry().setPoints(null);
			edge.setVisible(true);
		}
	}
}
