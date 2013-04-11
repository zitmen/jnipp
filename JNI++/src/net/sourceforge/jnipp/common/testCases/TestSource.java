package net.sourceforge.jnipp.common.testCases;

/**
 * @author Philllip E. Trewhella
 * @version 1.0
 */

public class TestSource
{
  /*
   * Inner class declarations.
	*/
  private class Inner
  {
  }

  /*
   * Primitive fields with different access specifiers.
	*/
  private boolean booleanField;
  private byte byteField;
  protected char charField;
  protected short shortField;
  public int intField;
  public long longField;
  private float floatField;
  private double doubleField;

  /*
   * A few non primitive fields.
	*/
  private String stringField;
  private java.util.Vector vectorField;

  /*
   * Array fields, used as test cases for arrays.
	*/
  private int[] oneDimOfInts = null;
  private int[][] twoDimOfInts = null;
  private Integer[] oneDimOfIntegers = null;

  /*
   * Ctors with different signatures and access specifiers.
	*/
  public TestSource()
  {
  }

  public TestSource(String s)
  {
  }

  private TestSource(java.util.Vector v)
  {
  }

  protected TestSource(int i)
  {
  }

  /*
   * Methods with varying signatures.
	*/
  public java.util.Vector test1()
  {
	 return null;
  }

  public void test2(java.util.Vector v)
  {
  }

  public void test3(java.util.Vector v, java.net.URL u, int[][] intArray)
  {
  }
}