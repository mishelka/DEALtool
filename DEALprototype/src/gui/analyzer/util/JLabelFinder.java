package gui.analyzer.util;

import gui.editor.DomainModelEditor;

import java.awt.Component;
import java.awt.Window;
import java.util.List;

import javax.swing.JLabel;

/**
 * Sometimes, components do not contain any domain information in them. However, 
 * a <code>JLabel</code> can describe other components. A <code>JLabel</code> has 
 * a <code>labelFor</code> attribute, which refers to the component, which this
 * JLabel is describing.
 */
public class JLabelFinder {
	/**
	 * Finds a <code>JLabel</code> with the 
	 * <code>labelFor</code> referring to the given component.
	 * @param component the component, for which a JLabel should be found
	 * @return a JLabel for the given component, if it exists, null otherwise.
	 */
	public static JLabel findLabelFor(Component component) {
		// TODO: important this was not working, if scenes are added into the
		// Application class
		// before creating the model, the GUI will be empty
		// List<Scene<?>> scenes = new ArrayList<Scene<?>>();
		// for(DomainModel dm : DomainModelEditor.getDomainModels()) {
		// scenes.add(dm.getScene());
		// }
		// This is a workaround, each new window is added into the
		// DomainModelEditor windows list.

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
	 * If there is a <code>JLabel</code> with the given 
	 * <code>labelFor</code> referring to the given component, returns true.
	 * The method is used in the replay function.
	 * @param labelFor the text of the <code>JLabel</code> component to be compared
	 * @param component the component, for which the JLabel component should be found with the given <code>labelFor</code> text.
	 * @return true, if a <code>JLabel</code> exists for the given component and with the given <code>labelFor</code> text, false otherwise.
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