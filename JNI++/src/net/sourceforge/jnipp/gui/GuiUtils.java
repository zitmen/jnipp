/*
 * GUIUtils.java - Utility class for GUI elements of the JNI++ utility
 */
package net.sourceforge.jnipp.gui;

import net.sourceforge.jnipp.gui.action.*;
import javax.swing.*;
import java.util.StringTokenizer;
import java.io.*;

public class GuiUtils {

    public static JMenuBar loadMenus() {
        String menuNames = App.getProperty("menubar");
        StringTokenizer st = new StringTokenizer(menuNames);

        JMenuBar menubar = new JMenuBar();

        while (st.hasMoreTokens()) {
            menubar.add(loadMenu(st.nextToken()));
        }

        return menubar;
    }

    public static JMenu loadMenu(String name) {
        String menuItemNames = App.getProperty(name);
        String display = App.getProperty(name.concat(".label"));
        if (display == null) {
            display = name;
        }
        JMenu menu = new JMenu(display);
        StringTokenizer st = new StringTokenizer(menuItemNames);
        String menuItem = "";
        while (st.hasMoreTokens()) {
            menuItem = st.nextToken();
            if (menuItem.equals("-")) {
                menu.addSeparator();
            } else {
                menu.add(loadMenuItem(menuItem));
            }
        }
        return menu;
    }

    public static JMenuItem loadMenuItem(String name) {
        String display = App.getProperty(name.concat(".label"));
        if (display == null) {
            display = name;
        }

        JMenuItem menuItem = new JMenuItem(display);

        String actionName = App.getProperty(name.concat(".action"));
        if (actionName != null) {
            try {
                menuItem.addActionListener(ActionFactory.getActionListener(actionName));
            } catch (ClassNotFoundException e) {
                System.out.println("[Class not found for " + actionName + "]");
            }
        }
        String actionCommand = App.getProperty(name.concat(".actioncommand"));
        if (actionCommand != null) {
            menuItem.setActionCommand(actionCommand);
        }

        return menuItem;
    }

    public static File DisplayOpenProjectDialog() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new ProjectFileFilter());
        chooser.showOpenDialog(App.getCurrentFrame());
        return chooser.getSelectedFile();
    }

    public static void displayMessage(String title, String label) {
        JFrame frame = (JFrame) App.getCurrentFrame();
        MessageBox msgBox = new MessageBox(frame, title, label);
        msgBox.show();
    }
}
