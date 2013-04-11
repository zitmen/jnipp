package net.sourceforge.jnipp.main;

import java.io.File;

/**
 * Helper class for makefile generators.
 *
 *
 * @author $Author: ptrewhella $
 * @version $Revision: 1.2 $
 */
public class DependencyData {

    private String path = null;
    private String headerFileName = null;
    private String cppFileName = null;

    public DependencyData(String path, String headerFileName, String cppFileName) {
        this.path = path;
        this.headerFileName = headerFileName;
        this.cppFileName = cppFileName;
    }

    public String getPath() {
        return path;
    }

    public String getHeaderFileName() {
        return headerFileName;
    }

    public String getCPPFileName() {
        return cppFileName;
    }

    public String getFullHeaderFileName() {
        if (path == null || path.equals("") == true) {
            return headerFileName;
        }
        return path + File.separatorChar + headerFileName;
    }

    public String getFullCPPFileName() {
        if (path == null || path.equals("") == true) {
            return cppFileName;
        }
        return path + File.separatorChar + cppFileName;
    }
}
