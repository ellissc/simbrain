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
package org.simbrain.workspace.gui;

import org.jetbrains.kotlinx.dl.api.core.Sequential;
import org.jetbrains.kotlinx.dl.api.core.layer.core.Dense;
import org.jetbrains.kotlinx.dl.api.core.layer.core.Input;
import org.simbrain.util.ResourceManager;
import org.simbrain.util.Utils;
import smile.math.blas.BLAS;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * @(#)Splasher.java  2.0  January 31, 2004
 *
 * Copyright (c) 2003-2004 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * This software is in the public domain.
 */

/**
 * <b>Splasher</b> displays the simbrain splash screen an initializes the
 * workspace.
 */
public class Splasher {
    /**
     * Shows the splash screen, launches the application and then disposes the
     * splash screen.
     *
     * @param args the command line arguments
     */
    public static void main(final String[] args) {

        System.setProperty("sun.java2d.metal", "true");

        // Set up loggers (other logging config for tinylog is in build.gradle)
        Logger.getLogger("com.jme").setLevel(Level.OFF);
        Logger.getLogger("com.jmex").setLevel(Level.OFF);

        // TODO: Consider adding a progress bar to show what's being loaded

        // Hack to force initialization of Smile matrix engine at startup and remove subsequent delays
        BLAS.engine.iamax(new float[]{1f, 2f, 3f});

        // Same hack as above, for deep net engine
        if (!Utils.isMacOSX() && !Utils.isLinux()) {
            var dummyDeepNet = Sequential.of(List.of(new Input(new long[]{1L},""), new Dense()),false);
        }

        SplashWindow.splash(ResourceManager.getImage("simbrain-logo.gif"));
        SplashWindow.invokeMain("org.simbrain.workspace.gui.SimbrainDesktop", args);
        SplashWindow.disposeSplash();

    }
}
