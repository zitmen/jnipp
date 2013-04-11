package net.sourceforge.jnipp.gui.appevent;

import net.sourceforge.jnipp.gui.*;
import java.util.Vector;
import java.util.Enumeration;

public class UserContextUpdatedAppEvent implements AppEvent {

    protected static Vector _subscribers;

    public UserContextUpdatedAppEvent() {
    }

    public static void subscribe(AppEventSubscriber subscriber) {
        if (_subscribers == null) {
            _subscribers = new Vector();
        }
        _subscribers.add(subscriber);
    }

    @Override
    public void publish() {
        AppEventSubscriber subscriber = null;
        for (Enumeration e = _subscribers.elements(); e.hasMoreElements();) {
            subscriber = (AppEventSubscriber) e.nextElement();
            subscriber.UpdateAvailable(this);
        }
    }

    public UserContext getContext() {
        return App.getUserContext();
    }
}
