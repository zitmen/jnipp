/*
 * JnippView.java - Main class of the JNI++ utility
 */
package net.sourceforge.jnipp.gui;

import net.sourceforge.jnipp.project.Project;
import java.util.Properties;
import java.io.*;

public class App {

    private static Properties properties;
    private static AppFrame appFrame;
    private static UserContext usercontext;

    public static void main(String[] args) {
        //create default project
        //replace this with loading last one if it's there.
        Project project = new Project();
        project.setName("New Project");
        usercontext = new UserContext();
        usercontext.setProject(new ProjectAdapter(project));

        loadGuiProperties();
        appFrame = new AppFrame();
        appFrame.show();
    }

    private static void loadGuiProperties() {
        if (properties == null) {
            properties = new Properties();
        }
        try {
            InputStream props = App.class.getResourceAsStream(
                    "/net/sourceforge/jnipp/gui/resources/gui.properties");
            props = new BufferedInputStream(props);
            properties.load(props);
            props.close();
        } catch (IOException e) {
            //loading properties file failed
            e.printStackTrace();
        }
    }

    public static String getProperty(String propertyName) {
        if (properties == null) {
            loadGuiProperties();
        }
        return properties.getProperty(propertyName);
    }

    public static AppFrame getCurrentFrame() {
        return appFrame;
    }

    public static ProjectAdapter getProject() {
        return usercontext.getProject();
    }

    public static UserContext getUserContext() {
        return usercontext;
    }
}
