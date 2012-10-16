package gui.analyzer.handlers;

import gui.analyzer.ProcessingException;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Sluzi na priradenie spravneho ovladaca prehravania pre triedu kontajnera.
 * Trieda DomainIdentifiables je impelentovana podla vzoru singleton.
 */
public class DomainIdentifiables {

	/** Nazov suboru property. */
	public static final String PROPERTIES_FILE = "domainIdentifiables.properties";
	/** Cesta k adresraru obsahujucemu ovladace. */
	public static final String PROPERTIES_FILE_PROPERTY = "gui.domainIdentifiables";
	/** Hash mapa obsahujuca dvojice: trieda kontanjera a ovladac pre nu. */
	private Map<Class<?>, DomainIdentifiable<?>> domainIdentifiables = new HashMap<Class<?>, DomainIdentifiable<?>>();
	/** Instancia triedy DomainIdentifiables. */
	private static DomainIdentifiables instance;

	/**
	 * Konstruktor. nacita property subor do pamate a vytvori podla neho hash
	 * mapu.
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
	 * @return Instancia triedy DomainIdentifiables.
	 */
	public static DomainIdentifiables getInstance() {
		if (instance == null) {
			instance = new DomainIdentifiables();
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
