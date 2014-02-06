package de.agilecoders.wicket.logging;

import de.agilecoders.wicket.logging.settings.ClientSideLoggingSettings;
import de.agilecoders.wicket.logging.settings.IClientSideLoggingSettings;
import de.agilecoders.wicket.webjars.util.WicketWebjars;
import org.apache.wicket.Application;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.lang.Args;

/**
 * base initialization class
 *
 * @author miha
 */
public final class ClientSideLogging {

    /**
     * The {@link org.apache.wicket.MetaDataKey} used to retrieve the {@link ClientSideLoggingSettings} from the Wicket {@link Appendable}.
     */
    private static final MetaDataKey<IClientSideLoggingSettings> METADATA_KEY = new MetaDataKey<IClientSideLoggingSettings>() {
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
    public static void install(final WebApplication application, final IClientSideLoggingSettings settings) {
        Args.notNull(application, "application");

        if (settings(application) == null) {
            application.setMetaData(METADATA_KEY, Args.notNull(settings, "settings"));

            WicketWebjars.install(application);
        }
    }

    /**
     * returns the {@link ClientSideLoggingSettings} which are assigned to given application
     *
     * @param app The current application
     * @return assigned {@link ClientSideLoggingSettings}
     */
    public static IClientSideLoggingSettings settings(final Application app) {
        return app.getMetaData(METADATA_KEY);
    }

    /**
     * returns the {@link ClientSideLoggingSettings} which are assigned to current application
     *
     * @return assigned {@link ClientSideLoggingSettings}
     */
    public static IClientSideLoggingSettings settings() {
        if (Application.exists()) {
            return settings(Application.get());
        }

        throw new IllegalStateException("there is no active application assigned to this thread.");
    }

}
