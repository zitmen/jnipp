/*
 * DynamicClassLoaderTree.java
*/

package net.sourceforge.jnipp.gui;

import net.sourceforge.jnipp.common.DynamicClassLoader;
import net.sourceforge.jnipp.common.DynamicClassLoader.PackageContents;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.util.HashMap;
import java.util.Iterator;

public class DynamicClassLoaderTree
	extends JTree
{
	DynamicClassLoader loader;
	DefaultMutableTreeNode root;
	Class selectedClass;
	public DynamicClassLoaderTree()
	{
	       	super(new DefaultTreeModel(new DefaultMutableTreeNode("Available Classes")));
		initModel();
		initSelection();
		expandRow(0);
	}

	public Class getSelectedClass()
	{
		return selectedClass;
	}

	private void initModel()
	{
		loader = new DynamicClassLoader();
		PackageContents classtree = loader.getLoadablePackages();
		root = (DefaultMutableTreeNode)this.getModel().getRoot();
		createNodes(root,classtree);
	}

	private void initSelection()
	{
		this.getSelectionModel().setSelectionMode(
			TreeSelectionModel.SINGLE_TREE_SELECTION);
		this.addTreeSelectionListener(new TreeSelectionListener()
		{
			public void valueChanged(TreeSelectionEvent e)
			{
				DefaultMutableTreeNode selectedNode =
					(DefaultMutableTreeNode)getLastSelectedPathComponent();
				if (selectedNode == null) return;
				
				if (selectedNode.isLeaf())
				{
					selectedClass
						= getClassFromNode(selectedNode);
				}
			}
		});
	}

	private Class getClassFromNode(DefaultMutableTreeNode node)
	{
		String className = node.toString();
		TreeNode marker;
		marker = node;
		while (marker.getParent() != node.getRoot())
		{
			marker = marker.getParent();
			className = marker.toString() + "." + className;
		}
		try
		{
			return Class.forName(className);
		}
		catch(Exception e)
		{
			return null;
		}
	}

	private void createNodes(DefaultMutableTreeNode parent, PackageContents source)
	{
		HashMap childPackages = source.getContainedPackages();
		Iterator it = childPackages.values().iterator();
		while ( it.hasNext() == true )
		{
			PackageContents pc = (PackageContents) it.next();
			DefaultMutableTreeNode branch = new
				DefaultMutableTreeNode(pc.getName());
			parent.add(branch);
			createNodes( branch , pc);
		}
		it = source.getContainedClasses().values().iterator();
		while ( it.hasNext() == true )
		{
			String className = (String) it.next();
			DefaultMutableTreeNode leaf = new
				DefaultMutableTreeNode(className);
			parent.add(leaf);
		}
	}

}
