/*
 * Copyright (C) 2005,2007 The Authors.  See http://www.simbrain.net/credits
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.simbrain.network;

import org.simbrain.network.core.Network;
import org.simbrain.network.events.NetworkEvents;
import org.simbrain.network.neurongroups.NeuronGroup;
import org.simbrain.util.XStreamUtils;
import org.simbrain.workspace.AttributeContainer;
import org.simbrain.workspace.WorkspaceComponent;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;

import static org.simbrain.network.core.NetworkUtilsKt.getNetworkXStream;

/**
 * Network component.
 */
public final class NetworkComponent extends WorkspaceComponent {

    /**
     * Reference to root network, the main model network.
     */
    private Network network = new Network();

    /**
     * Create a new network component.
     *
     * @param name name
     */
    public NetworkComponent(final String name) {
        super(name);
        init();
    }

    /**
     * Create a new network component.
     *
     * @param name    name of network
     * @param network the network being created
     */
    public NetworkComponent(final String name, final Network network) {
        super(name);
        this.network = network;
        init();
    }

    /**
     * Initialize attribute types and listeners.
     */
    private void init() {

        NetworkEvents event = network.getEvents();

        event.getModelAdded().on(null, true, list -> {
            list.forEach(m -> {
                setChangedSinceLastSave(true);
                if (m instanceof AttributeContainer) {
                    fireAttributeContainerAdded((AttributeContainer) m);
                }
                if (m instanceof NeuronGroup) {
                    ((NeuronGroup)m).getNeuronList().forEach(this::fireAttributeContainerAdded);
                }
            });
        });

        event.getModelRemoved().on(m -> {
            setChangedSinceLastSave(true);
            if (m instanceof AttributeContainer) {
                fireAttributeContainerRemoved((AttributeContainer) m);
            }
            if (m instanceof NeuronGroup) {
                ((NeuronGroup)m).getNeuronList().forEach(this::fireAttributeContainerRemoved);
            }
        });

//        event.onNeuronsUpdated(l -> setChangedSinceLastSave(true));
//
//        event.onTextAdded(t -> setChangedSinceLastSave(true));
//
//        event.onTextRemoved(t -> setChangedSinceLastSave(true));


    }

    @Override
    public List<AttributeContainer> getAttributeContainers() {
        return network.getAllModels().stream()
                .filter(m -> (m instanceof AttributeContainer))
                .map(m -> (AttributeContainer) m )
                .collect(Collectors.toList());
    }

    public static NetworkComponent open(final InputStream input, final String name, final String format) {
        Network newNetwork = (Network) getNetworkXStream().fromXML(input);
        return new NetworkComponent(name, newNetwork);
    }

    @Override
    public void save(final OutputStream output, final String format) {
        getNetworkXStream().toXML(network, output);
    }

    /**
     * Returns a copy of this NetworkComponent.
     *
     * @return the new network component
     */
    public NetworkComponent copy() {
        NetworkComponent ret = new NetworkComponent("Copy of " + getName(), network.copy());
        return ret;
    }

    public Network getNetwork() {
        return network;
    }

    @Override
    public void update() {
        network.update(getName());
    }

    @Override
    public String getXML() {
        return XStreamUtils.getSimbrainXStream().toXML(network);
    }

}
