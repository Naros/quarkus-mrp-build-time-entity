package io.github.naros;

/**
 * @author Chris Cranford
 */
public interface Constants {
    String ENTITY_NAME = "io.github.naros.OutboxEvent";
    String TABLE_NAME = "outbox_event";

    String COLUMN_ID_NAME = "id";
    String COLUMN_AGGREGATE_TYPE_NAME = "aggregate_type";
    String COLUMN_AGGREGATE_ID_NAME = "aggregate_id";
    String COLUMN_TYPE_NAME = "type";
    String COLUMN_TIMESTAMP_NAME = "timestamp";
    String COLUMN_PAYLOAD_NAME = "payload";
    String COLUMN_NAME_NAME = "name";

}
