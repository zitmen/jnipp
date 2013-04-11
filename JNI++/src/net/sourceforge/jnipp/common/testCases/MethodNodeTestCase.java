package net.sourceforge.jnipp.common.testCases;

import junit.framework.TestCase;
import net.sourceforge.jnipp.common.ClassNode;
import java.util.Iterator;
import java.lang.ClassNotFoundException;
import net.sourceforge.jnipp.common.MethodNode;

/**
 * @author Philllip E. Trewhella
 * @version 1.0
 */

public class MethodNodeTestCase
  extends TestCase
{
  private ClassNode node = null;

  public MethodNodeTestCase(String name)
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
	 Iterator it = node.getMethods();
	 MethodNode current = (MethodNode) it.next();
	 assertEquals( "test1", current.getName() );
	 assertEquals( "test1", current.getJNIName() );
	 assertEquals( "()Ljava/util/Vector;", current.getJNISignature() );
	 current = (MethodNode) it.next();
	 assertEquals( "test2", current.getName() );
	 assertEquals( "test2", current.getJNIName() );
	 assertEquals( "(Ljava/util/Vector;)V", current.getJNISignature() );
	 current = (MethodNode) it.next();
	 assertEquals( "test3", current.getName() );
	 assertEquals( "test3", current.getJNIName() );
	 assertEquals( "(Ljava/util/Vector;Ljava/net/URL;[[I)V", current.getJNISignature() );
  }
}