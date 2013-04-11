/*
 * AddClassAction.java - Main class of the JNI++ utility
 note. AddClassAction should really be 'editclassaction' since it handles add
 and remove.
 */
package net.sourceforge.jnipp.gui.action;

import net.sourceforge.jnipp.gui.*;
import net.sourceforge.jnipp.gui.appevent.*;
import java.awt.event.*;
import java.util.ArrayList;

public class AddClassAction implements ActionListener {

    public AddClassAction() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals("remove")) {
            removeClass();
        } else {
            addClass();
        }
    }

    private void addClass() {
        ClassChooserDialog chooser = new ClassChooserDialog(App.getCurrentFrame());
        chooser.show();
        Class selectedClass = chooser.getSelectedClass();
        if (selectedClass != null) {
            ProjectAdapter project = App.getProject();
            project.addClass(selectedClass.getName());
            ProjectUpdatedAppEvent event = new ProjectUpdatedAppEvent();
            event.publish(ProjectUpdatedAppEvent.UpdateType_ChangeClasses);
        }
    }

    private void removeClass() {
        ClassRemoveDialog chooser = new ClassRemoveDialog(App.getCurrentFrame());
        chooser.show();

        ProjectAdapter project = App.getProject();
        ArrayList classes = chooser.getClassesAffected();
        if (!classes.isEmpty()) {
            for (int i = 0; i <= classes.size() - 1; ++i) {
                project.removeClass((String) classes.get(i));
            }
        }
        ProjectUpdatedAppEvent event = new ProjectUpdatedAppEvent();
        event.publish(ProjectUpdatedAppEvent.UpdateType_ChangeClasses);
    }
}
