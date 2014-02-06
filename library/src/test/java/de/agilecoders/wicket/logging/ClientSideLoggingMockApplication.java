package de.agilecoders.wicket.logging;

import de.agilecoders.wicket.logging.settings.ClientSideLoggingSettings;
import org.apache.wicket.mock.MockApplication;

/**
 * special mock application that uses default settings
 *
 * @author miha
 */
public class ClientSideLoggingMockApplication extends MockApplication {

    @Override
    protected void init() {
        super.init();

        ClientSideLoggingSettings.install(this);
    }
}
