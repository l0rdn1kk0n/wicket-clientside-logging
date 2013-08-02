package de.agilecoders.wicket.logging;

import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

/**
 * The {@link JavaScriptResourceReference} that represents the "clientside-logging.js".
 *
 * @author miha
 */
public class ClientSideLoggingJavaScript extends JavaScriptResourceReference {
    private static final long serialVersionUID = 1L;

    /**
     * Singleton instance of this reference
     */
    private static final ClientSideLoggingJavaScript INSTANCE = new ClientSideLoggingJavaScript();


    /**
     * @return the single instance of the resource reference
     */
    public static ClientSideLoggingJavaScript instance() {
        return INSTANCE;
    }

    /**
     * @return a new {@link org.apache.wicket.markup.head.JavaScriptHeaderItem} instance that represents this resource reference.
     */
    public static JavaScriptHeaderItem asHeaderItem() {
        return JavaScriptHeaderItem.forReference(instance());
    }

    /**
     * Private constructor.
     */
    private ClientSideLoggingJavaScript() {
        super(ClientSideLoggingJavaScript.class, "js/clientside-logging.js");
    }

}
