package gui.analyzer;

import gui.editor.DomainModelEditor;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

/**
 * Based on the clicked component, displays the clicked component in the
 * component tree and in the domain model. Also displays all available info
 * in the information panel and highlights the clicked component in yellow.
 * In order for the Inspector to work, it has to be registered on all components
 * in the scene.
 * Inspector is disabled in this version because of the problems with
 * DomainModelEditor highlights. 
 */
public class Inspector implements AWTEventListener {

	/** True if the Inspector was registered, false otherwise. */
	private static boolean registered;

	/** Default constructor. */
	private Inspector() {
	}

	/**
	 * Registers the Inspector object and from this moment the mouse clicks on the
	 * target application will be registered.
	 * If the registration was successful, then the <code>isRegistered()</code> method will return <code>true</code>.
	 */
	public static void register() {
		if (!registered) {
			Toolkit.getDefaultToolkit().addAWTEventListener(new Inspector(),
					AWTEvent.MOUSE_EVENT_MASK);
			registered = true;
		}
	}
	
	/**
	 * Returns the value of the <code>registered</code> flag.
	 * @return true if the Inspector was registered on the target application, false otherwise.
	 */
	public static boolean isRegistered() {
		return registered;
	}

	/**
	 * If the mouse is over a component, this component will be highlighted
	 * by a yellow color. If clicked on the component, it will be displayed in
	 * the component tree and its representation in the domain model.
	 */
	@Override
	public void eventDispatched(AWTEvent event) {
		DomainModelEditor fde = DomainModelEditor.getInstance();

		Component clickedComponent = fde.getClickedComponent();
		Color clickedComponentColor = fde.getClickedComponentColor();
		boolean clickedComponentOpaque = fde.isClickedComponentOpaque();

		if (event instanceof MouseEvent) {
			if (event.getSource() instanceof Component && event.getID() == MouseEvent.MOUSE_CLICKED) {
				if (clickedComponent != null) {
					Window window = SwingUtilities
							.getWindowAncestor(clickedComponent);
					if (!(window instanceof DomainModelEditor)) {
						clickedComponent.setBackground(clickedComponentColor);
						if (clickedComponent instanceof JComponent)
							((JComponent) clickedComponent)
									.setOpaque(clickedComponentOpaque);
					}
				}
				fde.setClickedComponent(clickedComponent = SwingUtilities
						.getDeepestComponentAt((Component) event.getSource(),
								((MouseEvent) event).getX(),
								((MouseEvent) event).getY()));
				if (clickedComponent != null) {
					Window window = SwingUtilities
							.getWindowAncestor(clickedComponent);
					if (!(window instanceof DomainModelEditor)) {
						fde.setClickedComponentColor(clickedComponent
								.getBackground());
						clickedComponent.setBackground(Color.YELLOW);
						if (clickedComponent instanceof JComponent) {
							JComponent jc = (JComponent) clickedComponent;
							fde.setClickedComponentOpaque(jc.isOpaque());
							jc.setOpaque(true);
						}
					}
					if (event.getID() == MouseEvent.MOUSE_CLICKED) {
						DomainModelEditor.getInstance().showComponentInTrees(event.getSource());
					}
				}
			}
		}
	}
}
