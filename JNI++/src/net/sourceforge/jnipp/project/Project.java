package net.sourceforge.jnipp.project;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import java.io.IOException;
import org.w3c.dom.NodeList;
import org.w3c.dom.DOMException;
import java.util.Iterator;
import org.apache.xerces.dom.DocumentImpl;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.Serializer;
import org.apache.xml.serialize.XMLSerializer;
import java.io.FileWriter;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import java.io.FileReader;
import org.w3c.dom.Node;
import java.util.ArrayList;

/**
 * Root class for project settings.
 *
 * Each instance of this class represents the settings for a particular project.
 * An instance is created by the <code>Main</code> class where it is used to
 * read in a project file.  An instance is also created by the GUI when creating
 * a new or reading an existing project file.
 *
 * @author $Author: ptrewhella $
 * @version $Revision: 1.11 $
 * @see net.sourceforge.jnipp.main.Main
 * @see net.sourceforge.jnipp.gui.App
 */

public class Project 
{
	/**
	 * The name of the project.
	 */
	private String name = null;
	
	/**
	 * The settings for C++ peer class generation.
	 */
	private ArrayList peerGenSettingsList = new ArrayList();
	
	/**
	 * The settings for C++ proxy class generation.
	 */
	private ArrayList proxyGenSettingsList = new ArrayList();
	
	/**
	 * The settings for Win32 NMAKE makefile generation.
	 */
	private NMakefileSettings nMakefileSettings = null;
	
	/**
	 * The settings for GNU gmake makefile generation.
	 */
	private GNUMakefileSettings gnuMakefileSettings = null;
	
	/**
	 * Base directory for all generated files in this group.
	 */
	private String javaOutputDir = ".";
	
	private String cppOutputDir = ".";
	
	/**
	 * The target type for the group.
	 */
	private String targetType = null;
	
	/**
	 * The target name for the group.
	 */
	private String targetName = null;

	/**
	 * Use partial specialization?
	 */
	private boolean usePartialSpec = true;

	/**
	 * Default class constructor.
	 */
	public Project() 
	{
   }
	
	/**
	 * Public field mutator for the <code>name</code> field.
	 *
	 * @param name The name of the project.
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * Public field accessor for the <code>name</code> field.
	 *
	 * @return The name of the project.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Public accessor for the Peer Generator settings.
	 *
	 * @return The <code>Iterator</code> over the Peer Generator settings for the project.
	 * @see #peerGenSettingsList
	 */
	public Iterator getPeerGenSettings()
	{
		return peerGenSettingsList.iterator();
	}
	
	public void addPeerGenSettings(PeerGenSettings peerGenSettings)
	{
		peerGenSettingsList.add( peerGenSettings );
	}
	
	public void removePeerGenSettings(PeerGenSettings peerGenSettings)
	{
		peerGenSettingsList.remove( peerGenSettings );
	}
	
	public Iterator getProxyGenSettings()
	{
		return proxyGenSettingsList.iterator();
	}
	
	public void addProxyGenSettings(ProxyGenSettings proxyGenSettings)
	{
		proxyGenSettingsList.add( proxyGenSettings );
	}
	
	public void removeProxyGenSettings(ProxyGenSettings proxyGenSettings)
	{
		proxyGenSettingsList.remove( proxyGenSettings );
	}
	
	/**
	 * Public accessor for the NMake makefile Generator settings.
	 *
	 * @return The <code>NMakefileSettings</code> instance for the project group.
	 * @see #nMakefileSettings
	 */
	public NMakefileSettings getNMakefileSettings()
	{
		return nMakefileSettings;
	}
	
	/**
	 * Public mutator for the NMake makefile Generator settings.
	 *
	 * @param nMakefileSettings The <code>NMakefileSettings</code> instance to be
	 * used for the project group.
	 * @see #nMakefileSettings
	 */
	public void setNMakefileSettings(NMakefileSettings nMakefileSettings)
	{
		this.nMakefileSettings = nMakefileSettings;
	}
	
	/**
	 * Public accessor for the GNU makefile Generator settings.
	 *
	 * @return The <code>GNUMakefileSettings</code> instance for the project group.
	 * @see #gnuMakefileSettings
	 */
	public GNUMakefileSettings getGNUMakefileSettings()
	{
		return gnuMakefileSettings;
	}
	
	/**
	 * Public mutator for the GNU makefile Generator settings.
	 *
	 * @param gnuMakefileSettings The <code>GNUMakefileSettings</code> instance to be
	 * used for the project group.
	 * @see #gnuMakefileSettings
	 */
	public void setGNUMakefileSettings(GNUMakefileSettings gnuMakefileSettings)
	{
		this.gnuMakefileSettings = gnuMakefileSettings;
	}
	
	/**
	 * Public accessor for the output directory.
	 *
	 * @return The base directory for all code generated for this project group.
	 * @see #outputDir
	 */
	public String getJavaOutputDir()
	{
		return javaOutputDir;
	}
	
	/**
	 * Public mutator for the output directory.
	 *
	 * @param outputDir The base directory for all code generated for this project 
	 * group.
	 * @see #outputDir
	 */
	public void setJavaOutputDir(String javaOutputDir)
	{
		this.javaOutputDir = javaOutputDir;
	}
	
	public String getCPPOutputDir()
	{
		return cppOutputDir;
	}
	
	public void setCPPOutputDir(String cppOutputDir)
	{
		this.cppOutputDir = cppOutputDir;
	}
	
	public String getTargetType()
	{
		return targetType;
	}
	
	public void setTargetType(String targetType)
	{
		this.targetType = targetType;
	}
	
	public String getTargetName()
	{
		return targetName;
	}
	
	public void setTargetName(String targetName)
	{
		this.targetName = targetName;
	}
	
	public boolean getUsePartialSpec()
	{
		return usePartialSpec;
	}
	
	public void setUsePartialSpec(boolean usePartialSpec)
	{
		this.usePartialSpec = usePartialSpec;
	}
	
	/**
	 * Load a project from a file.
	 *
	 * This method is utilized to load and initialize this <code>Project</code>
	 * instance with the data contained in the specified project file.  The project
	 * file is in an XML format with defined tags for the various project attributes.
	 *
	 * @param fileName Name of project file to be loaded.
	 * @exception SAXException
	 * @exception IOException
	 * @exception DOMException
	 * @exception ProjectFormatException
	 */
	public void load(String fileName)
		throws SAXException, IOException, DOMException, ProjectFormatException
	{
		DOMParser parser = new DOMParser();
		InputSource is = new InputSource( new FileReader( fileName ) );
		parser.parse( is );
		Document root = parser.getDocument();
		
		Element projectElement = root.getDocumentElement();
		if ( projectElement.hasAttribute( "name" ) == false )
			throw new ProjectFormatException( "attribute \"name\" must be specified for the project" );
		if ( projectElement.hasAttribute( "targetType" ) == false )			
			throw new ProjectFormatException( "attribute \"targetType\" must be specified for the project" );
		if ( projectElement.hasAttribute( "targetName" ) == false )
			throw new ProjectFormatException( "attribute \"targetName\" must be specified for the project" );

		name = projectElement.getAttribute( "name" );
		targetType = projectElement.getAttribute( "targetType" );
		if ( targetType.equals( "shlib" ) != true && targetType.equals( "exe" ) != true )
			throw new ProjectFormatException( "attribute \"targetType\" must be one of \"shlib\" or \"exe\"" );

		targetName = projectElement.getAttribute( "targetName" );
		if ( projectElement.hasAttribute( "javaOutputDir" ) == true )
			javaOutputDir = projectElement.getAttribute( "javaOutputDir" );
		if ( projectElement.hasAttribute( "cppOutputDir" ) == true )
			cppOutputDir = projectElement.getAttribute( "cppOutputDir" );
		if ( projectElement.hasAttribute( "usePartialSpec" ) == true )
			usePartialSpec = Boolean.valueOf( projectElement.getAttribute( "usePartialSpec" ) ).booleanValue();

		NodeList childNodes = projectElement.getChildNodes();
		for ( int i = 0; i < childNodes.getLength(); ++i )
		{
			if ( childNodes.item( i ).getNodeType() != Node.ELEMENT_NODE )
				continue;
			Element currentChild = (Element) childNodes.item( i );
			String nodeName = currentChild.getNodeName();

			if ( nodeName.equals( "peergen" ) == true )
				addPeerGenSettings( new PeerGenSettings( this, currentChild ) );
			else
			if ( nodeName.equals( "proxygen" ) == true )
				addProxyGenSettings( new ProxyGenSettings( this, currentChild ) );
			else
			if ( nodeName.equals( "nmakefile" ) == true )
			{
				if ( nMakefileSettings != null )
					throw new ProjectFormatException( "only one \"nmakefile\" element allowed per project" );
				nMakefileSettings = new NMakefileSettings( this, currentChild );
			}
			else
			if ( nodeName.equals( "gnumakefile" ) == true )
			{
				if ( gnuMakefileSettings != null )
					throw new ProjectFormatException( "only one \"gnumakefile\" element allowed per project" );
				gnuMakefileSettings = new GNUMakefileSettings( this, currentChild );
			}
			else
				throw new ProjectFormatException( "unrecognized element: " + nodeName );
		}
	}
	
	/**
	 * Save a project to a file.
	 *
	 * This method is utilized to save the project settings to the file whose name
	 * is specified as parameter.  All of the data is stored in a defined XML
	 * format with defined tags for the various project attributes.
	 *
	 * @param fileName Name of project file to be loaded.
	 * @exception IOException
	 */
	public void save(String fileName)
		throws IOException
	{
		Document doc = new DocumentImpl();
		Element projectElement = doc.createElement( "project" );
		doc.appendChild( projectElement );
		projectElement.setAttribute( "name", name );
		projectElement.setAttribute( "targetType", targetType );
		projectElement.setAttribute( "targetName", targetName );
		projectElement.setAttribute( "javaOutputDir", javaOutputDir );
		projectElement.setAttribute( "cppOutputDir", cppOutputDir );
		projectElement.setAttribute( "usePartialSpec", String.valueOf( usePartialSpec ) );

		Iterator it = peerGenSettingsList.iterator();
		while ( it.hasNext() == true )
			projectElement.appendChild( ((PeerGenSettings) it.next()).getDOMNode( doc ) );
		
		it = proxyGenSettingsList.iterator();
		while ( it.hasNext() == true )
			projectElement.appendChild( ((ProxyGenSettings) it.next()).getDOMNode( doc ) );
		
		if ( nMakefileSettings != null )
			projectElement.appendChild( nMakefileSettings.getDOMNode( doc ) );
		
		if ( gnuMakefileSettings != null )
			projectElement.appendChild( gnuMakefileSettings.getDOMNode( doc ) );
		
		OutputFormat format = new OutputFormat( doc );
		FileWriter outFile = new FileWriter( fileName );
		XMLSerializer serial = new XMLSerializer( outFile, format );
		serial.asDOMSerializer();
		serial.serialize( doc.getDocumentElement() );
	}
}
