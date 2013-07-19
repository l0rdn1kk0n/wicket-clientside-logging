package de.agilecoders.wicket.logging;

import org.apache.wicket.request.Url;
import org.apache.wicket.util.string.StringValue;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests the {@link ILogCleaner.DefaultLogCleaner} class.
 *
 * @author miha
 */
public class LogCleanerTest {

    @Test
    public void stringGetsCleaned() {
        assertThat(new ILogCleaner.DefaultLogCleaner().clean("v\ra\nlu\te"), is(equalTo("value")));
    }

    @Test
    public void stringValueGetsCleaned() {
        assertThat(new ILogCleaner.DefaultLogCleaner().clean(StringValue.valueOf("v\ra\nlu\te")), is(equalTo("value")));
    }

    @Test
    public void pathStringGetsCleaned() {
        assertThat(new ILogCleaner.DefaultLogCleaner().toCleanPath(Url.parse("http://url\r.to/path/page").toString(Url.StringMode.FULL)), is(equalTo("/path/page")));
    }

    @Test
    public void pathStringValueGetsCleaned() {
        assertThat(new ILogCleaner.DefaultLogCleaner().toCleanPath(StringValue.valueOf(Url.parse("http://\nurl.to/path/page").toString(Url.StringMode.FULL))), is(equalTo("/path/page")));
    }
}
