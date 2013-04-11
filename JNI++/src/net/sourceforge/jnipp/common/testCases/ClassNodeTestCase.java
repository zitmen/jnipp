package net.sourceforge.jnipp.common.testCases;

import junit.framework.TestCase;
import net.sourceforge.jnipp.common.ClassNode;
import java.util.Iterator;
import java.lang.ClassNotFoundException;
import net.sourceforge.jnipp.common.FieldNode;
import net.sourceforge.jnipp.common.MethodNode;

/**
 * @author Philllip E. Trewhella
 * @version 1.0
 */

public class ClassNodeTestCase
  extends TestCase
{
  private ClassNode node = null;

  public ClassNodeTestCase(String name)
  {
	 super( name );
  }

  public static void main(String[] args)
  {
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

  public void testJNIString()
  {
	 assertEquals( "Lcom/objectsociety/jnipp/common/testCases/TestSource;", node.getJNIString() );

	 Iterator it = node.getFields();
	 assertEquals( "Z", ((FieldNode) it.next()).getType().getJNIString() );
	 assertEquals( "B", ((FieldNode) it.next()).getType().getJNIString() );
	 assertEquals( "C", ((FieldNode) it.next()).getType().getJNIString() );
	 assertEquals( "S", ((FieldNode) it.next()).getType().getJNIString() );
	 assertEquals( "I", ((FieldNode) it.next()).getType().getJNIString() );
	 assertEquals( "J", ((FieldNode) it.next()).getType().getJNIString() );
	 assertEquals( "F", ((FieldNode) it.next()).getType().getJNIString() );
	 assertEquals( "D", ((FieldNode) it.next()).getType().getJNIString() );
	 assertEquals( "Ljava/lang/String;", ((FieldNode) it.next()).getType().getJNIString() );
	 assertEquals( "Ljava/util/Vector;", ((FieldNode) it.next()).getType().getJNIString() );
	 assertEquals( "[I", ((FieldNode) it.next()).getType().getJNIString() );
	 assertEquals( "[[I", ((FieldNode) it.next()).getType().getJNIString() );
	 assertEquals( "[Ljava/lang/Integer;", ((FieldNode) it.next()).getType().getJNIString() );
  }

  public void testAttributes()
  {
	 assertEquals( "TestSource", node.getClassName() );
	 assertEquals( "com.objectsociety.jnipp.common.testCases", node.getPackageName() );
	 assertEquals( "com::objectsociety::jnipp::common::testCases", node.getNamespace() );

	 Iterator it = node.getNamespaceElements();
	 assert( it.hasNext() == true && ((String) it.next()).equals( "com" ) );
	 assert( it.hasNext() == true && ((String) it.next()).equals( "objectsociety" ) );
	 assert( it.hasNext() == true && ((String) it.next()).equals( "jnipp" ) );
	 assert( it.hasNext() == true && ((String) it.next()).equals( "common" ) );
	 assert( it.hasNext() == true && ((String) it.next()).equals( "testCases" ) );

	 it = node.getFields();
	 while ( it.hasNext() == true )
	 {
		FieldNode current = (FieldNode) it.next();
		if ( current.getName() == "oneDimOfInts" )
		{
		  ClassNode elType = current.getType().getComponentType();
		  assertEquals( "int", elType.getClassName() );
		  assertEquals( 1, elType.getArrayDims() );
		}
		else
		if ( current.getName() == "twoDimOfInts" )
		{
		  ClassNode elType = current.getType().getComponentType();
		  assertEquals( "int", elType.getClassName() );
		  assertEquals( 2, elType.getArrayDims() );
		}
		else
		if ( current.getName() == "oneDimOfIntegers" )
		{
		  ClassNode elType = current.getType().getComponentType();
		  assertEquals( "java.lang.Integer", elType.getClassName() );
		  assertEquals( 1, elType.getArrayDims() );
		}
	 }
  }
}