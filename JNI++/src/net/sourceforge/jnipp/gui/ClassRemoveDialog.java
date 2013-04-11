/*
 * ClassChooserDialog.java
 */
package net.sourceforge.jnipp.gui;

import java.util.Iterator;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ClassRemoveDialog extends JDialog implements ActionListener {

    JPanel contentPane;
    JList classList;
    DefaultListModel listModel;

    public ClassRemoveDialog(Frame owner) {
        super(owner, "JNI++", true);
        init();
    }

    public ArrayList getClassesAffected() {
        ArrayList theList = new ArrayList();
        int[] selected = classList.getSelectedIndices();
        for (int i = 0; i < selected.length; ++i) {
            String item = (String) classList.getModel().getElementAt(i);
            theList.add(item);
        }
        return (theList);
    }

    private void init() {
        initWindowListener();
        JButton cmdOK = new JButton(App.getProperty("button-ok.label"));
        cmdOK.setActionCommand("ok");
        cmdOK.addActionListener(this);

        JButton cmdCancel = new JButton(App.getProperty("button-cancel.label"));
        cmdCancel.setActionCommand("cancel");
        cmdCancel.addActionListener(this);

        JLabel instructions = new JLabel(App.getProperty("message.remove-class-instruction"));

        classList = new JList();
        listModel = new DefaultListModel();
        classList.setModel(listModel);
        populateList();

        GridBagLayout myLayout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        getContentPane().setLayout(myLayout);

        c.ipadx = 2;
        c.ipady = 2;
        c.insets = new Insets(5, 5, 5, 5);

        c.gridwidth = 2;
        myLayout.setConstraints(instructions, c);

        c.gridy = 2;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        myLayout.setConstraints(classList, c);


        c.anchor = GridBagConstraints.EAST;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.weighty = 0;
        c.gridwidth = 1;
        c.gridx = 2;
        c.gridy = 3;
        myLayout.setConstraints(cmdOK, c);
        c.gridx = 3;
        myLayout.setConstraints(cmdCancel, c);

        this.setSize(new Dimension(400, 300));
        getContentPane().add(instructions);
        getContentPane().add(classList);
        getContentPane().add(cmdOK);
        getContentPane().add(cmdCancel);
        getRootPane().setDefaultButton(cmdOK);
    }

    private void populateList() {
        //go through project and add classes to list.
        ProjectAdapter project = App.getProject();
        Iterator it = project.getClasses();
        while (it.hasNext() == true) {
            ProjectClass field = (ProjectClass) it.next();
            listModel.addElement(field.toString());
        }
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        if (e.getActionCommand().equals("ok")) {
            hide();
        } else {
            //TODO: clear selection
            hide();
        }

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
