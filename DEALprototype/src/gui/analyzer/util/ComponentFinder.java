package gui.analyzer.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

/**
 * Ponúka rôzne pomocné metódy pre vyhľadávanie v komponentovom strome
 * - získanie okna, ktoré obsahuje daný komponent
 * - získanie komponentovej cesty pre komponent (využíva sa v PathFinder)
 * - nájdenie komponentu v strome podľa rôznych parametrov
 * - transfromácia komponentového stromu do zoznamu komponentov
 */
public class ComponentFinder {

	/** Inštancia triedy ComponentFinder. */
	private static ComponentFinder instance;

	private ComponentFinder() {
	}

	/**
	 * @return inštancia triedy ComponentFinder
	 */
	public static ComponentFinder getInstance() {
		if (instance == null) {
			instance = new ComponentFinder();
		}
		return instance;
	}

	/**
	 * Vráti inštanciu triedy Window, ktorá obsahuje zadaný komponent.
	 * 
	 * @param windows
	 *            zoznam otvorených okien (inštancii tried Window) spustenej
	 *            aplikácie.
	 * @param component
	 *            komponent, ktorý sa má nájsť.
	 * @return inštancia triedy Window, v ktorom sa nachádza zadaný komponent.
	 */
	public Window getWindowContainingComponent(Window[] windows,
			Component component) {
		for (Window w : windows) {
			ArrayList<Component> components = (ArrayList<Component>) windowToComponentsList(w);
			if (components.contains(component)) {
				return w;
			}
		}
		return null;
	}

	/**
	 * Vráti komponent podľa zadanej komponentovej cesty v danom okne.
	 * 
	 * @param window
	 *            okno, v ktorom sa komponent nachádza.
	 * @param path
	 *            komponentová cesta komponentu.
	 * @return inštancia komponentu, ktorý bol nájdený podľa zadanej
	 *         komponentovej cesty.
	 */
	public Component getComponentFromPath(Window window, ArrayList<Integer> path) {
		Component component = window;
		for (int i : path) {
			if (component instanceof Container) {
				Component temp = ((Container) component).getComponents()[i];
				if (temp != null) {
					component = ((Container) component).getComponents()[i];
				} else
					return null;
			} else
				return null;
		}
		return component;
	}

	/**
	 * Vráti komponentovú cestu zadaného komponentu v zadanom okne, v tvare
	 * zoznamu �?ísel.
	 * 
	 * @param window
	 *            okno, v ktorom sa má komponent nachádzať.
	 * @param component
	 *            komponent, pre ktorého sa má nájsť cesta.
	 * @return komponentová cesta zadaného komponentu v zadanom okne v tvare
	 *         zoznamu �?ísel. V prípade, že window neobsahuje component, vráti
	 *         null.
	 */
	public ArrayList<Integer> getPathForComponent(Window window,
			Component component) {
		ArrayList<Component> allComponents = (ArrayList<Component>) windowToComponentsList(window);
		if (!allComponents.contains(component))
			return null;
		ArrayList<Integer> path = new ArrayList<Integer>();
		path.add(0); // moze to byt aj window
		if (component.equals(window)) {
			return path;
		}
		Component[] components = window.getComponents();
		getPathForComponent(components, path, component);
		return path;
	}

	private void getPathForComponent(Component[] components,
			ArrayList<Integer> path, Component component) {
		for (int i = 0; i < components.length; i++) {
			if (component.equals(components[i])) {
				path.add(i);
				return;
			} else {
				ArrayList<Component> componentsOfComponent = (ArrayList<Component>) componentToComponentsList(components[i]);
				if (componentsOfComponent.contains(component)) {
					path.add(i);
					if (components[i] instanceof Container) {
						getPathForComponent(
								((Container) components[i]).getComponents(),
								path, component);
						return;
					}
				}
			}
		}
		// TODO: neviem ci toto nespadne...
		path.removeAll(path);
		// ak tam bude -1, tak komponent sa nenasiel v okne... teda nema cestu,
		// teda nic sa neda robit, neda sa nahrat ani prehrat....
		path.add(-1);
		return;
	}

	ArrayList<Component> componentList;

	/**
	 * Získa zoznam všetkých komponentov okna.
	 * 
	 * @param window
	 *            okno, pre ktoré chceme získať zoznam komponentov.
	 * @return Zoznam všetkých komponentov okna.
	 */
	public List<Component> windowToComponentsList(Window window) {
		componentList = new ArrayList<Component>();
		Component[] components = window.getComponents();
		componentsToList(components);
		return componentList;
	}

	/**
	 * Získa zoznam všetkých subkomponentov komponentu.
	 * 
	 * @param component
	 *            komponent, ktorého zoznam subkomponentov sa má získať.
	 * @return Zoznam všetkých subkomponentov komponentu component.
	 */
	public List<Component> componentToComponentsList(Component component) {
		componentList = new ArrayList<Component>();
		if (component instanceof Container) {
			Component[] components = ((Container) component).getComponents();
			componentsToList(components);
		}
		return componentList;
	}

	private void componentsToList(Component[] components) {
		for (Component component : components) {
			if (component instanceof Container) {
				Container container = (Container) component;
				if (component instanceof JComponent) {
					componentList.add(component);
				}
				componentsToList(container.getComponents());
			}
		}
	}
}
