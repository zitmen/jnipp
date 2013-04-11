package net.sourceforge.jnipp.gui;

import net.sourceforge.jnipp.gui.appevent.*;

public class UserContext {

    private ContextType m_context = ContextType_Project;
    private ProjectAdapter m_project;
    private Object m_contextobj;
    public static final ContextType ContextType_Project = new ContextType();
    public static final ContextType ContextType_Class = new ContextType();

    public static final class ContextType {
    }

    public void setProject(ProjectAdapter project) {
        m_project = project;
    }

    public ProjectAdapter getProject() {
        return m_project;
    }

    public void setActiveContext(ContextType context, Object contextobj) {
        //TODO: type safety.
        if ((m_context != context) || (m_contextobj != contextobj)) {
            m_context = context;
            m_contextobj = contextobj;
            UserContextUpdatedAppEvent event = new UserContextUpdatedAppEvent();
            event.publish();
        }
    }

    public Object getContextObject() {
        //TODO: type safety.
        return m_contextobj;
    }

    public ContextType getActiveContext() {
        return m_context;
    }
}
