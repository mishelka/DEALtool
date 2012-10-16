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
//			System.out.println(">>> jmenu clicked " + ((JMenu)o).getText());
//		else if(o instanceof JRadioButtonMenuItem)
//			System.out.println(">>> radiomenu clicked " + ((JRadioButtonMenuItem)o).getText());
//		else if(o instanceof JCheckBoxMenuItem)
//			System.out.println(">>> checkboxmenu clicked " + ((JCheckBoxMenuItem)o).getText());
//		else if(o instanceof JMenuItem)
//			System.out.println(">>> jmenuitem clicked " + ((JMenuItem)o).getText());
		// if (o instanceof EnhancedButton) {
		// EnhancedButton b = (EnhancedButton) o;
		// System.out.println("Enhanced button name: " + b.getText());
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
			//System.out.println(">>> adding menuItem listener for " + m.getText());
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
			//System.out.println(">>> adding checkBox listener for " + m.getText());
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
			//System.out.println(">>> adding radioMenu listener for " + m.getText());
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
