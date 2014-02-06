package de.agilecoders.wicket.logging.settings;

import de.agilecoders.wicket.logging.IClientLogger;
import de.agilecoders.wicket.logging.ILogCleaner;
import de.agilecoders.wicket.logging.IParamValueExtractor;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;

/**
 * client side logging settings.
 *
 * @author miha
 */
public interface IClientSideLoggingSettings {

    /**
     * @return client side log level
     */
    String level();

    /**
     * @return log message cleaner
     */
    ILogCleaner cleaner();

    /**
     * @return server side logger
     */
    IClientLogger logger();

    /**
     * @return use debug mode on client side
     */
    boolean debug();

    /**
     * @since 0.1.3
     * @return TRUE, if stacktrace should be logged
     */
    boolean logStacktrace();

    /**
     * @return request parameter parser
     */
    IParamValueExtractor paramValueExtractor();

    /**
     * @return the library id (is used as logger name)
     */
    String id();

    /**
     * @return the javascript resource reference as header item.
     */
    JavaScriptHeaderItem javaScriptHeaderItem();
}
