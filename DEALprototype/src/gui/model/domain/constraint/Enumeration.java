package gui.model.domain.constraint;

import java.util.Arrays;

public class Enumeration extends Constraint {
	private String[] values = new String[]{};
	
	public String[] getValues() {
		return values;
	}
	
	public void setValues(String[] values) {
		this.values = values;
	}
	
	@Override
	public String toString() {
		return Arrays.toString(values);
	}
}
