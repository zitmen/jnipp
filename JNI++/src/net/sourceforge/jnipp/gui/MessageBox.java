package net.sourceforge.jnipp.gui;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.event.*;

public class MessageBox extends JDialog implements ActionListener {

    JButton cmdOk;

    //TODO: Make this thing ALOT less ugly.
    public MessageBox(JFrame parentframe, String title, String label) {
        super(parentframe, title, true);

        JLabel lbl = new JLabel(label);

        cmdOk = new JButton("Ok");
        cmdOk.addActionListener(this);

        getContentPane().add(lbl, BorderLayout.NORTH);
        getContentPane().add(cmdOk, BorderLayout.SOUTH);

        initWindowListener();
        pack();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        hide();
    }

    private void initWindowListener() {
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                hide();
            }
        });
    }
}
