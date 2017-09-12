package jclp.text;

public interface Converter<T> {
    String render(T obj);

    T parse(String str);
}
