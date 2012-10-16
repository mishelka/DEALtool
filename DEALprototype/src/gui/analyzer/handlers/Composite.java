package gui.analyzer.handlers;


/**
 * Predstavuj�ce v�etky grafick� komponenty typu kontajner.
 * 
 * @param <T>
 *            Trieda kontajnera.
 */
public interface Composite<T> {
	/**
	 * Z�ska v�etky komponenty, ktor� obsahuje kontajner.
	 * 
	 * @param container
	 *            In�tancia kontajnera.
	 * @return v�etky komponenty, ktor� obsahuje kontajner.
	 */
	public Object[] getComponents(T container);
}
