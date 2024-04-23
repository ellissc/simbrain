package org.simbrain.world.threedworld;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.simbrain.workspace.AttributeContainer;
import org.simbrain.workspace.Workspace;
import org.simbrain.workspace.WorkspaceComponent;
import org.simbrain.world.threedworld.engine.ThreeDEngine;
import org.simbrain.world.threedworld.engine.ThreeDEngineConverter;
import org.simbrain.world.threedworld.entities.Agent;
import org.simbrain.world.threedworld.entities.BoxEntityXmlConverter;
import org.simbrain.world.threedworld.entities.Entity;
import org.simbrain.world.threedworld.entities.ModelEntityXmlConverter;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * ThreeDWorldComponent is a workspace component to extract some serialization and attribute
 * management from the ThreeDWorld.
 */
public class ThreeDWorldComponent extends WorkspaceComponent {
    /**
     * @return A newly constructed xstream for serializing a ThreeDWorld.
     */
    public static XStream getXStream() {
        XStream stream = new XStream(new DomDriver());
        stream.registerConverter(new ThreeDEngineConverter());
        stream.registerConverter(new BoxEntityXmlConverter());
        stream.registerConverter(new ModelEntityXmlConverter());
        return stream;
    }

    /**
     * Open a saved ThreeDWorldComponent from an XML input stream.
     *
     * @param input  The input stream to read.
     * @param name   The name of the new world component.
     * @param format The format of the input stream. Should be xml.
     * @return A deserialized ThreeDWorldComponent with a valid ThreeDWorld.
     */
    public static ThreeDWorldComponent open(InputStream input, String name, String format) {
        ThreeDWorld world = (ThreeDWorld) getXStream().fromXML(input);
        world.getEngine().queueState(ThreeDEngine.State.RenderOnly, false);
        return new ThreeDWorldComponent(name, world);
    }

    public static ThreeDWorldComponent create(Workspace workspace, String name) {
        if (workspace.getComponentList(ThreeDWorldComponent.class).isEmpty()) {
            return new ThreeDWorldComponent(name);
        } else {
            throw new RuntimeException("Only one 3D World component is supported.");
        }
    }

    private ThreeDWorld world;

    /**
     * Construct a new ThreeDWorldComponent.
     *
     * @param name The name of the new component.
     */
    public ThreeDWorldComponent(String name) {
        super(name);
        world = new ThreeDWorld();
        world.getEvents().getClosed().on(this::close);
        world.getEvents().getAgentAdded().on(this::fireAttributeContainerAdded);
        world.getEvents().getAgentAdded().on(agent -> {
            fireAttributeContainerAdded(agent);
            setChangedSinceLastSave(true);
            agent.getEvents().getSensorAdded().on(this::fireAttributeContainerAdded);
            agent.getEvents().getEffectorAdded().on(this::fireAttributeContainerAdded);
            agent.getEvents().getSensorDeleted().on(this::fireAttributeContainerRemoved);
            agent.getEvents().getEffectorDeleted().on(this::fireAttributeContainerRemoved);
            setChangedSinceLastSave(true);
        });
        // TODO: Removed (see odorworldcomponent)
    }

    /**
     * Construct a ThreeDWorldComponent with an existing ThreeDWorld.
     *
     * @param name  The name of the new component.
     * @param world The world.
     */
    private ThreeDWorldComponent(String name, ThreeDWorld world) {
        super(name);
        this.world = world;
    }

    /**
     * @return The ThreeDWorld for this workspace component.
     */
    public ThreeDWorld getWorld() {
        return world;
    }

    @Override
    public List<AttributeContainer> getAttributeContainers() {
        List<AttributeContainer> models = new ArrayList<>();
        //models.add(world); No couplings at world level currently
        for (Entity entity : world.getEntities()) {
            models.add(entity);
            if (entity instanceof Agent) {
                Agent agent = (Agent) entity;
                models.addAll(agent.getSensors());
                models.addAll(agent.getEffectors());
            }
        }
        return models;
    }

    @Override
    public void save(OutputStream output, String format) {
        ThreeDEngine.State previousState = world.getEngine().getState();
        world.getEngine().queueState(ThreeDEngine.State.SystemPause, true);
        getXStream().toXML(world, output);
        world.getEngine().queueState(previousState, false);
    }

    @Override
    public void close() {
        super.close();
        world.getEngine().stop(false);
    }

    @Override
    public void update() {
        world.getEngine().updateSync();
    }
}
