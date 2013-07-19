package de.agilecoders.wicket.logging;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests the {@link CollectionType} enum
 *
 * @author miha
 */
public class CollectionTypeTest {

    @Test
    public void singleAsStringIsCorrect() {
        assertThat(CollectionType.Single.asString(), is(equalTo("single")));
    }

    @Test
    public void sizeAsStringIsCorrect() {
        assertThat(CollectionType.Size.asString(), is(equalTo("size")));
    }

    @Test
    public void timerAsStringIsCorrect() {
        assertThat(CollectionType.Timer.asString(), is(equalTo("timer")));
    }
}
