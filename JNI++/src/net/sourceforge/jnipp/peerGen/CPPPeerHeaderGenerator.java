package net.sourceforge.jnipp.peerGen;

import java.util.*;
import net.sourceforge.jnipp.common.*;
import net.sourceforge.jnipp.project.PeerGenSettings;
import java.io.File;

public class CPPPeerHeaderGenerator
{
	private PeerGenSettings peerGenSettings = null;
	
   public CPPPeerHeaderGenerator()
   {
   }

	public void generate(ClassNode root, PeerGenSettings peerGenSettings)
		throws java.io.IOException
	{
		this.peerGenSettings = peerGenSettings;
		
		String fullFileName = peerGenSettings.getProject().getCPPOutputDir() + File.separatorChar;
		if ( root.getPackageName().equals( "" ) == true )
			fullFileName = root.getCPPClassName() + "Peer.h";
		else
			fullFileName = root.getPackageName().replace( '.', File.separatorChar ) + File.separatorChar + root.getCPPClassName() + "Peer.h";
		
		FormattedFileWriter writer = new FormattedFileWriter( fullFileName, peerGenSettings.isDestructive() || peerGenSettings.getUseInheritance() );
		String def = "__" + root.getPackageName().replace( '.', '_' ) + "_" + root.getCPPClassName() + "Peer_H";
		writer.outputLine( "#ifndef " + def );
		writer.outputLine( "#define " + def );
		writer.newLine( 2 );

		generateIncludes( root, writer );
		
		Iterator it = root.getNamespaceElements();
		int nestedLevels = 0;
		while ( it.hasNext() == true )
		{
			writer.outputLine( "namespace " + (String) it.next() );
			writer.outputLine( "{" );
			writer.incTabLevel();
			++nestedLevels;
		}

		writer.outputLine( "class " + root.getCPPClassName() + "Peer" );
		writer.outputLine( "{" );
		writer.outputLine( "private:" );
		writer.incTabLevel();
		writer.decTabLevel();
		writer.newLine( 1 );
		writer.outputLine( "public:" );
		writer.incTabLevel();
		writer.outputLine( root.getCPPClassName() + "Peer();" );
		writer.newLine( 1 );
		generateMethods( root, writer );
		writer.decTabLevel();
		writer.outputLine( "};" );

		for ( int i = 0; i < nestedLevels; ++i )
		{
			writer.decTabLevel();
			writer.outputLine( "};" );
		}

		writer.newLine( 2 );
		writer.outputLine( "#endif" );
		writer.flush();
		writer.close();
	}

   private void generateIncludes(ClassNode root, FormattedFileWriter writer)
      throws java.io.IOException
   {
      HashMap alreadyGenerated = new HashMap();
      
      writer.outputLine( "#include <jni.h>" );
      writer.outputLine( "#include <string>" );
		
		if ( peerGenSettings.getUseRichTypes() == false )
			return;
		
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

		writer.newLine( 1 );
		ArrayList arrayIncludes = new ArrayList();
		ArrayList otherIncludes = new ArrayList();
		ArrayList componentTypeIncludes = new ArrayList();
		writer.outputLine( "// includes for parameter and return type proxy classes (arrays first)" );
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
					headerFile += cn.getCPPClassName() + (cn.needsProxy() == true ? "Proxy.h" : ".h");
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
		writer.newLine( 1 );
   }

	public void generateMethods(ClassNode root, FormattedFileWriter writer)
		throws java.io.IOException
	{
		writer.outputLine( "// methods" );
		Iterator it = root.getMethods();
		while ( it.hasNext() == true )
		{
			MethodNode node = (MethodNode) it.next();
			if ( peerGenSettings.getUseInheritance() == true )
				writer.output( "virtual " );
			if ( peerGenSettings.getUseRichTypes() == true )
				writer.output( node.getReturnType().getJNITypeName( peerGenSettings.getProject().getUsePartialSpec() ) + 
									" " + 
									node.getCPPName() );
			else
				writer.output( node.getReturnType().getPlainJNITypeName() + " " + node.getUniqueCPPName() );
			writer.output( "(JNIEnv* env, jobject obj" );
			Iterator params = node.getParameterList();
			int currentIndex = 0;
			while ( params.hasNext() == true )
			{
				writer.output( ", " );
				ClassNode currentParam = (ClassNode) params.next();
				if ( peerGenSettings.getUseRichTypes() == true )
            	writer.output( currentParam.getJNITypeName( peerGenSettings.getProject().getUsePartialSpec() ) + " p" + currentIndex++ );
				else
					writer.output( currentParam.getPlainJNITypeName() + " p" + currentIndex++ );
			}
			writer.outputLine( ");" );
		}

		writer.newLine( 1 );
	}
}
