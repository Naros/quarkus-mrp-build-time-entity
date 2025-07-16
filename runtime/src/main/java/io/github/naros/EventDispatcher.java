package io.github.naros;

import jakarta.enterprise.event.Observes;

/**
 * @author Chris Cranford
 */
public interface EventDispatcher {
    void onExportedEvent(@Observes ExportedEvent<?, ?> event);
}
