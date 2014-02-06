package de.agilecoders.wicket.logging;

import de.agilecoders.wicket.logging.settings.ClientSideLoggingSettings;
import de.agilecoders.wicket.logging.util.ClientInfos;
import de.agilecoders.wicket.logging.util.ILoggingBarrier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * An {@link IClientLogger} is responsible for writing the log messages
 * to the log store. Slf4j is used as default Logger implementation.
 *
 * @author miha
 */
public interface IClientLogger {

    /**
     * writes a set of log messages to the log store
     *
     * @param logObjects  all log messages that needs to be written
     * @param clientInfos the client information according to given logObjects.
     */
    void log(Collection<ClientSideLogObject> logObjects, ClientInfos clientInfos);

    /**
     * Default implementation of {@link IClientLogger} that uses slf4j as logger.
     */
    public static class DefaultClientLogger implements IClientLogger {
        private final Logger logger;
        private final ILoggingBarrier barrier;

        /**
         * Construct.
         *
         * @param id the logger id
         */
        public DefaultClientLogger(final String id) {
            this.logger = newLogger(id);
            this.barrier = newBarrier();
        }

        /**
         * creates a new slf4j logger
         *
         * @param loggerName the name of the logger
         * @return new logger
         */
        protected Logger newLogger(String loggerName) {
            return LoggerFactory.getLogger(loggerName);
        }

        /**
         * @return new barrier instance
         */
        protected ILoggingBarrier newBarrier() {
            return new ILoggingBarrier.AllowAllBarrier();
        }

        @Override
        public void log(Collection<ClientSideLogObject> logObjects, ClientInfos clientInfos) {
            if(!barrier.isAllowed(logObjects)) {
                return;
            }

            final ILogCleaner cleaner = ClientSideLogging.settings().cleaner();

            for (ClientSideLogObject logObject : logObjects) {
                if (barrier.isAllowed(logObject)) {
                    switch (logObject.level()) {
                        case "error":
                            if (logger.isErrorEnabled()) {
                                logger.error(newLogMessage("error", logObject, clientInfos, cleaner));
                            }
                            break;
                        case "warn":
                            if (logger.isWarnEnabled()) {
                                logger.warn(newLogMessage("warn", logObject, clientInfos, cleaner));
                            }
                            break;
                        case "info":
                            if (logger.isInfoEnabled()) {
                                logger.info(newLogMessage("info", logObject, clientInfos, cleaner));
                            }
                            break;
                        case "debug":
                            if (logger.isDebugEnabled()) {
                                logger.debug(newLogMessage("debug", logObject, clientInfos, cleaner));
                            }
                            break;
                        case "trace":
                            if (logger.isTraceEnabled()) {
                                logger.trace(newLogMessage("trace", logObject, clientInfos, cleaner));
                            }
                            break;
                        default:
                    }
                }
            }
        }

        /**
         * creates a new log line
         *
         * @param logLevel    the current log level
         * @param logObject   the log object that contains message and level
         * @param clientInfos the client information that contains user-agent and ajax base url
         * @param cleaner     the log cleaner implementation
         * @return new log message line.
         */
        protected String newLogMessage(String logLevel, ClientSideLogObject logObject, ClientInfos clientInfos, ILogCleaner cleaner) {
            return String.format("[%s] %s [%s]; Stack: %s", cleaner.toCleanPath(clientInfos.ajaxBaseUrl()), logObject, cleaner.clean(clientInfos.userAgent()), logObject.stacktrace());
        }
    }

}
