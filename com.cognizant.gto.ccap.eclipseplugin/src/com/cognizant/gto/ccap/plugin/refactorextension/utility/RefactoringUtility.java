package com.cognizant.gto.ccap.plugin.refactorextension.utility;

import org.eclipse.jdt.core.dom.ASTNode;

public class RefactoringUtility {

	/**
	 * Finds parent ASTNode of ASTType parentType for ASTNode node 
	 * @param node -- ASTNode for which the parent is to be searched
	 * @param parentType -- Use ASTNode Types for specifying type of parent ASTNode
	 * @return <b>null</b> if not found, else valid first parent ASTNode
	 */
	public static ASTNode findParentNodeofType(ASTNode node, int parentType) {
		ASTNode rNode = null;
		if (node != null) {
			ASTNode parent = node.getParent();
			if (null != parent) {
				if (parent.getNodeType() == parentType) {
					rNode = parent;
				} else {
					rNode = findParentNodeofType(parent, parentType);
				}
			}
		}
		return rNode;
	}
	
	


}
