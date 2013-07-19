package de.agilecoders.wicket.logging;

import org.apache.wicket.util.time.Duration;

/**
 * A set of default values that are used on client side to handle
 * logging.
 *
 * @author miha
 */
public final class DefaultValues {

    public static final boolean replaceWicketLog = false;
    public static final boolean replaceWindowOnError = false;
    public static final boolean wrapWindowOnError = true;
    public static final boolean wrapWicketLog = true;
    public static final boolean flushMessagesOnUnload = true;

    public static final String logLevel = "error";
    public static final String method = "POST";
    public static final int maxQueueSize = 10;
    public static final String loggerName = "Log";
    public static final long collectionTimer = Duration.seconds(5).getMilliseconds();
    public static final CollectionType collectionType = CollectionType.Single;

    public static final String defaultMessage = "NULL_MSG";
    public static final String defaultTimestamp = "NULL_TIMESTAMP";
    public static final String defaultClientInfoValue = "NULL";

    public static final String paramSplitter = "_";
    public static final char paramSplitterChar = paramSplitter.charAt(0);
}