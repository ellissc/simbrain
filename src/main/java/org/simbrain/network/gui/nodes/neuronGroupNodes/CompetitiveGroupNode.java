/*
 * Part of Simbrain--a java-based neural network kit
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
package org.simbrain.network.gui.nodes.neuronGroupNodes;

import org.simbrain.network.gui.NetworkPanel;
import org.simbrain.network.gui.nodes.NeuronGroupNode;
import org.simbrain.network.neurongroups.CompetitiveGroup;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * PNode representation of Competitive network.
 *
 * @author jyoshimi
 */
public class CompetitiveGroupNode extends NeuronGroupNode {

    /**
     * Create a Competitive Network PNode.
     *
     * @param networkPanel parent panel
     * @param group        the competitive network
     */
    public CompetitiveGroupNode(final NetworkPanel networkPanel, final CompetitiveGroup group) {
        super(networkPanel, group);

        super.addCustomMenuItem(new JMenuItem(new AbstractAction("Randomize Weights") {
            public void actionPerformed(final ActionEvent event) {
                CompetitiveGroup group = ((CompetitiveGroup) getNeuronGroup());
                group.randomize();
//                group.getParentNetwork().fireSynapsesUpdated(group.getIncomingWeights()); // TODO: [event]
            }
        }));

    }

}
