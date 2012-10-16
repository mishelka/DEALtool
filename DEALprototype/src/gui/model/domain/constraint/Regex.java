package gui.model.domain.constraint;

public class Regex extends Constraint {
    private final String regex;

    public Regex(String regex) {
        this.regex = regex.substring(1, regex.length() - 1);
    }

    public String getRegex() {
        return regex;
    }

    @Override
    public String toString() {
        return "regex regex=" + regex;
    }
}
