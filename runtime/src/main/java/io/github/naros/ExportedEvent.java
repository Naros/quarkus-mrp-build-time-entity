package io.github.naros;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

/**
 * @author Chris Cranford
 */
public interface ExportedEvent<I, P> {

    /**
     * The id of the aggregate affected by a given event.  For example, the order id in case of events
     * relating to an order, or order lines of that order.  This is used to ensure ordering of events
     * within an aggregate type.
     */
    I getAggregateId();

    /**
     * The type of the aggregate affected by the event.  For example, "order" in case of events relating
     * to an order, or order lines of that order.  This is used as the topic name.
     */
    String getAggregateType();

    /**
     * The type of an event.  For example, "Order Created" or "Order Line Cancelled" for events that
     * belong to an given aggregate type such as "order".
     */
    String getType();

    /**
     * The timestamp at which the event occurred.
     */
    Instant getTimestamp();

    /**
     * The event payload.
     */
    P getPayload();

    /**
     * The additional field values to be stored.
     */
    default Map<String, Object> getAdditionalFieldValues() {
        return Collections.emptyMap();
    }
}
