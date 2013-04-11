/*
 * OpenProjectAction.java - Main class of the JNI++ utility
 */
package net.sourceforge.jnipp.gui.action;

import net.sourceforge.jnipp.gui.appevent.*;
import net.sourceforge.jnipp.gui.*;
import java.awt.event.*;
import java.io.*;
import net.sourceforge.jnipp.project.ProjectFormatException;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

public class OpenProjectAction implements ActionListener {

    public OpenProjectAction() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        File projectFile = GuiUtils.DisplayOpenProjectDialog();
        if (projectFile != null) {
            ProjectUpdatedAppEvent event = new ProjectUpdatedAppEvent();
            try {
                try {
                    ProjectAdapter project = new ProjectAdapter(projectFile.getAbsolutePath());
                    UserContext usercontext = App.getUserContext();
                    usercontext.setProject(project);
                } catch (SAXException | IOException | DOMException | ProjectFormatException err) {
                    GuiUtils.displayMessage("Error", "Error opening project file");
                }
            } catch (Exception exception) {
                System.out.println(exception);
            }
            event.publish(ProjectUpdatedAppEvent.UpdateType_LoadProject);
        }
    }
}
