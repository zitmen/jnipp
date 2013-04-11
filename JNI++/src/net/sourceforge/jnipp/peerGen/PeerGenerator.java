package net.sourceforge.jnipp.peerGen;

import java.util.Iterator;
import net.sourceforge.jnipp.common.ClassNode;
import net.sourceforge.jnipp.project.Project;
import net.sourceforge.jnipp.project.PeerGenSettings;
import java.util.Collection;
import java.util.ArrayList;
import net.sourceforge.jnipp.main.DependencyData;
import java.io.File;
import net.sourceforge.jnipp.project.ProjectFormatException;
import net.sourceforge.jnipp.proxyGen.ProxyGenerator;
import net.sourceforge.jnipp.project.ProxyGenSettings;

/**
 * Entry point for invoking the Peer Generator.
 *
 * This class serves as the entry point to invoke the Peer Generator and is
 * called from the
 * <code>main</code> package and from the GUI. It coordinates all of the
 * activities required to generate native C++ peer and Java proxy classes that
 * can be used to simplify the creation and use of native code libraries.
 *
 * @author $Author: ptrewhella $
 * @version $Revision: 1.15 $
 */
public class PeerGenerator {

    /**
     * Peer header file generator.
     *
     * This variable contains a reference to the
     * <code>CPPPeerHeaderGenerator</code> instance utilized to generate the
     * declaration file for the C++ peer class.
     */
    private static CPPPeerHeaderGenerator peerHeaderGenerator = new CPPPeerHeaderGenerator();
    /**
     * Peer implementation file generator.
     *
     * This variable contains a reference to the
     * <code>CPPPeerImplGenerator</code> instance utilized to generate the
     * implementation file for the C++ peer class.
     */
    private static CPPPeerImplGenerator peerImplGenerator = new CPPPeerImplGenerator();
    /**
     * Mapping header file generator.
     *
     * This variable contains a reference to the
     * <code>CPPMappingCodeHeaderGenerator</code> instance utilized to generate
     * the header file for the procedural mapping code.
     */
    private static JNIMappingCodeHeaderGenerator mappingCodeHeaderGenerator = new JNIMappingCodeHeaderGenerator();
    /**
     * Mapping implementation file generator.
     *
     * This variable contains a reference to the
     * <code>CPPMappingCodeImplGenerator</code> instance utilized to generate
     * the implemenation file for the procedural mapping code.
     */
    private static JNIMappingCodeImplGenerator mappingCodeImplGenerator = new JNIMappingCodeImplGenerator();
    /**
     * Java proxy class file generator.
     *
     * This variable contains a reference to the
     * <code>JavaProxyGenerator</code> instance utilized to generate the Java
     * proxy class file that will be utilized to access the native code from
     * Java.
     */
    private static JavaProxyGenerator javaProxyGenerator = new JavaProxyGenerator();
    private static CPPPeerFactoryHeaderGenerator peerFactoryHeaderGenerator = new CPPPeerFactoryHeaderGenerator();

    /**
     * Public entry point.
     *
     * This method is invoked to generate the C++ peer header and implementation
     * files, the requisite mapping code and a Java proxy class for the Java
     * class specified in the supplied settings.
     *
     * @param peerGenSettings The settings used to control the code generation.
     * @return Collection of all <code>DependencyData</code> instances that were
     * processed.
     * @exception ClassNotFoundException
     * @exception java.io.IOException
     * @see net.sourceforge.jnipp.main.DependencyData
     * @see net.sourceforge.jnipp.project.Project
     */
    public static Collection generate(PeerGenSettings peerGenSettings)
            throws ClassNotFoundException, java.io.IOException, ProjectFormatException {
        ArrayList classList = new ArrayList();
        Iterator inputClassIterator = peerGenSettings.getClassNames();

        while (inputClassIterator.hasNext() == true) {
            ClassNode classNode = ClassNode.getClassNode((String) inputClassIterator.next());
            if (classNode.isInterface() == false) {
                throw new ProjectFormatException(classNode.getFullyQualifiedClassName() + " is not an interface.");
            }

            classList.add(new DependencyData(classNode.getPackageName().replace('.', File.separatorChar),
                    classNode.getCPPClassName() + "Peer.h",
                    classNode.getCPPClassName() + "Peer.cpp"));
            classList.add(new DependencyData(classNode.getPackageName().replace('.', File.separatorChar),
                    classNode.getCPPClassName() + "Mapping.h",
                    classNode.getCPPClassName() + "Mapping.cpp"));

            System.out.println("generating C++ Peer Class for " + classNode.getFullyQualifiedClassName() + " ...");
            peerHeaderGenerator.generate(classNode, peerGenSettings);
            peerImplGenerator.generate(classNode, peerGenSettings);
            mappingCodeHeaderGenerator.generate(classNode, peerGenSettings);
            mappingCodeImplGenerator.generate(classNode, peerGenSettings);
            javaProxyGenerator.generate(classNode, peerGenSettings);
            peerFactoryHeaderGenerator.generate(classNode, peerGenSettings);

            if (peerGenSettings.getUseRichTypes() == true) {
                ProxyGenSettings proxyGenSettings = peerGenSettings.getProxyGenSettings();
                Iterator deps = classNode.getDependencies();
                while (deps.hasNext() == true) {
                    ClassNode next = (ClassNode) deps.next();
                    if (next.isArray() == true) {
                        proxyGenSettings.addClassName(next.getJNIString().replace('/', '.'));
                    } else {
                        proxyGenSettings.addClassName(next.getFullyQualifiedClassName());
                    }
                }
            }
        }

        if (peerGenSettings.getUseRichTypes() == true) {
            classList.addAll(ProxyGenerator.generate(peerGenSettings.getProxyGenSettings()));
        }

        return classList;
    }
}
