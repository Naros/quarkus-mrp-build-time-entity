package io.github.naros;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import io.quarkus.deployment.GeneratedClassGizmoAdaptor;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;

import io.quarkus.deployment.builditem.GeneratedClassBuildItem;
import io.quarkus.gizmo.AnnotationCreator;
import io.quarkus.gizmo.ClassCreator;
import io.quarkus.gizmo.ClassOutput;
import io.quarkus.gizmo.FieldCreator;
import io.quarkus.gizmo.MethodCreator;
import io.quarkus.gizmo.MethodDescriptor;
import io.quarkus.hibernate.orm.deployment.JpaModelBuildItem;
import io.quarkus.hibernate.orm.deployment.JpaModelIndexBuildItem;
import io.quarkus.hibernate.orm.deployment.spi.AdditionalJpaModelBuildItem;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.ParameterizedType;
import org.jboss.jandex.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Chris Cranford
 */
public class ExtensionProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtensionProcessor.class);

    @BuildStep
    public void produceExtensionBuildItem(CombinedIndexBuildItem index, BuildProducer<ExtensionBuildItem> extensionBuildItems) {

        final DotName exportedEvent = DotName.createSimple(ExportedEvent.class.getName());

        Type aggregateIdType = Type.create(DotName.createSimple(String.class.getName()), Type.Kind.CLASS);
        Type payloadType = Type.create(DotName.createSimple(String.class.getName()), Type.Kind.CLASS);

        boolean parameterizedTypesDetected = false;
        for (ClassInfo classInfo : index.getIndex().getAllKnownImplementors(exportedEvent)) {
            LOGGER.info("Found ExportedEvent type: {}", classInfo.name());
            for (Type interfaceType : classInfo.interfaceTypes()) {
                if (interfaceType.name().equals(exportedEvent)) {
                    if (interfaceType.kind().equals(Type.Kind.PARAMETERIZED_TYPE)) {
                        final ParameterizedType pType = interfaceType.asParameterizedType();
                        if (pType.arguments().size() != 2) {
                            throw new IllegalArgumentException(
                                    String.format(
                                            "Expected 2 parameterized types for class %s using interface ExportedEvent",
                                            classInfo.name()));
                        }

                        final Type pTypeAggregateType = pType.arguments().get(0);
                        final Type pTypePayloadType = pType.arguments().get(1);
                        LOGGER.debug(" * Implements ExportedEvent with generic parameters:");
                        LOGGER.debug("     AggregateId: {}", pTypeAggregateType.name().toString());
                        LOGGER.debug("     Payload: {}", pTypePayloadType.name().toString());

                        if (parameterizedTypesDetected) {
                            if (!pTypeAggregateType.equals(aggregateIdType)) {
                                throw new IllegalStateException(
                                        String.format(
                                                "Class %s implements ExportedEvent and expected aggregate-id parameter type " +
                                                        "to be %s but was %s. All ExportedEvent implementors must use the same parameter types.",
                                                classInfo.name(),
                                                aggregateIdType.name(),
                                                pTypeAggregateType.name()));
                            }
                            if (!pTypePayloadType.equals(payloadType)) {
                                throw new IllegalStateException(
                                        String.format(
                                                "Class %s implements ExportedEvent and expected payload parameter type to be " +
                                                        "%s but was %s. All ExportedEvent implementors must use the same parameter types.",
                                                classInfo.name(),
                                                payloadType.name(),
                                                pTypePayloadType.name()));
                            }
                        }
                        else {
                            aggregateIdType = pTypeAggregateType;
                            payloadType = pTypePayloadType;
                            parameterizedTypesDetected = true;
                        }
                    }
                    else {
                        LOGGER.debug(" * Implements ExportedEvent without parameters, using:");
                        LOGGER.debug("     AggregateId: {}", aggregateIdType.name().toString());
                        LOGGER.debug("     Payload: {}", payloadType.name().toString());
                    }
                }
            }
        }

        LOGGER.info("Binding Aggregate Id as '{}'.", aggregateIdType.name().toString());
        LOGGER.info("Binding Payload as '{}'.", payloadType.name().toString());

        extensionBuildItems.produce(new ExtensionBuildItem(aggregateIdType, payloadType));
    }

    @BuildStep
    public void produceEntity(ExtensionBuildItem extensionItem,
                              BuildProducer<GeneratedClassBuildItem> generatedClasses,
                              BuildProducer<AdditionalJpaModelBuildItem> jpaModels) {

        final ClassOutput classOutput = new GeneratedClassGizmoAdaptor(generatedClasses, true);
        try (ClassCreator creator = ClassCreator.builder().classOutput(classOutput).className(Constants.ENTITY_NAME).build()) {
            // Class Annotations
            creator.addAnnotation(Entity.class).addValue("value", Constants.ENTITY_NAME);
            creator.addAnnotation(Table.class).addValue("name", Constants.TABLE_NAME);

            // Identifier
            final FieldCreator idField = creator.getFieldCreator("id", UUID.class);
            idField.addAnnotation(Id.class);
            idField.addAnnotation(GeneratedValue.class).addValue("strategy", GenerationType.UUID);
            final AnnotationCreator idFieldColumn = idField.addAnnotation(Column.class);
            idFieldColumn.addValue("name", Constants.COLUMN_ID_NAME);

            // Aggregate Type
            final FieldCreator aggregateTypeField = creator.getFieldCreator("aggregateType", String.class);
            final AnnotationCreator aggregateTypeColumn = aggregateTypeField.addAnnotation(Column.class);
            aggregateTypeColumn.addValue("name", Constants.COLUMN_AGGREGATE_TYPE_NAME);
            aggregateTypeColumn.addValue("nullable", false);

            // Aggregate Id
            final FieldCreator aggregateIdField = creator.getFieldCreator("aggregateId", extensionItem.getAggregatedType().name().toString());
            final AnnotationCreator aggregateIdFieldColumn = aggregateIdField.addAnnotation(Column.class);
            aggregateIdFieldColumn.addValue("name", Constants.COLUMN_AGGREGATE_ID_NAME);
            aggregateIdFieldColumn.addValue("nullable", false);

            // Type
            final FieldCreator typeField = creator.getFieldCreator("type", String.class);
            final AnnotationCreator typeFieldColumn = typeField.addAnnotation(Column.class);
            typeFieldColumn.addValue("name", Constants.COLUMN_TYPE_NAME);
            typeFieldColumn.addValue("nullable", false);

            // Timestamp
            final FieldCreator timestampField = creator.getFieldCreator("timestamp", Instant.class);
            final AnnotationCreator timestampFieldColumn = timestampField.addAnnotation(Column.class);
            timestampFieldColumn.addValue("name", Constants.COLUMN_TIMESTAMP_NAME);
            timestampFieldColumn.addValue("nullable", false);

            // Payload
            final FieldCreator payloadField = creator.getFieldCreator("payload", extensionItem.getPayloadType().name().toString());
            final AnnotationCreator payloadFieldColumn = payloadField.addAnnotation(Column.class);
            payloadFieldColumn.addValue("name", Constants.COLUMN_PAYLOAD_NAME);

            // Additional fields
            final FieldCreator nameField = creator.getFieldCreator("name", String.class);
            final AnnotationCreator nameFieldColumn = nameField.addAnnotation(Column.class);
            nameFieldColumn.addValue("name", Constants.COLUMN_NAME_NAME);

            // Constructors
            final MethodCreator ctor = creator.getMethodCreator("<init>", void.class);
            ctor.invokeSpecialMethod(MethodDescriptor.ofConstructor(Object.class), ctor.getThis());
            ctor.returnValue(null);
        }

        jpaModels.produce(new AdditionalJpaModelBuildItem(Constants.ENTITY_NAME));
    }
}
