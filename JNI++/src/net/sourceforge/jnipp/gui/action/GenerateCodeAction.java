package net.sourceforge.jnipp.gui.action;

import net.sourceforge.jnipp.main.*;
import net.sourceforge.jnipp.project.*;
import net.sourceforge.jnipp.gui.*;
import java.awt.event.*;
import java.io.IOException;

/**
 * Action for code generation.
 *
 * This class implements the use of the
 * <code>net.sourceforge.jnipp.peergen</code> and
 * <code>net.sourceforge.jnipp.proxygen</code> packages for generating peer and
 * proxy code. GenerateCodeAction is a singleton.
 *
 * @author $Author: ptornroth $
 * @version $Revision: 1.3 $
 */
public class GenerateCodeAction implements ActionListener {

    /**
     * Default constructor.
     */
    public GenerateCodeAction() {
    }

    /**
     * The implementation of actionPerformed This method initiates the code
     * generation.
     *
     * @param e The ActionEvent object for the action fired.
     * @see net.sourceforge.jnipp.gui.peergen.PeerGenerator
     * @see net.sourceforge.jnipp.gui.proxygen.ProxyGenerator
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        ProjectAdapter projectadapter = App.getProject();
        Project project = projectadapter.getProject();
        if (project == null) {
            return;
        }

        try {
            Main main = new Main();
            main.generate(project);
        } catch (ClassNotFoundException | IOException | ProjectFormatException err) {
            System.out.println(err);
        }
    }
}
