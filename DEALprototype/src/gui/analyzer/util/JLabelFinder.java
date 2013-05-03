package gui.analyzer.util;

import gui.editor.DomainModelEditor;
import gui.model.application.scenes.Scene;

import java.awt.Component;
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
		List<Scene<?>> scenes = DomainModelEditor.getApplication().getScenes();

		if (component == null)
			return null;
		
		for (Scene<?> s : scenes) {
			Component sceneComponent = ComponentFinder.getInstance()
					.getSceneContainerAsComponent(s);

			if (sceneComponent != null) {
				List<Component> list = ComponentFinder.getInstance()
						.toComponentList(sceneComponent);
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
}