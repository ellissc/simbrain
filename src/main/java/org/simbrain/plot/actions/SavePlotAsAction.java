/*
 * Part of Simbrain--a java-based neural network kit
 * Copyright (C) 2006 Jeff Yoshimi <www.jeffyoshimi.net>
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
package org.simbrain.plot.actions;

import org.simbrain.workspace.gui.DesktopComponent;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Save plot As Action.
 */
public final class SavePlotAsAction extends AbstractAction {

    /**
     * Plot GUI component.
     */
    private final DesktopComponent component;

    /**
     * Create a new save plot as action.
     *
     * @param component GUI Component, must not be null.
     */
    public SavePlotAsAction(final DesktopComponent component) {
        super("Save As...");
        if (component == null) {
            throw new IllegalArgumentException("Desktop component must not be null");
        }
        this.component = component;
        // putValue(SMALL_ICON,
        // ResourceManager.getImageIcon("PixelMatrix.png"));
        // putValue(SHORT_DESCRIPTION, "Create Pixel Matrix");
    }

    /**
     * {@inheritDoc}
     */
    public void actionPerformed(final ActionEvent event) {
        component.showExportDialog();
    }
}
