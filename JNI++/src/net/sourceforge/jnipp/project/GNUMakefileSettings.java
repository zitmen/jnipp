package net.sourceforge.jnipp.project;

import org.w3c.dom.Node;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class GNUMakefileSettings 
{
	private String name = null;
	private Project project = null;
	
	public GNUMakefileSettings(Project project, Element elementNode)
		throws DOMException, ProjectFormatException
	{
		this.project = project;
		initialize( elementNode );
	}

	public GNUMakefileSettings(Project project, String name)
	{
		this.project = project;
		setName( name );
	}

	private void initialize(Element elementNode)
		throws DOMException, ProjectFormatException
	{
		if ( elementNode.hasAttribute( "name" ) == false )
			throw new ProjectFormatException( "attribute \"name\" required for \"nmakefile\" element" );
		name = elementNode.getAttribute( "name" );
	}
	
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
	public Project getProject()
	{
		return project;
	}

	public Node getDOMNode(Document targetDoc)
	{
		Element node = targetDoc.createElement( "gnumakefile" );
		node.setAttribute( "name", name );

		return node;
	}
}
