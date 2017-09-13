package test.jclp;

import jclp.CollectionUtils;
import jclp.value.Types;
import jclp.value.Values;
import org.junit.Test;

import java.time.LocalDate;
import java.util.function.Supplier;

import static jclp.value.Types.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TypesTest {
    @Test
    public void testGetTypes() {
        assertEquals(getTypes(), CollectionUtils.setOf("str", "date", "int", "real", "datetime", "time", "locale"));
    }

    @Test
    public void testGetClass() {
        assertEquals(Types.getClass("str"), CharSequence.class);
        assertEquals(Types.getClass("date"), LocalDate.class);
        assertNull(Types.getClass(null));
        assertNull(Types.getClass(""));
        assertNull(Types.getClass("foo"));
    }

    @Test
    public void testGetType() {
        assertEquals(getType("string"), "str");
        assertEquals(getType(11), "int");
        assertEquals(getType(LocalDate.now()), "date");
        assertEquals(getType(Byte.class), "int");
        assertEquals(getType(Integer.class), "int");
    }

    @Test
    public void testGetDefault() {
        assertNull(getDefault("int"));
        setDefault("str", "");
        assertEquals(getDefault("str"), "");
        setDefault("date", LocalDate.now());
        assertEquals(getDefault("date"), LocalDate.now());
        setDefault("str", (Supplier<String>) () -> "hello");
        assertEquals(getDefault("str"), "hello");
        setDefault("int", Values.wrap(123));
        assertEquals(getDefault("int"), 123);
    }

    @Test
    public void testGetName() {
        assertNull(getName(""));
        assertNull(getName(null));
        assertNull(getName("int"));
        assertEquals(getName("str"), "String");
        assertEquals(getName("date"), "Date");
    }

    @Test
    public void testSetAlias() {
        setAlias("str", "text");
        setAlias("int", "byte", "long", "short");
        assertEquals(Types.getClass("text"), CharSequence.class);
    }
}
