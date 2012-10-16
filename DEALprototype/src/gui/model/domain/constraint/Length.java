package gui.model.domain.constraint;

public class Length extends Constraint {
    private final int minLength;

    private final int maxLength;

    public Length(int minLength, int maxLength) {
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public int getMinLength() {
        return minLength;
    }

    @Override
    public String toString() {
        return "length minLength=" + minLength + " maxLength=" + maxLength;
    }
}
