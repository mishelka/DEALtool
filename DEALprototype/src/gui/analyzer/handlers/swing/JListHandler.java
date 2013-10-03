package gui.analyzer.handlers.swing;

import gui.analyzer.handlers.DomainIdentifiable;
import gui.model.domain.ComponentInfoType;
import gui.model.domain.DomainModel;
import gui.model.domain.Term;
import gui.model.domain.relation.RelationType;

import javax.swing.Icon;
import javax.swing.JList;
import javax.swing.ListModel;


@SuppressWarnings("rawtypes")
public class JListHandler extends DomainIdentifiable<JList> {

	@Override
	public String getDomainIdentifier(JList component) {
		return null;
	}

	@Override
	public String getDomainDescriptor(JList component) {
		String ttt = component.getToolTipText();
		if (ttt == null || ttt.isEmpty())
			return null;
		return ttt;
	}

	@Override
	public Icon getIcon(JList component) {
		return null;
	}
	
	@Override
	public Term createTerm(JList component, DomainModel domainModel) {
		Term listTerm = super.createTerm(component, domainModel);
		
		ListModel model = component.getModel();
		for(int i = 0; i < model.getSize(); i++) {
			String item = model.getElementAt(i).toString();
			Term t = new Term(domainModel, item);
			t.setComponent(component);
			t.setComponentClass(component.getClass());
			listTerm.addChild(t);
		}
		
		return listTerm;
	}
	
//	@Override
//	public List<Constraint> getConstraints(JList component) {
//		List<Constraint> constraints = super.getConstraints(component);
//		if(constraints == null) constraints = new ArrayList<Constraint>();
//		
//		List<String> values = new ArrayList<String>();
//		
//		ListModel model = component.getModel();
//		for (int i = 0; i < model.getSize(); i++) {
//			String item = model.getElementAt(i).toString();
//			values.add(item);
//		}
//		
//		Enumeration enumeration = new Enumeration();
//		enumeration.setValues(values.toArray(new String[]{}));
//		
//		constraints.add(enumeration);
//		
//		return constraints;
//	}
	
	@Override
	public RelationType getRelation(JList component) {
		switch(component.getSelectionMode()) {
			case 0: return RelationType.MUTUALLY_EXCLUSIVE; //single selection
			case 2: return RelationType.MUTUALLY_NOT_EXCLUSIVE; //multiple selection
			default: return RelationType.AND;
		}
	}
	
	@Override
	public ComponentInfoType getComponentInfoType(JList component) {
		return ComponentInfoType.TEXTUAL;
	}
	
	@Override
	public boolean extractChildren() {
		return false;
	}
}
