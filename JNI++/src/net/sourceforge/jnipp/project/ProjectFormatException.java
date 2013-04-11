package net.sourceforge.jnipp.project;

/**
 * Exception class for project file errors.
 *
 * An instance of this exception is thrown to indicate a problem with the
 * project file being parsed.
 *
 * @author $Author: ptrewhella $
 * @version $Revision: 1.1 $
 */
public class ProjectFormatException
        extends Exception {

    public ProjectFormatException(String msg) {
        super(msg);
    }
}
