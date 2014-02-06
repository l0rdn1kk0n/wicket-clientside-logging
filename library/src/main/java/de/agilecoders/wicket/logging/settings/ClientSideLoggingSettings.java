package de.agilecoders.wicket.logging.settings;

import de.agilecoders.wicket.logging.ClientSideLoggingJavaScript;
import de.agilecoders.wicket.logging.IClientLogger;
import de.agilecoders.wicket.logging.ILogCleaner;
import de.agilecoders.wicket.logging.IParamValueExtractor;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.util.lang.Args;

/**
 * A set of configuration options for the clientside logging library
 *
 * @author miha
 */
public class ClientSideLoggingSettings implements IClientSideLoggingSettings {

    private String id = "client-side-logging";
    private String level = "error";
    private boolean debug = false;
    private boolean logStacktrace = false;
    private ILogCleaner cleaner = new ILogCleaner.DefaultLogCleaner();
    private IClientLogger logger = new IClientLogger.DefaultClientLogger(id);
    private ResourceReference reference = ClientSideLoggingJavaScript.instance();
    private IParamValueExtractor paramValueExtractor = new IParamValueExtractor.DefaultParamValueExtractor();

    /**
     * sets the client side error level
     *
     * @param level the error level to use on client side
     * @return this instance for chaining
     */
    public ClientSideLoggingSettings level(final String level) {
        this.level = level;
        return this;
    }

    /**
     * sets the log message cleaner
     *
     * @param cleaner the log message cleaner
     * @return this instance for chaining
     */
    public ClientSideLoggingSettings cleaner(final ILogCleaner cleaner) {
        this.cleaner = Args.notNull(cleaner, "cleaner");
        return this;
    }

    /**
     * sets the logger implementation to use
     *
     * @param logger the logger implementation to use
     * @return this instance for chaining
     */
    public ClientSideLoggingSettings logger(final IClientLogger logger) {
        this.logger = Args.notNull(logger, "logger");
        return this;
    }

    /**
     * sets the request parameter parser
     *
     * @param paramValueExtractor the request parameter parser
     * @return this instance for chaining
     */
    public ClientSideLoggingSettings paramValueExtractor(final IParamValueExtractor paramValueExtractor) {
        this.paramValueExtractor = Args.notNull(paramValueExtractor, "paramValueExtractor");
        return this;
    }

    /**
     * sets the javascript reference to use to render clientside logging js
     *
     * @param reference the js reference
     * @return this instance for chaining
     */
    public ClientSideLoggingSettings reference(final ResourceReference reference) {
        this.reference = Args.notNull(reference, "reference");
        return this;
    }

    /**
     * sets the debug mode on client side
     *
     * @param debug whether to activate debugging or not
     * @return this instance for chaining
     */
    public ClientSideLoggingSettings debug(final boolean debug) {
        this.debug = debug;
        return this;
    }

    /**
     * whether to log stacktrace or not
     *
     * @since 0.1.3
     * @param value whether to log stacktrace
     * @return this instance for chaining
     */
    public ClientSideLoggingSettings logStacktrace(final boolean value) {
        this.logStacktrace = value;
        return this;
    }

    /**
     * @return client side log level
     */
    public String level() { return level; }

    /**
     * @return log message cleaner
     */
    public ILogCleaner cleaner() { return cleaner; }

    /**
     * @return server side logger
     */
    public IClientLogger logger() { return logger; }

    /**
     * @return use debug mode on client side
     */
    public boolean debug() { return debug; }

    /**
     * @since 0.1.3
     * @return TRUE, if stacktrace should be logged
     */
    public boolean logStacktrace() { return logStacktrace; }

    /**
     * @return request parameter parser
     */
    public IParamValueExtractor paramValueExtractor() { return paramValueExtractor; }

    /**
     * @return the library id (is used as logger name)
     */
    public String id() { return id; }

    /**
     * @return the javascript resource reference as header item.
     */
    public JavaScriptHeaderItem javaScriptHeaderItem() { return JavaScriptHeaderItem.forReference(reference); }
}
