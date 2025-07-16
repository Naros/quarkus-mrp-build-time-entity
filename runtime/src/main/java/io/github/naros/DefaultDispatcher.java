package io.github.naros;

import java.util.HashMap;
import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;

/**
 * @author Chris Cranford
 */
@ApplicationScoped
public class DefaultDispatcher implements EventDispatcher {

    @Inject
    EntityManager entityManager;

    @Override
    public void onExportedEvent(ExportedEvent<?, ?> event) {
        persist(getDataMapFromEvent(event));
    }

    protected Void persist(Map<String, Object> dataMap) {
        // Unwrap to Hibernate session and save
        Session session = entityManager.unwrap(Session.class);
        session.save(Constants.ENTITY_NAME, dataMap);
        session.setReadOnly(dataMap, true);
        return null;
    }

    protected Map<String, Object> getDataMapFromEvent(ExportedEvent<?, ?> event) {
        final Map<String, Object> dataMap = createDataMap(event);

        for (Map.Entry<String, Object> additionalFields : event.getAdditionalFieldValues().entrySet()) {
            if (dataMap.containsKey(additionalFields.getKey())) {
                continue;
            }
            dataMap.put(additionalFields.getKey(), additionalFields.getValue());
        }

        return dataMap;
    }

    protected Map<String, Object> createDataMap(ExportedEvent<?, ?> event) {
        final Map<String, Object> dataMap = new HashMap<>();
        dataMap.put(Constants.COLUMN_AGGREGATE_TYPE_NAME, event.getAggregateType());
        dataMap.put(Constants.COLUMN_AGGREGATE_ID_NAME, event.getAggregateId());
        dataMap.put(Constants.COLUMN_TYPE_NAME, event.getType());
        dataMap.put(Constants.COLUMN_PAYLOAD_NAME, event.getPayload());
        dataMap.put(Constants.COLUMN_TIMESTAMP_NAME, event.getTimestamp());
        return dataMap;
    }
}
