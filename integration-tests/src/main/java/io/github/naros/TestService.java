package io.github.naros;

import java.util.HashMap;
import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * @author Chris Cranford
 */
@ApplicationScoped
public class TestService {
    @Inject
    Event<ExportedEvent<?, ?>> event;

    @Transactional
    public void doSomething() {
        final Map<String, Object> values = new HashMap<>();
        values.put("name", "John Doe");
        event.fire(new TestEvent(values));
    }
}
