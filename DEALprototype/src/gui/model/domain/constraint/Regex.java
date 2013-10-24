package gui.model.domain.constraint;

/**
 * Restricts the possible values of the term with a regular expression (regex).
 * 
 * @author Michaela Bacikova, Slovakia,
 * michaela.bacikova@tuke.sk
 */
public class Regex extends Constraint {
	/** regular expression restricting the possible values of the term, which has the constraint */
    private final String regex;

    /**
     * @param regex regular expression restricting the possible values of the term, which has the constraint
     */
    public Regex(String regex) {
        this.regex = regex;
    }

    /**
     * @return regular expression restricting the possible values of the term, which has the constraint
     */
    public String getRegex() {
        return regex;
    }

    @Override
    public String toString() {
        return "regex regex=" + regex;
    }
}
