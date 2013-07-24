package de.agilecoders.wicket.logging;

import org.apache.wicket.util.string.StringValue;
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
        ClientSideLogObject logObject = new ClientSideLogObject(StringValue.valueOf("error"),
                                                                StringValue.valueOf("message"),
                                                                StringValue.valueOf("timestamp"),
                                                                0);

        assertThat(logObject.level(), is(equalTo("error")));
        assertThat(logObject.message(), is(equalTo("message")));
        assertThat(logObject.timestamp(), is(equalTo("timestamp")));
        assertThat(logObject.index(), is(equalTo(0)));
    }

}
