package gui.analyzer.handlers.finanx;

import gui.analyzer.handlers.DomainIdentifiable;
import gui.analyzer.util.Util;
import gui.model.domain.ComponentInfoType;

import java.awt.Image;
import java.lang.reflect.Field;

import javax.swing.Icon;

import components.HP12CImageButton;

public class HP12CImageButtonHandler extends DomainIdentifiable<HP12CImageButton>{

	@Override
	public String getDomainIdentifier(HP12CImageButton component) {
		return component.getKey().getName();		
	}

	@Override
	public String getDomainDescriptor(HP12CImageButton component) {
		return "" + component.getKey().getCode();
	}

	@Override
	//not possible to get image, why?
	public Icon getIcon(HP12CImageButton component) {
		try {
			Field iconObj = component.getClass().getDeclaredField("image");
			iconObj.setAccessible(true);
			Image image = (Image) iconObj.get(component);
			
			Icon icon = Util.imageToIcon(image);
			return icon;
		} catch (Exception e) {
			//do nothing, just return getIcon() which is null
		}
		
		return component.getIcon();
	}

	@Override
	public ComponentInfoType getComponentInfoType(HP12CImageButton component) {
		return ComponentInfoType.FUNCTIONAL;
	}
}
