package io.github.naros;

import java.time.Instant;
import java.util.Map;

/**
 * @author Chris Cranford
 */
public class TestEvent implements ExportedEvent<Long, String> {

    private final Map<String, Object> additionalValues;

    public TestEvent(Map<String, Object> values) {
        this.additionalValues = values;
    }

    @Override
    public Long getAggregateId() {
        return 1L;
    }

    @Override
    public String getAggregateType() {
        return "TestEvent";
    }

    @Override
    public String getType() {
        return "SomeType";
    }

    @Override
    public Instant getTimestamp() {
        return Instant.now();
    }

    @Override
    public String getPayload() {
        return "Some amazing payload";
    }

    @Override
    public Map<String, Object> getAdditionalFieldValues() {
        return additionalValues;
    }
}
