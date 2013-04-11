package net.sourceforge.jnipp.common.testCases;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;

/**
 * All JUnit test cases for this package.
 *
 * @author Philllip E. Trewhella
 * @version 1.0
 */

public class AllJUnitTests
  extends TestCase
{
  public AllJUnitTests(String name)
  {
	 super( name );
  }

  public static Test suite()
  {
	 TestSuite suite = new TestSuite();
	 suite.addTest( new TestSuite( ClassNodeTestCase.class ) );
	 suite.addTest( new TestSuite( FieldNodeTestCase.class ) );
	 suite.addTest( new TestSuite( MethodNodeTestCase.class ) );
	 return suite;
  }
}