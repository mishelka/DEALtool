package gui.analyzer.handlers;


/**
 * Predstavujúce všetky grafické komponenty typu kontajner.
 * 
 * @param <T>
 *            Trieda kontajnera.
 */
public interface Composite<T> {
	/**
	 * Získa všetky komponenty, ktoré obsahuje kontajner.
	 * 
	 * @param container
	 *            Inštancia kontajnera.
	 * @return všetky komponenty, ktoré obsahuje kontajner.
	 */
	public Object[] getComponents(T container);
}
