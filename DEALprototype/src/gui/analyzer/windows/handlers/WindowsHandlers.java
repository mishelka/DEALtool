package gui.analyzer.windows.handlers;

import gui.analyzer.windows.handlers.impl.WindowsButtonHandler;
import gui.analyzer.windows.handlers.impl.WindowsCheckBoxHandler;
import gui.analyzer.windows.handlers.impl.WindowsComboBoxHandler;
import gui.analyzer.windows.handlers.impl.WindowsContainerHandler;
import gui.analyzer.windows.handlers.impl.WindowsLabelHandler;
import gui.analyzer.windows.handlers.impl.WindowsListItemHandler;
import gui.analyzer.windows.handlers.impl.WindowsMenuBarHandler;
import gui.analyzer.windows.handlers.impl.WindowsMenuItemHandler;
import gui.analyzer.windows.handlers.impl.WindowsRadioButtonHandler;
import gui.analyzer.windows.handlers.impl.WindowsTabPageHandler;
import gui.analyzer.windows.handlers.impl.WindowsTableCellHandler;
import gui.analyzer.windows.handlers.impl.WindowsTextComponentHandler;
import gui.analyzer.windows.handlers.impl.WindowsTextHandler;
import gui.analyzer.windows.handlers.impl.WindowsTreeItemHandler;

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
		windowsHandlers.add(WindowsButtonHandler.getInstance());
		windowsHandlers.add(WindowsComboBoxHandler.getInstance());
		windowsHandlers.add(WindowsContainerHandler.getInstance());
		windowsHandlers.add(WindowsCheckBoxHandler.getInstance());
		windowsHandlers.add(WindowsLabelHandler.getInstance());
		windowsHandlers.add(WindowsMenuBarHandler.getInstance());
		windowsHandlers.add(WindowsRadioButtonHandler.getInstance());
		windowsHandlers.add(WindowsTreeItemHandler.getInstance());
		windowsHandlers.add(WindowsListItemHandler.getInstance());
		windowsHandlers.add(WindowsTableCellHandler.getInstance());
		windowsHandlers.add(WindowsTabPageHandler.getInstance());
		windowsHandlers.add(WindowsTextHandler.getInstance());
		
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
