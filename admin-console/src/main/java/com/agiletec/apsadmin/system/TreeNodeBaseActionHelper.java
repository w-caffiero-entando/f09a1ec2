/*
 * Copyright 2015-Present Entando Inc. (http://www.entando.com) All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.agiletec.apsadmin.system;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.entando.entando.ent.exception.EntException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.agiletec.aps.system.common.tree.ITreeNode;
import com.agiletec.aps.system.common.tree.ITreeNodeManager;


/**
 * Classe base per gli helper che gestiscono le operazioni su oggetti alberi.
 *
 * @author E.Santoboni
 */
public abstract class TreeNodeBaseActionHelper extends BaseActionHelper implements ITreeNodeBaseActionHelper {

	private static final Logger _logger = LoggerFactory.getLogger(TreeNodeBaseActionHelper.class);

	/**
	 * Costruisce il codice univoco di un nodo in base ai parametri specificato.
	 * Il metodo: 1) elimina i caratteri non compresi tra "a" e "z", tra "0" e
	 * "9". 2) taglia (se necessario) la stringa secondo la lunghezza massima
	 * immessa. 3) verifica se esistono entità con il codice ricavato (ed in tal
	 * caso appende il suffisso "_<numero>" fino a che non trova un codice
	 * univoco).
	 *
	 * @param title Il titolo del nuovo nodo.
	 * @param baseDefaultCode Un codice nodo di default.
	 * @param maxLength La lunghezza massima del codice.
	 * @return Il codice univoco univoco ricavato.
	 * @throws EntException In caso di errore.
	 */
	@Override
	public String buildCode(String title, String baseDefaultCode, int maxLength) throws EntException {
		String uniqueCode = null;
		try {
			// punto 1
			uniqueCode = (null != title) ? purgeString(title) : baseDefaultCode;
			if (uniqueCode.length() == 0) {
				uniqueCode = baseDefaultCode;
			}
			// punto 2
			if (uniqueCode.length() > maxLength) {
				uniqueCode = uniqueCode.substring(0, maxLength);
				if (uniqueCode.charAt(uniqueCode.length() - 1) == '_') {
					uniqueCode = uniqueCode.substring(0, uniqueCode.length() - 1);
				}
			}
			//punto 3
			if (null != this.getTreeNode(uniqueCode)) {
				int index = 0;
				String currentCode = null;
				do {
					index++;
					currentCode = uniqueCode + "_" + index;
				} while (null != this.getTreeNode(currentCode));
				uniqueCode = currentCode;
			}
		} catch (Throwable t) {
			throw new EntException("Errore in creazione nuovo codice", t);
		}
		return uniqueCode;
	}

	@Override
	public Set<String> checkTargetNodes(String nodeToOpen, Set<String> lastOpenedNodes, Collection<String> groupCodes) throws EntException {
		Set<String> checkedTargetNodes = new HashSet<>();
		try {
			if (null != nodeToOpen && this.checkNode(nodeToOpen, groupCodes)) {
				checkedTargetNodes.add(nodeToOpen);
			}
			if (null != lastOpenedNodes) {
				Iterator<String> iter = lastOpenedNodes.iterator();
				while (iter.hasNext()) {
					String code = (String) iter.next();
					if (null != code && this.checkNode(code, groupCodes)) {
						checkedTargetNodes.add(code);
					}
				}
			}
		} catch (Throwable t) {
			_logger.error("Error check target nodes", t);
			throw new EntException("Error check target nodes", t);
		}
		return checkedTargetNodes;
	}

	@Override
	public Set<String> checkTargetNodesOnClosing(String nodeToCloseCode, Set<String> lastOpenedNodes, Collection<String> groupCodes) throws EntException {
		ITreeNode nodeToClose = this.getTreeNode(nodeToCloseCode);
		if (null == nodeToCloseCode || null == nodeToClose) {
			return this.checkTargetNodes(null, lastOpenedNodes, groupCodes);
		}
		Set<String> checkedTargetNodes = new HashSet<>();
		try {
			if (nodeToClose.isRoot()) {
				return checkedTargetNodes;
			}
			if (null != lastOpenedNodes) {
				Iterator<String> iter = lastOpenedNodes.iterator();
				while (iter.hasNext()) {
					String code = (String) iter.next();
					if (null != code && this.checkNode(code, groupCodes)
							&& !code.equals(nodeToCloseCode) && !this.getTreeNode(code).isChildOf(nodeToCloseCode, this.getTreeNodeManager())) {
						checkedTargetNodes.add(code);
					}
				}
			}
			if (null != nodeToClose.getParentCode()
					&& this.checkNode(nodeToClose.getParentCode(), groupCodes)) {
				checkedTargetNodes.add(nodeToClose.getParentCode());
			}
		} catch (Throwable t) {
			_logger.error("Error check target nodes on closing tree", t);
			throw new EntException("Error check target nodes on closing tree", t);
		}
		return checkedTargetNodes;
	}

	protected boolean checkNode(String nodeCode, Collection<String> groupCodes) {
		if (!this.isNodeAllowed(nodeCode, groupCodes)) {
			_logger.error("Node '{}' not allowed ", nodeCode);
			return false;
		}
		ITreeNode treeNode = this.getTreeNode(nodeCode);
		if (null == treeNode) {
			_logger.error("Node '{}' null", nodeCode);
			return false;
		}
		return true;
	}

	@Override
	public TreeNodeWrapper getShowableTree(Set<String> treeNodesToOpen, ITreeNode fullTree, Collection<String> groupCodes) throws EntException {
		if (null == treeNodesToOpen || treeNodesToOpen.isEmpty()) {
			_logger.warn("No selected nodes");
			return this.buildWrapper(fullTree);
		}
		TreeNodeWrapper root = null;
		try {
			Set<String> nodesToShow = new HashSet<>();
			this.buildCheckNodes(treeNodesToOpen, nodesToShow, groupCodes);
			root = this.buildWrapper(fullTree);
			root.setParent(root);
			this.builShowableTree(root, fullTree, nodesToShow);
		} catch (Throwable t) {
			_logger.error("Error creating showable tree", t);
			throw new EntException("Error creating showable tree", t);
		}
		return root;
	}

	private void buildCheckNodes(Set<String> treeNodesToOpen, Set<String> nodesToShow, Collection<String> groupCodes) {
		if (null == treeNodesToOpen) {
			return;
		}
		Iterator<String> iter = treeNodesToOpen.iterator();
		while (iter.hasNext()) {
			String targetNode = (String) iter.next();
			ITreeNode treeNode = this.getTreeNode(targetNode);
			if (null != treeNode) {
				this.buildCheckNodes(treeNode, nodesToShow, groupCodes);
			}
		}
	}

	protected void buildCheckNodes(ITreeNode treeNode, Set<String> nodesToShow, Collection<String> groupCodes) {
		nodesToShow.add(treeNode.getCode());
		ITreeNode parent = this.getTreeNodeManager().getNode(treeNode.getParentCode());
		if (parent != null && parent.getParentCode() != null
				&& !parent.getCode().equals(treeNode.getCode())) {
			this.buildCheckNodes(parent, nodesToShow, groupCodes);
		}
	}

	private void builShowableTree(TreeNodeWrapper currentNode, ITreeNode currentTreeNode, Set<String> checkNodes) {
		if (checkNodes.contains(currentNode.getCode())) {
			currentNode.setOpen(true);
			String[] children = currentTreeNode.getChildrenCodes();
			for (int i = 0; i < children.length; i++) {
				ITreeNode newCurrentTreeNode = this.getTreeNode(children[i]);
				TreeNodeWrapper newNode = this.buildWrapper(newCurrentTreeNode);
				newNode.setParent(currentNode);
				newNode.setParentCode(currentNode.getCode());
				currentNode.addChildCode(newNode.getCode());
				currentNode.addChild(newNode);
				this.builShowableTree(newNode, newCurrentTreeNode, checkNodes);
			}
		}
	}
    
	protected abstract boolean isNodeAllowed(String code, Collection<String> groupCodes);

	/**
	 * Default implementation of the method. Build a root node cloning the
	 * returned tree from the helper.
	 *
	 * @param groupCodes the groups codes
	 * @return the root node
	 * @throws EntException in caso of error
	 */
	@Override
	public ITreeNode getAllowedTreeRoot(Collection<String> groupCodes) throws EntException {
		ITreeNode currentRoot = this.getRoot();
		TreeNodeWrapper root = this.buildWrapper(currentRoot);
		this.addTreeWrapper(root, currentRoot);
		return root;
	}

	private void addTreeWrapper(TreeNodeWrapper currentNode, ITreeNode currentTreeNode) {
		String[] children = currentTreeNode.getChildrenCodes();
		for (int i = 0; i < children.length; i++) {
			ITreeNode newCurrentTreeNode = this.getTreeNode(children[i]);
			TreeNodeWrapper newNode = this.buildWrapper(newCurrentTreeNode);
			currentNode.addChildCode(newNode.getCode());
			currentNode.addChild(newNode);
			this.addTreeWrapper(newNode, newCurrentTreeNode);
		}
	}

	protected TreeNodeWrapper buildWrapper(ITreeNode treeNode) {
        ITreeNode parent = this.getTreeNodeManager().getNode(treeNode.getParentCode());
		return new TreeNodeWrapper(treeNode, parent);
	}
    
    protected abstract ITreeNodeManager getTreeNodeManager();

	/**
	 * Return the root node of the managed tree.
	 *
	 * @return The root node.
	 */
	protected abstract ITreeNode getRoot();

	/**
	 * Return a node of the managed tree.
	 *
	 * @param code The code of the node to return.
	 * @return The required node.
	 */
	protected abstract ITreeNode getTreeNode(String code);

}
