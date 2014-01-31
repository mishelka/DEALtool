package gui.analyzer.handlers.sweetHome3D;

import gui.analyzer.handlers.DomainIdentifiable;
import gui.analyzer.util.Util;
import gui.model.domain.ComponentInfoType;
import gui.model.domain.DomainModel;
import gui.model.domain.Term;
import gui.model.domain.relation.RelationType;

import java.lang.reflect.Method;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.TreeModel;

public class FurnitureTreeHelper extends DomainIdentifiable<JTree> {

	@Override
	public String getDomainIdentifier(JTree component) {
		return null;
	}

	@Override
	public String getDomainDescriptor(JTree component) {
		return null;
	}

	@Override
	public Icon getIcon(JTree component) {
		return null;
	}

	@Override
	public ComponentInfoType getComponentInfoType(JTree component) {
		return null;
	}

	@Override
	public Term createTerm(JTree component, DomainModel domainModel) {
		Term treeTerm = super.createTerm(component, domainModel);
		treeTerm.setName("Furniture");

		Object furnitureCatalogObject = getFurnitureCatalog(component);
		
			List<?> categories = getCategoriesList(furnitureCatalogObject);
			
			if(categories != null) {
				for(Object furnitureCategory : categories) {
					String name = getCategoryName(furnitureCategory);
					Term categoryTerm = createTermForCategory(name, domainModel, component);
					treeTerm.addChild(categoryTerm);
					
					List<?> furnitureList = getFurnitureList(furnitureCategory);
					if(furnitureList != null) {
						for(Object furniture : furnitureList) {
							Term furnitureTerm = createTermForFurniture(furniture, domainModel, component);
							if(furnitureTerm != null) {
								furnitureTerm.setComponent(component);
								furnitureTerm.setComponentClass(component.getClass());
								categoryTerm.addChild(furnitureTerm);
							}
						}
					}
				}
			}
				
		return treeTerm;
	}

	public static final String FURNITURE_CATALOG_TREE_CLASS_NAME = "com.eteks.sweethome3d.swing.FurnitureCatalogTree";
	private static final String FURNITURE_CATALOG_CLASS_NAME = "com.eteks.sweethome3d.model.FurnitureCatalog";
	private static final String GET_CATEGORIES_METHOD = "getCategories";
	private static final String GET_CATEGORY_NAME_METHOD = "getName";
	private static final String GET_FURNITURE_METHOD = "getFurniture";
	private static final String GET_FURNITURE_NAME_METHOD = "getName";
	private static final String GET_FURNITURE_DESCRIPTION_METHOD = "getDescription";
	private static final String GET_FURNITURE_ICON_METHOD = "getIcon";

	private Object getFurnitureCatalog(JTree component) {
		if (component.getClass().getName()
				.equals(FURNITURE_CATALOG_TREE_CLASS_NAME)) {
			TreeModel tm = component.getModel();
			Object furnitureCatalogObject = tm.getRoot();
			if (furnitureCatalogObject.getClass().getName()
					.equals(FURNITURE_CATALOG_CLASS_NAME)) {
				return furnitureCatalogObject;
			}
		}
		return null;
	}
	
	private List<?> getCategoriesList(Object furnitureCatalogObject) {
		try {
			if (furnitureCatalogObject == null)
				return null;

			Object categoriesList = invokeMethod(furnitureCatalogObject,
					GET_CATEGORIES_METHOD);

			if (categoriesList instanceof List) {
				return (List<?>) categoriesList;
			}

			return null;
		} catch (FurnitureTreeParserException ex) {
			return null;
		}
	}
	
	private String getCategoryName(Object categoryObject) {
		try {
			if(categoryObject == null) return null;
			
			Object categoryName = invokeMethod(categoryObject, GET_CATEGORY_NAME_METHOD);
			
			if(categoryName != null && categoryName instanceof String) {
				return (String) categoryName;
			}
			
			return null;
		} catch (FurnitureTreeParserException ex) {
			return null;
		}
	}
	
	public Term createTermForCategory(String name, DomainModel domainModel, JTree component) {
		Term t = super.createTerm(component, domainModel);
		t.setName(name);
		return t;
	}
	
	private List<?> getFurnitureList(Object furnitureCategoryObject) {
		try {
			if (furnitureCategoryObject == null)
				return null;

			Object categoriesList = invokeMethod(furnitureCategoryObject,
					GET_FURNITURE_METHOD);

			if (categoriesList instanceof List) {
				return (List<?>) categoriesList;
			}

			return null;
		} catch (FurnitureTreeParserException ex) {
			return null;
		}
	}
	
	private Term createTermForFurniture(Object furnitureObject, 
			DomainModel domainModel, JTree component) {
		Term t = null;
		try {
			String furnitureName = getFurnitureName(furnitureObject);
			if(!Util.isEmpty(furnitureName)) {
				t = new Term(domainModel, furnitureName);
			}
		} catch (FurnitureTreeParserException e) {
			// do nothing
		}
		try {
			String furnitureDescription = getFurnitureDescription(furnitureObject);
			if(!Util.isEmpty(furnitureDescription)) {
				if(t == null) {
					t = new Term(domainModel);
				}
				t.setDescription(furnitureDescription);
			}
		} catch (FurnitureTreeParserException e) {
			// do nothing
		}
		
		if(t != null) {
			t.setRelation(RelationType.AND);

			t.setComponentClass(component.getClass());
			t.setComponent(component);
			t.setComponentInfoType(ComponentInfoType.DESCRIPTIVE);
		}
		
		try {
			getFurnitureIcon(furnitureObject);
			if(t != null) {
				t.setIcon(null);
			}
		} catch (FurnitureTreeParserException e) {
			// do nothing
		}
		
		return t;
	}
	
	private String getFurnitureName(Object furnitureObject) throws FurnitureTreeParserException {
		Object furnitureName = invokeMethod(furnitureObject, GET_FURNITURE_NAME_METHOD);
		if(furnitureName != null && furnitureName instanceof String) {
			return (String) furnitureName;
		}
		return null;
	}
	
	private String getFurnitureDescription(Object furnitureObject) throws FurnitureTreeParserException {
		Object furnitureDescription = invokeMethod(furnitureObject, GET_FURNITURE_DESCRIPTION_METHOD);
		if(furnitureDescription != null && furnitureDescription instanceof String) {
			return (String) furnitureDescription;
		}
		return null;
	}
	
	private void getFurnitureIcon(Object furnitureObject) throws FurnitureTreeParserException {
		Object furnitureIcon = invokeMethod(furnitureObject, GET_FURNITURE_ICON_METHOD);
		if(furnitureIcon != null) {
			System.out.println("?????" + furnitureIcon.getClass());
		}
	}
	
	private Object invokeMethod(Object object, String methodName) throws FurnitureTreeParserException {
		try {
			Class<?> objectClass = object.getClass();
			
			Method method = objectClass.getMethod(methodName);
			Object toReturn = method.invoke(object);
			
			return toReturn;
		} catch (Exception e) {
			throw new FurnitureTreeParserException(e);
		}
	}
}
