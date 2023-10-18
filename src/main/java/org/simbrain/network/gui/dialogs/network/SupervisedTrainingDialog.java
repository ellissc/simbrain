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
import org.simbrain.network.trainers.Trainable;
import org.simbrain.util.StandardDialog;
import org.simbrain.util.table.BasicDataWrapperKt;
import org.simbrain.util.table.SimbrainDataViewer;
import org.simbrain.util.widgets.ShowHelpAction;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * <b>SupervisedTrainingDialog</b> is the superclass of edit dialogs associated
 * with most supervised learning networks.
 */
public abstract class SupervisedTrainingDialog extends StandardDialog {

    /**
     * Network panel.
     */
    protected NetworkPanel networkPanel;

    /**
     * Main tabbed pane.
     */
    private JTabbedPane tabbedPane = new JTabbedPane();

    /**
     * Reference to the trainable network being edited.
     */
    private Trainable trainable;

    /**
     * Reference to input data panel.
     */
    private SimbrainDataViewer inputPanel;

    /**
     * Reference to training data panel.
     */
    private SimbrainDataViewer trainingPanel;

    /**
     * Reference to validate inputs panel
     */
    private JPanel validateInputsPanel;

    /**
     * List of tabs in the dialog.
     */
    private List<Component> tabs = new ArrayList<Component>();

    public SupervisedTrainingDialog(Frame parent, NetworkPanel networkPanel, Trainable trainable) {
        super(parent, "");
        this.networkPanel = networkPanel;
        this.trainable = trainable;
        // Stop trainer from running any time the window is closed
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                stopTrainer();
            }
        });
    }

    /**
     * Method to stop the trainer from running.
     */
    protected abstract void stopTrainer();

    /**
     * This method initializes the components on the panel.
     */
    protected void initDefaultTabs() {

        // Set to modeless so the dialog can be left open
        setModalityType(ModalityType.MODELESS);

        // Input data tab
        inputPanel =
                new SimbrainDataViewer(BasicDataWrapperKt.createFromMatrix(trainable.getTrainingSet().getInputs()), true);
        // inputPanel = new DataPanel(trainable.getInputNeurons(), trainable.getTrainingSet().getInputDataMatrix(), 5, "Input");
        // inputPanel.setFrame(this);
        addTab("Input data", inputPanel);

        // Training data tab
        trainingPanel =
                new SimbrainDataViewer(BasicDataWrapperKt.createFromMatrix(trainable.getTrainingSet().getTargets()), true);
        // trainingPanel.setFrame(this);
        addTab("Target data", trainingPanel);

        // Testing tab
        // TODO: Needs to be converted
        // validateInputsPanel = TestInputPanel.createTestInputPanel(
        //         networkPanel,
        //         trainable.getInputLayer(),
        //         trainable.getTrainingSet().getInputs());
        // addTab("Validate Input Data", validateInputsPanel);

        // Finalize
        setContentPane(tabbedPane);

        // Listen for tab changed events.
        ChangeListener changeListener = new ChangeListener() {
            public void stateChanged(ChangeEvent changeEvent) {
                JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
                int index = sourceTabbedPane.getSelectedIndex();
                Component current = tabs.get(index);
                int numTabs = tabs.size();
                for (int i = 0; i < numTabs; i++) {
                    if (i == index) {
                        tabbedPane.setComponentAt(i, current);
                        tabbedPane.repaint();
                        continue;
                    } else {
                        JPanel tmpPanel = new JPanel();
                        int minPx = tabbedPane.getTabCount() * 120;
                        if (current.getPreferredSize().width < minPx) {
                            tmpPanel.setPreferredSize(new Dimension(minPx, current.getPreferredSize().height));
                        } else {
                            tmpPanel.setPreferredSize(current.getPreferredSize());
                        }
                        tabbedPane.setComponentAt(i, tmpPanel);
                    }
                }
                tabbedPane.revalidate();

                // TODO
                // if (index == 0) {
                //     // When entering training tab, commit table changes
                //     inputPanel.commitChanges();
                //     trainingPanel.commitChanges();
                // } else if (index == 3) {
                //     // Set validation data to whatever input data currently is
                //     if (inputPanel.getTable().getData() != null) {
                //         validateInputsPanel.setData(((NumericTable) inputPanel.getTable().getData()).as2DDoubleArray());
                //     }
                // }
                pack();
                //setLocationRelativeTo(null);
                updateData();
            }
        };

        tabbedPane.addChangeListener(changeListener);

        // Set up help
        // if (trainable instanceof LMSNetwork) {
        //     Action helpAction = new ShowHelpAction("Pages/Network/network/lmsnetwork.html");
        //     addButton(new JButton(helpAction));
        // } else if (trainable instanceof BackpropNetwork) {
            Action helpAction = new ShowHelpAction("Pages/Network/network/backpropnetwork.html");
            addButton(new JButton(helpAction));
        // }

    }


    /**
     * Called when switching tabs.  Intended to be overridden.
     */
    void updateData() {
    }

    /**
     * Add a tab to the dialog.
     *
     * @param name name to be displayed
     * @param tab  the tab itself
     */
    public void addTab(String name, Component tab) {
        if (tabs.size() == 0) {
            tabbedPane.addTab(name, tab);
        } else {
            tabbedPane.addTab(name, new JPanel());
        }
        tabs.add(tab);
    }

    /**
     * Called when dialog closes.
     */
    protected void closeDialogOk() {
        super.closeDialogOk();
        // inputPanel.commitChanges();
        // trainingPanel.commitChanges();
    }
}
