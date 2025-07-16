package io.github.naros;

import io.quarkus.builder.item.SimpleBuildItem;
import org.jboss.jandex.Type;

/**
 * @author Chris Cranford
 */
public final class ExtensionBuildItem extends SimpleBuildItem {

    private final Type aggregatedType;
    private final Type payloadType;

    public ExtensionBuildItem(Type aggregatedType, Type payloadType) {
        this.aggregatedType = aggregatedType;
        this.payloadType = payloadType;
    }

    public Type getAggregatedType() {
        return aggregatedType;
    }

    public Type getPayloadType() {
        return payloadType;
    }

}
