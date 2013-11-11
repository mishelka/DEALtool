package gui.analyzer.util;

import gui.editor.DomainModelEditor;

import java.awt.Component;
import java.awt.Window;
import java.util.List;

import javax.swing.JLabel;

/**
 * Najde komponentovu cestu v komponentovom strome pre komponent Najde Label
 * komponent podla atributu labelFor (ak existuje) Zisti, ci existuje Label
 * komponent podla atributu labelFor
 */
public class JLabelFinder {
	/**
	 * Ziska objekt typu JLabel s hodnotou labelFor odkazujucou sa na component.
	 * 
	 * @param component
	 *            komponent, pre ktora sa ma najst komponent JLabel.
	 * @return najdena objekt typu JLabel pre component, ak sa nenajde, tak
	 *         null.
	 */
	public static JLabel findLabelFor(Component component) {
		// TODO: important this was not working, if scenes are added into the
		// Application class
		// before creating the model, the GUI will be empty
		// This is a workaround, each new window is added into the
		// DomainModelEditor windows list.
		// List<Scene<?>> scenes = new ArrayList<Scene<?>>();
		// for(DomainModel dm : DomainModelEditor.getDomainModels()) {
		// scenes.add(dm.getScene());
		// }

		List<Window> windows = DomainModelEditor.getInstance()
				.getApplicationWindows();
		if (component == null) {
			return null;
		}

		for (Window w : windows) {
			if (w != null) {
				List<Component> list = ComponentFinder.getInstance().toComponentList(w);
				
				for (Component comp : list) {
					if (comp instanceof JLabel) {
						JLabel label = (JLabel) comp;
						Component lf = label.getLabelFor();

						if (component.equals(lf)) {
							return label;
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * Finds out, if there is a JLabel component for the given component (the
	 * labelFor value in the JLabel refers to this component).
	 * 
	 * @param labelFor
	 *            the text of the JLabel component to be found
	 * @param component
	 *            the component, for which the JLabel component should be found
	 * @return true, if a JLabel exists for this component, false otherwise
	 */
	public static boolean existsComponentByLabelFor(String labelFor,
			Component component) {
		JLabel label = findLabelFor(component);
		if(label != null && label.getText().equalsIgnoreCase(labelFor)) {
			return true;
		}
		
		return false;
	}
}