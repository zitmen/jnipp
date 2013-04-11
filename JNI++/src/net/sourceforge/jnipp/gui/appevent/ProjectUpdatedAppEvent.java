package net.sourceforge.jnipp.gui.appevent;

import net.sourceforge.jnipp.project.*;
import net.sourceforge.jnipp.gui.*;
import net.sourceforge.jnipp.common.*;
import java.util.Vector;
import java.util.Enumeration;


public class ProjectUpdatedAppEvent implements AppEvent
{

	protected static Vector _subscribers;
	protected UpdateType _type;

	public static final UpdateType  UpdateType_Unknown = new UpdateType("Unspecified");
	public static final UpdateType  UpdateType_LoadProject = new UpdateType("Loaded Project");
	public static final UpdateType  UpdateType_ChangeClasses = new UpdateType("Changed Classes");
	public static final UpdateType  UpdateType_ChangeProperties = new UpdateType("Changed Properties");

	public static final class UpdateType
	{
		private String name;
		
		private UpdateType(String name)
		{
			this.name = name;
		}

		public String toString ()
		{
			return name;
		}
	}
	
	public ProjectUpdatedAppEvent()
	{
		_type = UpdateType_Unknown;
	}

	public ProjectUpdatedAppEvent(UpdateType type)
	{
		_type = type;
	}
	
	public static void subscribe(AppEventSubscriber subscriber)
	{
		 if (_subscribers == null)
		 {
		 	_subscribers = new Vector();
		 }
		_subscribers.add(subscriber);
	}

	public void publish()
	{
		AppEventSubscriber subscriber = null;
		for (Enumeration e = _subscribers.elements(); e.hasMoreElements();)
		{
			subscriber = (AppEventSubscriber)e.nextElement();
			subscriber.UpdateAvailable(this);
		}
	}

	public void publish(UpdateType type)
	{
		setType(type);
		publish();
	}

	public void setType(UpdateType type)
	{
		_type = type;
	}

	public UpdateType getType()
	{
		return (_type);
	}
	
	public ProjectAdapter getProject()
	{
		return App.getProject();
	}
}
