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
package org.simbrain.world.odorworld.actions;

import org.simbrain.util.ResourceManager;
import org.simbrain.util.SwingKt;
import org.simbrain.world.odorworld.OdorWorldPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Action for showing world preferences.
 */
public final class ShowWorldPrefsAction extends AbstractAction {

    /**
     * Plot GUI component.
     */
    private final OdorWorldPanel component;

    /**
     * Construct a show prefs action.
     *
     * @param component parent component
     */
    public ShowWorldPrefsAction(final OdorWorldPanel component) {
        super("World Preferences...");
        if (component == null) {
            throw new IllegalArgumentException("Desktop component must not be null");
        }
        this.component = component;
        this.putValue(this.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_P, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        putValue(SMALL_ICON, ResourceManager.getImageIcon("menu_icons/Prefs.png"));
        putValue(SHORT_DESCRIPTION, "Odor world preferences...");
    }

    @Override
    public void actionPerformed(final ActionEvent event) {
        var dialog = SwingKt.createDialog(component.getWorld());
        SwingKt.display(dialog);

    }
}
