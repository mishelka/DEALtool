package gui.settings;

import java.io.Serializable;

public class Setting implements Serializable {
	private static final long serialVersionUID = 1L;
	private final boolean extractFunctionalComponents;
	
	public Setting(boolean extractFunctionalComponents) {
		this.extractFunctionalComponents = extractFunctionalComponents;
	}
	
	public boolean isExtractFunctionalComponents() {
		return extractFunctionalComponents;
	}

}
