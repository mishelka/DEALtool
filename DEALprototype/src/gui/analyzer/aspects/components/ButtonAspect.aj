package gui.analyzer.aspects.components;


privileged public aspect ButtonAspect {

//	// fireActionPerformed(ActionEvent event) {
//
//	// // for buttons & menus
//	// WORKS ONLY IF THERE IS AN ACTIONEVENT ADDED TO BUTTON
//	pointcut buttonPointcut(ActionEvent actionEvent): within(AbstractButton+) && execution(* *.actionPerformed(*)) && args(actionEvent);
//
//	after(ActionEvent actionEvent): buttonPointcut(actionEvent) {
//		Object o = actionEvent.getSource();
//
//		System.out.println(">>> button clicked " + ((AbstractButton) o).getText());
//
//		// if (o instanceof EnhancedButton) {
//		// EnhancedButton b = (EnhancedButton) o;
//		// System.out.println("Enhanced button name: " + b.getText());
//		// }
//	}
//
//	// after(): call(public *.*(*)) && target(AbstractButton+) {
//	// System.out.println(">>> " );
//	// }
//
//	// for buttons that have no listener
//	// HOW TO NOT ADD A LISTENER FOR BUTTONS THAT ALREADY HAVE A LISTENER?
//	// do this the old way or implement handling in this method?
//	// IS THERE ANY OTHER WAY OF HOW TO DETECT BUTTON CLICK?
//	after() returning(AbstractButton b): call(public AbstractButton+.new(..)) {
//		if (b != null) {
//			System.out.println(">>> adding button listener for " + b.getText());
//			ActionListener listener = new ActionListener() {
//				@Override
//				public void actionPerformed(ActionEvent e) {
//					// let our buttonAspect handle this
//				}
//			};
//			b.addActionListener(listener);
//		}
//	}
//
//	// // for jEdit
//	// // neviem ci toto zasa neberie triedy z nasho classloadera a original
//	// triedy
//	// // su z druheho... nezdetekuje to v jEdit
//	// after() returning(EnhancedButton b): call(public EnhancedButton+.new(..))
//	// {
//	// System.out.println(">>> adding an EnhancedButton listener");
//	// ActionListener listener = new ActionListener() {
//	// @Override
//	// public void actionPerformed(ActionEvent e) {
//	// // let our buttonAspect handle this
//	// }
//	// };
//	// b.addActionListener(listener);
//	// }
}
