package net.sourceforge.jnipp.peerGen;

import java.util.*;
import net.sourceforge.jnipp.common.*;
import java.io.File;
import net.sourceforge.jnipp.project.PeerGenSettings;

public class JavaProxyGenerator
{
   public JavaProxyGenerator()
   {
   }

	public void generate(ClassNode root, PeerGenSettings peerGenSettings)
		throws java.io.IOException
	{
		String fullFileName = peerGenSettings.getProject().getJavaOutputDir() + File.separatorChar;
		if ( root.getPackageName().equals( "" ) == true )
			fullFileName = root.getCPPClassName() + "Proxy.java";
		else
			fullFileName = root.getPackageName().replace( '.', File.separatorChar ) + File.separatorChar + root.getCPPClassName() + "Proxy.java";
		
		FormattedFileWriter writer = new FormattedFileWriter( fullFileName );

		writer.outputLine( "package " + root.getPackageName() + ";" );
		writer.newLine( 1 );

		writer.outputLine( "import " + root.getPackageName() + "." + root.getClassName() + ";" );
		writer.newLine( 1 );
		writer.outputLine( "public class " + root.getClassName() + "Proxy" );
		writer.incTabLevel();
		writer.outputLine( "implements " + root.getClassName() );
		writer.decTabLevel();
		writer.outputLine( "{" );
		writer.incTabLevel();
		writer.outputLine( "private long peerPtr = 0;" );
		writer.newLine( 1 );
		generateMethods( root, writer );
		writer.newLine( 1 );
		writer.outputLine( "static" );
		writer.outputLine( "{" );
		writer.incTabLevel();
		writer.outputLine( "System.loadLibrary( \"" + peerGenSettings.getProject().getTargetName() + "\" );" );
		writer.outputLine( "init();" );
		writer.decTabLevel();
		writer.outputLine( "}" );
		writer.decTabLevel();
		writer.outputLine( "}" );
		writer.flush();
		writer.close();
		
		/*
		String javacCommand = null;
		String sourceDir = peerGenSettings.getProject().getJavaOutputDir();
		javacCommand = (javacCommand == null ? "javac" : javacCommand);
		String source = sourceDir + File.separatorChar;
		if ( root.getPackageName().equals( "" ) == false )
			source += root.getPackageName().replace( '.', File.separatorChar ) + File.separatorChar;
		source += root.getClassName() + "Proxy.java";
		System.out.println( "compiling " + source + " ..." );
		String cmd = javacCommand + " " + source;
		Process proc = Runtime.getRuntime().exec( cmd );
		new RuntimeExecStreamHandler( proc.getErrorStream() ).start();
		new RuntimeExecStreamHandler( proc.getInputStream() ).start();
		int result = 0;
		
		try
		{
			result = proc.waitFor();
		}
		catch(InterruptedException ex)
		{
		}
		 */
	}

	public void generateMethods(ClassNode root, FormattedFileWriter writer)
		throws java.io.IOException
	{
		writer.outputLine( "private static native void init();" );
		writer.outputLine( "private native void releasePeer();" );
		writer.newLine( 1 );
		writer.outputLine( "protected void finalize()" );
		writer.incTabLevel();
		writer.outputLine( "throws Throwable" );
		writer.decTabLevel();
		writer.outputLine( "{" );
		writer.incTabLevel();
		writer.outputLine( "releasePeer();" );
		writer.decTabLevel();
		writer.outputLine( "}" );
		writer.newLine( 1 );
		writer.outputLine( "// methods" );
		Iterator it = root.getMethods();
		while ( it.hasNext() == true )
		{
			int currentIndex = 0;
			MethodNode node = (MethodNode) it.next();
			String className = node.getReturnType().getFullyQualifiedClassName();
			writer.output( "public native " + className + " " );
			writer.output( node.getName() + "(" );
			Iterator params = node.getParameterList();
			while ( params.hasNext() == true )
			{
				if ( currentIndex != 0 )
					writer.output( ", " );
				ClassNode currentParam = (ClassNode) params.next();
				writer.output( currentParam.getFullyQualifiedClassName() );
				writer.output( " p" + currentIndex++ );
			}
			writer.outputLine( ");" );
		}

		writer.newLine( 1 );
	}
}
