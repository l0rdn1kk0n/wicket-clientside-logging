package de.agilecoders.wicket.logging.util;

import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.IResource;

import java.io.IOException;

/**
 * An empty resource response that disables caching and uses a given status code
 * or 200.
 *
 * @author miha
 */
public class EmptyResourceResponse extends AbstractResource.ResourceResponse {

    /**
     * Construct. Uses "200" as default http status code.
     */
    public EmptyResourceResponse() {
        this(200);
    }

    /**
     * Construct.
     *
     * @param statusCode the return http status code
     */
    public EmptyResourceResponse(int statusCode) {
        super();

        disableCaching();
        setStatusCode(statusCode);
        setContentLength(0);
        setContentType("text/plain");
        setWriteCallback(newWriteCallback());
    }

    /**
     * creates a new empty write callback.
     *
     * @return noop {@link org.apache.wicket.request.resource.AbstractResource.WriteCallback}
     */
    protected AbstractResource.WriteCallback newWriteCallback() {
        return new AbstractResource.WriteCallback() {
            @Override
            public void writeData(IResource.Attributes attributes) throws IOException {
            }
        };
    }

}
