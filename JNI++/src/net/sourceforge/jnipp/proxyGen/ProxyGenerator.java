package net.sourceforge.jnipp.proxyGen;

import java.util.HashMap;
import java.util.Iterator;
import net.sourceforge.jnipp.common.ClassNode;
import net.sourceforge.jnipp.project.ProxyGenSettings;
import java.util.Collection;
import net.sourceforge.jnipp.main.DependencyData;
import java.io.File;

/**
 * Entry point for invoking the Proxy Generator.
 *
 * This class serves as the entry point to invoke the Proxy Generator and is
 * called from the
 * <code>main</code> package and from the GUI. It coordinates all of the
 * activities required to generate native C++ proxy classes that can be used to
 * automate their Java counterparts on the other side of the JNI.
 *
 * @author $Author: ptrewhella $
 * @version $Revision: 1.25 $
 */
public class ProxyGenerator {

    /**
     * Header file generator.
     *
     * This variable contains a reference to the
     * <code>CPPProxyHeaderGenerator</code> instance utilized to generate the
     * declaration file for the C++ proxy class.
     */
    private static CPPProxyHeaderGenerator headerGen = new CPPProxyHeaderGenerator();
    /**
     * Implementation file generator.
     *
     * This variable contains a reference to the
     * <code>CPPProxyImplGenerator</code> instance utilized to generate the
     * implementation file for the C++ proxy class.
     */
    private static CPPProxyImplGenerator implGen = new CPPProxyImplGenerator();
    private static CPPProxyForwardHeaderGenerator forwGen = new CPPProxyForwardHeaderGenerator();

    /**
     * Public entry point.
     *
     * This method is invoked to generate the C++ proxy header and
     * implementation files for all of the Java classes specified in the
     * supplied settings.
     *
     * @param proxyGenSettings The settings used to control the code generation.
     * @return Collection of all <code>ClassNode</code> instances that were
     * processed.
     * @exception ClassNotFoundException
     * @exception java.io.IOException
     * @see net.sourceforge.jnipp.common.ClassNode
     * @see net.sourceforge.jnipp.project.Project
     */
    public static Collection generate(ProxyGenSettings proxyGenSettings)
            throws ClassNotFoundException, java.io.IOException {
        HashMap alreadyGenerated = new HashMap();
        Iterator inputClassIterator = proxyGenSettings.getClassNames();
        while (inputClassIterator.hasNext() == true) {
            ClassNode classNode = ClassNode.getClassNode((String) inputClassIterator.next());
            generate(classNode, alreadyGenerated, proxyGenSettings);
        }

        return alreadyGenerated.values();
    }

    /**
     * Recursive code generation method.
     *
     * This method is the workhorse of the code generator. It is initially
     * called from the public
     * <code>generate()</code> entry point method, but then also possibly calls
     * itself recursively, depending on the settings passed in.
     *
     * @param classNode The <code>ClassNode</code> of the class for which the
     * proxy is to be generated.
     * @param alreadyGenerated Map used to prevent generating already generated
     * proxy.
     * @param proxyGenSettings The settings used to control the code generation.
     * @exception ClassNotFoundException
     * @exception java.io.IOException
     * @see net.sourceforge.jnipp.common.ClassNode
     */
    private static void generate(ClassNode classNode, HashMap alreadyGenerated, ProxyGenSettings proxyGenSettings)
            throws ClassNotFoundException, java.io.IOException {
        if (classNode.isBuiltIn() == true || alreadyGenerated.containsKey(classNode.getFullyQualifiedClassName()) == true) {
            return;
        }

        System.out.println("generating C++ Proxy Class for " + classNode.getFullyQualifiedClassName() + " ...");

        alreadyGenerated.put(classNode.getFullyQualifiedClassName(),
                new DependencyData(classNode.getPackageName().replace('.', File.separatorChar),
                classNode.getCPPClassName() + "Proxy.h",
                classNode.getCPPClassName() + "Proxy.cpp"));

        forwGen.generate(classNode, proxyGenSettings);
        headerGen.generate(classNode, proxyGenSettings);
        implGen.generate(classNode, proxyGenSettings);

        if (proxyGenSettings.getUseRichTypes() == true) {
            Iterator deps = classNode.getDependencies();
            while (deps.hasNext() == true) {
                generate((ClassNode) deps.next(), alreadyGenerated, proxyGenSettings);
            }
        } else if (proxyGenSettings.getRecursionLevel() > 0) {
            int newLevel = proxyGenSettings.getRecursionLevel() - 1;
            proxyGenSettings.setRecursionLevel(newLevel);
            Iterator deps = classNode.getDependencies();
            while (deps.hasNext() == true) {
                generate((ClassNode) deps.next(), alreadyGenerated, proxyGenSettings);
            }
        }

        if (proxyGenSettings.getUseInheritance() == true) {
            if (classNode.isInterface() == false) {
                if (classNode.getSuperClass() != null) {
                    generate(classNode.getSuperClass(), alreadyGenerated, proxyGenSettings);
                }
            } else {
                Iterator intfcs = classNode.getInterfaces();
                while (intfcs.hasNext() == true) {
                    generate((ClassNode) intfcs.next(), alreadyGenerated, proxyGenSettings);
                }
            }
        }

        /*
         if ( proxyGenSettings.getGenerateInnerClasses() == true )
         {
         Iterator inners = classNode.getInnerClasses();
         while ( inners.hasNext() == true )
         generate( (ClassNode) inners.next(), alreadyGenerated, proxyGenSettings );
         }
         */
    }
}
