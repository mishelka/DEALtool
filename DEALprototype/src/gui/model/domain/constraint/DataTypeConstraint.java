package gui.model.domain.constraint;

/**
 * DataTypeConstraint represents the data type of a term.
 * 
 * @author Michaela Bacikova, Slovakia,
 * michaela.bacikova@tuke.sk
 */
public class DataTypeConstraint extends Constraint {
	/** The data type of the term, which has this constraint. */
	private DataType dataType = null;
	
	/**
	 * @param dataType the data type of the term, which has the constraint
	 */
	public DataTypeConstraint(DataType dataType) {
		this.dataType = dataType;
	}
	
	/**
	 * @return the data type of the term, which has the constraint
	 */
	public DataType getType() {
		return dataType;
	}
	
	/**
	 *  @param dataType the data type of the term, which has the constraint
	 */
	public void setType(DataType dataType) {
		this.dataType = dataType;
	}
	
	@Override
	public String toString() {
		return dataType.toString();
	}
}
