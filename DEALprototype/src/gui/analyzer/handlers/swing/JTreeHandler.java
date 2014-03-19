package gui.analyzer.handlers.swing;

import gui.analyzer.handlers.DomainIdentifiable;
import gui.analyzer.handlers.sweetHome3D.FurnitureTreeHelper;
import gui.model.domain.ComponentInfoType;
import gui.model.domain.DomainModel;
import gui.model.domain.Term;
import gui.model.domain.relation.RelationType;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;

public class JTreeHandler extends DomainIdentifiable<JTree> {

	@Override
	public String getDomainIdentifier(JTree component) {
		return null;
	}

	@Override
	public String getDomainDescriptor(JTree component) {
		return component.getToolTipText();
	}

	@Override
	public Icon getIcon(JTree component) {
		return null;
	}

	@Override
	public ComponentInfoType getComponentInfoType(JTree component) {
		return ComponentInfoType.CONTAINERS;
	}

	@Override
	public Term createTerm(JTree component, DomainModel domainModel) {
		Term treeTerm = super.createTerm(component, domainModel);
		
		if(component.getClass().getName().equals(FurnitureTreeHelper.FURNITURE_CATALOG_TREE_CLASS_NAME)) {
			FurnitureTreeHelper helper = new FurnitureTreeHelper();
			treeTerm = helper.createTerm(component, domainModel);
			return treeTerm;
		}
		
		
		TreeModel tm = component.getModel();
		Object root = tm.getRoot();
		if(root instanceof TreeNode) {
			TreeNode rootNode = (TreeNode) root;
			createTermsFromNodes(rootNode, treeTerm, domainModel, component);
		}
		return treeTerm;
	}
	
	private void createTermsFromNodes(TreeNode thisTreeNode, Term parentTerm, DomainModel domainModel, JTree component) {
		Term thisTerm = createTerm(thisTreeNode, domainModel);
		thisTerm.setRelation(getRelation(thisTreeNode, component));
		parentTerm.addChild(thisTerm);
		thisTerm.setComponent(component);
		thisTerm.setComponentClass(component.getClass());
		
		for(int i = 0; i < thisTreeNode.getChildCount(); i++) {
			createTermsFromNodes(thisTreeNode.getChildAt(i), thisTerm, domainModel, component);
		}
	}
	
	private RelationType getRelation(TreeNode treeNode, JTree component) {
		if(treeNode.getChildCount() == 0)
			return RelationType.AND;
		
		boolean hasOnlyLeafs = true;
		
		for(int i = 0; i < treeNode.getChildCount(); i++) {
			TreeNode tn = treeNode.getChildAt(i);
			if(tn.getChildCount() != 0) 
				hasOnlyLeafs = false;
		}
		
		if(hasOnlyLeafs) {
			return getRelationForSelectionMode(component);
		}
		return RelationType.AND;
	}
	
	private RelationType getRelationForSelectionMode(JTree component) {
		int selMode = component.getSelectionModel().getSelectionMode();
		
		if(selMode == TreeSelectionModel.SINGLE_TREE_SELECTION) {
			return RelationType.MUTUALLY_EXCLUSIVE;
		} else return RelationType.MUTUALLY_NOT_EXCLUSIVE;
	}
	
	@Override
	public boolean extractChildren() {
		return false;
	}
	
	private Term createTerm(TreeNode treeNode, DomainModel domainModel) {
		Term t = new Term(domainModel);

		t.setName(treeNode.toString());

		t.setRelation(RelationType.AND);

		t.setComponentClass(JTree.class);
		t.setComponent(treeNode);
		
		//t.setIcon(this.getIcon(component));
		
		//t.setConstraints(this.getConstraints(component));
		
		t.setComponentInfoType(ComponentInfoType.LOGICALLY_GROUPING);

		return t;
	}
}
