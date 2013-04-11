/*
 * AddClassAction.java - Main class of the JNI++ utility
 */
package net.sourceforge.jnipp.gui.action;

import net.sourceforge.jnipp.gui.*;
import java.awt.event.*;
import java.awt.FileDialog;

public class SaveProjectAction implements ActionListener {

    public SaveProjectAction() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ProjectAdapter project = App.getProject();

        String command = e.getActionCommand();
        if (command.equals("save-as")) {
            FileDialog filedialog = new FileDialog(App.getCurrentFrame(), "Save project", FileDialog.SAVE);
            filedialog.show();
            String path = filedialog.getDirectory() + filedialog.getFile();
            try {
                project.saveAs(path);
            } catch (Exception err) {
                System.out.println("Save Project Failed");
            }
        } else {
            //todo: verify project existence.
            //todo: in the future Project might reflect that it's been 
            //changed since it was saved, and we'll need to push a
            // 'projectupdated' event
            try {
                project.save();
            } catch (Exception ex) {
                System.out.println("Save Project Failed");
            }
        }
    }
}
