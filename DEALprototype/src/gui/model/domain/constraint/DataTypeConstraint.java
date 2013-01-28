package gui.model.domain.constraint;

public class DataTypeConstraint extends Constraint {
	private DataType dataType = null;
	
	public DataTypeConstraint(DataType dataType) {
		this.dataType = dataType;
	}
	
	public DataType getType() {
		return dataType;
	}
	
	public void setType(DataType dataType) {
		this.dataType = dataType;
	}
	
	@Override
	public String toString() {
		return dataType.toString();
	}
}
