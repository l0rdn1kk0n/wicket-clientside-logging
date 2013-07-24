package de.agilecoders.wicket.logging;

/**
 * Defines all supported collection types.
 *
 * @author miha
 */
public enum CollectionType {
    /**
     * each message will be sent to the backend directly
     */
    Single,

    /**
     * after a configurable (defaults.collectionTimer) amount of time all
     * queued messages will be sent to backend
     */
    Timer,

    /**
     * sends all messages before unload event is fired on client side
     */
    Unload,

    /**
     * after a configurable (defaults.maxQueueSize) size of queue all queued
     * messages will be sent to backend
     */
    Size;

    /**
     * @return collection type as string
     */
    public String asString() {
        return name().toLowerCase();
    }
}
