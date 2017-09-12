package jclp.text;

import jclp.DateUtils;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.Locale;

@RequiredArgsConstructor
public class DefaultConverter<T> implements Converter<T> {
    private final Class<T> type;

    @Override
    public String render(T obj) {
        if (obj instanceof Date) {
            return DateUtils.toISO((Date) obj);
        } else if (obj instanceof Class) {
            return ((Class<?>) obj).getName();
        }
        return obj.toString();
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public T parse(String str) {
        if (type == String.class) {
            return (T) str;
        } else if (type == Byte.class) {
            return (T) Byte.decode(str);
        } else if (type == Short.class) {
            return (T) Short.decode(str);
        } else if (type == Integer.class) {
            return (T) Integer.decode(str);
        } else if (type == Long.class) {
            return (T) Long.decode(str);
        } else if (type == Float.class) {
            return (T) Float.valueOf(str);
        } else if (type == Double.class) {
            return (T) Double.valueOf(str);
        } else if (type == Boolean.class) {
            return (T) Boolean.valueOf(str);
        } else if (type == Date.class) {
            return (T) DateUtils.parse(str, "yyyy-M-d H:m:s", "yyyy-M-d", "H:m:s");
        } else if (type == Locale.class) {
            return (T) Locale.forLanguageTag(str);
        } else if (type == LocalDate.class) {
            return (T) LocalDate.parse(str, DateUtils.LOOSE_ISO_DATE.get());
        } else if (type == LocalTime.class) {
            return (T) LocalTime.parse(str, DateUtils.LOOSE_ISO_TIME.get());
        } else if (type == LocalDateTime.class) {
            return (T) LocalDateTime.parse(str, DateUtils.LOOSE_ISO_DATE_TIME.get());
        } else if (Enum.class.isAssignableFrom(type)) {
            return (T) Enum.valueOf((Class<Enum>) type, str);
        } else if (Class.class.isAssignableFrom(type)) {
            try {
                return (T) Class.forName(str);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
        throw new IllegalStateException("Unreachable code");
    }
}
