package gui.analyzer.filter;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

public class GeneralTerm {
	private String name;
	private List<GeneralTerm> subTerms;
	@XStreamOmitField
	private GeneralTerm parent;
	
	public GeneralTerm(String name, GeneralTerm parent) {
		this.name = name;
		this.parent = parent;
		subTerms = new ArrayList<GeneralTerm>();
		if(parent != null) {
			parent.addSubTerm(this);
		}
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public List<GeneralTerm> getSubTerms() {
		return subTerms;
	}
	
	public void setSubTerms(List<GeneralTerm> subTerms) {
		this.subTerms = subTerms;
	}
	
	public GeneralTerm getParent() {
		return parent;
	}
	
	public void setParent(GeneralTerm parent) {
		this.parent = parent;
	}
	
	public void addSubTerm(GeneralTerm subTerm) {
		subTerms.add(subTerm);
	}
	
	public void addSubTermAndSetParent(GeneralTerm subTerm) {
		subTerms.add(subTerm);
		subTerm.setParent(this);
	}
	
	public GeneralTerm getSubTerm(int index) {
		return subTerms.get(index);
	}
	
	public void removeSubTerm(int index) {
		subTerms.remove(index);
	}
	
	public void removeSubTerm(GeneralTerm subTerm) {
		subTerms.remove(subTerm);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("name:" + name + ", ");
		sb.append(parent == null ? " {" : ("parent: " + parent.getName() + ", {"));
		
		for(GeneralTerm gt : subTerms) {
			sb.append(gt.toString() + ", ");
		}
		sb.append("}");
		
		return sb.toString();
	}
}