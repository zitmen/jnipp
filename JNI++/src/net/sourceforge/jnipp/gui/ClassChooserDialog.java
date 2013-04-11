/*
 * ClassChooserDialog.java
*/

package net.sourceforge.jnipp.gui;

import net.sourceforge.jnipp.gui.App;
import net.sourceforge.jnipp.project.*;
import java.util.Iterator;
import java.util.Vector;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ClassChooserDialog
	extends JDialog
	implements ActionListener
{
	private DynamicClassLoaderTree tree;
	JPanel contentPane;

	public ClassChooserDialog(Frame owner)
	{
		super(owner,"JNI++ - Select A Target Class",true);
		init();
	}

	public Class getSelectedClass()
	{
		return tree.getSelectedClass();
	}

	private void init()
	{
		initWindowListener();
		JButton cmdOK = new JButton(App.getProperty("button-ok.label"));
		cmdOK.setActionCommand("ok");
		cmdOK.addActionListener(this);
		
		JButton cmdCancel = new JButton(App.getProperty("button-cancel.label"));
		cmdCancel.setActionCommand("cancel");
		cmdCancel.addActionListener(this);
		
		tree = new DynamicClassLoaderTree();		
		JScrollPane treeView = new JScrollPane(tree);
		
		JLabel instructions = new JLabel(App.getProperty("message.add-class-instruction"));
		
		GridBagLayout myLayout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		getContentPane().setLayout(myLayout);
		
		c.ipadx = 2;
		c.ipady = 2;
		c.insets = new Insets(5,5,5,5);
		
		c.gridwidth = GridBagConstraints.REMAINDER; //2;
		myLayout.setConstraints(instructions,c);
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		myLayout.setConstraints(treeView,c);
		
		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0;
		c.weighty = 0;
		c.gridwidth = 1;
		c.gridx = 2;
		c.gridy = 2;
		myLayout.setConstraints(cmdOK,c);
		c.gridx = 3;
		myLayout.setConstraints(cmdCancel,c);
		
		this.setSize(new Dimension(400, 300));
		getContentPane().add(instructions);
		getContentPane().add(treeView);
		getContentPane().add(cmdOK);
		getContentPane().add(cmdCancel);
		getRootPane().setDefaultButton(cmdOK);
	}

	public void actionPerformed(java.awt.event.ActionEvent e) {
		if (e.getActionCommand().equals("ok"))
		{
			hide();
		}
		else
		{
			//TODO: clear selection
			hide();
		}
			
	}

	private void initWindowListener()
	{
		this.addWindowListener( new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				hide();
			}
		});
	}
}
