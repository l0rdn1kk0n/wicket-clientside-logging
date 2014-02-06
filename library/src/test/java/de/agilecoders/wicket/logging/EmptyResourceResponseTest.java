package de.agilecoders.wicket.logging;


import de.agilecoders.wicket.logging.util.EmptyResourceResponse;
import org.apache.wicket.mock.MockWebRequest;
import org.apache.wicket.mock.MockWebResponse;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.util.time.Duration;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * tests the {@link de.agilecoders.wicket.logging.util.EmptyResourceResponse} class
 *
 * @author miha
 */
public class EmptyResourceResponseTest {

    @Test
    public void correctContentLengthIsSet() {
        EmptyResourceResponse response = new EmptyResourceResponse();

        assertThat(response.getContentLength(), is(equalTo(0L)));
    }

    @Test
    public void cachingIsDeactivated() {
        EmptyResourceResponse response = new EmptyResourceResponse();

        assertThat(response.getCacheDuration(), is(equalTo(Duration.milliseconds(0))));
    }

    @Test
    public void statusCodeCanBeChanged() {
        EmptyResourceResponse response = new EmptyResourceResponse(404);

        assertThat(response.getStatusCode(), is(equalTo(404)));
    }

    @Test
    public void defaultContentTypeIsText() {
        EmptyResourceResponse response = new EmptyResourceResponse();

        assertThat(response.getContentType(), is(equalTo("text/plain")));
    }

    @Test
    public void defaultStatusCodeIs200() {
        EmptyResourceResponse response = new EmptyResourceResponse();

        assertThat(response.getStatusCode(), is(equalTo(200)));
    }

    @Test
    public void noContentWillBeWrittenToResponse() throws IOException {
        MockWebResponse webResponse = spy(new MockWebResponse());
        EmptyResourceResponse response = new EmptyResourceResponse();
        IResource.Attributes attr = spy(new IResource.Attributes(new MockWebRequest(Url.parse("./")), webResponse));

        response.getWriteCallback().writeData(attr);

        verify(webResponse, never()).write(any(CharSequence.class));
        verify(webResponse, never()).write(any(byte[].class));
        verify(webResponse, never()).write(any(byte[].class), anyInt(), anyInt());
    }

}
