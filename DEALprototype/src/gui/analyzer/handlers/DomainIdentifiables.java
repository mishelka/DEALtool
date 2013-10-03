package gui.analyzer.handlers;

import gui.analyzer.ProcessingException;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Asigns the right DomainIdentifiable handler for the given container class.
 * DomainIdentifiables class is implemented according to the singleton pattern.
 */
public class DomainIdentifiables {

	/** The name of the property file. */
	public static final String PROPERTIES_FILE = "domainIdentifiables.properties";
	/** Path to the directory, where the handlers are stored. */
	public static final String PROPERTIES_FILE_PROPERTY = "gui.domainIdentifiables";
	/** Hash map with touples: container class and a handler for it. */
	private Map<Class<?>, DomainIdentifiable<?>> domainIdentifiables = new HashMap<Class<?>, DomainIdentifiable<?>>();
	/** DomainIdentifiables class instance. */
	private static DomainIdentifiables instance;

	/**
	 * Constructor. Loads the property file into memory and creates a the composites hash map.
	 */
	private DomainIdentifiables() {
		Properties properties = new Properties();
		try {
			String propertiesFile = System.getProperty(
					PROPERTIES_FILE_PROPERTY, PROPERTIES_FILE);
			properties.load(getClass().getResourceAsStream(propertiesFile));

			for (String className : properties.stringPropertyNames()) {
				Class<?> componentClass = Class.forName(className);
				@SuppressWarnings("unchecked")
				Class<? extends DomainIdentifiable<?>> domainIdentifiableClass = (Class<DomainIdentifiable<?>>) Class
						.forName(properties.getProperty(className));
				domainIdentifiables.put(componentClass, domainIdentifiableClass.getConstructor()
						.newInstance());
			}
		} catch (Exception e) {
			e.printStackTrace();
			new ProcessingException("Cannot load " + PROPERTIES_FILE, e);
		}
	}

	/**
	 * @return DomainIdentifiables class instance.
	 */
	public static DomainIdentifiables getInstance() {
		if (instance == null) {
			instance = new DomainIdentifiables();
		}
		return instance;
	}

	/**
	 * Based on the given component class, returns a DomainIdentifiable handler for it.
	 * 
	 * @param <T>
	 *            Component class.
	 * @param componentClass
	 *            Component class.
	 * @return Handler for the given component class.
	 */
	public <T> DomainIdentifiable<? super T> getDomainIdentifiable(Class<T> componentClass) {
		Class<? super T> componentClassCurrent = componentClass;
		while (componentClassCurrent != null) {
			@SuppressWarnings("unchecked")
			DomainIdentifiable<? super T> domainIdentifiable = (DomainIdentifiable<? super T>) domainIdentifiables
					.get(componentClassCurrent);
			if (domainIdentifiable != null) {
				return domainIdentifiable;
			}
			componentClassCurrent = componentClassCurrent.getSuperclass();
		}
		return null;
	}
}
