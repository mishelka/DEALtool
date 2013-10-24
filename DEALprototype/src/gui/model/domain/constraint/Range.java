package gui.model.domain.constraint;

/**
 * Defines the minimum and maximum value of a numeric data type constraint.
 * 
 * @author Michaela Bacikova, Slovakia,
 * michaela.bacikova@tuke.sk
 */
public class Range extends Constraint {
	/** minimum value of a numeric data type term */
    private final long minValue;

    /** maximum value of a numeric data type term */
    private final long maxValue;

    /**
     * @param minValue minimum value of a numeric data type term. 
     * @param maxValue maximum value of a numeric data type term. 
     */
    public Range(long minValue, long maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    /**
     * @return maximum value of a numeric data type term
     */
    public long getMaxValue() {
        return maxValue;
    }

    /**
     * @return minimum value of a numeric data type term
     */
    public long getMinValue() {
        return minValue;
    }

    @Override
    public String toString() {
        return "range minValue=" + minValue + " maxValue=" + maxValue;
    }
}
