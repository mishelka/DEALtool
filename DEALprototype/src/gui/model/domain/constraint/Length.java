package gui.model.domain.constraint;

/**
 * Defines the maximum or minimum length of a string data type term.
 * 
 * @author Michaela Bacikova, Slovakia,
 * michaela.bacikova@tuke.sk
 */
public class Length extends Constraint {
	/** Minimum length of a string data type term. */
    private final int minLength;

    /** Maximum length of a string data type term. */
    private final int maxLength;

    /**
     * @param minLength minimum length of a string data type term. 
     * @param maxLength maximum length of a string data type term. 
     */
    public Length(int minLength, int maxLength) {
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    /**
     * @return maximum length of a string data type term. 
     */
    public int getMaxLength() {
        return maxLength;
    }

    /**
     * @return minimum length of a string data type term. 
     */
    public int getMinLength() {
        return minLength;
    }

    @Override
    public String toString() {
        return "length minLength=" + minLength + " maxLength=" + maxLength;
    }
}
