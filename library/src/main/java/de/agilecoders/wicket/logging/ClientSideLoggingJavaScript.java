package de.agilecoders.wicket.logging;

import com.google.common.collect.Lists;
import org.apache.wicket.Application;
import org.apache.wicket.ajax.WicketAjaxJQueryResourceReference;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import java.util.List;

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

    @Override
    public Iterable<? extends HeaderItem> getDependencies() {
        final List<HeaderItem> dependencies = Lists.newArrayList(super.getDependencies());
        dependencies.add(JavaScriptHeaderItem.forReference(Application.get().getJavaScriptLibrarySettings().getJQueryReference()));
        dependencies.add(JavaScriptHeaderItem.forReference(WicketAjaxJQueryResourceReference.get()));

        return dependencies;
    }
}
