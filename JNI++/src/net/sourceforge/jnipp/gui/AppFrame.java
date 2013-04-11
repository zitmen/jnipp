package net.sourceforge.jnipp.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class AppFrame extends JFrame {

    JPanel contentPane;
    ImageIcon image1;
    ImageIcon image2;
    ImageIcon image3;
    ProjectViewer projectViewer;
    ProjectDetailsPane detailsPane;
    StatusPanel statusBar = new StatusPanel();

    /**
     * Construct the frame
     */
    public AppFrame() {
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Component initialization
     */
    private void jbInit() throws Exception {
        setIconImage(Toolkit.getDefaultToolkit().createImage(AppFrame.class.getResource("/net/sourceforge/jnipp/gui/resources/jnippIcon.gif")));

        GridBagLayout myLayout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        contentPane = (JPanel) this.getContentPane();
        contentPane.setLayout(myLayout);
        this.setSize(new Dimension(600, 400));
        this.setTitle("JNI++ -");

        ProjectAdapter project = App.getProject();
        if (project != null) {
            projectViewer = new ProjectViewer(project);
        } else {
            projectViewer = new ProjectViewer();
        }
        detailsPane = new ProjectDetailsPane();

        this.setJMenuBar(GuiUtils.loadMenus());

        c.ipadx = 3;
        c.ipady = 3;
        c.insets = new Insets(0, 0, 0, 0);

        c.anchor = GridBagConstraints.NORTHWEST;

        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = GridBagConstraints.RELATIVE;
        c.gridheight = GridBagConstraints.RELATIVE;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = .5;

        JSplitPane horizontalSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, projectViewer, detailsPane);
        horizontalSplit.setDividerLocation(250);
        JSplitPane verticalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, horizontalSplit, statusBar);

        myLayout.setConstraints(verticalSplit, c);
        contentPane.add(verticalSplit);
    }

    /**
     * Help | About action performed
     */
    public void jMenuHelpAbout_actionPerformed(ActionEvent e) {
        AboutBox dlg = new AboutBox(this);
        Dimension dlgSize = dlg.getPreferredSize();
        Dimension frmSize = getSize();
        Point loc = getLocation();
        dlg.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
        dlg.setModal(true);
        dlg.show();
    }

    /**
     * Overridden so we can exit when window is closed
     */
    @Override
    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            System.exit(0);
        }
    }
}
