package gui.model.domain.constraint;

import java.util.Arrays;

/**
 * Enumeration constraint is a constraint, which restricts the possible
 * values of the term to a specific list of values.
 * 
 * @author Michaela Bacikova, Slovakia,
 * michaela.bacikova@tuke.sk
 */
public class Enumeration extends Constraint {
	/** The list of possible values of the term, which has the constraint. */
	private String[] values = new String[]{};
	
	/**
	 * @return the list of possible values of the term, which has the constraint. 
	 */
	public String[] getValues() {
		return values;
	}
	
	/**
	 * @param values the list of possible values of the term, which has the constraint. 
	 */
	public void setValues(String[] values) {
		this.values = values;
	}
	
	@Override
	public String toString() {
		return Arrays.toString(values);
	}
}
