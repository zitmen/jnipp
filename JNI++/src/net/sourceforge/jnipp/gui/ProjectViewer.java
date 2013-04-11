package net.sourceforge.jnipp.gui;

import net.sourceforge.jnipp.gui.appevent.*;
import javax.swing.JTree;
import javax.swing.JScrollPane;
import javax.swing.tree.*;
import javax.swing.event.*;
import java.awt.*;
import java.util.Iterator;
import javax.swing.ImageIcon;
import javax.swing.ToolTipManager;

public class ProjectViewer extends JScrollPane implements AppEventSubscriber {

    private JTree tree;
    DefaultMutableTreeNode root;

    public ProjectViewer() {
        super();
        initTree("");
    }

    public ProjectViewer(ProjectAdapter project) {
        super();
        initTree(project.getName());
        buildProjectView(project, root);
        tree.expandRow(0);
    }

    private void initTree(String rootLabel) {
        subscribeToProjectUpdates();
        root = new DefaultMutableTreeNode(rootLabel);
        tree = new JTree(root);
        tree.setSize(this.getSize());
        ToolTipManager.sharedInstance().registerComponent(tree);
        tree.setCellRenderer(new ProjectTreeRenderer());

        tree.addTreeSelectionListener(
                new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent evt) {
                DefaultMutableTreeNode node =
                        (DefaultMutableTreeNode) (evt.getPath().getLastPathComponent());
                process(node);
            }
        });
        this.viewport.add(tree);
    }

    private void process(DefaultMutableTreeNode node) {
        UserContextUpdatedAppEvent event = new UserContextUpdatedAppEvent();
        UserContext context = event.getContext();
        ProjectAdapter project = context.getProject();

        if ((node.isRoot()) || (node.getParent() == node.getRoot())) {
            context.setActiveContext(UserContext.ContextType_Project, project);
        } else {
            //we should be a class
            try {
                ProjectClass projectclass = (ProjectClass) node.getUserObject();
                context.setActiveContext(UserContext.ContextType_Class, projectclass);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    public void setProject(ProjectAdapter project) {
        root.removeAllChildren();
        root.setUserObject(project.getName());
        buildProjectView(project, root);
        tree.setModel(new DefaultTreeModel(root));
        tree.treeDidChange();
    }

    private void buildProjectView(ProjectAdapter project, DefaultMutableTreeNode root) {
        DefaultMutableTreeNode inputClasses = new DefaultMutableTreeNode("classes");
        root.add(inputClasses);

        Iterator it = project.getClasses();
        while (it.hasNext() == true) {
            DefaultMutableTreeNode classNode = new DefaultMutableTreeNode((ProjectClass) it.next());
            inputClasses.add(classNode);
        }
    }

    //AppEventSubscriber method(s)
    @Override
    public void UpdateAvailable(AppEvent e) {
        ProjectUpdatedAppEvent subject = (ProjectUpdatedAppEvent) e;
        setProject(subject.getProject());
        ProjectAdapter theproject = subject.getProject();
        setProject(theproject);
    }

    private void subscribeToProjectUpdates() {
        ProjectUpdatedAppEvent.subscribe(this);
    }

    protected class ProjectTreeRenderer extends DefaultTreeCellRenderer {

        ImageIcon classesIcon;
        ImageIcon projectIcon;
        ImageIcon classIcon;

        public ProjectTreeRenderer() {
            projectIcon = new ImageIcon(ProjectTreeRenderer.class.getResource("/net/sourceforge/jnipp/gui/resources/projectIcon.gif"));
            classesIcon = new ImageIcon(ProjectTreeRenderer.class.getResource("/net/sourceforge/jnipp/gui/resources/classesIcon.gif"));
            classIcon = new ImageIcon(ProjectTreeRenderer.class.getResource("/net/sourceforge/jnipp/gui/resources/classIcon.gif"));
        }

        @Override
        public Component getTreeCellRendererComponent(
                JTree tree,
                Object value,
                boolean sel,
                boolean expanded,
                boolean leaf,
                int row,
                boolean hasFocus) {

            super.getTreeCellRendererComponent(
                    tree, value, sel,
                    expanded, leaf, row,
                    hasFocus);

            if (isProject(value)) {
                setIcon(projectIcon);
                setToolTipText(null);
                expanded = true;
            } else if (isClassesCollection(value)) {
                setIcon(classesIcon);
                setToolTipText(null);
                expanded = true;
                hasFocus = false;
            } else {
                // it's a class
                setIcon(classIcon);
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                String nodeInfo = node.toString();
                setToolTipText(nodeInfo);
            }

            return this;
        }

        protected boolean isProject(Object value) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            if (node.isRoot()) {
                return true;
            }
            return false;
        }

        protected boolean isClassesCollection(Object value) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            String nodeInfo = node.toString();
            if (nodeInfo.equals("classes")) {
                return true;
            }
            return false;
        }
    }
}
