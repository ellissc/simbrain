package org.simbrain.world.odorworld.entities;

import org.simbrain.util.propertyeditor.CopyableObject;
import org.simbrain.workspace.AttributeContainer;
import org.simbrain.world.odorworld.events.SensorEffectorEvents;

/**
 * Interface for effectors and sensors. "Peripheral" is supposed to suggest
 * the peripheral nervous system, which encompasses sensory and motor neurons.
 * It's the best I could come up with... :/
 *
 * @author Jeff Yoshimi
 */
public interface PeripheralAttribute extends AttributeContainer, CopyableObject {

    String getLabel();

    void setLabel(String label);

    void update(OdorWorldEntity parent);

    SensorEffectorEvents getEvents();

    /**
     * Called by reflection to return a custom description for the {@link
     * org.simbrain.workspace.gui.couplingmanager.AttributePanel.ProducerOrConsumer}
     * corresponding to object sensors and effectors.
     */
    default String getAttributeDescription() {
        return getId() + ":" + getLabel();
    }
}
