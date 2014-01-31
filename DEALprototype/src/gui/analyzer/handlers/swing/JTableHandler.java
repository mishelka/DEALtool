package gui.analyzer.handlers.swing;

import gui.analyzer.handlers.DomainIdentifiable;
import gui.analyzer.util.Util;
import gui.model.domain.ComponentInfoType;
import gui.model.domain.DomainModel;
import gui.model.domain.Term;
import gui.model.domain.constraint.Constraint;
import gui.model.domain.constraint.Enumeration;

import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.table.TableModel;

public class JTableHandler extends DomainIdentifiable<JTable> {
	@Override
	public String getDomainIdentifier(JTable component) {
		return component.getName();
		//return null;
	}

	@Override
	public String getDomainDescriptor(JTable component) {
		return component.getToolTipText();
		//return null;
	}

	@Override
	public Icon getIcon(JTable component) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ComponentInfoType getComponentInfoType(JTable component) {
		return ComponentInfoType.LOGICALLY_GROUPING;
	}
	
	@Override
	public Term createTerm(JTable component, DomainModel domainModel) {
		Term tableTerm = super.createTerm(component, domainModel);
		if(Util.isEmpty(tableTerm.getName())) {
			tableTerm.setName("Table");
		}
		
		TableModel model = component.getModel();
		for(int c = 0; c < model.getColumnCount(); c++) {
			Term t = new Term(domainModel, model.getColumnName(c)); //extract column names
			
			for(int r = 0; r < model.getRowCount(); r++) { //extract table values - are the values really important??
				ArrayList<String> enumValues = new ArrayList<String>();
				Object valObj = model.getValueAt(r, c);
				String val = valObj == null ? null : model.getValueAt(r, c).toString();
				if(!Util.isEmpty(val)) {
					enumValues.add(val);
				}
				
				if(enumValues.size() >0) {
					ArrayList<Constraint> consts = new ArrayList<Constraint>();
					Enumeration enumConstr = new Enumeration();
					consts.add(enumConstr);
					enumConstr.setValues(enumValues.toArray(new String[enumValues.size()]));
				
					t.setConstraints(consts);
				}
			}
			tableTerm.addChild(t);
		}
		
		return tableTerm;
	}
}
