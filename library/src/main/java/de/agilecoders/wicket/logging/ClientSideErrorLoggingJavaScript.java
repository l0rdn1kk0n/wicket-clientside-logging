package de.agilecoders.wicket.logging;

import com.google.common.collect.Lists;
import org.apache.wicket.Application;
import org.apache.wicket.ajax.WicketAjaxJQueryResourceReference;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import java.util.List;

/**
 * The {@link JavaScriptResourceReference} that represents the "clientside-error-logging.js".
 *
 * @author miha
 */
public class ClientSideErrorLoggingJavaScript extends JavaScriptResourceReference {
    private static final long serialVersionUID = 1L;

    /**
     * Singleton instance of this reference
     */
    private static final ClientSideErrorLoggingJavaScript INSTANCE = new ClientSideErrorLoggingJavaScript();


    /**
     * @return the single instance of the resource reference
     */
    public static ClientSideErrorLoggingJavaScript instance() {
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
    private ClientSideErrorLoggingJavaScript() {
        super(ClientSideErrorLoggingJavaScript.class, "js/clientside-error-logging.js");
    }

    @Override
    public Iterable<? extends HeaderItem> getDependencies() {
        final List<HeaderItem> dependencies = Lists.newArrayList(super.getDependencies());
        dependencies.add(JavaScriptHeaderItem.forReference(Application.get().getJavaScriptLibrarySettings().getJQueryReference()));
        dependencies.add(JavaScriptHeaderItem.forReference(WicketAjaxJQueryResourceReference.get()));

        return dependencies;
    }
}
