package net.sourceforge.jnipp.peerGen;

import java.util.*;
import net.sourceforge.jnipp.common.*;
import net.sourceforge.jnipp.project.PeerGenSettings;
import java.io.File;

public class CPPPeerImplGenerator {

    private PeerGenSettings peerGenSettings = null;

    public CPPPeerImplGenerator() {
    }

    public void generate(ClassNode root, PeerGenSettings peerGenSettings)
            throws java.io.IOException {
        this.peerGenSettings = peerGenSettings;

        String fullFileName = peerGenSettings.getProject().getCPPOutputDir() + File.separatorChar;
        if (root.getPackageName().equals("") == true) {
            fullFileName = root.getCPPClassName() + "Peer.cpp";
        } else {
            fullFileName = root.getPackageName().replace('.', File.separatorChar) + File.separatorChar + root.getCPPClassName() + "Peer.cpp";
        }

        FormattedFileWriter writer = new FormattedFileWriter(fullFileName, peerGenSettings.isDestructive() || peerGenSettings.getUseInheritance());

        writer.outputLine("#include \"" + root.getCPPClassName() + "Peer.h\"");
        writer.newLine(1);
        Iterator it = root.getNamespaceElements();
        if (it.hasNext() == true) {
            writer.output("using namespace " + (String) it.next());
            while (it.hasNext() == true) {
                writer.output("::" + (String) it.next());
            }
            writer.outputLine(";");
            writer.newLine(1);
        }

        writer.outputLine(root.getCPPClassName() + "Peer::" + root.getCPPClassName() + "Peer()");
        writer.outputLine("{");
        writer.outputLine("}");
        writer.newLine(1);
        generateMethods(root, writer);

        writer.flush();
        writer.close();
    }

    public void generateMethods(ClassNode root, FormattedFileWriter writer)
            throws java.io.IOException {
        writer.outputLine("// methods");
        Iterator it = root.getMethods();
        while (it.hasNext() == true) {
            int currentIndex = 0;
            MethodNode node = (MethodNode) it.next();
            if (peerGenSettings.getUseRichTypes() == true) {
                writer.output(node.getReturnType().getJNITypeName(peerGenSettings.getProject().getUsePartialSpec())
                        + " "
                        + root.getCPPClassName()
                        + "Peer::"
                        + node.getCPPName());
            } else {
                writer.output(node.getReturnType().getPlainJNITypeName()
                        + " "
                        + root.getCPPClassName()
                        + "Peer::"
                        + node.getUniqueCPPName());
            }
            writer.output("(JNIEnv* env, jobject obj");
            Iterator params = node.getParameterList();
            while (params.hasNext() == true) {
                writer.output(", ");
                ClassNode currentParam = (ClassNode) params.next();
                if (peerGenSettings.getUseRichTypes() == true) {
                    writer.output(currentParam.getJNITypeName(peerGenSettings.getProject().getUsePartialSpec()) + " p" + currentIndex++);
                } else {
                    writer.output(currentParam.getPlainJNITypeName() + " p" + currentIndex++);
                }
            }
            writer.outputLine(")");
            writer.outputLine("{");
            writer.incTabLevel();
            if (peerGenSettings.getUseInheritance() == false) {
                writer.outputLine("// TODO: Fill in your implementation here");
            } else {
                if (node.getReturnType().getPlainJNITypeName().equals("void") == false) {
                    writer.outputLine("return "
                            + (node.getReturnType().isPrimitive() == true ? "static_cast" : "reinterpret_cast")
                            + "<" + node.getReturnType().getPlainJNITypeName() + ">( 0 );");
                }
            }
            writer.decTabLevel();
            writer.outputLine("}");
            writer.newLine(1);
        }
    }
}
