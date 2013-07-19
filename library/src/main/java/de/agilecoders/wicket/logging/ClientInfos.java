package de.agilecoders.wicket.logging;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A wrapped {@link HashMap} that provides a simple interface
 * to access special fields. Please notice that you can add only
 * special set of keys to this map (see {@link #validKeys}).
 *
 * @author miha
 */
public class ClientInfos extends HashMap<String, String> {
    /**
     * a list of all valid keys.
     */
    private static final List<String> validKeys = Arrays.asList(
            ParamNames.USER_AGENT, ParamNames.AJAX_BASE_URL, ParamNames.WINDOW_SIZE, ParamNames.SCREEN_SIZE
    );

    /**
     * @return the user agent
     */
    public String userAgent() {
        return get(ParamNames.USER_AGENT);
    }

    /**
     * @return the ajax base url
     */
    public String ajaxBaseUrl() {
        return get(ParamNames.AJAX_BASE_URL);
    }

    /**
     * @return the window size (wxh: 1024x768)
     */
    public String windowSize() {
        return get(ParamNames.WINDOW_SIZE);
    }

    /**
     * @return the screen size (wxh: 1024x768)
     */
    public String screenSize() {
        return get(ParamNames.SCREEN_SIZE);
    }

    @Override
    public String put(String key, String value) {
        if (validKeys.contains(key)) {
            return super.put(key, value);
        }
        return null;
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        throw new UnsupportedOperationException();
    }
}
