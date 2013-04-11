package net.sourceforge.jnipp.proxyGen;

import java.util.Iterator;
import java.util.HashMap;
import net.sourceforge.jnipp.common.*;
import net.sourceforge.jnipp.project.ProxyGenSettings;
import java.io.File;
import java.util.ArrayList;

/**
 * C++ proxy class header file generator.
 *
 * This class is responsible for generating the C++ proxy header file for a
 * specified Java class.  The specifics of the code generation process are
 * guided by the settings supplied in the call to the <code>generate()</code> 
 * method.
 *
 * @author $Author: ptrewhella $
 * @version $Revision: 1.40 $
 */

public class CPPProxyHeaderGenerator
{
   /**
    * Settings specifying the code generation options.
    *
    * This reference points to the <code>ProxyGenSettings</code> instance passed
    * into the <code>generate()</code> method and is used by various methods to
    * guide the code generation.
    *
    * @see #generate
    */
   private ProxyGenSettings proxyGenSettings = null;
   
   /**
    * Defualt constructor.
    */
   public CPPProxyHeaderGenerator()
   {
   }

   /**
    * Public entry point.
    *
    * This method is called to begin the code generation process for the specified
    * Java class.  This method will call one or more of the <code>private</code>
    * helper methods to perform the various code generation tasks.
    *
    * @param root The <code>ClassNode</code> instance referencing the Java class
    * for which we are generating code.
    * @param proxyGenSettings The settings used to obtain the code generation
    * options.
    * @exception java.io.IOException
    */
   public void generate(ClassNode root, ProxyGenSettings proxyGenSettings)
      throws java.io.IOException
   {
      if ( root.isPrimitive() == true || root.needsProxy() == false )
         return;
      
      this.proxyGenSettings = proxyGenSettings;

      String fullFileName = proxyGenSettings.getProject().getCPPOutputDir() + File.separatorChar;
      if ( root.getPackageName().equals( "" ) == true )
         fullFileName = root.getCPPClassName() + "Proxy.h";
      else
         fullFileName = root.getPackageName().replace( '.', File.separatorChar ) + File.separatorChar + root.getCPPClassName() + "Proxy.h";
      
      FormattedFileWriter writer = new FormattedFileWriter( fullFileName, true );
      String def = "__" + root.getPackageName().replace( '.', '_' ) + "_" + root.getCPPClassName() + "Proxy_H";
      writer.outputLine( "#ifndef " + def );
      writer.outputLine( "#define " + def );
      writer.newLine( 2 );

      generateIncludes( root, writer );

      Iterator it = root.getNamespaceElements();
  	   while ( it.hasNext() == true )
     	{
        	String current = (String) it.next();
         writer.outputLine( "namespace " + current );
  	      writer.outputLine( "{" );
     	   writer.incTabLevel();
     	}
	
      writer.output( "class " + root.getCPPClassName() + "Proxy" );

		if ( proxyGenSettings.getUseInheritance() == true )
		{
			if ( root.isInterface() == false )
			{
				if ( root.getSuperClass() != null )
					writer.output( " : public " + root.getSuperClass().getFullyQualifiedCPPClassName() + "Proxy" );
			}
			else
			{
				Iterator intfcs = root.getInterfaces();
				if ( intfcs.hasNext() == true )
				{
					writer.output( " : public " + ((ClassNode) intfcs.next()).getFullyQualifiedCPPClassName() + "Proxy" );
					while ( intfcs.hasNext() == true )
						writer.output( ", public " + ((ClassNode) intfcs.next()).getFullyQualifiedCPPClassName() + "Proxy" );
				}
			}
		}

		writer.outputLine( "" );
      writer.outputLine( "{" );
      writer.outputLine( "private:" );
      writer.incTabLevel();
      writer.outputLine( "static std::string className;" );
      writer.outputLine( "static jclass objectClass;" );
      writer.outputLine( "jobject peerObject;" );
      writer.decTabLevel();
      writer.newLine( 1 );
      writer.outputLine( "protected:" );
      writer.incTabLevel();
		writer.outputLine( root.getCPPClassName() + "Proxy(void* unused);" );
      writer.outputLine( "virtual jobject _getPeerObject() const;" );
      writer.decTabLevel();
      writer.newLine( 1 );
      writer.outputLine( "public:" );
      writer.incTabLevel();
      writer.outputLine( "static jclass _getObjectClass();" );
		writer.outputLine( "static inline std::string _getClassName()" );
		writer.outputLine( "{" );
		writer.incTabLevel();
		writer.outputLine( "return className;" );
		writer.decTabLevel();
		writer.outputLine( "}" );
		writer.newLine( 1 );
      writer.outputLine( "jclass getObjectClass();" );
      writer.outputLine( "operator jobject();" );
      if ( root.isThrowable() == true )
         writer.outputLine( "operator jthrowable();" );
		if ( root.getFullyQualifiedClassName().equals( "java.lang.Class" ) == true )
			writer.outputLine( "operator jclass();" );

      generateCtors( root, writer );
		writer.outputLine( "virtual ~" + root.getCPPClassName() + "Proxy();" );
		writer.outputLine( root.getCPPClassName() + "Proxy& operator=(const " + root.getCPPClassName() + "Proxy& rhs);" );
		writer.newLine( 1 );
      if ( proxyGenSettings.getGenerateAttributeGetters() == true )
         generateGetters( root, writer );
      if ( proxyGenSettings.getGenerateAttributeSetters() == true )
         generateSetters( root, writer );
      generateMethods( root, writer );

      writer.decTabLevel();
      writer.outputLine( "};" );

     	for ( it = root.getNamespaceElements(); it.hasNext() == true; it.next() )
     	{
        	writer.decTabLevel();
        	writer.outputLine( "}" );
     	}

      writer.newLine( 2 );
      writer.outputLine( "#endif" );
      writer.flush();
      writer.close();
   }

   /**
    * Private helper method to generate the requisite <code>#include</code> 
    * statements.
    *
    * This method is called by the <code>generate()</code> method to generate
    * the <code>#include</code> statements that will be needed to compile the
    * generated C++ proxy class.
    *
    * @param root The <code>ClassNode</code> instance referencing the Java class
    * for which we are generating code.
    * @param writer The <code>FormattedFileWriter</code> instance where all output
    * is directed.
    * @exception java.io.IOException
    * @see #generate
    */
   private void generateIncludes(ClassNode root, FormattedFileWriter writer)
      throws java.io.IOException
   {
      HashMap alreadyGenerated = new HashMap();
      
      writer.outputLine( "#include <jni.h>" );
      writer.outputLine( "#include <string>" );
		writer.newLine( 1 );
		
		if ( proxyGenSettings.getUseRichTypes() == true )
		{
			writer.outputLine( "#include \"net/sourceforge/jnipp/JBooleanArrayHelper.h\"" );
			writer.outputLine( "#include \"net/sourceforge/jnipp/JByteArrayHelper.h\"" );
			writer.outputLine( "#include \"net/sourceforge/jnipp/JCharArrayHelper.h\"" );
			writer.outputLine( "#include \"net/sourceforge/jnipp/JDoubleArrayHelper.h\"" );
			writer.outputLine( "#include \"net/sourceforge/jnipp/JFloatArrayHelper.h\"" );
			writer.outputLine( "#include \"net/sourceforge/jnipp/JIntArrayHelper.h\"" );
			writer.outputLine( "#include \"net/sourceforge/jnipp/JLongArrayHelper.h\"" );
			writer.outputLine( "#include \"net/sourceforge/jnipp/JShortArrayHelper.h\"" );
			writer.outputLine( "#include \"net/sourceforge/jnipp/JStringHelper.h\"" );
			writer.outputLine( "#include \"net/sourceforge/jnipp/JStringHelperArray.h\"" );
			writer.outputLine( "#include \"net/sourceforge/jnipp/ProxyArray.h\"" );
		}

		if ( proxyGenSettings.getUseInheritance() == true )
		{
			if ( root.isInterface() == false )
			{
				if ( root.getSuperClass() != null )
				{
					String headerFile = root.getSuperClass().getPackageName().replace( '.', File.separatorChar );
					if ( headerFile.equals( "" ) == false )
						headerFile += File.separatorChar;
					headerFile += root.getSuperClass().getCPPClassName() + "Proxy.h";
					writer.outputLine( "#include \"" + headerFile + "\"" );
					alreadyGenerated.put( root.getFullyQualifiedCPPClassName(), root );
				}
			}
			else
			{
				Iterator it = root.getInterfaces();
				while ( it.hasNext() == true )
				{
					ClassNode cn = (ClassNode) it.next();
					if ( alreadyGenerated.get( cn.getFullyQualifiedCPPClassName() ) == null )
					{
						String headerFile = cn.getPackageName().replace( '.', File.separatorChar );
						if ( headerFile.equals( "" ) == false )
							headerFile += File.separatorChar;
						headerFile += cn.getCPPClassName() + "Proxy.h";
						writer.outputLine( "#include \"" + headerFile + "\"" );
						alreadyGenerated.put( cn.getFullyQualifiedCPPClassName(), cn );
					}
				}
			}
		}
		
		if ( proxyGenSettings.getUseRichTypes() == true )
		{
			writer.newLine( 1 );
			ArrayList arrayIncludes = new ArrayList();
			ArrayList otherIncludes = new ArrayList();
			ArrayList componentTypeIncludes = new ArrayList();
			writer.outputLine( "// includes for parameter and return type proxy classes" );
			Iterator it = root.getDependencies();
			while ( it.hasNext() == true )
			{
				ClassNode cn = (ClassNode) it.next();
				if ( alreadyGenerated.get( cn.getFullyQualifiedCPPClassName() ) == null )
					if ( cn.isArray() == true )
					{
						if ( cn.getComponentType().needsProxy() == true )
						{
							String headerFile = cn.getComponentType().getPackageName().replace( '.', File.separatorChar );
							if ( headerFile.equals( "" ) == false )
								headerFile += File.separatorChar;
							headerFile += cn.getComponentType().getCPPClassName() + "Proxy.h";
							componentTypeIncludes.add( headerFile );
							alreadyGenerated.put( cn.getComponentType().getFullyQualifiedCPPClassName(), cn.getComponentType() );
						}
					}
					else
					if ( cn.needsProxy() == true )
					{
						String headerFile = cn.getPackageName().replace( '.', File.separatorChar );
						if ( headerFile.equals( "" ) == false )
							headerFile += File.separatorChar;
						headerFile += cn.getCPPClassName() + (cn.needsProxy() == true ? "ProxyForward.h" : ".h");
						otherIncludes.add( headerFile );
						alreadyGenerated.put( cn.getFullyQualifiedCPPClassName(), cn );
					}
			}
		
			it = arrayIncludes.iterator();
			while ( it.hasNext() == true )
				writer.outputLine( "#include \"" + (String) it.next() + "\"" );
			it = otherIncludes.iterator();
			while ( it.hasNext() == true )
				writer.outputLine( "#include \"" + (String) it.next() + "\"" );
			it = componentTypeIncludes.iterator();
			while ( it.hasNext() == true )
				writer.outputLine( "#include \"" + (String) it.next() + "\"" );
		}

		writer.newLine( 1 );
	}

   /**
    * Private helper method to generate the constructors for the generated C++
    * proxy class.
    *
    * This method is called by the <code>generate()</code> method to generate
    * the constructors for the generated C++ proxy class.  A constructor will be
    * generated for each constructor found in the corresponding Java class.
    *
    * @param root The <code>ClassNode</code> instance referencing the Java class
    * for which we are generating code.
    * @param writer The <code>FormattedFileWriter</code> instance where all output
    * is directed.
    * @exception java.io.IOException
    * @see #generate
    */
   private void generateCtors(ClassNode root, FormattedFileWriter writer)
      throws java.io.IOException
   {
      writer.outputLine( "// constructors" );
      writer.outputLine( root.getCPPClassName() + "Proxy(jobject obj);" );
      Iterator it = root.getConstructors();
      while ( it.hasNext() == true )
      {
         int currentIndex = 0;
         MethodNode node = (MethodNode) it.next();
         writer.output( root.getCPPClassName() + "Proxy(" );
         Iterator params = node.getParameterList();
			for ( int count = 0; params.hasNext() == true; ++count )
			{
				ClassNode currentParam = (ClassNode) params.next();
				if ( proxyGenSettings.getUseRichTypes() == true )
				{
					if ( currentParam.getFullyQualifiedClassName().equals( root.getFullyQualifiedClassName() ) == true && count == 0 && params.hasNext() == false )
						writer.output( currentParam.getJNITypeName( proxyGenSettings.getProject().getUsePartialSpec() ) + "& p" + currentIndex++ );
					else
						writer.output( currentParam.getJNITypeName( proxyGenSettings.getProject().getUsePartialSpec() ) + " p" + currentIndex++ );
				}
				else
					writer.output( currentParam.getPlainJNITypeName() + " p" + currentIndex++ );
				if ( params.hasNext() == true )
					writer.output( ", " );
			}

			writer.outputLine( ");" );
		}

      writer.newLine( 1 );
   }

   /**
    * Private helper method to generate the attribute "getters" for the generated
    * C++ proxy class.
    *
    * This method is called by the <code>generate()</code> method to generate
    * the field accessors for the generated C++ proxy class.  A "getter" will
    * optionally be generated for each field found in the corresponding Java 
    * class.  This option is specified in the supplied settings.
    *
    * @param root The <code>ClassNode</code> instance referencing the Java class
    * for which we are generating code.
    * @param writer The <code>FormattedFileWriter</code> instance where all output
    * is directed.
    * @exception java.io.IOException
    * @see #generate
    * @see net.sourceforge.jnipp.project#getGenerateAttributeGetters
    */
   private void generateGetters(ClassNode root, FormattedFileWriter writer)
      throws java.io.IOException
   {
      HashMap methodNames = new HashMap();
      Iterator it = root.getMethods();
      while ( it.hasNext() == true )
      {
         MethodNode node = (MethodNode) it.next();
         String name = node.getCPPName();
         Iterator params = node.getParameterList();
         if ( params.hasNext() == false && name.substring( 0, 3 ).equals( "get" ) == true )
            methodNames.put( name, name );
      }
      
      writer.outputLine( "// attribute getters" );
      it = root.getFields();
      while ( it.hasNext() == true )
      {
         FieldNode node = (FieldNode) it.next();
         if ( node.isStatic() == true )
            writer.output( "static " );
         ClassNode current = node.getType();
         String methodName = "get" + Character.toUpperCase( node.getName().charAt( 0 ) );
         methodName += node.getName().substring( 1 );
         if ( methodNames.containsKey( methodName ) == true )
            methodName = "_" + methodName;
			if ( proxyGenSettings.getUseRichTypes() == true )
         	writer.output( current.getJNITypeName( proxyGenSettings.getProject().getUsePartialSpec() ) + " " + methodName );
			else
				writer.output( current.getPlainJNITypeName() + " " + methodName );
         writer.outputLine( (node.isStatic() == true ? "();" : "() const;") );
      }
      writer.newLine( 1 );
   }

   /**
    * Private helper method to generate the attribute "setters" for the generated
    * C++ proxy class.
    *
    * This method is called by the <code>generate()</code> method to generate
    * the field mutators for the generated C++ proxy class.  A "setter" will
    * optionally be generated for each field found in the corresponding Java 
    * class.  This option is specified in the supplied settings.
    *
    * @param root The <code>ClassNode</code> instance referencing the Java class
    * for which we are generating code.
    * @param writer The <code>FormattedFileWriter</code> instance where all output
    * is directed.
    * @exception java.io.IOException
    * @see #generate
    * @see net.sourceforge.jnipp.project#getGenerateAttributeSetters
    */
   public void generateSetters(ClassNode root, FormattedFileWriter writer)
      throws java.io.IOException
   {
      HashMap methodNames = new HashMap();
      Iterator it = root.getMethods();
      while ( it.hasNext() == true )
      {
         MethodNode node = (MethodNode) it.next();
         String name = node.getCPPName();
         Iterator params = node.getParameterList();
         if ( params.hasNext() == true )
         {
            params.next();
            if ( params.hasNext() == false && name.substring( 0, 3 ).equals( "set" ) == true );
            methodNames.put( name, name );
         }
      }

      writer.outputLine( "// attribute setters" );
      it = root.getFields();
      while ( it.hasNext() == true )
      {
         FieldNode node = (FieldNode) it.next();
         if ( node.isStatic() == true )
            writer.output( "static " );
         ClassNode current = node.getType();
         String methodName = "set" + Character.toUpperCase( node.getName().charAt( 0 ) );
         methodName += node.getName().substring( 1 );
         if ( methodNames.containsKey( methodName ) == true )
            methodName = "_" + methodName;
			if ( proxyGenSettings.getUseRichTypes() == true )
         	writer.outputLine( "void " + methodName + "(" + current.getJNITypeName( proxyGenSettings.getProject().getUsePartialSpec() ) + " " + node.getCPPName() + ");" );
			else
				writer.outputLine( "void " + methodName + "(" + current.getPlainJNITypeName() + " " + node.getName() + ");" );
      }
      writer.newLine( 1 );
   }

   /**
    * Private helper method to generate the methods for the generated C++ proxy
    * class.
    *
    * This method is called by the <code>generate()</code> method to generate
    * the methods for the generated C++ proxy class.  A method will be generated
    * for each method found in the corresponding Java class.
    *
    * @param root The <code>ClassNode</code> instance referencing the Java class
    * for which we are generating code.
    * @param writer The <code>FormattedFileWriter</code> instance where all output
    * is directed.
    * @exception java.io.IOException
    * @see #generate
    */
   private void generateMethods(ClassNode root, FormattedFileWriter writer)
      throws java.io.IOException
   {
      writer.outputLine( "// methods" );
      Iterator it = root.getMethods();
      while ( it.hasNext() == true )
      {
         int currentIndex = 0;
         MethodNode node = (MethodNode) it.next();
         writer.outputLine( "/*" );
         writer.outputLine( " * " + (node.isStatic() == true ? "static " : "") + node.getJavaSignature() );
         writer.outputLine( " */" );
         if ( node.isStatic() == true )
            writer.output( "static " );
			if ( proxyGenSettings.getUseRichTypes() == true )
			{
         	writer.output( node.getReturnType().getJNITypeName( proxyGenSettings.getProject().getUsePartialSpec() ) + " " );
         	writer.output( node.getCPPName() + "(" );
			}
			else
			{
				writer.output( node.getReturnType().getPlainJNITypeName() + " " );
         	writer.output( node.getUniqueCPPName() + "(" );
			}

         Iterator params = node.getParameterList();
         while ( params.hasNext() == true )
         {
            ClassNode currentParam = (ClassNode) params.next();
				if ( proxyGenSettings.getUseRichTypes() == true )
            	writer.output( currentParam.getJNITypeName( proxyGenSettings.getProject().getUsePartialSpec() ) + " p" + currentIndex++ );
				else
					writer.output( currentParam.getPlainJNITypeName() + " p" + currentIndex++ );
            if ( params.hasNext() == true )
               writer.output( ", " );
         }
         writer.outputLine( ");" );
      }

      writer.newLine( 1 );
   }
}
