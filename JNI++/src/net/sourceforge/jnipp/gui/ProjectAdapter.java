package net.sourceforge.jnipp.gui;

import net.sourceforge.jnipp.project.*;
import java.util.HashMap;
import java.util.Iterator;
import org.xml.sax.SAXException;
import java.io.IOException;
import org.w3c.dom.DOMException;

public class ProjectAdapter
{
	protected Project _project = null;
	protected HashMap _classes = null;
	protected String _projectname = "";
	protected String _filename = "";
	protected String _nmakefilesettings = "";
	protected String _gnumakefilesettings = "";
	protected String _outputdir = "";
	protected String _targetname = "";
	protected TargetType _targettype = TargetType_EXE;
	
	public ProjectAdapter(String filename) 
		throws SAXException, IOException, DOMException, ProjectFormatException
	{
		_project = new Project();
		_project.load(filename);
		_filename = filename;
		init();
	}

	public ProjectAdapter(Project project)
	{
		_project = project;
		_filename = "";
		init();
	}

	public Project getProject()
	{
		return (_project);
	}
	
	public void save()
	{
		resolveProjectSettings();
		try
		{
			_project.save(_filename);
		}
		catch (Exception e)
		{
			//TODO: something
		}
	}
	
	public void saveAs(String filename)
	{
		resolveProjectSettings();
		try
		{
			_project.save(filename);
			_filename = filename;
		}
		catch (Exception e)
		{
			//TODO: something
		}
	}
	
	public Iterator getClasses()
	{
		return _classes.values().iterator();
	}

	public void addClass(String className)
	{
		if (_classes.containsKey(className) == false)
		{
			ProjectClass newclass = new ProjectClass(className);
			_classes.put(className, newclass);
		}
	}
	
	public void removeClass(String className)
	{
		try
		{
			_classes.remove(className);
		}
		catch (Exception e)
		{
			//eat it.
		}
	}
	
	public String getName()
	{
		return (_projectname);
	}
	
	public void setName(String name)
	{
		_projectname = name;
	}
	
	public String getOutputDir()
	{
		return (_outputdir);
	}
	
	public void setOutputDir(String outputdir)
	{
		_outputdir = outputdir;
	}
	
	public String getTargetName()
	{
		return (_targetname);
	}
	
	public void setTargetName(String targetname)
	{
		_targetname = targetname;
	}

	public TargetType getTargetType()
	{
		return (_targettype);
	}
	
	public void setTargetType(TargetType targettype)
	{
		_targettype = targettype;
	}
	
	public String getNMakeFileSettings()
	{
		return (_nmakefilesettings);
	}
	
	public void setNMakeFileSettings(String settings)
	{
		_nmakefilesettings = settings;
	}
	
	public String getGNUMakeFileSettings()
	{
		return (_gnumakefilesettings);
	}
	
	public void setGNUMakeFileSettings(String settings)
	{
		_gnumakefilesettings = settings;
	}
	
	private void init()
	{
		if (_project == null)
			return;
		
		_projectname = _project.getName();
		if (_project.getNMakefileSettings() != null)
		{
			_nmakefilesettings = _project.getNMakefileSettings().getName();
		}
		if (_project.getGNUMakefileSettings() != null)
		{
		_gnumakefilesettings = _project.getGNUMakefileSettings().getName();
		}
		_outputdir = _project.getCPPOutputDir();
		_targetname = _project.getTargetName();
		if (_project.getTargetType() != null)
		{
			if (_project.getTargetType().equals("shlib"))
			{
				_targettype = TargetType_SHLIB;
			}
			else
			{
				_targettype = TargetType_EXE;
			}
		}
		buildClasses();
	}
	
	private void buildClasses()
	{
		if (_classes == null)
		{
			_classes = new HashMap();
		}
		
		PeerGenSettings peergenset;
		ProxyGenSettings proxygenset;
		
		Iterator iclasses;
		Iterator it = _project.getPeerGenSettings();
		while (it.hasNext())
		{
			peergenset = (PeerGenSettings) it.next();
			iclasses = peergenset.getClassNames();
			while (iclasses.hasNext())
			{
				String className = (String) iclasses.next();
				ProjectClass projectclass;
				if (_classes.containsKey(className) == false)
				{
					projectclass = new ProjectClass(className);
					_classes.put(className,projectclass);
				}
				else
				{
					projectclass = (ProjectClass) _classes.get(className);
				}
				//note.this assumes that classes may not be
				//owned by more than one peergenset or
				//proxygenset.
				projectclass.getProjectSettingsAdapter().setPeerGenSettings(peergenset);
			}
		}
		it = _project.getProxyGenSettings();
		while (it.hasNext())
		{
			proxygenset = (ProxyGenSettings) it.next();
			iclasses = proxygenset.getClassNames();
			while (iclasses.hasNext())
			{
				String className = (String) iclasses.next();
				ProjectClass projectclass;
				if (_classes.containsKey(className) == false)
				{
					projectclass = new ProjectClass(className);
					_classes.put(className,projectclass);
				}
				else
				{
					projectclass = (ProjectClass) _classes.get(className);
				} 
				//note.this assumes that classes may not be
				//owned by more than one peergenset or
				//proxygenset.
				projectclass.getProjectSettingsAdapter().setProxyGenSettings(proxygenset);
			}
		}
	}

	protected void resolveProjectSettings()
	{
		_project.setName(_projectname);
		if ((_nmakefilesettings == "") || (_nmakefilesettings == null))
		{
			_project.setNMakefileSettings(null);
		}
		else
		{
			if (_project.getNMakefileSettings() == null)
			{
				_project.setNMakefileSettings(new NMakefileSettings(_project, _nmakefilesettings));
			}
			else
			{
				_project.getNMakefileSettings().setName(_nmakefilesettings);
			}
		}
		
		if ((_gnumakefilesettings == "") || (_gnumakefilesettings == null))
		{
			_project.setGNUMakefileSettings(null);
		}
		else
		{
			if (_project.getGNUMakefileSettings() == null)
			{
				_project.setGNUMakefileSettings(new GNUMakefileSettings(_project, _gnumakefilesettings));
			}
			else
			{
				_project.getGNUMakefileSettings().setName(_gnumakefilesettings);
			}
		}
		
		_project.setCPPOutputDir(_outputdir);
		_project.setTargetName(_targetname);
		_project.setTargetType(_targettype.toString());
		
		Iterator it = _classes.values().iterator();
		while (it.hasNext())
		{
			ProjectClass projectclass = (ProjectClass) it.next();
			ProjectSettingsAdapter settings = projectclass.getProjectSettingsAdapter();
			resolvePeerGenSettings(projectclass, settings);
			resolveProxyGenSettings(projectclass,settings);
			//TODO: collapse duplicates
			
		}
	}
	
	private void resolvePeerGenSettings(ProjectClass projectclass, ProjectSettingsAdapter settings)
	{
		PeerGenSettings peergenset = settings.getPeerGenSettings();
		
		if (settings.getDoPeerGen() == true)
		{
			if (peergenset == null)
			{
				//build a new one
				peergenset = new PeerGenSettings(_project);
				peergenset.addClassName(projectclass.getClassName());
				settings.setPeerGenSettingsPushTo(peergenset);
				_project.addPeerGenSettings(peergenset);
			}
			else
			{
				if (settings.getPeerIsDirty())
				{
					//test for more than one,
					//iterator doesn't lend itself to this.
					Iterator i = peergenset.getClassNames();
					String s;
					int count = 0;
					while (i.hasNext())
					{
						s = (String)i.next();
						count++;
					}
					
					if (count > 1)
					{
						PeerGenSettings newPeergenset = new PeerGenSettings(_project);
						newPeergenset.addClassName(projectclass.getClassName());
						settings.setPeerGenSettingsPushTo(newPeergenset);
						peergenset.removeClassName(projectclass.getClassName());
					}
				}
			}
		}
		else
		{
			if (peergenset != null)
			{
				peergenset.removeClassName(projectclass.getClassName());
				if (peergenset.getClassNames().hasNext() == false)
				{
					//it's empty, let's remove it.
					_project.removePeerGenSettings(peergenset);
				}
			}
		}
	}
	
	private void resolveProxyGenSettings(ProjectClass projectclass, ProjectSettingsAdapter settings)
	{
		ProxyGenSettings proxygenset = settings.getProxyGenSettings();
		
		if (settings.getDoProxyGen() == true)
		{
			if (proxygenset == null)
			{
				//build a new one
				proxygenset = new ProxyGenSettings(_project);
				proxygenset.addClassName(projectclass.getClassName());
				settings.setProxyGenSettingsPushTo(proxygenset);
				_project.addProxyGenSettings(proxygenset);
			}
			else
			{
				if (settings.getProxyIsDirty())
				{
					//test for more than one,
					//iterator doesn't lend itself to this.
					Iterator i = proxygenset.getClassNames();
					String s;
					int count = 0;
					while (i.hasNext())
					{
						s = (String)i.next();
						count++;
					}
					
					if (count > 1)
					{
						PeerGenSettings newProxygenset = new PeerGenSettings(_project);
						newProxygenset.addClassName(projectclass.getClassName());
						settings.setPeerGenSettingsPushTo(newProxygenset);
						proxygenset.removeClassName(projectclass.getClassName());
					}
				}
			}
		}
		else
		{
			if (proxygenset != null)
			{
				proxygenset.removeClassName(projectclass.getClassName());
				if (proxygenset.getClassNames().hasNext() == false)
				{
					//it's empty, let's remove it.
					_project.removeProxyGenSettings(proxygenset);
				}
			}
		}
	}

	public static final TargetType TargetType_SHLIB = new TargetType("shlib");
	public static final TargetType TargetType_EXE = new TargetType("exe");
	public static class TargetType
	{
		private final String _tag;
		public TargetType(String tag)
		{
			_tag = tag;
		}
		public String toString()
		{
			return _tag;
		}
	}
}
