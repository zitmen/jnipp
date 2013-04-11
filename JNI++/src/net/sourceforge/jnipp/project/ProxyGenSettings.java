package net.sourceforge.jnipp.project;

import org.w3c.dom.Node;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.util.ArrayList;
import java.util.Iterator;
import org.w3c.dom.NodeList;

/**
 * Class for manipulating Proxy Generator settings.
 *
 * Each instance of this class contains data utilized to specify and query settings
 * controlling the manner in which the C++ Proxy Generator is run.  Each 
 * <code>Project</code> instance contains zero or one <code>ProxyGenSettings</code>
 * instance.
 *
 * @author $Author: ptrewhella $
 * @version $Revision: 1.21 $
 * @see net.sourceforge.jnipp.project.Project#proxyGenSettings
 */

public class ProxyGenSettings 
{
	/**
	 * Generate field accessor methods?
	 */
	private boolean generateAttributeGetters = false;
	
	/**
	 * Generate field mutator methods?
	 */
	private boolean generateAttributeSetters = false;
	
	/**
	 * Generate inheritance hierarchy?
	 */
	private boolean useInheritance = false;
	
	/**
	 * Generate proxy and helper classes for all parameter, return
	 * and field types?
	 */
	private boolean useRichTypes = true;
	
	/**
	 * Generate proxy classes for all declared inner classes?
	 */
	private boolean generateInnerClasses = false;
	
	private int recursionLevel = -1;
	
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
	 * @exception ProjectFormatException
	 * @see #project
	 * @see #initialize
	 */
	public ProxyGenSettings(Project project, Element elementNode)
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
	public ProxyGenSettings(Project project)
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
	 * the settings for the Proxy Generator.
	 * @exception ProjectFormatException
	 * @see #ProxyGenSettings
	 */
	private void initialize(Element elementNode)
		throws DOMException, ProjectFormatException
	{
		if ( elementNode.hasAttribute( "attributeGetters" ) == true )
			generateAttributeGetters = Boolean.valueOf( elementNode.getAttribute( "attributeGetters" ) ).booleanValue();
		if ( elementNode.hasAttribute( "attributeSetters" ) == true )
			generateAttributeSetters = Boolean.valueOf( elementNode.getAttribute( "attributeSetters" ) ).booleanValue();
		if ( elementNode.hasAttribute( "useInheritance" ) == true )
			useInheritance = Boolean.valueOf( elementNode.getAttribute( "useInheritance" ) ).booleanValue();
		if ( elementNode.hasAttribute( "useRichTypes" ) == true )
			useRichTypes = Boolean.valueOf( elementNode.getAttribute( "useRichTypes" ) ).booleanValue();
		if ( elementNode.hasAttribute( "innerClasses" ) == true )
			generateInnerClasses = Boolean.valueOf( elementNode.getAttribute( "innerClasses" ) ).booleanValue();
		if ( elementNode.hasAttribute( "recursionLevel" ) == true )
			recursionLevel = Integer.valueOf( elementNode.getAttribute( "recursionLevel" ) ).intValue();

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
				throw new ProjectFormatException( "unrecognized element: " + currentChild.getNodeName() );
		}
	}
	
	/**
	 * Public accessor for the <code>generateAttributeGetters</code> setting.
	 *
	 * @see #generateAttributeGetters
	 */
	public boolean getGenerateAttributeGetters()
	{
		return generateAttributeGetters;
	}
	
	/**
	 * Public mutator for the <code>generateAttributeGetters</code> setting.
	 *
	 * @see #generateAttributeGetters
	 */
	public void setGenerateAttributeGetters(boolean generateAttributeGetters)
	{
		this.generateAttributeGetters = generateAttributeGetters;
	}
	
	/**
	 * Public accessor for the <code>generateAttributeSetters</code> setting.
	 *
	 * @see #generateAttributeSetters
	 */
	public boolean getGenerateAttributeSetters()
	{
		return generateAttributeSetters;
	}
	
	/**
	 * Public mutator for the <code>generateAttributeSetters</code> setting.
	 *
	 * @see #generateAttributeSetters
	 */
	public void setGenerateAttributeSetters(boolean generateAttributeSetters)
	{
		this.generateAttributeSetters = generateAttributeSetters;
	}
	
	/**
	 * Public accessor for the <code>useInheritance</code> setting.
	 *
	 * @see #useInheritance
	 */
	public boolean getUseInheritance()
	{
		return useInheritance;
	}
	
	/**
	 * Public mutator for the <code>useInheritance</code> setting.
	 *
	 * @see #useInheritance
	 */
	public void setUseInheritance(boolean useInheritance)
	{
		this.useInheritance = useInheritance;
	}
	
	/**
	 * Public accessor for the <code>usrRichTypes</code> setting.
	 *
	 * @see #useRichTypes
	 */
	public boolean getUseRichTypes()
	{
		return useRichTypes;
	}
	
	/**
	 * Public mutator for the <code>useRichTypes</code> setting.
	 *
	 * @see #useRichTypes
	 */
	public void setUseRichTypes(boolean useRichTypes)
	{
		this.useRichTypes = useRichTypes;
	}
	
	/**
	 * Public accessor for the <code>generateInnerClasses</code> setting.
	 *
	 * @see #generateInnerClasses
	 */
	public boolean getGenerateInnerClasses()
	{
		return generateInnerClasses;
	}
	
	/**
	 * Public mutator for the <code>generateInnerClasses</code> setting.
	 *
	 * @see #generateInnerClasses
	 */
	public void setGenerateInnerClasses(boolean generateInnerClasses)
	{
		this.generateInnerClasses = generateInnerClasses;
	}
	
	/**
	 * Public accessor for the <code>recursive</code> setting.
	 *
	 * @return Boolean indicating whether to generate code recursively.
	 * @see #recursive
	 */
	public int getRecursionLevel()
	{
		return recursionLevel;
	}
	
	/**
	 * Public mutator for the <code>recursive</code> setting.
	 *
	 * @param recursive Boolean indicating whether to generate code recursively.
	 * @see #recursive
	 */
	public void setRecursionLevel(int recursionLevel)
	{
		this.recursionLevel = recursionLevel;
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
	 * Method for retrieving the equivalent DOM node representation of the Proxy
	 * Generator settings.
	 *
	 * This method is utilized by the <code>Project</code> class as part of 
	 * its <code>getDOMNode()</code> method to serialize the Proxy Generator
	 * settings, along with all of its child settings.
	 *
	 * @param targetDoc The target <code>Document</code> instance.
	 * @return The DOM <code>Node</code> representation of the settings.
	 * @see net.sourceforge.jnipp.project.Project#save
	 */
	public Node getDOMNode(Document targetDoc)
	{
		Element node = targetDoc.createElement( "proxygen" );
		node.setAttribute( "attributeGetters", String.valueOf( generateAttributeGetters ) );
		node.setAttribute( "attributeSetters", String.valueOf( generateAttributeSetters ) );
		node.setAttribute( "useInheritance", String.valueOf( useInheritance ) );
		node.setAttribute( "useRichTypes", String.valueOf( useRichTypes ) );
		node.setAttribute( "innerClasses", String.valueOf( generateInnerClasses ) );
		node.setAttribute( "recursionLevel", String.valueOf( recursionLevel ) );
		
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

		return node;
	}
	
	/**
	 * Public accessor for the owning <code>Project</code>.
	 *
	 * @see #project
	 */
	public Project getProject()
	{
		return project;
	}
}
