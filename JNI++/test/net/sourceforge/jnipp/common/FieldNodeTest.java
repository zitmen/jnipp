package net.sourceforge.jnipp.common;

import java.util.Iterator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Philllip E. Trewhella
 * @version 1.0
 */
public class FieldNodeTest {

    private ClassNode node = null;

    @Before
    public void setUp() {
        try {
            node = ClassNode.getClassNode("com.objectsociety.jnipp.common.testCases.TestSource");
        } catch (ClassNotFoundException ex) {
            assertTrue(ex.toString(), false);
        }
    }

    @After
    public void tearDown() {
        node = null;
    }

    @Test
    public void testAttributes() {
        Iterator it = node.getFields();
        assert (it.hasNext() == true && ((FieldNode) it.next()).getName().equals("booleanField"));
        assert (it.hasNext() == true && ((FieldNode) it.next()).getName().equals("byteField"));
        assert (it.hasNext() == true && ((FieldNode) it.next()).getName().equals("charField"));
        assert (it.hasNext() == true && ((FieldNode) it.next()).getName().equals("shortField"));
        assert (it.hasNext() == true && ((FieldNode) it.next()).getName().equals("intField"));
        assert (it.hasNext() == true && ((FieldNode) it.next()).getName().equals("longField"));
        assert (it.hasNext() == true && ((FieldNode) it.next()).getName().equals("floatField"));
        assert (it.hasNext() == true && ((FieldNode) it.next()).getName().equals("doubleField"));
        assert (it.hasNext() == true && ((FieldNode) it.next()).getName().equals("stringField"));
        assert (it.hasNext() == true && ((FieldNode) it.next()).getName().equals("vectorField"));
    }
}