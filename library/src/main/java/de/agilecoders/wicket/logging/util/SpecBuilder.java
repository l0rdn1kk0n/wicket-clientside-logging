package de.agilecoders.wicket.logging.util;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.time.Duration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A specification of a {@link de.agilecoders.wicket.logging.ClientSideLoggingBehavior.Builder} configuration.
 * <p/>
 * <p>{@code SpecBuilder} supports parsing a string configuration, which
 * is useful for command-line configuration of a {@code ClientSideLoggingBehavior.Builder}.
 * <p/>
 * <p>The string syntax is a series of comma-separated keys or key-value pairs,
 * each corresponding to a {@code ClientSideLoggingBehavior.Builder} method.
 *
 * @author miha
 * @since 0.1.2
 */
public class SpecBuilder {
    private static final ImmutableMap<String, ValueParser<?>> VALUE_PARSERS = ImmutableMap.<String, ValueParser<?>>builder()
            .put("replaceWicketLog", new BooleanParser(DefaultValues.replaceWicketLog))
            .put("replaceWindowOnError", new BooleanParser(DefaultValues.replaceWindowOnError))
            .put("wrapWicketLog", new BooleanParser(DefaultValues.wrapWicketLog))
            .put("wrapWindowOnError", new BooleanParser(DefaultValues.wrapWindowOnError))
            .put("logAdditionalErrors", new BooleanParser(DefaultValues.logAdditionalErrors))
            .put("flushMessagesOnUnload", new BooleanParser(DefaultValues.flushMessagesOnUnload))
            .put("collectClientInfos", new BooleanParser(DefaultValues.collectClientInfos))
            .put("collectionTimer", new DurationParser(DefaultValues.collectionTimer))
            .put("maxQueueSize", new LongParser(DefaultValues.maxQueueSize))
            .put("maxEntriesPerPage", new LongParser(DefaultValues.maxEntriesPerPage))
            .put("loggerName", new StringParser(DefaultValues.loggerName))
            .put("collectionType", new CollectionTypeParser(DefaultValues.collectionType))
            .build();

    /**
     * base interface for all value parser
     *
     * @param <T> the return type of parse method
     */
    static interface ValueParser<T> {
        T parse(String value);
    }

    /**
     * parse a give string specification
     *
     * @param spec the specification to parse
     * @return a SpecBuilder instance
     */
    public static SpecBuilder parse(final String spec) {
        return new SpecBuilder(spec);
    }

    private static final Splitter propertySplitter = Splitter.on(',').omitEmptyStrings();
    private static final Splitter keyValueSplitter = Splitter.on('=').omitEmptyStrings().limit(2);

    private final String spec;
    private final Map<String, Object> data;

    /**
     * Construct.
     *
     * @param spec the specification to parse
     */
    public SpecBuilder(String spec) {
        this.spec = spec;
        this.data = toParsedData(parseKeyValues(spec));
    }

    private Map<String, Object> toParsedData(Map<String, String> keyValues) {
        final Map<String, Object> data = new HashMap<>();

        for (Map.Entry<String, String> keyValue : keyValues.entrySet()) {
            ValueParser parser = VALUE_PARSERS.get(keyValue.getKey());
            Object val = parser.parse(keyValue.getValue());

            if (val != null) {
                data.put(keyValue.getKey(), val);
            }
        }
        return data;
    }

    private Map<String, String> parseKeyValues(String spec) {
        Map<String, String> map = new HashMap<>();
        for (String prop : propertySplitter.split(spec)) {
            if (prop != null && prop.contains("=")) {
                final List<String> keyValue = Lists.newArrayList(keyValueSplitter.split(prop));
                map.put(keyValue.get(0), keyValue.get(1));
            }
        }

        return map;
    }

    /**
     * @return specification that is used to create data
     */
    public String specification() {
        return spec;
    }

    /**
     * @return the parsed data
     */
    public Map<String, Object> data() {
        return new HashMap<>(data);
    }

    static class BooleanParser implements ValueParser<Boolean> {
        private final boolean defaultValue;

        BooleanParser(boolean defaultValue) {
            this.defaultValue = defaultValue;
        }

        @Override
        public Boolean parse(String value) {
            boolean val = Strings.isTrue(value);

            return val == defaultValue ? null : val;
        }
    }

    static class LongParser implements ValueParser<Long> {
        private final long defaultValue;

        LongParser(long defaultValue) {
            this.defaultValue = defaultValue;
        }

        @Override
        public Long parse(String value) {
            Long val = Long.valueOf(value);

            return defaultValue == val ? null : val;
        }
    }

    static class DurationParser implements ValueParser<Long> {
        private final Duration defaultValue;

        DurationParser(long defaultValue) {
            this.defaultValue = Duration.valueOf(defaultValue);
        }

        @Override
        public Long parse(String value) {
            Duration d = Duration.valueOf(value);

            return d == null || defaultValue.equals(d) ? null : d.getMilliseconds();
        }
    }

    static class CollectionTypeParser implements ValueParser<String> {
        private final CollectionType defaultValue;

        CollectionTypeParser(CollectionType defaultValue) {
            this.defaultValue = defaultValue;
        }

        @Override
        public String parse(String value) {
            CollectionType type = CollectionType.valueOf(value);

            return type == null || defaultValue.equals(type) ? null : type.asString();
        }
    }

    static class StringParser implements ValueParser<String> {
        private final String defaultValue;

        StringParser(String defaultValue) {
            this.defaultValue = defaultValue;
        }

        @Override
        public String parse(String value) {
            return defaultValue.equals(value) ? null : value;
        }
    }
}
