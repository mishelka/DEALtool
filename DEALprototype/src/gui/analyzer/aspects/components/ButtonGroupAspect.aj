package gui.analyzer.aspects.components;

import gui.analyzer.util.Logger;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;

public aspect ButtonGroupAspect {
	pointcut addButtonPointcut(AbstractButton abstractButton): call(*
			 *.add(*)) && target(ButtonGroup) && args(abstractButton);
	
	after(AbstractButton abstractButton): addButtonPointcut(abstractButton) {
		Logger.log("button was added to buttonGroup");
	}
}
 