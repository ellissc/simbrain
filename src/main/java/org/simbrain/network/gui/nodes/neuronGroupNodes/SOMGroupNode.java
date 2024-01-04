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
import org.simbrain.network.neurongroups.SOMGroup;
import org.simbrain.util.Utils;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * PNode representation of Self-Organizing Map.
 *
 * @author jyoshimi
 */
public class SOMGroupNode extends NeuronGroupNode {

    /**
     * Create a SOM Network PNode.
     *
     * @param networkPanel parent panel
     * @param group        the SOM network
     */
    public SOMGroupNode(final NetworkPanel networkPanel, final SOMGroup group) {
        super(networkPanel, group);
        // setStrokePaint(Color.green);
        setCustomMenuItems();
        setInteractionBox(new SOMInteractionBox(networkPanel));
        // setOutlinePadding(15f);
        updateText();
    }

    /**
     * Custom interaction box for SOM group node.
     */
    private class SOMInteractionBox extends NeuronGroupInteractionBox {
        public SOMInteractionBox(NetworkPanel net) {
            super(net);
        }

        @Override
        public String getToolTipText() {
            return "Current learning rate: " + Utils.round(((SOMGroup) getNeuronGroup()).getLearningRate(), 2) + "  Current neighborhood size: " + Utils.round(((SOMGroup) getNeuronGroup()).getNeighborhoodSize(), 2);
        }
    }

    /**
     * Sets custom menu for SOM node.
     */
    protected void setCustomMenuItems() {
        super.addCustomMenuItem(new JMenuItem(new AbstractAction("Reset SOM Network") {
            public void actionPerformed(final ActionEvent event) {
                SOMGroup group = ((SOMGroup) getNeuronGroup());
                group.reset();
                //TODO
                //group.getParentNetwork().fireGroupUpdated(group);
            }
        }));
        super.addCustomMenuItem(new JMenuItem(new AbstractAction("Recall SOM Memory") {
            public void actionPerformed(final ActionEvent event) {
                SOMGroup group = ((SOMGroup) getNeuronGroup());
                group.recall();
                //TODO
                // group.getParentNetwork().fireGroupUpdated(group);
            }
        }));
        super.addCustomMenuItem(new JMenuItem(new AbstractAction("Randomize SOM Weights") {
            public void actionPerformed(final ActionEvent event) {
                SOMGroup group = ((SOMGroup) getNeuronGroup());
                group.randomizeIncomingWeights();
                //TODO
                // group.getParentNetwork().fireGroupUpdated(group);
            }
        }));
    }

}
