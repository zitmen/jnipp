package net.sourceforge.jnipp.project;

import org.w3c.dom.Node;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Class for manipulating NMake makefile generator settings.
 *
 * Each instance of this class contains data utilized to specify and query
 * settings controlling the manner in which the NMake makefile generator is run.
 * Each
 * <code>Project</code> instance contains zero or one
 * <code>NMakefileSettings</code> instance.
 *
 * @author $Author: ptornroth $
 * @version $Revision: 1.12 $
 * @see net.sourceforge.jnipp.project.Project#nMakefileSettings
 */
public class NMakefileSettings {

    /**
     * The name of the target makefile.
     */
    private String name = null;
    /**
     * Reference to the owning
     * <code>Project</code> instance.
     */
    private Project project = null;

    /**
     * Public class constructor.
     *
     * The class constructor sets the owning
     * <code>Project</code> instance and then calls the
     * <code>initialize()</code> method using the Element node passed in as
     * parameter.
     *
     * @param Project The owning <code>Project</code> instance.
     * @param elementNode The DOM representation of the settings.
     * @exception ProjectFormatException
     * @see #project
     * @see #initialize
     */
    public NMakefileSettings(Project project, Element elementNode)
            throws DOMException, ProjectFormatException {
        this.project = project;
        initialize(elementNode);
    }

    /**
     * Public class constructor.
     *
     * The class constructor sets the owning
     * <code>Project</code> instance and the name String value.
     *
     * @param project The owning <code>Project</code> instance.
     * @param name The String representing the name.
     * @see #project
     */
    public NMakefileSettings(Project project, String name) {
        this.project = project;
        setName(name);
    }

    /**
     * Public class instance initializer.
     *
     * The
     * <code>initialize()</code> method is called to initialize the settings for
     * this
     * <code>NMakefileSettings</code> instance using the elements contained in
     * the supplied
     * <code>Element</code> parameter. This method is called from the
     * constructor.
     *
     * @param elementNode The <code>Element</code> containing the elements that
     * specify the settings for the NMake makefile generator.
     * @exception ProjectFormatException
     * @see #NMakefileSettings
     */
    private void initialize(Element elementNode)
            throws DOMException, ProjectFormatException {
        if (elementNode.hasAttribute("name") == false) {
            throw new ProjectFormatException("attribute \"name\" required for \"nmakefile\" element");
        }
        name = elementNode.getAttribute("name");
    }

    /**
     * Public accessor for the
     * <code>name</code> field.
     *
     * @return The name of the makefile.
     * @see #name
     */
    public String getName() {
        return name;
    }

    /**
     * Public mutator for the
     * <code>name</code> field.
     *
     * @param name The name of the makefile.
     * @see #name
     */
    public void setName(String name) {
        this.name = name;
    }

    public Project getProject() {
        return project;
    }

    public Node getDOMNode(Document targetDoc) {
        Element node = targetDoc.createElement("nmakefile");
        node.setAttribute("name", name);

        return node;
    }
}
