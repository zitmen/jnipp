package net.sourceforge.jnipp.proxyGen;

import java.util.Iterator;
import java.util.HashMap;
import net.sourceforge.jnipp.common.*;
import net.sourceforge.jnipp.project.ProxyGenSettings;
import java.io.File;

/**
 * C++ proxy class implementation file generator.
 *
 * This class is responsible for generating the C++ proxy implementation file
 * for a specified Java class. The specifics of the code generation process are
 * guided by the settings supplied in the call to the
 * <code>generate()</code> method.
 *
 * @author $Author: ptrewhella $
 * @version $Revision: 1.40 $
 */
public class CPPProxyImplGenerator {

    /**
     * Settings specifying the code generation options.
     *
     * This reference points to the
     * <code>ProxyGenSettings</code> instance passed into the
     * <code>generate()</code> method and is used by various methods to guide
     * the code generation.
     *
     * @see #generate
     */
    private ProxyGenSettings proxyGenSettings = null;

    /**
     * Defualt constructor.
     */
    public CPPProxyImplGenerator() {
    }

    /**
     * Public entry point.
     *
     * This method is called to begin the code generation process for the
     * specified Java class. This method will call one or more of the
     * <code>private</code> helper methods to perform the various code
     * generation tasks.
     *
     * @param root The <code>ClassNode</code> instance referencing the Java
     * class for which we are generating code.
     * @param proxyGenSettings The settings used to obtain the code generation
     * options.
     * @exception java.io.IOException
     */
    public void generate(ClassNode root, ProxyGenSettings proxyGenSettings)
            throws java.io.IOException {
        if (root.isPrimitive() == true || root.needsProxy() == false) {
            return;
        }

        this.proxyGenSettings = proxyGenSettings;

        String fullFileName = proxyGenSettings.getProject().getCPPOutputDir() + File.separatorChar;
        if (root.getPackageName().equals("") == true) {
            fullFileName = root.getCPPClassName() + "Proxy.cpp";
        } else {
            fullFileName = root.getPackageName().replace('.', File.separatorChar) + File.separatorChar + root.getCPPClassName() + "Proxy.cpp";
        }

        FormattedFileWriter writer = new FormattedFileWriter(fullFileName, true);

        generateIncludes(root, writer);
        generateUsing(root, writer);

        writer.output("std::string " + root.getCPPClassName() + "Proxy::className = \"");

        if (root.getPackageName().equals("") == false) {
            writer.output(root.getPackageName().replace('.', '/') + "/");
        }
        writer.outputLine(root.getClassName() + "\";");
        writer.outputLine("jclass " + root.getCPPClassName() + "Proxy::objectClass = NULL;");
        writer.newLine(1);

        writer.outputLine("jclass " + root.getCPPClassName() + "Proxy::_getObjectClass()");
        writer.outputLine("{");
        writer.incTabLevel();
        writer.outputLine("if ( objectClass == NULL )");
        writer.incTabLevel();
        writer.outputLine("objectClass = static_cast<jclass>( JNIEnvHelper::NewGlobalRef( JNIEnvHelper::FindClass( className.c_str() ) ) );");
        writer.decTabLevel();
        writer.newLine(1);
        writer.outputLine("return objectClass;");
        writer.decTabLevel();
        writer.outputLine("}");
        writer.newLine(1);

        writer.outputLine(root.getCPPClassName() + "Proxy::" + root.getCPPClassName() + "Proxy(void* unused)");
        if (proxyGenSettings.getUseInheritance() == true) {
            if (root.isInterface() == false) {
                if (root.getSuperClass() != null) {
                    writer.incTabLevel();
                    writer.output(": " + root.getSuperClass().getFullyQualifiedCPPClassName() + "Proxy( unused )");
                    writer.decTabLevel();
                }
            } else {
                Iterator intfcs = root.getInterfaces();
                if (intfcs.hasNext() == true) {
                    writer.incTabLevel();
                    writer.output(": " + ((ClassNode) intfcs.next()).getFullyQualifiedCPPClassName() + "Proxy( unused )");
                    while (intfcs.hasNext() == true) {
                        writer.output(", " + ((ClassNode) intfcs.next()).getFullyQualifiedCPPClassName() + "Proxy( unused )");
                    }
                    writer.decTabLevel();
                }
            }
            writer.newLine(1);
        }
        writer.outputLine("{");
        writer.outputLine("}");
        writer.newLine(1);

        writer.outputLine("jobject " + root.getCPPClassName() + "Proxy::_getPeerObject() const");
        writer.outputLine("{");
        writer.incTabLevel();
        writer.outputLine("return peerObject;");
        writer.decTabLevel();
        writer.outputLine("}");
        writer.newLine(1);

        writer.outputLine("jclass " + root.getCPPClassName() + "Proxy::getObjectClass()");
        writer.outputLine("{");
        writer.incTabLevel();
        writer.outputLine("return _getObjectClass();");
        writer.decTabLevel();
        writer.outputLine("}");
        writer.newLine(1);

        writer.outputLine(root.getCPPClassName() + "Proxy::operator jobject()");
        writer.outputLine("{");
        writer.incTabLevel();
        writer.outputLine("return _getPeerObject();");
        writer.decTabLevel();
        writer.outputLine("}");
        writer.newLine(1);

        if (root.isThrowable() == true) {
            writer.outputLine(root.getCPPClassName() + "Proxy::operator jthrowable()");
            writer.outputLine("{");
            writer.incTabLevel();
            writer.outputLine("return reinterpret_cast<jthrowable>(_getPeerObject());");
            writer.decTabLevel();
            writer.outputLine("}");
            writer.newLine(1);
        }

        if (root.getFullyQualifiedClassName().equals("java.lang.Class") == true) {
            writer.outputLine(root.getCPPClassName() + "Proxy::operator jclass()");
            writer.outputLine("{");
            writer.incTabLevel();
            writer.outputLine("return reinterpret_cast<jclass>(_getPeerObject());");
            writer.decTabLevel();
            writer.outputLine("}");
            writer.newLine(1);
        }

        generateCtors(root, writer);

        writer.outputLine(root.getCPPClassName() + "Proxy::~" + root.getCPPClassName() + "Proxy()");
        writer.outputLine("{");
        writer.incTabLevel();
        writer.outputLine("JNIEnvHelper::DeleteGlobalRef( peerObject );");
        writer.decTabLevel();
        writer.outputLine("}");
        writer.newLine(1);

        writer.outputLine(root.getCPPClassName() + "Proxy& " + root.getCPPClassName() + "Proxy::operator=(const " + root.getCPPClassName() + "Proxy& rhs)");
        writer.outputLine("{");
        writer.incTabLevel();
        writer.outputLine("JNIEnvHelper::DeleteGlobalRef( peerObject );");
        writer.outputLine("peerObject = JNIEnvHelper::NewGlobalRef( rhs.peerObject );");
        writer.outputLine("return *this;");
        writer.decTabLevel();
        writer.outputLine("}");
        writer.newLine(1);

        if (proxyGenSettings.getGenerateAttributeGetters() == true && root.isInterface() == false) {
            generateGetters(root, writer);
        }
        if (proxyGenSettings.getGenerateAttributeSetters() == true && root.isInterface() == false) {
            generateSetters(root, writer);
        }
        generateMethods(root, writer);

        writer.flush();
        writer.close();
    }

    /**
     * Private helper method to generate the requisite
     * <code>#include</code> statements.
     *
     * This method is called by the
     * <code>generate()</code> method to generate the
     * <code>#include</code> statements that will be needed to compile the
     * generated C++ proxy class.
     *
     * @param root The <code>ClassNode</code> instance referencing the Java
     * class for which we are generating code.
     * @param writer The <code>FormattedFileWriter</code> instance where all
     * output is directed.
     * @exception java.io.IOException
     * @see #generate
     */
    public void generateIncludes(ClassNode root, FormattedFileWriter writer)
            throws java.io.IOException {
        HashMap alreadyGenerated = new HashMap();

        writer.outputLine("#include \"net/sourceforge/jnipp/JNIEnvHelper.h\"");
        writer.outputLine("#include \"" + root.getCPPClassName() + "Proxy.h\"");

        if (proxyGenSettings.getUseRichTypes() == true) {
            writer.newLine(1);
            writer.outputLine("// includes for parameter and return type proxy classes");
            Iterator it = root.getDependencies();
            while (it.hasNext() == true) {
                ClassNode cn = (ClassNode) it.next();
                if (alreadyGenerated.get(cn.getFullyQualifiedCPPClassName()) == null && cn.needsProxy() == true) {
                    String headerFile = cn.getPackageName().replace('.', File.separatorChar);
                    if (headerFile.equals("") == false) {
                        headerFile += File.separatorChar;
                    }
                    headerFile += cn.getCPPClassName() + "Proxy.h";
                    writer.outputLine("#include \"" + headerFile + "\"");
                    alreadyGenerated.put(cn.getFullyQualifiedCPPClassName(), cn);
                }
            }
        }

        writer.newLine(1);
    }

    /**
     * Private helper method to generate the requisite
     * <code>using</code> statements.
     *
     * This method is called by the
     * <code>generate()</code> method to generate the
     * <code>using</code> statements that will be needed to compile the
     * generated C++ proxy class.
     *
     * @param root The <code>ClassNode</code> instance referencing the Java
     * class for which we are generating code.
     * @param writer The <code>FormattedFileWriter</code> instance where all
     * output is directed.
     * @exception java.io.IOException
     * @see #generate
     */
    public void generateUsing(ClassNode root, FormattedFileWriter writer)
            throws java.io.IOException {
        HashMap alreadyGenerated = new HashMap();
        writer.outputLine("using namespace net::sourceforge::jnipp;");
        if (root.getNamespace().equals("") == false) {
            writer.outputLine("using namespace " + root.getNamespace() + ";");
            alreadyGenerated.put(root.getNamespace(), root);
        }

        writer.newLine(2);
    }

    /**
     * Private helper method to generate the constructors for the generated C++
     * proxy class.
     *
     * This method is called by the
     * <code>generate()</code> method to generate the constructors for the
     * generated C++ proxy class. A constructor will be generated for each
     * constructor found in the corresponding Java class.
     *
     * @param root The <code>ClassNode</code> instance referencing the Java
     * class for which we are generating code.
     * @param writer The <code>FormattedFileWriter</code> instance where all
     * output is directed.
     * @exception java.io.IOException
     * @see #generate
     */
    private void generateCtors(ClassNode root, FormattedFileWriter writer)
            throws java.io.IOException {
        writer.outputLine("// constructors");
        writer.outputLine(root.getCPPClassName() + "Proxy::" + root.getCPPClassName() + "Proxy(jobject obj)");
        if (proxyGenSettings.getUseInheritance() == true) {
            if (root.isInterface() == false) {
                if (root.getSuperClass() != null) {
                    writer.incTabLevel();
                    writer.output(": " + root.getSuperClass().getFullyQualifiedCPPClassName() + "Proxy( reinterpret_cast<void*>(NULL) )");
                    writer.decTabLevel();
                }
            } else {
                Iterator intfcs = root.getInterfaces();
                if (intfcs.hasNext() == true) {
                    writer.incTabLevel();
                    writer.output(": " + ((ClassNode) intfcs.next()).getFullyQualifiedCPPClassName() + "Proxy( reinterpret_cast<void*>(NULL) )");
                    while (intfcs.hasNext() == true) {
                        writer.output(", " + ((ClassNode) intfcs.next()).getFullyQualifiedCPPClassName() + "Proxy( reinterpret_cast<void*>(NULL) )");
                    }
                    writer.decTabLevel();
                }
            }
            writer.newLine(1);
        }
        writer.outputLine("{");
        writer.incTabLevel();
        writer.outputLine("peerObject = JNIEnvHelper::NewGlobalRef( obj );");
        writer.decTabLevel();
        writer.outputLine("}");
        writer.newLine(1);
        Iterator it = root.getConstructors();
        while (it.hasNext() == true) {
            int currentIndex = 0;
            MethodNode node = (MethodNode) it.next();
            writer.output(root.getCPPClassName() + "Proxy::" + root.getCPPClassName() + "Proxy(");
            Iterator params = node.getParameterList();
            for (int count = 0; params.hasNext() == true; ++count) {
                ClassNode currentParam = (ClassNode) params.next();
                if (proxyGenSettings.getUseRichTypes() == true) {
                    if (currentParam.getFullyQualifiedClassName().equals(root.getFullyQualifiedClassName()) == true && count == 0 && params.hasNext() == false) {
                        writer.output(currentParam.getJNITypeName(proxyGenSettings.getProject().getUsePartialSpec()) + "& p" + currentIndex++);
                    } else {
                        writer.output(currentParam.getJNITypeName(proxyGenSettings.getProject().getUsePartialSpec()) + " p" + currentIndex++);
                    }
                } else {
                    writer.output(currentParam.getPlainJNITypeName() + " p" + currentIndex++);
                }
                if (params.hasNext() == true) {
                    writer.output(", ");
                }
            }
            writer.outputLine(")");
            if (proxyGenSettings.getUseInheritance() == true) {
                if (root.isInterface() == false) {
                    if (root.getSuperClass() != null) {
                        writer.incTabLevel();
                        writer.output(": " + root.getSuperClass().getFullyQualifiedCPPClassName() + "Proxy( reinterpret_cast<void*>(NULL) )");
                        writer.decTabLevel();
                    }
                } else {
                    Iterator intfcs = root.getInterfaces();
                    if (intfcs.hasNext() == true) {
                        writer.incTabLevel();
                        writer.output(": " + ((ClassNode) intfcs.next()).getFullyQualifiedCPPClassName() + "Proxy( reinterpret_cast<void*>(NULL) )");
                        while (intfcs.hasNext() == true) {
                            writer.output(", " + ((ClassNode) intfcs.next()).getFullyQualifiedCPPClassName() + "Proxy( reinterpret_cast<void*>(NULL) )");
                        }
                        writer.decTabLevel();
                    }
                }
                writer.newLine(1);
            }
            writer.outputLine("{");
            writer.incTabLevel();
            writer.output("jmethodID mid = JNIEnvHelper::GetMethodID( _getObjectClass(), ");
            writer.outputLine("\"" + node.getJNIName() + "\", \"" + node.getJNISignature() + "\" );");
            writer.output("peerObject = JNIEnvHelper::NewGlobalRef( JNIEnvHelper::NewObject( _getObjectClass(), mid");
            currentIndex = 0;
            params = node.getParameterList();
            while (params.hasNext() == true) {
                ClassNode currentParam = (ClassNode) params.next();
                if (proxyGenSettings.getUseRichTypes() == true && currentParam.isPrimitive() == false) {
                    writer.output(", static_cast<" + currentParam.getPlainJNITypeName() + ">( p" + currentIndex++ + " )");
                } else {
                    writer.output(", p" + currentIndex++);
                }
            }
            writer.outputLine(" ) );");
            writer.decTabLevel();
            writer.outputLine("}");
            writer.newLine(1);
        }
    }

    /**
     * Private helper method to generate the attribute "getters" for the
     * generated C++ proxy class.
     *
     * This method is called by the
     * <code>generate()</code> method to generate the field accessors for the
     * generated C++ proxy class. A "getter" will optionally be generated for
     * each field found in the corresponding Java class. This option is
     * specified in the supplied settings.
     *
     * @param root The <code>ClassNode</code> instance referencing the Java
     * class for which we are generating code.
     * @param writer The <code>FormattedFileWriter</code> instance where all
     * output is directed.
     * @exception java.io.IOException
     * @see #generate
     * @see net.sourceforge.jnipp.project#getGenerateAttributeGetters
     */
    private void generateGetters(ClassNode root, FormattedFileWriter writer)
            throws java.io.IOException {
        HashMap methodNames = new HashMap();
        Iterator it = root.getMethods();
        while (it.hasNext() == true) {
            MethodNode node = (MethodNode) it.next();
            String name = node.getCPPName();
            Iterator params = node.getParameterList();
            if (params.hasNext() == false && name.substring(0, 3).equals("get") == true) {
                methodNames.put(name, name);
            }
        }

        writer.outputLine("// attribute getters");
        it = root.getFields();
        while (it.hasNext() == true) {
            FieldNode node = (FieldNode) it.next();
            ClassNode current = node.getType();
            String methodName = "get" + Character.toUpperCase(node.getName().charAt(0));
            methodName += node.getName().substring(1);
            if (methodNames.containsKey(methodName) == true) {
                methodName = "_" + methodName;
            }
            if (proxyGenSettings.getUseRichTypes() == true) {
                writer.output(current.getJNITypeName(proxyGenSettings.getProject().getUsePartialSpec()) + " " + root.getCPPClassName() + "Proxy::" + methodName);
            } else {
                writer.output(current.getPlainJNITypeName() + " " + root.getCPPClassName() + "Proxy::" + methodName);
            }
            writer.outputLine((node.isStatic() == true ? "()" : "() const"));
            writer.outputLine("{");
            writer.incTabLevel();
            writer.output("jfieldID fid = JNIEnvHelper::Get");
            if (node.isStatic() == true) {
                writer.output("Static");
            }
            writer.output("FieldID( _getObjectClass(), ");
            writer.outputLine("\"" + node.getName() + "\", \"" + current.getJNIString() + "\" );");
            String plainReturnTypeName = node.getType().getPlainJNITypeName();
            writer.output("return ");
            if (node.getType().isPrimitive() == false && plainReturnTypeName.equals("jobject") == false) {
                writer.output("reinterpret_cast< " + plainReturnTypeName + " >( ");
            }
            writer.output(node.getJNIGetFieldCall());
            if (node.isStatic() == true) {
                writer.output(" _getObjectClass(), ");
            } else {
                writer.output(" _getPeerObject(), ");
            }
            writer.output("fid )");
            if (node.getType().isPrimitive() == false && plainReturnTypeName.equals("jobject") == false) {
                writer.output(" )");
            }
            writer.outputLine(";");

            writer.decTabLevel();
            writer.outputLine("}");
            writer.newLine(1);
        }
    }

    /**
     * Private helper method to generate the attribute "setters" for the
     * generated C++ proxy class.
     *
     * This method is called by the
     * <code>generate()</code> method to generate the field mutators for the
     * generated C++ proxy class. A "setter" will optionally be generated for
     * each field found in the corresponding Java class. This option is
     * specified in the supplied settings.
     *
     * @param root The <code>ClassNode</code> instance referencing the Java
     * class for which we are generating code.
     * @param writer The <code>FormattedFileWriter</code> instance where all
     * output is directed.
     * @exception java.io.IOException
     * @see #generate
     * @see net.sourceforge.jnipp.project#getGenerateAttributeSetters
     */
    private void generateSetters(ClassNode root, FormattedFileWriter writer)
            throws java.io.IOException {
        HashMap methodNames = new HashMap();
        Iterator it = root.getMethods();
        while (it.hasNext() == true) {
            MethodNode node = (MethodNode) it.next();
            String name = node.getCPPName();
            Iterator params = node.getParameterList();
            if (params.hasNext() == true) {
                params.next();
                if (params.hasNext() == false && name.substring(0, 3).equals("set") == true);
                methodNames.put(name, name);
            }
        }

        writer.outputLine("// attribute setters");
        it = root.getFields();
        while (it.hasNext() == true) {
            FieldNode node = (FieldNode) it.next();
            ClassNode current = node.getType();
            String methodName = "set" + Character.toUpperCase(node.getName().charAt(0));
            methodName += node.getName().substring(1);
            if (methodNames.containsKey(methodName) == true) {
                methodName = "_" + methodName;
            }
            if (proxyGenSettings.getUseRichTypes() == true) {
                writer.outputLine("void " + root.getCPPClassName() + "Proxy::" + methodName + "(" + current.getJNITypeName(proxyGenSettings.getProject().getUsePartialSpec()) + " " + node.getCPPName() + ")");
            } else {
                writer.outputLine("void " + root.getCPPClassName() + "Proxy::" + methodName + "(" + current.getPlainJNITypeName() + " " + node.getCPPName() + ")");
            }
            writer.outputLine("{");
            writer.incTabLevel();
            writer.output("jfieldID fid = JNIEnvHelper::Get");
            if (node.isStatic() == true) {
                writer.output("Static");
            }
            writer.output("FieldID( _getObjectClass(), ");
            writer.outputLine("\"" + node.getName() + "\", \"" + current.getJNIString() + "\" );");
            writer.output(node.getJNISetFieldCall());
            if (node.isStatic() == true) {
                writer.output(" _getObjectClass(), ");
            } else {
                writer.output(" _getPeerObject(), ");
            }
            writer.outputLine("fid, " + node.getCPPName() + " );");

            writer.decTabLevel();
            writer.outputLine("}");
            writer.newLine(1);
        }
    }

    /**
     * Private helper method to generate the methods for the generated C++ proxy
     * class.
     *
     * This method is called by the
     * <code>generate()</code> method to generate the methods for the generated
     * C++ proxy class. A method will be generated for each method found in the
     * corresponding Java class.
     *
     * @param root The <code>ClassNode</code> instance referencing the Java
     * class for which we are generating code.
     * @param writer The <code>FormattedFileWriter</code> instance where all
     * output is directed.
     * @exception java.io.IOException
     * @see #generate
     */
    private void generateMethods(ClassNode root, FormattedFileWriter writer)
            throws java.io.IOException {
        writer.outputLine("// methods");
        Iterator it = root.getMethods();
        while (it.hasNext() == true) {
            int currentIndex = 0;
            MethodNode node = (MethodNode) it.next();
            if (proxyGenSettings.getUseRichTypes() == true) {
                writer.output(node.getReturnType().getJNITypeName(proxyGenSettings.getProject().getUsePartialSpec()) + " " + root.getCPPClassName() + "Proxy::");
                writer.output(node.getCPPName() + "(");
            } else {
                writer.output(node.getReturnType().getPlainJNITypeName() + " " + root.getCPPClassName() + "Proxy::");
                writer.output(node.getUniqueCPPName() + "(");
            }
            Iterator params = node.getParameterList();
            while (params.hasNext() == true) {
                ClassNode currentParam = (ClassNode) params.next();
                if (proxyGenSettings.getUseRichTypes() == true) {
                    writer.output(currentParam.getJNITypeName(proxyGenSettings.getProject().getUsePartialSpec()) + " p" + currentIndex++);
                } else {
                    writer.output(currentParam.getPlainJNITypeName() + " p" + currentIndex++);
                }
                if (params.hasNext() == true) {
                    writer.output(", ");
                }
            }
            writer.outputLine(")");
            writer.outputLine("{");
            writer.incTabLevel();
            writer.outputLine("static jmethodID mid = NULL;");
            writer.outputLine("if ( mid == NULL )");
            writer.incTabLevel();
            writer.output("mid = JNIEnvHelper::Get");
            if (node.isStatic() == true) {
                writer.output("Static");
            }
            writer.output("MethodID( _getObjectClass(), ");
            writer.outputLine("\"" + node.getJNIName() + "\", \"" + node.getJNISignature() + "\" );");
            writer.decTabLevel();
            String returnTypeName = null;
            if (proxyGenSettings.getUseRichTypes() == true) {
                returnTypeName = node.getReturnType().getJNITypeName(proxyGenSettings.getProject().getUsePartialSpec());
            } else {
                returnTypeName = node.getReturnType().getPlainJNITypeName();
            }
            if (returnTypeName.equals("void") == false) {
                writer.output("return ");
                if (proxyGenSettings.getUseRichTypes() == true
                        && (node.getReturnType().needsProxy() == true || node.getReturnType().isBuiltIn() == true)) {
                    writer.output(returnTypeName + "( ");
                } else if (node.getReturnType().isPrimitive() == false) {
                    writer.output("reinterpret_cast< " + returnTypeName + " >( ");
                }
            }

            writer.output(node.getJNIMethodCall());

            if (node.isStatic() == true) {
                writer.output(" _getObjectClass(), ");
            } else {
                writer.output(" _getPeerObject(), ");
            }
            writer.output("mid");
            currentIndex = 0;
            params = node.getParameterList();
            while (params.hasNext() == true) {
                ClassNode currentParam = (ClassNode) params.next();
                if (proxyGenSettings.getUseRichTypes() == true && currentParam.isPrimitive() == false) {
                    writer.output(", static_cast<" + currentParam.getPlainJNITypeName() + ">( p" + currentIndex++ + " )");
                } else {
                    writer.output(", p" + currentIndex++);
                }
            }

            if (returnTypeName.equals("void") == false && node.getReturnType().isPrimitive() == false) {
                writer.output(" ) ");
            }

            writer.outputLine(" );");
            writer.decTabLevel();
            writer.outputLine("}");
            writer.newLine(1);
        }
    }
}
