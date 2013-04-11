package net.sourceforge.jnipp.gui.action;

import java.awt.event.*;
import java.util.Vector;
import java.util.Iterator;

/**
 * Factory for action objects
 *
 * This class acts as a factory that maps action strings stored in the
 * <code>gui.properties</code> file, to an instance of an object belonging to
 * the
 * <code> net.sourceforge.jnipp.gui.action </code> package. This object
 * implements the <i>FlyweightFactory</i> entity of the flyweight pattern.
 *
 * @author $Author: ptornroth $
 * @version $Revision: 1.4 $
 */
public class ActionFactory {
    //do we need to clean this up now ??

    protected static Vector _actionObjects;

    /**
     * This method returns an instance of an Action object for a given tag. This
     * method is called by the dynamic menu building code found in
     * <code>net.sourceforge.jnipp.gui.GuiUtils</code>
     *
     * @param actionName The tag used to map a specific Action instance.
     * @return An instance of a specific AbstractAction subclass, or null.
     * @see net.sourceforge.jnipp.gui.action.OpenProjectAction
     * @see net.sourceforge.jnipp.gui.action.SaveProjectAction
     * @see net.sourceforge.jnipp.gui.action.ExitApplicationAction
     * @see net.sourceforge.jnipp.gui.action.GenerateCodeAction
     */
    public static synchronized ActionListener getActionListener(String actionName) throws ClassNotFoundException {
        if (_actionObjects == null) {
            _actionObjects = new Vector();
        }

        ActionListener action;
        Class actionClass = Class.forName(actionName);

        for (Iterator it = _actionObjects.iterator(); it.hasNext();) {
            action = (ActionListener) it.next();
            if (actionClass.isInstance(action)) {
                return (action);
            }
        }

        try {
            action = (ActionListener) actionClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            action = null;
        }
        return (action);
    }
}
