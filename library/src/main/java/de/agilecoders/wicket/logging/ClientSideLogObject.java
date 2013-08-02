package de.agilecoders.wicket.logging;

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
    private final StringValue stacktrace;
    private final int index;

    /**
     * Construct.
     *
     * @param lvl        the log level as {@link StringValue}
     * @param message    the message as {@link StringValue}
     * @param timestamp  the timestamp as {@link StringValue} in UTC format.
     * @param stacktrace the client side stacktrace
     * @param index      the parameter index that was used on client side to generate this log object
     */
    public ClientSideLogObject(String lvl, String message, String timestamp, String stacktrace, int index) {
        this(StringValue.valueOf(lvl), StringValue.valueOf(message), StringValue.valueOf(timestamp), StringValue.valueOf(stacktrace), index);
    }

    /**
     * Construct.
     *
     * @param lvl        the log level as {@link StringValue}
     * @param message    the message as {@link StringValue}
     * @param timestamp  the timestamp as {@link StringValue} in UTC format.
     * @param stacktrace the client side stacktrace as {@link StringValue}
     * @param index      the param index
     */
    public ClientSideLogObject(StringValue lvl, StringValue message, StringValue timestamp, StringValue stacktrace, int index) {
        this.lvl = lvl;
        this.message = message;
        this.timestamp = timestamp;
        this.stacktrace = stacktrace;
        this.index = index;
    }

    /**
     * @return parameter index
     */
    public int index() {
        return index;
    }

    /**
     * @return log level as string or "error" if not valid
     */
    public String level() {
        return lvl.toString("error");
    }

    /**
     * @return stacktrace or empty string if non is set
     */
    public String stacktrace() {
        return stacktrace.toString("");
    }

    /**
     * @return log message or default message if invalid
     */
    public String message() {
        return message.toString(DefaultValues.defaultMessage);
    }

    /**
     * @return timestamp as string or default timestamp if invalid
     */
    public String timestamp() {
        return timestamp.toString(DefaultValues.defaultTimestamp);
    }

    @Override
    public String toString() {
        return String.format("[%s | %s] %s", timestamp(), level(), message());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ClientSideLogObject logObject = (ClientSideLogObject) o;

        if (lvl != null ? !lvl.equals(logObject.lvl) : logObject.lvl != null) {
            return false;
        }
        if (message != null ? !message.equals(logObject.message) : logObject.message != null) {
            return false;
        }
        if (stacktrace != null ? !stacktrace.equals(logObject.stacktrace) : logObject.stacktrace != null) {
            return false;
        }
        if (timestamp != null ? !timestamp.equals(logObject.timestamp) : logObject.timestamp != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = lvl != null ? lvl.hashCode() : 0;
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (stacktrace != null ? stacktrace.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        return result;
    }

    /**
     * @return TRUE, if all values are set
     */
    public boolean isValid() {
        return !message.isEmpty() && !timestamp.isEmpty() && !lvl.isEmpty();
    }
}