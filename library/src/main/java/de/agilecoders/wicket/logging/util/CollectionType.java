package de.agilecoders.wicket.logging.util;

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
     *
     * @since 0.1.1
     */
    Unload,

    /**
     * stores all messages in local storage and sends them to server if
     * client isn't busy.
     *
     * @since 0.1.3
     */
    LocalStorage,

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


    @Override
    public String toString() {
        return asString();
    }
}
