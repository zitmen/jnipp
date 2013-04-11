package net.sourceforge.jnipp.gui.action;

import net.sourceforge.jnipp.gui.App;
import net.sourceforge.jnipp.project.*;
import java.awt.event.*;
import javax.swing.*;

public class ExitApplicationAction implements ActionListener
{
	public ExitApplicationAction()
	{
	}

	public void actionPerformed(ActionEvent e)
	{
		//TODO: implement 'save first' functionality.
		System.exit(0);
	}
}
