package de.agilecoders.wicket.logging;

import com.google.common.base.Charsets;
import org.apache.wicket.request.Url;
import org.apache.wicket.util.string.StringValue;

import static com.google.common.base.Strings.nullToEmpty;

/**
 * An {@link ILogCleaner} is responsible for cleaning log messages.
 *
 * @author miha
 */
public interface ILogCleaner {

    /**
     * cleans a given string
     *
     * @param value the value to clean
     * @return cleaned value as string
     */
    String clean(final String value);

    /**
     * cleans a given path string
     *
     * @param value the value to clean
     * @return cleaned value as string
     */
    String toCleanPath(final String value);

    /**
     * cleans a given StringValue
     *
     * @param value the value to clean
     * @return cleaned value as string
     */
    String clean(final StringValue value);

    /**
     * cleans a given path as StringValue
     *
     * @param value the value to clean
     * @return cleaned value as string
     */
    String toCleanPath(final StringValue value);

    /**
     * Default implementation of {@link ILogCleaner} that removes
     * line breaks and tabs and it transforms absolute paths to relative.
     */
    public static class DefaultLogCleaner implements ILogCleaner {

        @Override
        public String clean(String value) {
            return nullToEmpty(value).replaceAll("[\t\n\r]", "").trim();
        }

        @Override
        public String clean(StringValue value) {
            return clean(value.toString(""));
        }

        @Override
        public String toCleanPath(StringValue value) {
            return toCleanPath(value.toString("/"));
        }

        @Override
        public String toCleanPath(String value) {
            return Url.parse(clean(value), Charsets.UTF_8).toString(Url.StringMode.LOCAL);
        }
    }

    /**
     * A special log cleaner that doesn't clean anything and
     * only returns given values
     */
    public static class NoOpLogCleaner implements ILogCleaner {

        @Override
        public String clean(String value) {
            return value;
        }

        @Override
        public String toCleanPath(String value) {
            return value;
        }

        @Override
        public String clean(StringValue value) {
            return value.toString();
        }

        @Override
        public String toCleanPath(StringValue value) {
            return value.toString();
        }
    }
}
