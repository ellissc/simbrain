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
package org.simbrain.network.gui.actions;

import org.simbrain.network.core.Neuron;
import org.simbrain.network.gui.NetworkDialogsKt;
import org.simbrain.network.gui.NetworkPanel;
import org.simbrain.util.ResourceManager;

import java.awt.event.ActionEvent;

/**
 * Action to construct a test input panel. The user of this class provides the
 * input neurons and network panel from which the action gets the network to be
 * updated. If input neurons are not provided, selected neurons are used as
 * input neurons.
 *
 * @author Jeff Yoshimi
 * @author Lam Nguyen
 */
public class TestInputAction extends ConditionallyEnabledAction {

    /**
     * Network panel.
     */
    private NetworkPanel networkPanel;

    /**
     * Construct action.
     *
     * @param networkPanel networkPanel, must not be null
     */
    public TestInputAction(NetworkPanel networkPanel) {

        super(networkPanel, "Create Input Table...", EnablingCondition.NEURONS);
        putValue(SHORT_DESCRIPTION, "Create a table whose rows provide input to selected neurons");
        putValue(SMALL_ICON, ResourceManager.getImageIcon("menu_icons/TestInput.png"));

        this.networkPanel = networkPanel;
    }


    /**
     * Initialize and display the test input panel.
     */
    public void actionPerformed(ActionEvent event) {
        NetworkDialogsKt.showInputPanel(networkPanel,
                networkPanel.getSelectionManager().filterSelectedModels(Neuron.class));
    }
}