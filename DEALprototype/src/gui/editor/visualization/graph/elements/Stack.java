package gui.editor.visualization.graph.elements;

import gui.editor.visualization.graph.Constants;

import java.util.ArrayList;

import com.mxgraph.model.mxCell;

/**
 * Z‡sobn’k
 * @author D‡vid
 *
 */
public class Stack {

	/**
	 * prednastavenŽ maxim‡lne mnoìstvo uzlov v z‡sobn’ku
	 */
	public static int DEFAULT_MAX_ELEMENTS = 5;
	/**
	 * list uzlov
	 */
	private ArrayList<mxCell> cells;
	/**
	 * poz’cia a rozmery
	 */
	private double x, y, width, height;
	/**
	 * vlastnŽ maxim‡lne mnoìstvo uzlov v z‡sobn’ku
	 */
	private boolean otherMaxElements;
	/**
	 * maxim‡lne mnoìstvo uzlov v z‡sobn’ku
	 */
	private int maxElements;

	/**
	 * konätruktor
	 * @param x poz’cia x
	 * @param y poz’cia y
	 * @param otherMaxElements maxim‡lne mnoìstvo uzlov v z‡sobn’ku
	 */
	public Stack(double x, double y, boolean otherMaxElements) {
		cells = new ArrayList<mxCell>();
		this.x = x;
		this.y = y;
		this.otherMaxElements = otherMaxElements;
	}

	/**
	 * prid‡ uzol do z‡sobn’ka
	 * @param cell uzol
	 * @return je vloìenù
	 */
	public boolean addCell(mxCell cell) {
		int size = 0;
		if(otherMaxElements)
			size = maxElements;
		else 
			size = DEFAULT_MAX_ELEMENTS;
		if (cells.size() < size) {
			cell.getGeometry().setX(x);
			cell.getGeometry().setY(cells.size() * Constants.VERTEX_HEIGHT + y);

			cells.add(cell);
			width = Constants.VERTEX_WIDTH;
			height = cells.size() * Constants.VERTEX_HEIGHT;
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @return je z‡sobn’k plnù
	 */
	public boolean isFull(){
		int size = 0;
		if(otherMaxElements)
			size = maxElements;
		else 
			size = DEFAULT_MAX_ELEMENTS;
		return (cells.size() < size ? false : true);
	}
	
	/**
	 * 
	 * @param cell uzol
	 * @return poz’ciu uzla v z‡sobn’ku
	 */
	public int getCellPosition(mxCell cell){
		for(int i=0;i<cells.size();i++){
			if(cell == cells.get(i)){
				return i;
			}
		}
		return 0;
	}

	/**
	 * 
	 * @param pos poz’cia
	 * @return uzol ktorù sa nach‡dza na zvolenej poz’ci’
	 */
	public mxCell getCellAt(int pos) {
		// pozor na null pointer
		return cells.get(pos);
	}

	/**
	 * nastav’ x sœradn’cu
	 * @param x x sœradn’ca
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * nastav’ y sœradn’cu
	 * @param y y sœradn’ca
	 */
	public void setY(double y) {
		this.y = y;
	}

	/**
	 * 
	 * @return x sœradn’ca
	 */
	public double getX() {
		return this.x;
	}

	/**
	 * 
	 * @return y sœradn’ca
	 */
	public double getY() {
		return this.y;
	}

	/**
	 * 
	 * @return ä’rka z‡sobn’ka v px
	 */
	public double getWidth() {
		return this.width;
	}

	/**
	 * 
	 * @return vùäka z‡sobn’ka v px
	 */
	public double getHeight() {
		return this.height;
	}
	
	/**
	 * nastav’ maxim‡lny po‹et prvkov v z‡sobn’ku
	 * @param val hodnota
	 */
	public void setMaxElements(int val){
		maxElements = val;
	}

}
