package de.agilecoders.wicket.logging;

import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.util.time.Duration;

import java.util.HashMap;
import java.util.Map;

/**
 * This behavior renders the necessary js file and initializer script to the
 * markup and handles incoming log requests.
 *
 * @author miha
 */
public class ClientSideErrorLoggingBehavior extends Behavior {

    private final Map<String, Object> data;

    /**
     * @return current active settings
     */
    private static ClientSideErrorLoggingSettings settings() {
        return ClientSideErrorLoggingSettings.get();
    }

    /**
     * @return new builder object.
     */
    public static Builder newBuilder() {
        return new Builder();
    }

    /**
     * The builder to configure this behavior
     */
    public static final class Builder {
        private Map<String, Object> data = new HashMap<>();

        public Builder replaceWicketLog(final boolean value) {
            data.put("replaceWicketLog", value);

            if (DefaultValues.replaceWicketLog == value) {
                data.remove("replaceWicketLog");
            }

            return this;
        }

        public Builder replaceWindowOnError(final boolean value) {
            data.put("replaceWindowOnError", value);

            if (DefaultValues.replaceWindowOnError == value) {
                data.remove("replaceWindowOnError");
            }

            return this;
        }

        public Builder wrapWicketLog(final boolean value) {
            data.put("wrapWicketLog", value);

            if (DefaultValues.wrapWicketLog == value) {
                data.remove("wrapWicketLog");
            }

            return this;
        }

        public Builder wrapWindowOnError(final boolean value) {
            data.put("wrapWindowOnError", value);

            if (DefaultValues.wrapWindowOnError == value) {
                data.remove("wrapWindowOnError");
            }

            return this;
        }

        public Builder flushMessagesOnUnload(final boolean value) {
            data.put("flushMessagesOnUnload", value);

            if (DefaultValues.flushMessagesOnUnload == value) {
                data.remove("flushMessagesOnUnload");
            }

            return this;
        }

        public Builder collectionTimer(final Duration value) {
            data.put("collectionTimer", value.getMilliseconds());

            if (DefaultValues.collectionTimer == value.getMilliseconds()) {
                data.remove("collectionTimer");
            }

            return this;
        }

        public Builder collectionType(final CollectionType value) {
            data.put("collectionType", value.asString());

            if (DefaultValues.collectionType.equals(value)) {
                data.remove("collectionType");
            }

            return this;
        }

        public Builder loggerName(final String value) {
            data.put("loggerName", value);

            if (DefaultValues.loggerName.equals(value)) {
                data.remove("loggerName");
            }

            return this;
        }

        public Builder maxQueueSize(final int value) {
            data.put("maxQueueSize", value);

            if (DefaultValues.maxQueueSize == value) {
                data.remove("maxQueueSize");
            }

            return this;
        }

        /**
         * @return a new {@link ClientSideErrorLoggingBehavior}
         */
        public ClientSideErrorLoggingBehavior build() {
            return new ClientSideErrorLoggingBehavior(data);
        }
    }

    /**
     * Construct.
     */
    public ClientSideErrorLoggingBehavior() {
        this(new HashMap<String, Object>());
    }

    /**
     * Construct.
     *
     * @param data initial configuration to use
     */
    private ClientSideErrorLoggingBehavior(final Map<String, Object> data) {
        data.put("url", createCallbackUrl());
        data.put("logLevel", settings().level());

        if (settings().debug()) {
            data.put("debug", true);
        }

        this.data = data;
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        final ClientSideErrorLoggingSettings settings = settings();

        response.render(settings.javaScriptHeaderItem());
        response.render(JavaScriptHeaderItem.forScript(createInitializerScript(data), settings.id()));
    }

    /**
     * creates the initializer script
     *
     * @param data configuration data
     * @return new initializer script.
     */
    protected CharSequence createInitializerScript(final Map<String, Object> data) {
        try {
            return "$.wicketClientSideLogging(" + JSONObject.valueToString(data) + ");";
        } catch (JSONException e) {
            throw new WicketRuntimeException(e);
        }
    }

    /**
     * @return callback url that is used as client side logging entry
     */
    protected CharSequence createCallbackUrl() {
        return RequestCycle.get().urlFor(new ClientSideErrorLoggingRR(), null);
    }

    /**
     * The resource reference that represents the entry point on server side
     * for client side logging.
     */
    private static final class ClientSideErrorLoggingRR extends ResourceReference {

        /**
         * Construct.
         */
        public ClientSideErrorLoggingRR() {
            super(new Key(ClientSideErrorLoggingBehavior.class.getName(), ClientSideErrorLoggingSettings.get().id(), null, null, null));
        }

        @Override
        public org.apache.wicket.request.resource.IResource getResource() {
            return new ClientSideErrorLoggingResource();
        }
    }

    /**
     * The resource that is executed when an incoming log request needs to be handled.
     */
    private static final class ClientSideErrorLoggingResource extends AbstractResource {

        @Override
        protected org.apache.wicket.request.resource.AbstractResource.ResourceResponse newResourceResponse(Attributes attributes) {
            writeToLog(attributes);

            return new EmptyResourceResponse();
        }

        /**
         * writes all log messages to the log store
         *
         * @param attributes response attributes
         */
        private void writeToLog(Attributes attributes) {
            final IRequestParameters params = attributes.getRequest().getPostParameters();
            final IParamValueExtractor.Result result = settings().paramValueExtractor().parse(params);

            settings().logger().log(result.logObjects(), result.clientInfos());
        }


    }
}
