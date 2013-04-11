package net.sourceforge.jnipp.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.io.FileInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.io.InputStream;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Enumeration;

public class DynamicClassLoader 
	extends ClassLoader
{
	public class PackageContents
	{
		private String packageName = null;
		private HashMap childPackages = new HashMap();
		private HashMap classList = new HashMap();
		
		private PackageContents(String packageName)
		{
			this.packageName = packageName;
		}
		
		private void addContainedPackage(PackageContents childPackage)
		{
			childPackages.put( childPackage.packageName, childPackage );
		}
		
		public void removeContainedPackage(String packageName)
		{
			childPackages.remove( packageName );
		}
		
		private void addContainedClass(String className)
		{
			classList.put( className, className );
		}
		
		public String getName()
		{
			return packageName;
		}
		
		public HashMap getContainedClasses()
		{
			return classList;
		}
		
		public HashMap getContainedPackages()
		{
			return childPackages;
		}
		
		public String getContainedClass(String className)
		{
			return (String) classList.get( className );
		}
		
		public PackageContents getContainedPackage(String packageName)
		{
			return (PackageContents) childPackages.get( packageName );
		}
		
		public boolean isEmpty()
		{
			if ( classList.isEmpty() == false )
				return false;
			
			Iterator it = childPackages.values().iterator();
			while ( it.hasNext() == true )
				if ( ((PackageContents) it.next()).isEmpty() == false )
					return false;
			
			return true;
		}
	}
	
	public ArrayList cpElements = new ArrayList();
	
	public DynamicClassLoader() 
	{
		super();
		addClasspathElement( System.getProperties().getProperty( "java.class.path" ) );
	}
	
	public DynamicClassLoader(ClassLoader parent)
	{
		super( parent );
		addClasspathElement( System.getProperties().getProperty( "java.class.path" ) );
	}
	
	public void addClasspathElement(String element)
	{
		StringTokenizer st = new StringTokenizer( element, File.pathSeparator );
		while( st.hasMoreTokens() == true )
			cpElements.add( st.nextToken() );
	}
	
	public void removeClasspathElement(String element)
	{
		StringTokenizer st = new StringTokenizer( element, File.pathSeparator );
		while( st.hasMoreTokens() == true )
			cpElements.remove( st.nextToken() );
	}
	
	protected Class findClass(String name)
		throws ClassNotFoundException
	{
		String fileName = name.replace( '.', '/' ) + ".class";
		Iterator it = cpElements.iterator();
		String next = null;
		byte[] classData = null;
		
		while ( it.hasNext() == true && classData == null )
		{
			next = (String) it.next();
			if ( isJarOrZip( next ) == true )
				classData = loadClassFromJarOrZip( next, fileName );
			else
				classData = loadClassFromPath( next, fileName );
		}
		
		if ( classData == null )
			throw new ClassNotFoundException( name );
		
		return this.defineClass( name, classData, 0, classData.length );
	}
	
	private boolean isJarOrZip(String path)
	{
		String lc = path.toLowerCase();
		return lc.endsWith( ".jar" ) || lc.endsWith( ".zip" );
	}
	
	private byte[] loadClassFromJarOrZip(String path, String name)
	{
		File archiveFile = new File( path );
		if ( archiveFile.exists() == false )
			return null;
		
		ZipFile zipFile = null;
		
		try
		{
			zipFile = new ZipFile( archiveFile );
		}
		catch(IOException ex)
		{
			return null;
		}
		
		ZipEntry entry = zipFile.getEntry( name );
		if ( entry == null )
			return null;
		
		try
		{
			InputStream is = zipFile.getInputStream( entry );
			int size = (int) entry.getSize();
			byte[] buffer = new byte[size];
			for ( int pos = 0; pos < size; pos += is.read( buffer, pos, size - pos ) )
				;
			
			zipFile.close();
			return buffer;
		}
		catch(IOException ex)
		{
		}

		return null;
	}
	
	private byte[] loadClassFromPath(String path, String name)
	{
		File classFile = new File( path, name );
		if ( classFile.exists() == true )
		{
			try
			{
				FileInputStream in = new FileInputStream( classFile );
				ByteArrayOutputStream out = new ByteArrayOutputStream( 5000 );
				byte[] buffer = new byte[5000];
				int len = 0;
				while ( (len = in.read( buffer )) != -1 )
					out.write( buffer, 0, len );
				in.close();
				out.close();
				return out.toByteArray();
			}
			catch(IOException ex)
			{
			}
		}

		return null;
	}
	
	public PackageContents getLoadablePackages()
	{
		Iterator it = cpElements.iterator();
		String next = null;
		PackageContents root = new PackageContents( "root" );
	
		while ( it.hasNext() == true )
		{
			next = (String) it.next();
			if ( isJarOrZip( next ) == true )
			{
				File archiveFile = new File( next );
				if ( archiveFile.exists() == false )
					continue;

				ZipFile zipFile = null;

				try
				{
					zipFile = new ZipFile( archiveFile );
					constructPackageContents( zipFile, root );
				}
				catch(IOException ex)
				{
				}
			}
			else
				constructPackageContents( new File( next ), root );
		}
		
		return root;
	}
		
	private void constructPackageContents(ZipFile zipFile, PackageContents pkgRoot)
	{
		Enumeration zipEntries = zipFile.entries();
		while ( zipEntries.hasMoreElements() == true )
		{
			ZipEntry ze = (ZipEntry) zipEntries.nextElement();
			if ( ze.getName().toLowerCase().endsWith( ".class" ) == true )
			{
				StringTokenizer st = new StringTokenizer( ze.getName(), "/" );
				PackageContents current = pkgRoot;
				String next = null;
				for ( next = st.nextToken(); 
					   st.hasMoreTokens() == true && next.toLowerCase().endsWith( ".class" ) == false; 
						next = st.nextToken() )
				{
					PackageContents pc = current.getContainedPackage( next );
					if ( pc == null )
					{
						pc = new PackageContents( next );
						current.addContainedPackage( pc );
					}
					current = pc;
				}
				current.addContainedClass( next.substring( 0, next.lastIndexOf( '.' ) ) );
			}
		}
	}

	private void constructPackageContents(File fileRoot, PackageContents pkgRoot)
	{
		if ( fileRoot.isDirectory() == false )
			return;
		
		File[] entries = fileRoot.listFiles();
		
		for ( int i = 0; i < entries.length; ++i )
		{
			if ( entries[i].isDirectory() == true )
			{
				PackageContents pc = pkgRoot.getContainedPackage( entries[i].getName() );
				if ( pc == null )
				{
					pc = new PackageContents( entries[i].getName() );
					pkgRoot.addContainedPackage( pc );
				}
				constructPackageContents( entries[i], pc );
				if ( pc.isEmpty() == true )
					pkgRoot.removeContainedPackage( pc.getName() );
			}
			else
			if ( entries[i].getName().toLowerCase().endsWith( ".class" ) == true )
				if ( pkgRoot.getContainedClass( entries[i].getName() ) == null )
					pkgRoot.addContainedClass( entries[i].getName().substring( 0, entries[i].getName().lastIndexOf( '.' ) ) );
		}
	}
	
	public static void dump(PackageContents pc, int tabLevel)
	{
		for ( int i = 0; i < tabLevel; ++i )
			System.out.print( "\t" );
		System.out.println( "package " + pc.getName() + " : " );
		HashMap childPackages = pc.getContainedPackages();
		Iterator it = childPackages.values().iterator();
		while ( it.hasNext() == true )
			dump ( (PackageContents) it.next(), tabLevel + 1 );
		
		it = pc.getContainedClasses().values().iterator();
		while ( it.hasNext() == true )
		{
			for ( int i = 0; i <= tabLevel; ++i )
				System.out.print( "\t" );
			System.out.println( (String) it.next() );
		}
	}
	
	public static void main(String[] args)
	{
		try
		{
			DynamicClassLoader loader = new DynamicClassLoader();
			String path = "d:\\development\\jnipp\\build\\lib\\common.jar";
			path += File.pathSeparator + "d:\\development\\jnipp\\build\\lib\\peergen.jar";
			path += File.pathSeparator + "d:\\development\\jnipp\\build\\lib\\proxygen.jar";
			path += File.pathSeparator + "d:\\development\\jnipp\\build\\lib\\gui.jar";
			path += File.pathSeparator + "d:\\development\\jnipp\\build\\classes";
			loader.addClasspathElement( "d:\\development\\jnipp\\build\\classes" );
			PackageContents root = loader.getLoadablePackages();
			dump( (PackageContents) root, 0 );
		}
		catch(Exception ex)
		{
			System.out.println( ex );
		}
	}
}
