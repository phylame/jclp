package jclp;

import jclp.value.Lazy;
import jclp.value.Value;
import lombok.NonNull;
import lombok.val;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

/**
 * Utilities for date.
 */
public final class DateUtils {
    private DateUtils() {
    }

    public static final String ISO_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String ANSIC_FORMAT = "EEE MMM d HH:mm:ss z yyyy";

    public static final String RFC1123_FORMAT = "EEE, dd MMM yyyy HH:mm:ss z";

    public static final String RFC1036_FORMAT = "EEEEEE, dd-MMM-yy HH:mm:ss z";

    public static final String LOOSE_ISO_TIME_FORMAT = "H:m:s";

    public static final String LOOSE_ISO_DATE_FORMAT = "yyyy-M-d";

    public static final String LOOSE_ISO_DATE_TIME_FORMAT = "yyyy-M-d H:m:s";

    public static final Value<DateTimeFormatter> LOOSE_ISO_DATE = new Lazy<>(() -> DateTimeFormatter.ofPattern(LOOSE_ISO_DATE_FORMAT));

    public static final Value<DateTimeFormatter> LOOSE_ISO_TIME = new Lazy<>(() -> DateTimeFormatter.ofPattern(LOOSE_ISO_TIME_FORMAT));

    public static final Value<DateTimeFormatter> LOOSE_ISO_DATE_TIME = new Lazy<>(() -> DateTimeFormatter.ofPattern(LOOSE_ISO_DATE_TIME_FORMAT));

    public static String toISO(@NonNull Date date) {
        return new SimpleDateFormat(ISO_FORMAT).format(date);
    }

    public static Date forISO(@NonNull String str) throws ParseException {
        return new SimpleDateFormat(ISO_FORMAT).parse(str);
    }

    public static String toANSIC(@NonNull Date date) {
        return new SimpleDateFormat(ANSIC_FORMAT, Locale.ENGLISH).format(date);
    }

    public static Date forANSIC(@NonNull String str) throws ParseException {
        return new SimpleDateFormat(ANSIC_FORMAT, Locale.ENGLISH).parse(str);
    }

    public static String toRFC1123(@NonNull Date date) {
        return new SimpleDateFormat(RFC1123_FORMAT, Locale.ENGLISH).format(date);
    }

    public static String toRFC822(@NonNull Date date) {
        return toRFC1123(date);
    }

    public static Date forRFC1123(@NonNull String str) throws ParseException {
        return new SimpleDateFormat(RFC1123_FORMAT, Locale.ENGLISH).parse(str);
    }

    public static Date forRFC822(@NonNull String str) throws ParseException {
        return forRFC1123(str);
    }

    public static String toRFC1036(@NonNull Date date) {
        return new SimpleDateFormat(RFC1036_FORMAT, Locale.ENGLISH).format(date);
    }

    public static String toRFC850(@NonNull Date date) {
        return toRFC1036(date);
    }

    public static Date forRFC1036(@NonNull String str) throws ParseException {
        return new SimpleDateFormat(RFC1036_FORMAT, Locale.ENGLISH).parse(str);
    }

    public static Date forRFC850(@NonNull String str) throws ParseException {
        return forRFC1036(str);
    }

    public static String format(@NonNull Date date, @NonNull String format) {
        return new SimpleDateFormat(format).format(date);
    }

    public static Date parse(@NonNull String str, @NonNull String format) throws ParseException {
        return new SimpleDateFormat(format).parse(str);
    }

    public static Date parse(String str, @NonNull String format, Date fallback) {
        if (StringUtils.isEmpty(str)) {
            return fallback;
        }
        try {
            return new SimpleDateFormat(format).parse(str);
        } catch (ParseException ignored) {
        }
        return fallback;
    }

    public static Date parse(@NonNull String str, String... formats) {
        for (val format : formats) {
            try {
                return parse(str, format);
            } catch (ParseException ignored) {
            }
        }
        throw new IllegalArgumentException("Invalid date string: " + str);
    }
}
