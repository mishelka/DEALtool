package gui.analyzer.handlers;

import gui.analyzer.ProcessingException;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class CommandHandlers {
	public static final String PROPERTIES_FILE = "handlers.properties";
	public static final String PROPERTIES_FILE_PROPERTY = "gui.handlers";
	private Map<Class<?>, CommandHandler<?>> handlers = new HashMap<Class<?>, CommandHandler<?>>();
	private static CommandHandlers instance;

	private CommandHandlers() {
		Properties properties = new Properties();
		try {
			String propertiesFile = System.getProperty(
					PROPERTIES_FILE_PROPERTY, PROPERTIES_FILE);
			properties.load(getClass().getResourceAsStream(propertiesFile));

			for (String className : properties.stringPropertyNames()) {
				Class<?> componentClass = Class.forName(className);
				@SuppressWarnings("unchecked")
				Class<? extends CommandHandler<?>> commandHandlerClass = (Class<? extends CommandHandler<?>>) Class
						.forName(properties.getProperty(className));
				handlers.put(componentClass, commandHandlerClass.newInstance());
			}
		} catch (Exception e) {
			e.printStackTrace();
			new ProcessingException("Cannot load " + PROPERTIES_FILE, e);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> CommandHandler<T> getHandler(Class<T> componentClass) {
		return (CommandHandler<T>) handlers.get(componentClass);
	}

	public static CommandHandlers getInstance() {
		if (instance == null) {
			instance = new CommandHandlers();
		}
		return instance;
	}
}
