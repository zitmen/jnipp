package net.sourceforge.jnipp.main;

import net.sourceforge.jnipp.common.CommandLineParser;
import net.sourceforge.jnipp.project.Project;
import net.sourceforge.jnipp.peerGen.PeerGenerator;
import net.sourceforge.jnipp.proxyGen.ProxyGenerator;
import java.util.Iterator;
import java.util.ArrayList;
import java.io.IOException;
import net.sourceforge.jnipp.project.PeerGenSettings;
import net.sourceforge.jnipp.project.ProxyGenSettings;
import net.sourceforge.jnipp.project.ProjectFormatException;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

/**
 * Entry point to invoke code generators.
 *
 * This class provides a
 * <code>main()</code> method that accepts a project file name as parameter,
 * attempts to open the file and, if successful, will invoke the code generators
 * as specified therein.
 *
 * @author $Author: ptrewhella $
 * @version $Revision: 1.13 $
 */
public class Main {

    /**
     * Public entry point.
     *
     * This
     * <code>main()</code> method will attempt to load the project file
     * identified by the
     * <code>projectFile</code> argument from the command line. It will delegate
     * to the
     * <code>generate()</code> method to coordinate the code generation.
     *
     * @param args The command-line argument vector.
     * @see #generate
     * @see net.sourceforge.jnipp.project.Project
     */
    public static void main(String args[]) {
        try {
            CommandLineParser clp = new CommandLineParser(args);

            String projectFileName = clp.getParamValue("projectFile");
            if (projectFileName == null) {
                System.out.println("missing projectFile argument");
                return;
            }

            Project project = new Project();
            project.load(projectFileName);

            generate(project);
        } catch (SAXException | IOException | DOMException | ProjectFormatException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        System.exit(0);
    }

    /**
     * Private method that coordinates code generation activities.
     *
     * This method is called by the
     * <code>main()</code> method to invoke the code generations as specified by
     * the project file that has been read in.
     *
     * @param project The <code>Project</code> instance containing the code
     * generation settings.
     * @exception ClassNotFoundException
     * @exception IOException
     * @see #main
     */
    public static void generate(Project project)
            throws ClassNotFoundException, IOException, ProjectFormatException {
        ArrayList proxyClassList = new ArrayList();
        ArrayList peerClassList = new ArrayList();
        Iterator it = project.getProxyGenSettings();
        while (it.hasNext() == true) {
            proxyClassList.addAll(ProxyGenerator.generate((ProxyGenSettings) it.next()));
        }
        it = project.getPeerGenSettings();
        while (it.hasNext() == true) {
            peerClassList.addAll(PeerGenerator.generate((PeerGenSettings) it.next()));
        }
        if (project.getNMakefileSettings() != null) {
            NMakefileGenerator.generate(project.getNMakefileSettings(), peerClassList, proxyClassList);
        }
        if (project.getGNUMakefileSettings() != null) {
            GNUMakefileGenerator.generate(project.getGNUMakefileSettings(), peerClassList, proxyClassList);
        }
        System.out.println("\ncode generation complete.");
    }
}
