package gui.analyzer.handlers;


/**
 * Represents all graphical components of type container.
 * 
 * @param <T>
 *            The container class.
 */
public interface Composite<T> {
	/**
	 * Gets components, which are direct children of the container.
	 * 
	 * @param container
	 *            The container instance.
	 * @return components, which are direct children of the container.
	 */
	public Object[] getComponents(T container);
}
