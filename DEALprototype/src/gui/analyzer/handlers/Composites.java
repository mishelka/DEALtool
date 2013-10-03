package gui.analyzer.handlers;

import gui.analyzer.ProcessingException;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Asigns the right handler for the given container class.
 * Composites class is implemented according to the singleton pattern.
 */
public class Composites {

	/** The name of the property file. */
	public static final String PROPERTIES_FILE = "composite.properties";
	/** Path to the directory, where the handlers are stored. */
	public static final String PROPERTIES_FILE_PROPERTY = "gui.composites";
	/** Hash map with touples: container class and a handler for it. */
	private Map<Class<?>, Composite<?>> composites = new HashMap<Class<?>, Composite<?>>();
	/** Composites class instance. */
	private static Composites instance;

	/**
	 * Constructor. Loads the property file into memory and creates a the composites hash map.
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
	 * @return Composites class instance.
	 */
	public static Composites getInstance() {
		if (instance == null) {
			instance = new Composites();
		}
		return instance;
	}

	/**
	 * Based on the given container class, returns a hander for it.
	 * 
	 * @param <T>
	 *            Container class.
	 * @param componentClass
	 *            Container class.
	 * @return Handler for the given container class.
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
