package de.agilecoders.wicket.logging;

import org.apache.wicket.Application;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.util.lang.Args;

/**
 * A set of configuration options for the clientside logging library
 *
 * @author miha
 */
public class ClientSideLoggingSettings {
    /**
     * The {@link org.apache.wicket.MetaDataKey} used to retrieve the {@link ClientSideLoggingSettings} from the Wicket {@link Appendable}.
     */
    private static final MetaDataKey<ClientSideLoggingSettings> ClientSideErrorLoggingSettings_METADATA_KEY = new MetaDataKey<ClientSideLoggingSettings>() {
    };

    /**
     * installs default settings on given application
     *
     * @param application the application to add the settings to
     */
    public static void install(final WebApplication application) {
        install(application, new ClientSideLoggingSettings());
    }

    /**
     * installs given settings on given application
     *
     * @param application the application to add the settings to
     * @param settings    the settings to add
     */
    public static void install(final WebApplication application, final ClientSideLoggingSettings settings) {
        Args.notNull(application, "application").setMetaData(ClientSideErrorLoggingSettings_METADATA_KEY,
                                                             Args.notNull(settings, "settings"));
    }

    /**
     * returns the {@link ClientSideLoggingSettings} which are assigned to given application
     *
     * @param app The current application
     * @return assigned {@link ClientSideLoggingSettings}
     */
    public static ClientSideLoggingSettings get(final Application app) {
        return app.getMetaData(ClientSideErrorLoggingSettings_METADATA_KEY);
    }

    /**
     * returns the {@link ClientSideLoggingSettings} which are assigned to current application
     *
     * @return assigned {@link ClientSideLoggingSettings}
     */
    public static ClientSideLoggingSettings get() {
        if (Application.exists()) {
            return get(Application.get());
        }

        throw new IllegalStateException("there is no active application assigned to this thread.");
    }

    private String id = "client-side-logging";
    private String level = "error";
    private boolean debug = false;
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
