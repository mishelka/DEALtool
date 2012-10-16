package gui.analyzer.handlers;

import gui.analyzer.ProcessingException;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Sluzi na priradenie spravneho ovladaca prehravania pre triedu kontajnera.
 * Trieda Composites je impelentovana podla vzoru singleton.
 */
public class Composites {

	/** Nazov suboru property. */
	public static final String PROPERTIES_FILE = "composite.properties";
	/** Cesta k adresraru obsahujucemu ovladace. */
	public static final String PROPERTIES_FILE_PROPERTY = "gui.composites";
	/** Hash mapa obsahujuca dvojice: trieda kontanjera a ovladac pre nu. */
	private Map<Class<?>, Composite<?>> composites = new HashMap<Class<?>, Composite<?>>();
	/** Instancia triedy Composites. */
	private static Composites instance;

	/**
	 * Konstruktor. nacita property subor do pamate a vytvori podla neho hash
	 * mapu.
	 */
	private Composites() {
		Properties properties = new Properties();
		try {
			String propertiesFile = System.getProperty(
					PROPERTIES_FILE_PROPERTY, PROPERTIES_FILE);
			properties.load(getClass().getResourceAsStream(propertiesFile));

			for (String className : properties.stringPropertyNames()) {
				Class<?> componentClass = Class.forName(className);
				@SuppressWarnings("unchecked")
				Class<? extends Composite<?>> compositeClass = (Class<Composite<?>>) Class
						.forName(properties.getProperty(className));
				composites.put(componentClass, compositeClass.getConstructor()
						.newInstance());
			}
		} catch (Exception e) {
			e.printStackTrace();
			new ProcessingException("Cannot load " + PROPERTIES_FILE, e);
		}
	}

	/**
	 * @return Instancia triedy Composites.
	 */
	public static Composites getInstance() {
		if (instance == null) {
			instance = new Composites();
		}
		return instance;
	}

	/**
	 * Na zaklade zadanej triedy kontajnera vrati pre nu ovladac.
	 * 
	 * @param <T>
	 *            Trieda komponentu.
	 * @param componentClass
	 *            Trieda komponentu.
	 * @return Ovladac pre zadanu triedu komponentu.
	 */
	public <T> Composite<? super T> getComposite(Class<T> componentClass) {
		Class<? super T> componentClassCurrent = componentClass;
		while (componentClassCurrent != null) {
			@SuppressWarnings("unchecked")
			Composite<? super T> composite = (Composite<? super T>) composites
					.get(componentClassCurrent);
			if (composite != null) {
				return composite;
			}
			componentClassCurrent = componentClassCurrent.getSuperclass();
		}
		return null;
	}
}
