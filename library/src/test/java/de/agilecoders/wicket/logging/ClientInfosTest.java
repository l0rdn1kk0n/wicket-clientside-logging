package de.agilecoders.wicket.logging;

import de.agilecoders.wicket.logging.util.ClientInfos;
import de.agilecoders.wicket.logging.util.ParamNames;
import org.junit.Test;

import java.util.HashMap;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests the {@link de.agilecoders.wicket.logging.util.ClientInfos} class
 *
 * @author miha
 */
public class ClientInfosTest {

    @Test
    public void putValuesForValidKeysIsAllowed() {
        final ClientInfos clientInfos = new ClientInfos();
        clientInfos.put(ParamNames.AJAX_BASE_URL, "baseUrl");
        clientInfos.put(ParamNames.WINDOW_SIZE, "windowSize");
        clientInfos.put(ParamNames.SCREEN_SIZE, "screenSize");
        clientInfos.put(ParamNames.USER_AGENT, "userAgent");

        assertThat(clientInfos.ajaxBaseUrl(), is(equalTo("baseUrl")));
        assertThat(clientInfos.windowSize(), is(equalTo("windowSize")));
        assertThat(clientInfos.screenSize(), is(equalTo("screenSize")));
        assertThat(clientInfos.userAgent(), is(equalTo("userAgent")));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void putAllIsntSupported() {
        new ClientInfos().putAll(new HashMap<String, String>());
    }

    @Test
    public void putValuesForInvalidKeysIsForbidden() {
        final ClientInfos clientInfos = new ClientInfos();
        String setValue = clientInfos.put("invalidKey", "value");

        assertThat(setValue, is(nullValue()));
        assertThat(clientInfos.get("invalidKey"), is(nullValue()));
    }

}
