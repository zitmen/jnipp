package net.sourceforge.jnipp.gui.action;

import java.awt.event.*;

public class ExitApplicationAction implements ActionListener {

    public ExitApplicationAction() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //TODO: implement 'save first' functionality.
        System.exit(0);
    }
}
