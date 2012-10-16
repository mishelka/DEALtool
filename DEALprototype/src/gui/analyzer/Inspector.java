package gui.analyzer;

import gui.ui.DomainModelEditor;

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
 * Na zaklade kliknuti mysi zobrazi kliknuty komponent v komponentovom strome v
 * ComponentTreeUi. Na zaklade toho, nad ktorym komponentom sa nachadza mys
 * vysvieti tento komponent zltou farbou.
 */
public class Inspector implements AWTEventListener {

	private static boolean registered;

	private Inspector() {
	}

	/**
	 * Vykona registraciu objektu triedy Inspector, pricom to znamena, ze od
	 * tohto momentu uz bude zaznamenavat udalosti mysi nad spustanou
	 * aplikaciou.
	 */
	public static void register() {
		if (!registered) {
			Toolkit.getDefaultToolkit().addAWTEventListener(new Inspector(),
					AWTEvent.MOUSE_EVENT_MASK);
			registered = true;
		}
	}

	/**
	 * Ak je mys nad komponentom, tento komponent sa vysvieti zltou farbou Ak sa
	 * klikne mysou na komponent, zobrazi sa v komponentovom strome
	 */
	@Override
	public void eventDispatched(AWTEvent event) {
		DomainModelEditor fde = DomainModelEditor.getInstance();

		Component clickedComponent = fde.getClickedComponent();
		Color clickedComponentColor = fde.getClickedComponentColor();
		boolean clickedComponentOpaque = fde.isClickedComponentOpaque();

		if (event instanceof MouseEvent) {
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

			if (event.getSource() instanceof Component) {
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
