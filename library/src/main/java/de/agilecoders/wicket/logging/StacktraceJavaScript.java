package de.agilecoders.wicket.logging;

import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

/**
 * The {@link org.apache.wicket.request.resource.JavaScriptResourceReference} that represents the "stacktrace.js".
 *
 * @author miha
 */
public class StacktraceJavaScript extends JavaScriptResourceReference {
    private static final long serialVersionUID = 1L;

    /**
     * Singleton instance of this reference
     */
    private static final StacktraceJavaScript INSTANCE = new StacktraceJavaScript();


    /**
     * @return the single instance of the resource reference
     */
    public static StacktraceJavaScript instance() {
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
    private StacktraceJavaScript() {
        super(StacktraceJavaScript.class, "js/stacktrace-0.4.js");
    }

}
