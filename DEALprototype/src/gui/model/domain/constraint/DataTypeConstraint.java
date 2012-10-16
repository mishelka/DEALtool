package gui.model.domain.constraint;

public class DataTypeConstraint extends Constraint {
	private DataType dataType = null;
	
	public DataType getType() {
		return dataType;
	}
	
	public void getType(DataType type) {
		this.dataType = type;
	}
	
	@Override
	public String toString() {
		return dataType.toString();
	}
}
