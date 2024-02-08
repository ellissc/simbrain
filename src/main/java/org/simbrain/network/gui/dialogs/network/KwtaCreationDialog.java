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
package org.simbrain.network.gui.dialogs.network;

import org.simbrain.network.gui.NetworkPanel;
import org.simbrain.network.layouts.GridLayout;
import org.simbrain.network.layouts.Layout;
import org.simbrain.network.subnetworks.KWTA;
import org.simbrain.util.LabelledItemPanel;
import org.simbrain.util.StandardDialog;
import org.simbrain.util.propertyeditor.AnnotatedPropertyEditor;

import javax.swing.*;

/**
 * <b>KwtaDialog</b> is used as an assistant to create Kwta networks.
 * <p>
 * TODO: When this is re-implemented, use new property panel format.
 */
public class KwtaCreationDialog extends StandardDialog {

    /**
     * Tabbed pane.
     */
    private JTabbedPane tabbedPane = new JTabbedPane();

    /**
     * Logic tab panel.
     */
    private JPanel tabLogic = new JPanel();

    /**
     * Layout tab panel.
     */
    private JPanel tabLayout = new JPanel();

    /**
     * Logic panel.
     */
    private LabelledItemPanel logicPanel = new LabelledItemPanel();

    private Layout layout = new GridLayout();

    /**
     * Layout panel.
     */
    private AnnotatedPropertyEditor layoutPanel;

    // TODO: Separate this from number of neurons! Add a second field.
    /**
     * K field.
     */
    private JTextField tfK = new JTextField("5");

    /**
     * Network Panel.
     */
    private NetworkPanel networkPanel;

    /**
     * This method is the default constructor.
     *
     * @param networkPanel Network panel
     */
    public KwtaCreationDialog(final NetworkPanel networkPanel) {
        this.networkPanel = networkPanel;
        layoutPanel = new AnnotatedPropertyEditor(layout);
        init();
    }

    /**
     * Called when dialog closes.
     */
    protected void closeDialogOk() {
        KWTA kWTA = new KWTA(Integer.parseInt(tfK.getText()));
        layoutPanel.commitChanges();
        layout.layoutNeurons(kWTA.getNeuronList());
        networkPanel.getNetwork().addNetworkModelAsync(kWTA);
        networkPanel.repaint();
        super.closeDialogOk();
    }

    /**
     * Initializes all components used in dialog.
     */
    private void init() {
        // Initializes dialog
        setTitle("New K Winner Take All Network");

        fillFieldValues();

        tfK.setColumns(5);

        // Set up logic panel
        logicPanel.addItem("Number of Neurons", tfK);

        // Set up tab panels
        tabLogic.add(logicPanel);
        tabLayout.add(layoutPanel);
        tabbedPane.addTab("Logic", tabLogic);
        tabbedPane.addTab("Layout", layoutPanel);
        setContentPane(tabbedPane);
    }

    /**
     * Populate fields with current data.
     */
    private void fillFieldValues() {
        // KWTA kw = new KWTA();
        tfK.setText("5");

    }

}
