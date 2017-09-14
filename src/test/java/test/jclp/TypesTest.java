package test.jclp;

import jclp.CollectionUtils;
import jclp.i18n.MapTranslator;
import jclp.value.Types;
import jclp.value.Values;
import lombok.val;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Supplier;

import static jclp.M.M;
import static jclp.value.Types.*;
import static org.junit.Assert.*;

public class TypesTest {
    @Before
    public void setup() {
        val map = new HashMap<String, String>();
        map.put("type.xyz", "Xyz");
        M.attach(CollectionUtils.setOf(new MapTranslator(map)));
        mapClass("xyz", Date.class);
        setDefault("xyz", Values.wrap(new Date()));
    }

    @Test
    public void testGetTypes() {
        assertEquals(getTypes(), CollectionUtils.setOf("str", "xyz", "jdate", "date", "bool", "int", "real", "datetime", "time", "locale"));
    }

    @Test
    public void testGetClass() {
        assertEquals(Types.getClass("str"), CharSequence.class);
        assertEquals(Types.getClass("date"), LocalDate.class);
        assertNull(Types.getClass(null));
        assertNull(Types.getClass(""));
        assertNull(Types.getClass("foo"));
        assertEquals(Types.getClass("xyz"), Date.class);
    }

    @Test
    public void testGetType() {
        assertEquals(getType("string"), "str");
        assertEquals(getType(11), "int");
        assertEquals(getType(LocalDate.now()), "date");
        assertEquals(getType(Byte.class), "int");
        assertEquals(getType(Integer.class), "int");
        assertEquals(getType(Date.class), "xyz");
        assertNull(getType(System.class));
        mapClass("is", InputStream.class);
        assertEquals(getType(BufferedInputStream.class), "is");
        mapClass("is", FilterInputStream.class);
        assertEquals(getType(BufferedInputStream.class), "is");
    }

    @Test
    public void testGetDefault() {
        assertEquals(getDefault("int"), 0);
        setDefault("str", "");
        assertEquals(getDefault("str"), "");
        setDefault("date", LocalDate.now());
        assertEquals(getDefault("date"), LocalDate.now());
        setDefault("str", (Supplier<String>) () -> "hello");
        assertEquals(getDefault("str"), "hello");
        setDefault("int", Values.wrap(123));
        assertEquals(getDefault("int"), 123);
        assertNotNull(getDefault("xyz"));
    }

    @Test
    public void testGetName() {
        assertNull(getName(""));
        assertNull(getName(null));
        assertNull(getName("intx"));
        assertEquals(getName("xyz"), "Xyz");
    }

    @Test
    public void testSetAlias() {
        setAlias("str", "text");
        setAlias("int", "byte", "long", "short");
        assertEquals(Types.getClass("text"), CharSequence.class);
    }
}
