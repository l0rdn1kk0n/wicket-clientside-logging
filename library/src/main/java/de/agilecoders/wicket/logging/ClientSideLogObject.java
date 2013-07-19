package de.agilecoders.wicket.logging;

import com.google.common.annotations.VisibleForTesting;
import org.apache.wicket.util.string.StringValue;

/**
 * A {@link ClientSideLogObject} represents a log message that was created
 * on client side.
 *
 * @author miha
 */
public class ClientSideLogObject {

    private final StringValue lvl;
    private final StringValue message;
    private final StringValue timestamp;
    private final ClientSideErrorLoggingSettings settings;

    /**
     * Construct.
     *
     * @param lvl the log level as {@link StringValue}
     * @param message the message as {@link StringValue}
     * @param timestamp the timestamp as {@link StringValue} in UTC format.
     */
    public ClientSideLogObject(StringValue lvl, StringValue message, StringValue timestamp) {
        this(lvl, message, timestamp, ClientSideErrorLoggingSettings.get());
    }

    /**
     * Construct.
     *
     * @param lvl the log level as {@link StringValue}
     * @param message the message as {@link StringValue}
     * @param timestamp the timestamp as {@link StringValue} in UTC format.
     */
    @VisibleForTesting
    protected ClientSideLogObject(StringValue lvl, StringValue message, StringValue timestamp, ClientSideErrorLoggingSettings settings) {
        this.lvl = lvl;
        this.message = message;
        this.timestamp = timestamp;
        this.settings = settings;
    }

    /**
     * @return log level as string or default log level if not valid
     */
    public String level() {
        return settings.cleaner().clean(lvl.toString(settings.level()));
    }

    /**
     * @return log message or default message if invalid
     */
    public String message() {
        return settings.cleaner().clean(message.toString(DefaultValues.defaultMessage));
    }

    /**
     * @return timestamp as string or default timestamp if invalid
     */
    public String timestamp() {
        return settings.cleaner().clean(timestamp.toString(DefaultValues.defaultTimestamp));
    }

    @Override
    public String toString() {
        return String.format("[%s | %s] %s", timestamp(), level(), message());
    }
}