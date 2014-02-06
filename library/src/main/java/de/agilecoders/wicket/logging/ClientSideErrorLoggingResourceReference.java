package de.agilecoders.wicket.logging;

import de.agilecoders.wicket.logging.settings.ClientSideLoggingSettings;
import de.agilecoders.wicket.logging.util.EmptyResourceResponse;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;

/**
 * The resource reference that represents the entry point on server side
 * for client side logging.
 */
public class ClientSideErrorLoggingResourceReference extends ResourceReference {

    /**
     * @return current active settings
     */
    private static ClientSideLoggingSettings settings() {
        return ClientSideLoggingSettings.get();
    }

    /**
     * Construct.
     */
    public ClientSideErrorLoggingResourceReference() {
        this(settings().id());
    }

    /**
     * Construct.
     *
     * @param id the id of this resource reference
     */
    public ClientSideErrorLoggingResourceReference(final String id) {
        super(new Key(ClientSideLoggingBehavior.class.getName(), id, null, null, null));
    }

    @Override
    public IResource getResource() {
        return new ClientSideErrorLoggingResource();
    }

    /**
     * The resource that is executed when an incoming log request needs to be handled.
     */
    public static class ClientSideErrorLoggingResource extends AbstractResource {

        @Override
        protected ResourceResponse newResourceResponse(Attributes attributes) {
            writeToLog(attributes);

            return new EmptyResourceResponse();
        }

        /**
         * writes all log messages to the log store
         *
         * @param attributes response attributes
         */
        protected void writeToLog(Attributes attributes) {
            final IRequestParameters params = attributes.getRequest().getPostParameters();
            final IParamValueExtractor.Result result = settings().paramValueExtractor().parse(params);

            settings().logger().log(result.logObjects(), result.clientInfos());
        }


    }
}
