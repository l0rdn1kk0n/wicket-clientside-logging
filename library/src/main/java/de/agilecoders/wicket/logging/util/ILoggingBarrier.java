package de.agilecoders.wicket.logging.util;

import de.agilecoders.wicket.logging.ClientSideLogObject;

import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * An {@link de.agilecoders.wicket.logging.util.ILoggingBarrier} checks whether an logging event
 * is allowed to be written to log or not.
 *
 * @author miha
 */
public interface ILoggingBarrier {

    /**
     * checks a single log event
     *
     * @param logObject the log event to check
     * @return true, if it's allowed to be logged
     */
    boolean isAllowed(ClientSideLogObject logObject);

    /**
     * checks multiple log events. Please don't use this to iterate over all log events, for this usecase you can use
     * {@link #isAllowed(de.agilecoders.wicket.logging.ClientSideLogObject)}, which is called for each single event.
     *
     * @param logObjects the log events to check
     * @return true, if all loge events are allowed to be logged
     */
    boolean isAllowed(Collection<ClientSideLogObject> logObjects);

    /**
     * Default implementation that allows everything.
     */
    public static final class AllowAllBarrier implements ILoggingBarrier {

        @Override
        public boolean isAllowed(ClientSideLogObject logObject) {
            return true;
        }

        @Override
        public boolean isAllowed(Collection<ClientSideLogObject> logObjects) {
            return true;
        }
    }

    /**
     * A special barrier that allows a maximum number of events in a time frame.
     */
    public static final class SizeAndTimeFrameBasedBarrier implements ILoggingBarrier {

        private final ScheduledExecutorService executor;
        private final AtomicLong counter;
        private final long maxSize;

        /**
         * Construct.
         *
         * @param maxSize  the max number of messages that can be sent during time frame.
         * @param period   defines the time frame period
         * @param timeUnit defines the time frame time unit
         */
        public SizeAndTimeFrameBasedBarrier(long maxSize, long period, TimeUnit timeUnit) {
            this.maxSize = maxSize;
            this.counter = new AtomicLong(maxSize);

            this.executor = newScheduledExecutorService();
            this.executor.scheduleAtFixedRate(newOnTimerEventHandler(), period, period, timeUnit);
        }

        /**
         * @return new timer event handler, default implementation delegates work to {@link #onTimerEvent()}
         */
        protected Runnable newOnTimerEventHandler() {
            return new Runnable() {
                @Override
                public void run() {
                    SizeAndTimeFrameBasedBarrier.this.onTimerEvent();
                }
            };
        }

        /**
         * NOTICE: If you override this method, please implement {@link #shutdownScheduledExecutorService()} too.
         *
         * @return new scheduled executor service
         */
        protected ScheduledExecutorService newScheduledExecutorService() {
           return Executors.newScheduledThreadPool(1);
        }

        /**
         * shutdown executor service
         *
         * @throws Throwable if executor can't be stopped
         */
        protected void shutdownScheduledExecutorService() throws Throwable {
            this.executor.shutdownNow();
            this.executor.awaitTermination(1, TimeUnit.SECONDS);
        }

        /**
         * this method is triggered after some period of time. The default implementation resets the counter.
         */
        protected void onTimerEvent() {
            counter.set(maxSize);
        }

        @Override
        public boolean isAllowed(ClientSideLogObject logObject) {
            return counter.decrementAndGet() > 0;
        }

        @Override
        public boolean isAllowed(Collection<ClientSideLogObject> logObjects) {
            return counter.get() - logObjects.size() > 0;
        }

        @Override
        protected void finalize() throws Throwable {
            try {
                shutdownScheduledExecutorService();
            } catch (Throwable e) {
                // ignore
            } finally {
                super.finalize();
            }
        }
    }
}
