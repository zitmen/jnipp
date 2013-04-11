package net.sourceforge.jnipp.project;

import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.util.ArrayList;
import java.util.Iterator;
import org.w3c.dom.NodeList;

/**
 * Class for manipulating Peer Generator settings.
 *
 * Each instance of this class contains data utilized to specify and query settings
 * controlling the manner in which the C++ Peer Generator is run.  Each 
 * <code>Project</code> instance contains zero or one <code>PeerGenSettings</code>
 * instance.
 *
 * @author $Author: ptrewhella $
 * @version $Revision: 1.15 $
 * @see net.sourceforge.jnipp.project.Project#peerGenSettings
 */

public class PeerGenSettings 
{
	/**
	 * Generate code destrucively?
	 */
	private boolean destructive = false;
	
	/**
	 * Generate virtual methods to accommodate implementation through inheritance?
	 */
	private boolean useInheritance = false;
	
	/**
	 * Generate proxy and helper classes as parameters, return and attribute types?
	 */
	private boolean useRichTypes = false;
	
	/**
	 * The proxy generator settings for use if "useRichTypes" is set.
	 */
	private ProxyGenSettings proxyGenSettings = null;

	/**
	 * Reference to the owning <code>Project</code> instance.
	 */
	private Project project = null;
	
	private ArrayList classNames = new ArrayList();
	
	/**
	 * Public class constructor.
	 *
	 * The class constructor sets the owning <code>Project</code> instance
	 * and then calls the <code>initialize()</code> method using the Element node
	 * passed in as parameter.
	 *
	 * @param project The owning <code>Project</code> instance.
	 * @param elementNode The DOM representation of the settings.
	 * @see #project
	 * @see #initialize
	 */
	public PeerGenSettings(Project project, Element elementNode)
		throws DOMException, ProjectFormatException
	{
		this.project = project;
		initialize( elementNode );
	}

	/**
	 * Public class constructor.
	 *
	 * The class constructor sets the owning <code>Project</code> instance
	 *
	 * @param project The owning <code>Project</code> instance.
	 * @see #project
	 */
	public PeerGenSettings(Project project)
		throws DOMException
	{
		this.project = project;
	}
	
	/**
	 * Public class instance initializer.
	 *
	 * The <code>initialize()</code> method is called to initialize the settings
	 * for this <code>ProxyGenSettings</code> instance using the elements contained
	 * in the supplied <code>Element</code> parameter.  This method is called from
	 * the constructor.
	 *
	 * @param elementNode The <code>Element</code> containing the elements that specify
	 * the settings for the Peer Generator.
	 * @exception DOMException
	 * @exception ProjectFormatException
	 * @see #PeerGenSettings
	 */
	public void initialize(Element elementNode)
		throws DOMException, ProjectFormatException
	{
		if ( elementNode.hasAttribute( "destructive" ) == true )
			destructive = Boolean.valueOf( elementNode.getAttribute( "destructive" ) ).booleanValue();
		if ( elementNode.hasAttribute( "useInheritance" ) == true )
			useInheritance = Boolean.valueOf( elementNode.getAttribute( "useInheritance" ) ).booleanValue();
		if ( elementNode.hasAttribute( "useRichTypes" ) == true )
			useRichTypes = Boolean.valueOf( elementNode.getAttribute( "useRichTypes" ) ).booleanValue();
			
		NodeList childNodes = elementNode.getChildNodes();
		for ( int i = 0; i < childNodes.getLength(); ++i )
		{
			if ( childNodes.item( i ).getNodeType() != Node.ELEMENT_NODE )
				continue;
			Element currentChild = (Element) childNodes.item( i );

			if ( currentChild.getNodeName().equals( "input-classes" ) == true )
			{
				NodeList inputClassNodes = currentChild.getChildNodes();
				
				for ( int j = 0; j < inputClassNodes.getLength(); ++j )
				{
					if ( inputClassNodes.item( j ).getNodeType() != Node.ELEMENT_NODE )
						continue;
						
					if ( inputClassNodes.item( j ).getNodeName().equals( "input-class" ) == true )
					{
						Element inputClassElement = (Element) inputClassNodes.item( j );
						if ( inputClassElement.hasAttribute( "name" ) == false )
							throw new ProjectFormatException( "attribute \"name\" required for element \"input-class\"" );
						classNames.add( inputClassElement.getAttribute( "name" ) );
					}
					else
						throw new ProjectFormatException( "unrecognized element: " + inputClassNodes.item( j ).getNodeName() );
				}
			}
			else
			if ( currentChild.getNodeName().equals( "proxygen" ) == true )
			{
				if ( proxyGenSettings == null )
					proxyGenSettings = new ProxyGenSettings( project, currentChild );
				else
					throw new ProjectFormatException( "Only one \"proxygen\" element is allowed under each \"peergen\" element." );
			}
			else
				throw new ProjectFormatException( "unrecognized element: " + currentChild.getNodeName() );
		}
		
		if ( useRichTypes == true && proxyGenSettings == null )
			throw new ProjectFormatException( "When \"useRichTypes\" is \"true\", you must supply a nested \"proxygen\" element." );
			
		if ( useRichTypes == false && proxyGenSettings != null )
			throw new ProjectFormatException( "The \"useRichTypes\" attribute must be \"true\" if the \"proxygen\" element is supplied." );
	}

	/**
	 * Public accessor for the <code>destructive</code> field.
	 *
	 * @return Boolean indicating whether to generate code destructively.
	 * @see #destructive
	 */
	public boolean isDestructive()
	{
		return destructive;
	}
	
	/**
	 * Public mutator for the <code>destructive</code> field.
	 *
	 * @param destructive Boolean indicating whether to generate code
	 * destructively.
	 * @see #destructive
	 */
	public void setDestructive(boolean destructive)
	{
		this.destructive = destructive;
	}

	/**
	 * Public accessor for the <code>useInheritance</code> field.
	 *
	 * @return Boolean indicating whether to generate virtual methods with
	 * a default no-op implementation.
	 * @see #useInheritance
	 */
	 public boolean getUseInheritance()
	 {
	 	return useInheritance;
	 }
	 
	/**
	 * Public mutator for the <code>useInheritance</code> field.
	 *
	 * @param useInheritance Boolean indicating whether to generate virtual
	 * methods with a default no-op implementation.
	 * @see #useInheritance
	 */
	public void setUseInheritance(boolean useInheritance)
	{
		this.useInheritance = useInheritance;
	}
	
	/**
	 * Public accessor for the <code>useRichTypes</code> field.
	 *
	 * @return Boolean indicating whether to generate proxy and helper classes for
	 * return, parameter and attribute types.
	 * @see #useRichTypes
	 */
	public boolean getUseRichTypes()
	{
		return useRichTypes;
	}
	
	/**
	 * Public mutator for the <code>useRichTypes</code> field.
	 *
	 * @param useRichTypes Boolean indicating whether to generate proxy and helper
	 * classes for return, parameter and attribute types.
	 * @see #useRichTypes
	 */
	public void setUseRichTypes(boolean useRichTypes)
	{
		this.useRichTypes = useRichTypes;
	}
	
	/**
	 * Public accessor for the class names.
	 *
	 * This method will return an iterator over the set of classes that are
	 * defined to be part of the group.
	 *
	 * @return Iterator over the set of classes defined for the group.
	 * @see #classNames
	 */
	public Iterator getClassNames()
	{
		return classNames.iterator();
	}
	
	/**
	 * Public method to add a class to the group.
	 *
	 * This method is utilized to add a class to the set of classes defined as
	 * part of the group.
	 *
	 * @param className The name of the class to be added to the group.
	 * @see #classNames
	 */
	public void addClassName(String className)
	{
		classNames.add( className );
	}
	
	/**
	 * Public method for removing a class from the group.
	 *
	 * This method is utilized to remove a class with the given name from the
	 * set of classes defined as part of the group.
	 *
	 * @param className The name of the class to be removed from the group.
	 * @see #classNames
	 */
	public void removeClassName(String className)
	{
		classNames.remove( className );
	}
	
	/**
	 * Method for retrieving the equivalent DOM node representation of the Peer
	 * Generator settings.
	 *
	 * This method is utilized by the <code>Project</code> class as part of 
	 * its <code>save()</code> method to serialize the Peer Generator
	 * settings, along with all of its child settings.
	 *
	 * @param targetDoc The target <code>Document</code> instance.
	 * @return The DOM <code>Node</code> representation of the settings.
	 * @see net.sourceforge.jnipp.project.Project#save
	 */
	public Node getDOMNode(Document targetDoc)
	{
		Element node = targetDoc.createElement( "peergen" );
		node.setAttribute( "destructive", String.valueOf( destructive ) );
		node.setAttribute( "useInheritance", String.valueOf( useInheritance ) );
		node.setAttribute( "useRichTypes", String.valueOf( useRichTypes ) );
		
		Element classesElement = null;
		
		Iterator it = getClassNames();
		while ( it.hasNext() )
		{
			if ( classesElement == null )
			{
				classesElement = targetDoc.createElement( "input-classes" );
				node.appendChild( classesElement );
			}
		
			Element classNameElement = targetDoc.createElement( "input-class" );
			classesElement.appendChild( classNameElement );
			classNameElement.setAttribute( "name", (String) it.next() );
		}
		
		if ( useRichTypes == true && proxyGenSettings != null )
			node.appendChild( proxyGenSettings.getDOMNode( targetDoc ) );
		
		return node;
	}
	
	/**
	 * Public accessor for the owning <code>Project</code>.
	 *
	 * @return The owning <code>Project</code> instance.
	 * @see #project
	 */
	public Project getProject()
	{
		return project;
	}
	
	public ProxyGenSettings getProxyGenSettings()
	{
		return proxyGenSettings;
	}
}
