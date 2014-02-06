package de.agilecoders.wicket.logging.util;

import de.agilecoders.wicket.logging.ClientSideLogObject;
import de.agilecoders.wicket.logging.Mocks;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

/**
 * Tests the {@link de.agilecoders.wicket.logging.util.ILoggingBarrier.AllowAllBarrier}
 *
 * @author miha
 */
public class SizeAndTimeBasedBarrierTest {

    private volatile ILoggingBarrier.SizeAndTimeFrameBasedBarrier barrier;
    private ClientSideLogObject logObject;
    private ExecutorService pool;

    @Before
    public void setUp() throws Exception {
        barrier = new ILoggingBarrier.SizeAndTimeFrameBasedBarrier(10, 1, TimeUnit.SECONDS);
        logObject = Mocks.createClientSideLogObject();
        pool = Executors.newFixedThreadPool(2);
    }

    @After
    public void tearDown() throws Throwable {
        barrier.shutdownScheduledExecutorService();
        barrier = null;
    }

    @Test
    @Ignore
    public void isAllowOnSingleEventReturnsTrueUntilMaxSizeIsReached() throws Exception {
        checkTenAndCheckForBlockedEleventh();
    }

    @Test
    @Ignore
    public void isAllowOnSingleEventReturnsTrueUntilMaxSizeIsReachedAndResetsToInitialValueAfterTimeFrame() throws Exception {
        checkTenAndCheckForBlockedEleventh();

        Thread.sleep(1000);

        checkTenAndCheckForBlockedEleventh();
    }

    private void checkTenAndCheckForBlockedEleventh() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(2);
        for (int i = 0; i < 2; ++i) {
            pool.execute(newRunnable(i, latch));
        }

        if (latch.await(100, TimeUnit.MILLISECONDS)) {
            assertThat("number: 11", barrier.isAllowed(logObject), is(false));
        } else {
            fail("there was an assertion error. check lines before.");
        }
    }

    private Runnable newRunnable(final int id, final CountDownLatch latch) {
        return new Runnable() {
            @Override
            public void run() {
                for (int i = 5; i > 0; --i) {
                    assertThat("number[" + id + "]: " + i, barrier.isAllowed(logObject), is(true));
                }

                // count down only if no assertion error was thrown before.
                latch.countDown();
            }
        };
    }
}
