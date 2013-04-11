package net.sourceforge.jnipp.common.testCases;

import junit.framework.TestCase;
import net.sourceforge.jnipp.common.ClassNode;
import java.util.Iterator;
import java.lang.ClassNotFoundException;
import net.sourceforge.jnipp.common.FieldNode;

/**
 * @author Philllip E. Trewhella
 * @version 1.0
 */

public class FieldNodeTestCase
  extends TestCase
{
  private ClassNode node = null;

  public FieldNodeTestCase(String name)
  {
	 super( name );
  }

  public void setUp()
  {
	 try
	 {
		node = ClassNode.getClassNode( "com.objectsociety.jnipp.common.testCases.TestSource" );
	 }
	 catch(ClassNotFoundException ex)
	 {
		assert( ex.toString(), false );
	 }
  }

  public void tearDown()
  {
	 node = null;
  }

  public void testAttributes()
  {
	 Iterator it = node.getFields();
	 assert( it.hasNext() == true && ((FieldNode) it.next()).getName().equals( "booleanField" ) );
	 assert( it.hasNext() == true && ((FieldNode) it.next()).getName().equals( "byteField" ) );
	 assert( it.hasNext() == true && ((FieldNode) it.next()).getName().equals( "charField" ) );
	 assert( it.hasNext() == true && ((FieldNode) it.next()).getName().equals( "shortField" ) );
	 assert( it.hasNext() == true && ((FieldNode) it.next()).getName().equals( "intField" ) );
	 assert( it.hasNext() == true && ((FieldNode) it.next()).getName().equals( "longField" ) );
	 assert( it.hasNext() == true && ((FieldNode) it.next()).getName().equals( "floatField" ) );
	 assert( it.hasNext() == true && ((FieldNode) it.next()).getName().equals( "doubleField" ) );
	 assert( it.hasNext() == true && ((FieldNode) it.next()).getName().equals( "stringField" ) );
	 assert( it.hasNext() == true && ((FieldNode) it.next()).getName().equals( "vectorField" ) );
  }
}