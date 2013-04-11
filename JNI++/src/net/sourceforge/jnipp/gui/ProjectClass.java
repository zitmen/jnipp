package net.sourceforge.jnipp.gui;

public class ProjectClass
{
	String _className = null;
	ProjectSettingsAdapter _projectSettings = new ProjectSettingsAdapter();

	public ProjectClass(String className)
	{
		_className = className;
	}
	
	public ProjectSettingsAdapter getProjectSettingsAdapter()
	{
		return _projectSettings;
	}

	public String getClassName()
	{
		return (_className);
	}

	public String toString()
	{
		return (_className);
	}
}
