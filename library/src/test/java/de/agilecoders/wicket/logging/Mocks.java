package de.agilecoders.wicket.logging;

import org.apache.wicket.util.string.StringValue;

/**
 * helper class to create mocks
 *
 * @author miha
 */
public final class Mocks {

    public static ClientSideLogObject createClientSideLogObject() {
        return new ClientSideLogObject(StringValue.valueOf("error"),
                                       StringValue.valueOf("message"),
                                       StringValue.valueOf("timestamp"),
                                       StringValue.valueOf("file"),
                                       StringValue.valueOf("line"),
                                       StringValue.valueOf("stacktrace"),
                                       0);
    }

}
