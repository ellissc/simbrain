package org.simbrain.workspace.serialization;

import org.simbrain.workspace.*;

import java.util.Objects;

/**
 * Class used to represent a coupling in the archive.
 *
 * @author Matt Watson
 */
class ArchivedCoupling {

    /**
     * The source attribute for the coupling.
     */
    private ArchivedAttribute producer;

    /**
     * The target attribute for the coupling.
     */
    private ArchivedAttribute consumer;

    /**
     * Creates a new instance.
     *
     * @param producer The producer attribute.
     * @param consumer The consumer attribute.
     */
    ArchivedCoupling(ArchivedAttribute producer, ArchivedAttribute consumer) {
        this.producer = producer;
        this.consumer = consumer;
    }

    public ArchivedAttribute getProducer() {
        return producer;
    }

    public ArchivedAttribute getConsumer() {
        return consumer;
    }

    public Producer createProducer(Workspace workspace) {
        AttributeContainer container = getObjectFromWorkspace(workspace, producer);
        String method = producer.getMethodName();
        return workspace.getCouplingManager().getProducer(container, method);
    }

    public Consumer createConsumer(Workspace workspace) {
        AttributeContainer container = getObjectFromWorkspace(workspace, consumer);
        String method = consumer.getMethodName();
        return workspace.getCouplingManager().getConsumer(container, method);
    }

    /**
     * Find the attribute container corresponding to an archived attribute object.
     */
    private AttributeContainer getObjectFromWorkspace(Workspace workspace, ArchivedAttribute attribute) {
        WorkspaceComponent component = workspace.getComponent(attribute.getComponentId());
        for(AttributeContainer container : component.getAttributeContainers()) {
            if (Objects.equals(container.getId(), attribute.getAttributeId())) {
                return container;
            }
        }
        throw new RuntimeException(String.format("Failed to retrieve object %s from serialized component %s.", attribute.getAttributeId(), attribute.getComponentId()));
    }

}
