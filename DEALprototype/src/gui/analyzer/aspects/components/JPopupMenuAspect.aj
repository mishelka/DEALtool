package gui.analyzer.aspects.components;

import gui.analyzer.util.Logger;

import javax.swing.JPopupMenu;

public privileged aspect JPopupMenuAspect {
	// for popup menu show
	pointcut popupPointcut(JPopupMenu jPopupMenu): call(*
			 *.show(*)) && target(jPopupMenu);

	after(JPopupMenu jPopupMenu): popupPointcut(jPopupMenu) {
		Logger.logError(">>>>>>>>>>>>>>>>>>>>>> POPUP <<<<<<<<<<<<<<<<<<<<<<");
	}
}

