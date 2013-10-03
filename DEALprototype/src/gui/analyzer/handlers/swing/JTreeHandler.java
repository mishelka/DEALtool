package gui.analyzer.handlers.swing;

import gui.analyzer.handlers.DomainIdentifiable;
import gui.analyzer.util.Logger;
import gui.model.domain.ComponentInfoType;
import gui.model.domain.DomainModel;
import gui.model.domain.Term;
import gui.model.domain.relation.RelationType;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import com.eteks.sweethome3d.swing.FurnitureCatalogTree;

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
		
		Logger.log("found a tree " + component.getClass().getName());
		
		
		/*************************/
		if(component instanceof FurnitureCatalogTree) {
			Logger.log("!!!!!!!!!!!!!!!!!");
			//we have two class loaders here....
		}
		
		Logger.log(component.getClass().getClassLoader());
		Logger.log(this.getClass().getClassLoader());
		/*************************/
		
		
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
		parentTerm.addChild(thisTerm);
		thisTerm.setComponent(component);
		thisTerm.setComponentClass(component.getClass());
		
		for(int i = 0; i < thisTreeNode.getChildCount(); i++) {
			createTermsFromNodes(thisTreeNode.getChildAt(i), thisTerm, domainModel, component);
		}
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
