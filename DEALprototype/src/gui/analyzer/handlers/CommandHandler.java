package gui.analyzer.handlers;

import gui.model.application.program.Command;

/**
 * @author Michaela Bacikova <michaela.bacikova@tuke.sk>
 *
 * @param <T> The component class type.
 */
public interface CommandHandler<T> {
	/**
	 * @param command
	 *            Command, which sould be executed on the component.
	 * @param component
	 *            Component, on which the command should be executed.
	 * @return Hodnota true, if it is possible to execute the command on the component, false otherwise.
	 */
	public boolean supportsCommand(Command command, T component);

	/**
	 * Executes the command on the component.
	 * 
	 * @param command
	 *            Command, which sould be executed on the component.
	 * @param component
	 *            Component, on which the command should be executed
	 */
	public void execute(Command command, T component);
}
