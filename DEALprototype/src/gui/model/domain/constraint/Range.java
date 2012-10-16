package gui.model.domain.constraint;

public class Range extends Constraint {
    private final long minValue;

    private final long maxValue;

    public Range(long minValue, long maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public long getMaxValue() {
        return maxValue;
    }

    public long getMinValue() {
        return minValue;
    }

    @Override
    public String toString() {
        return "range minValue=" + minValue + " maxValue=" + maxValue;
    }
}
