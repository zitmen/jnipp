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
public class MethodNodeTest {

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
        Iterator it = node.getMethods();
        MethodNode current = (MethodNode) it.next();
        assertEquals("test1", current.getName());
        assertEquals("test1", current.getJNIName());
        assertEquals("()Ljava/util/Vector;", current.getJNISignature());
        current = (MethodNode) it.next();
        assertEquals("test2", current.getName());
        assertEquals("test2", current.getJNIName());
        assertEquals("(Ljava/util/Vector;)V", current.getJNISignature());
        current = (MethodNode) it.next();
        assertEquals("test3", current.getName());
        assertEquals("test3", current.getJNIName());
        assertEquals("(Ljava/util/Vector;Ljava/net/URL;[[I)V", current.getJNISignature());
    }
}