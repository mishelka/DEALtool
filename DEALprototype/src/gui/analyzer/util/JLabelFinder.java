package gui.analyzer.util;

import gui.editor.DomainModelEditor;
import gui.model.application.Scene;
import gui.model.application.WindowScene;

import java.awt.Component;
import java.awt.Window;
import java.util.List;

import javax.swing.JLabel;
//
//import gui.model.application.Scene;
//import gui.model.application.WindowScene;
//import gui.ui.DomainModelEditor;
//
//import java.awt.Component;
//import java.awt.Window;
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.swing.JLabel;

/**
 * Najde komponentovu cestu v komponentovom strome pre komponent Najde Label
 * komponent podla atributu labelFor (ak existuje) Zisti, ci existuje Label
 * komponent podla atributu labelFor
 */
public class JLabelFinder {
//	private static PathFinder instance;
//
//	public static PathFinder getInstance() {
//		if (instance == null)
//			instance = new PathFinder();
//		return instance;
//	}

	// /** Oddelovac jednotlivach poloziek komponentovej cesty. */
	// protected static final String PATH_SEPARATOR = "/";
	//
	// /**
	// * Najde komponentovu cestu zadaneho komponentu.
	// *
	// * @param component
	// * komponent, pre ktora sa ma najst komponentova cesta.
	// * @return retazec komponentovej cesty pre komponent.
	// */
	// public String findPath(Component component) {
	// String stringPath = "";
	// ComponentFinder componentFinder = ComponentFinder.getInstance();
	// Scene<?> scene = componentFinder.getSceneContainingComponent(
	// DomainModelEditor.getWindows(), component);
	// if (window != null) {
	// if (window.getName() == null || window.getName().equals(""))
	// return null;
	// stringPath = window.getName();
	// ArrayList<Integer> path = componentFinder.getPathForComponent(
	// window, component);
	// if (path != null && path.size() != 0 && !path.contains(-1)) {
	// if (path.size() > 1) {
	// stringPath = stringPath + PATH_SEPARATOR;
	// for (int i = 1; i < path.size(); i++) {
	// stringPath = stringPath + path.get(i);
	// if (i != path.size() - 1) {
	// stringPath = stringPath + PATH_SEPARATOR;
	// }
	// }
	// }
	// return stringPath;
	// }
	// }
	// return null;
	// }
	//
	// /**
	// * Zisti, ci pre dana komponent existuje komponent typu JLabel s hodnotou
	// * labelFor odkazujucou na tento komponent.
	// *
	// * @param labelFor
	// * popis komponentu JLabel, ktora sa ma najst.
	// * @param component
	// * komponent, na ktora sa ma odkazovat atribut labelFor
	// * komponentu JLabel.
	// * @return Hodnota true ak existuje komponent typu JLabel s hodnotou
	// * labelFor odkazujucou sa na component, false inak.
	// */
	// public boolean existsComponentByLabelFor(String labelFor,
	// Component component) {
	// List<Scene> scenes = DomainModelEditor.getApplication().getScenes();
	// for (Scene s : scenes) {
	// if (s instanceof WindowScene) {
	// Window window = ((WindowScene) s).getSceneContainer();
	// List<Component> list = ComponentFinder.getInstance()
	// .windowToComponentsList(window);
	// for (Component comp : list) {
	// if (comp instanceof JLabel) {
	// final JLabel label = (JLabel) comp;
	// if (label.getText().equalsIgnoreCase(labelFor)) {
	// Component lf = label.getLabelFor();
	// if (lf != null && lf.equals(component)) {
	// return true;
	// }
	// }
	// }
	// }
	// }
	// }
	// return false;
	// }
	//
	/**
	 * Ziska objekt typu JLabel s hodnotou labelFor odkazujucou sa na component.
	 * 
	 * @param component
	 *            komponent, pre ktora sa ma najst komponent JLabel.
	 * @return najdena objekt typu JLabel pre component, ak sa nenajde, tak
	 *         null.
	 */
	public static JLabel findLabelFor(Component component) {
		List<Scene<?>> scenes = DomainModelEditor.getApplication().getScenes();

		if (component == null)
			return null;

		// in each scene try to find a JLabel for this component
		for (Scene<?> s : scenes) {
			Component sceneComponent = ComponentFinder.getInstance()
					.getSceneContainerAsComponent(s);

			if (sceneComponent != null) {
				List<Component> list = ComponentFinder.getInstance()
						.componentToComponentsList(sceneComponent);
				for (Component comp : list) {
					if (comp instanceof JLabel) {
						JLabel label = (JLabel) comp;
						Component lf = label.getLabelFor();
						if (component.equals(lf))
							return label;
					}
				}
			}
		}
		return null;
	}
}