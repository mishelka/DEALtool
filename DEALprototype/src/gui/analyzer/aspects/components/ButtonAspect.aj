package gui.analyzer.aspects.components;

import gui.analyzer.util.Logger;

import java.awt.event.ActionEvent;

import javax.swing.AbstractButton;


privileged public aspect ButtonAspect {

	// fireActionPerformed(ActionEvent event) {

	// // for buttons & menus
	// WORKS ONLY IF THERE IS AN ACTIONEVENT ADDED TO BUTTON
	pointcut buttonPointcut(ActionEvent actionEvent): within(AbstractButton+) && execution(* *.actionPerformed(*)) && args(actionEvent);

	after(ActionEvent actionEvent): buttonPointcut(actionEvent) {
		Object o = actionEvent.getSource();

		Logger.log(">>> button clicked " + ((AbstractButton) o).getText());

		// if (o instanceof EnhancedButton) {
		// EnhancedButton b = (EnhancedButton) o;
		// Logger.log("Enhanced button name: " + b.getText());
		// }
	}

	// after(): call(public *.*(*)) && target(AbstractButton+) {
	// Logger.log(">>> " );
	// }

	// for buttons that have no listener
	// HOW TO NOT ADD A LISTENER FOR BUTTONS THAT ALREADY HAVE A LISTENER?
	// do this the old way or implement handling in this method?
	// IS THERE ANY OTHER WAY OF HOW TO DETECT BUTTON CLICK?
//	after() returning(AbstractButton b): call(public AbstractButton+.new(..)) {
//		if (b != null) {
//			Logger.log(">>> adding button listener for " + b.getText());
//			ActionListener listener = new ActionListener() {
//				@Override
//				public void actionPerformed(ActionEvent e) {
//					// let our buttonAspect handle this
//				}
//			};
//			b.addActionListener(listener);
//		}
//	}

	// // for jEdit
	// // neviem ci toto zasa neberie triedy z nasho classloadera a original
	// triedy
	// // su z druheho... nezdetekuje to v jEdit
	// after() returning(EnhancedButton b): call(public EnhancedButton+.new(..))
	// {
	// Logger.log(">>> adding an EnhancedButton listener");
	// ActionListener listener = new ActionListener() {
	// @Override
	// public void actionPerformed(ActionEvent e) {
	// // let our buttonAspect handle this
	// }
	// };
	// b.addActionListener(listener);
	// }
}
