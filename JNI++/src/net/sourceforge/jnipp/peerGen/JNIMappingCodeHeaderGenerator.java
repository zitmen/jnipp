package net.sourceforge.jnipp.peerGen;

import java.util.*;
import net.sourceforge.jnipp.common.*;
import net.sourceforge.jnipp.project.PeerGenSettings;
import java.io.File;

public class JNIMappingCodeHeaderGenerator
{
	private PeerGenSettings peerGenSettings = null;
	
   public JNIMappingCodeHeaderGenerator()
   {
   }

	public void generate(ClassNode root, PeerGenSettings peerGenSettings)
		throws java.io.IOException
	{
		this.peerGenSettings = peerGenSettings;
		
		String fullFileName = peerGenSettings.getProject().getCPPOutputDir() + File.separatorChar;
		if ( root.getPackageName().equals( "" ) == true )
			fullFileName = root.getCPPClassName() + "Mapping.h";
		else
			fullFileName = root.getPackageName().replace( '.', File.separatorChar ) + File.separatorChar + root.getCPPClassName() + "Mapping.h";
		
		FormattedFileWriter writer = new FormattedFileWriter( fullFileName, true );
		Iterator it = root.getNamespaceElements();
		String def = "__";
		while ( it.hasNext() == true )
			def += (String) it.next() + "_";
		def += root.getCPPClassName() + "Mapping_H";
		writer.outputLine( "#ifndef " + def );
		writer.outputLine( "#define " + def );
		writer.newLine( 2 );

		writer.outputLine( "#include <jni.h>" );
		writer.outputLine( "#include \"" + root.getClassName() + "Peer.h\"" );
		writer.newLine( 1 );
		
		generateMethods( root, writer );

		writer.newLine( 2 );
		writer.outputLine( "#endif" );
		writer.flush();
		writer.close();
	}

	public void generateMethods(ClassNode root, FormattedFileWriter writer)
		throws java.io.IOException
	{
		String namespace = root.getPackageName().replace( '.', '_' );
		if ( namespace == null || namespace.equals( "" ) == true )
			namespace = "Java_";
		else
			namespace = "Java_" + namespace + "_";

		writer.outputLine( "extern \"C\"" );
		writer.outputLine( "{" );
		writer.incTabLevel();
		writer.outputLine( "JNIEXPORT void JNICALL " + namespace + root.getClassName() + "Proxy_init(JNIEnv*, jclass);" );
		writer.decTabLevel();
		writer.outputLine( "};" );
		writer.newLine( 1 );

		Iterator nit = root.getNamespaceElements();

		while ( nit.hasNext() == true )
			writer.output( (String) nit.next() + "::" );
		writer.outputLine( root.getCPPClassName() + "Peer* " + namespace + root.getClassName() + "Proxy_getPeerPtr(JNIEnv*, jobject);" );
		writer.outputLine( "JNIEXPORT void JNICALL " + namespace + root.getClassName() + "Proxy_releasePeer(JNIEnv*, jobject);" );
		writer.newLine( 1 );
		writer.outputLine( "// methods" );
		Iterator it = root.getMethods();
		while ( it.hasNext() == true )
		{
			MethodNode node = (MethodNode) it.next();
			writer.output( "JNIEXPORT " );
			writer.output( node.getReturnType().getPlainJNITypeName() + " " );
			writer.output( "JNICALL " + namespace + root.getClassName() + "Proxy_" );
			writer.output( (peerGenSettings.getUseRichTypes() == true ? node.getCPPName() : node.getUniqueCPPName()) + 
								"(JNIEnv*, jobject" );
			Iterator params = node.getParameterList();
			while ( params.hasNext() == true )
			{
				ClassNode currentParam = (ClassNode) params.next();
				writer.output( ", " + currentParam.getPlainJNITypeName() );
			}
			writer.outputLine( ");" );
		}

		writer.newLine( 1 );
	}
	
}
