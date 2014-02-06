package de.agilecoders.wicket.logging.util;

import com.google.common.collect.Sets;
import de.agilecoders.wicket.logging.ClientSideLogObject;
import de.agilecoders.wicket.logging.Mocks;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests the {@link de.agilecoders.wicket.logging.util.ILoggingBarrier.AllowAllBarrier}
 *
 * @author miha
 */
public class AllowAllBarrierTest {

    private ILoggingBarrier.AllowAllBarrier barrier;

    @Before
    public void setUp() throws Exception {
        barrier = new ILoggingBarrier.AllowAllBarrier();
    }

    @Test
    public void isAllowOnSingleEventReturnsTrue() throws Exception {
        assertThat(barrier.isAllowed((ClientSideLogObject) null), is(true));
        assertThat(barrier.isAllowed(Mocks.createClientSideLogObject()), is(true));
    }

    @Test
    public void isAllowOnMultipleEventReturnsTrue() throws Exception {
        assertThat(barrier.isAllowed((Collection<ClientSideLogObject>) null), is(true));
        assertThat(barrier.isAllowed(Sets.newHashSet(Mocks.createClientSideLogObject())), is(true));
    }
}
