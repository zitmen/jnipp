package net.sourceforge.jnipp.gui;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class SourceFileFilter extends FileFilter
{
	public boolean accept(File file)
	{
		if (file.isDirectory())
		{
         	   return true;
        	}

		String fileName = file.getName();
		return (fileName.endsWith(".xml"));
	}

	public String getDescription()
	{
		return (".xml");
	}
}
