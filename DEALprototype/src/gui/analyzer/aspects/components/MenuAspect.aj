package gui.analyzer.aspects.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

public aspect MenuAspect {
	pointcut menuPointcut(ActionEvent actionEvent): execution(* *.actionPerformed(*)) && args(actionEvent);

	after(ActionEvent actionEvent): menuPointcut(actionEvent) {
		//Object o = actionEvent.getSource();
//		if(o instanceof JMenu)
//			Logger.log("jmenu clicked " + ((JMenu)o).getText());
//		else if(o instanceof JRadioButtonMenuItem)
//			Logger.log("radiomenu clicked " + ((JRadioButtonMenuItem)o).getText());
//		else if(o instanceof JCheckBoxMenuItem)
//			Logger.log("checkboxmenu clicked " + ((JCheckBoxMenuItem)o).getText());
//		else if(o instanceof JMenuItem)
//			Logger.log("jmenuitem clicked " + ((JMenuItem)o).getText());
		// if (o instanceof EnhancedButton) {
		// EnhancedButton b = (EnhancedButton) o;
		// Logger.log("Enhanced button name: " + b.getText());
		// }
	}
	
	after() returning(JMenu m): call(public JMenu+.new(..)) {
		if (m != null) {
			ActionListener listener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// let our buttonAspect handle this
				}
			};
			m.addActionListener(listener);
		}
	}
	
	after() returning(JMenuItem m): call(public JMenuItem+.new(..)) {
		if (m != null  && !(m instanceof JMenu) && !(m instanceof JRadioButtonMenuItem) && !(m instanceof JCheckBoxMenuItem)) {
			//Logger.log("adding menuItem listener for " + m.getText());
			ActionListener listener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// let our buttonAspect handle this
				}
			};
			m.addActionListener(listener);
		}
	}
	
	after() returning(JCheckBoxMenuItem m): call(public JCheckBoxMenuItem+.new(..)) {
		if (m != null) {
			//Logger.log("adding checkBox listener for " + m.getText());
			ActionListener listener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// let our buttonAspect handle this
				}
			};
			m.addActionListener(listener);
		}
	}
	
	after() returning(JRadioButtonMenuItem m): call(public JRadioButtonMenuItem+.new(..)) {
		if (m != null) {
			//Logger.log("adding radioMenu listener for " + m.getText());
			ActionListener listener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// let our buttonAspect handle this
				}
			};
			m.addActionListener(listener);
		}
	}
}
