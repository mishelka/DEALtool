package gui.analyzer.windows.handlers;

import gui.analyzer.windows.handlers.impl.WindowsMenuItemHandler;
import gui.analyzer.windows.handlers.impl.WindowsTextComponentHandler;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

public class WindowsHandlers {
	private List<AbstractWindowsHandler> windowsHandlers = new ArrayList<AbstractWindowsHandler>();
	private AbstractWindowsHandler defaultHandler = DefaultWindowsHandler.getInstance();
	
	private static WindowsHandlers instance;
	
	//TODO: Peter ak bude novy handler, tu doplnit
	public WindowsHandlers() {
		windowsHandlers.add(WindowsMenuItemHandler.getInstance());
		windowsHandlers.add(WindowsTextComponentHandler.getInstance());
		//TODO: add new handlers into the htmlHandlers list, otherwise they won't work
		//windowsHandlers.add(...);
		//windowsHandlers.add(...);
		//windowsHandlers.add(...);
		//...
	}
	
	public AbstractWindowsHandler getWindowsHandler(Element element) {
		for (AbstractWindowsHandler windowsHandler : windowsHandlers) {
			if (windowsHandler.matches(element)) {
				return windowsHandler;
			}
		}
		
		return defaultHandler;
	}
	
	/**
	 * Singleton pattern.
	 * @return WindowsHandlers class instance.
	 */
	public static WindowsHandlers getInstance() {
		if (instance == null) {
			instance = new WindowsHandlers();
		}
		return instance;
	}
}
