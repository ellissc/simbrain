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
package org.simbrain.console;

import bsh.Interpreter;
import bsh.util.JConsole;
import org.simbrain.util.genericframe.GenericFrame;
import org.simbrain.workspace.Workspace;
import org.simbrain.workspace.gui.DesktopComponent;
import org.simbrain.workspace.gui.SimbrainDesktop;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Executors;

/**
 * Desktop graphical component displaying a beanshell interpreter.
 */
public class ConsoleDesktopComponent extends DesktopComponent<ConsoleComponent> {


    public ConsoleDesktopComponent(GenericFrame frame, ConsoleComponent component) {
        super(frame, component);
        setPreferredSize(new Dimension(500, 400));
        setLayout(new BorderLayout());
        JConsole console = new JConsole();
        Interpreter interpreter = getSimbrainInterpreter(console, super.getWorkspaceComponent().getWorkspace());
        Executors.newSingleThreadExecutor().execute(interpreter);
        add("Center", console);

        JMenuBar menu = new JMenuBar();
        getParentFrame().setJMenuBar(menu);
        JMenu fileMenu = new JMenu("File");
        menu.add(fileMenu);
        fileMenu.add(SimbrainDesktop.INSTANCE.getActionManager().createImportAction(this));
        fileMenu.add(SimbrainDesktop.INSTANCE.getActionManager().createExportAction(this));
        fileMenu.addSeparator();
        fileMenu.add(SimbrainDesktop.INSTANCE.getActionManager().createRenameAction(this));
        fileMenu.addSeparator();
        fileMenu.add(SimbrainDesktop.INSTANCE.getActionManager().createCloseAction(this));

    }

    /**
     * Returns a Simbrain interpreter.
     *
     * @param console   console for interpreter
     * @param workspace workspace references
     * @return simbrain interpreter
     */
    public static Interpreter getSimbrainInterpreter(final JConsole console, final Workspace workspace) {
        Interpreter interpreter = new Interpreter(console);
        interpreter.getNameSpace().importPackage("org.simbrain.network");
        interpreter.getNameSpace().importPackage("org.simbrain.network.core");
        interpreter.getNameSpace().importPackage("org.simbrain.network.connections");
        interpreter.getNameSpace().importPackage("org.simbrain.network.layouts");
        interpreter.getNameSpace().importPackage("org.simbrain.network.networks");
        interpreter.getNameSpace().importPackage("org.simbrain.network.neuron_update_rules");
        interpreter.getNameSpace().importPackage("org.simbrain.network.neuron_update_rules");
        interpreter.getNameSpace().importPackage("org.simbrain.network.synapse_update_rules");
        interpreter.getNameSpace().importPackage("org.simbrain.network.synapse_update_rules.spikeresponders");
        interpreter.getNameSpace().importPackage("org.simbrain.network.trainers");
        interpreter.getNameSpace().importPackage("org.simbrain.network.groups");
        interpreter.getNameSpace().importPackage("org.simbrain.custom_sims");
        interpreter.getNameSpace().importPackage("org.simbrain.custom_sims.simulations");
        interpreter.getNameSpace().importPackage("org.simbrain.workspace");
        interpreter.getNameSpace().importCommands(".");
        interpreter.getNameSpace().importCommands("org.simbrain.console.commands");
        interpreter.getOut();
        interpreter.getErr();
        try {
            interpreter.set("workspace", workspace);
            interpreter.set("desktop", SimbrainDesktop.INSTANCE);
            interpreter.set("bsh.prompt", ">");
            interpreter.eval("addClassPath(\"scripts/console\");");
            interpreter.print("Simbrain console\n");
            interpreter.print("Enter \"help(); or \"tips();\" for help.\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return interpreter;
    }

}
