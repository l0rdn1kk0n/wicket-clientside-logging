package de.agilecoders.wicket.logging;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests the client side log object
 *
 * @author miha
 */
public class ClientSideLogObjectTest {

    @Test
    public void valuesCanBeRead() {
        ClientSideLogObject logObject = Mocks.createClientSideLogObject();

        assertThat(logObject.level(), is(equalTo("error")));
        assertThat(logObject.message(), is(equalTo("message")));
        assertThat(logObject.timestamp(), is(equalTo(1234l)));
        assertThat(logObject.file(), is(equalTo("file")));
        assertThat(logObject.line(), is(equalTo("line")));
        assertThat(logObject.stacktrace(), is(equalTo("stacktrace")));
        assertThat(logObject.index(), is(equalTo(0)));
    }

}
