package net.sourceforge.jnipp.peerGen;

import java.util.*;
import net.sourceforge.jnipp.common.*;
import net.sourceforge.jnipp.project.PeerGenSettings;
import java.io.File;

public class CPPPeerFactoryHeaderGenerator {

    private PeerGenSettings peerGenSettings = null;

    public CPPPeerFactoryHeaderGenerator() {
    }

    public void generate(ClassNode root, PeerGenSettings peerGenSettings)
            throws java.io.IOException {
        this.peerGenSettings = peerGenSettings;

        String fullFileName = peerGenSettings.getProject().getCPPOutputDir() + File.separatorChar;
        if (root.getPackageName().equals("") == true) {
            fullFileName = root.getCPPClassName() + "PeerFactory.h";
        } else {
            fullFileName = root.getPackageName().replace('.', File.separatorChar) + File.separatorChar + root.getCPPClassName() + "PeerFactory.h";
        }

        FormattedFileWriter writer = new FormattedFileWriter(fullFileName, peerGenSettings.getUseInheritance() == false);
        String def = "__" + root.getPackageName().replace('.', '_') + "_" + root.getCPPClassName() + "PeerFactory_H";
        writer.outputLine("#ifndef " + def);
        writer.outputLine("#define " + def);
        writer.newLine(2);

        writer.outputLine("#include \"" + root.getCPPClassName() + "Peer.h\"");
        writer.newLine(1);

        Iterator it = root.getNamespaceElements();
        int nestedLevels = 0;
        while (it.hasNext() == true) {
            writer.outputLine("namespace " + (String) it.next());
            writer.outputLine("{");
            writer.incTabLevel();
            ++nestedLevels;
        }

        writer.outputLine("class " + root.getCPPClassName() + "PeerFactory");
        writer.outputLine("{");
        writer.outputLine("public:");
        writer.incTabLevel();
        writer.outputLine("static inline " + root.getCPPClassName() + "Peer* newPeer()");
        writer.outputLine("{");
        writer.incTabLevel();
        if (peerGenSettings.getUseInheritance() == true) {
            writer.outputLine("/*");
            writer.outputLine(" * TODO: Implement the factory method.  For example:");
            writer.outputLine(" * return new " + root.getCPPClassName() + "PeerImpl;");
            writer.outputLine(" */");
        } else {
            writer.outputLine("return new " + root.getCPPClassName() + "Peer;");
        }
        writer.decTabLevel();
        writer.outputLine("}");
        writer.decTabLevel();
        writer.outputLine("};");

        for (int i = 0; i < nestedLevels; ++i) {
            writer.decTabLevel();
            writer.outputLine("};");
        }

        writer.newLine(2);
        writer.outputLine("#endif");
        writer.flush();
        writer.close();
    }
}
