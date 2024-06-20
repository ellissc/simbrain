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
package org.simbrain.network.core

/**
 * Classes that implement this interface describe individual actions that
 * together comprise a network update.
 *
 * @author jyoshimi
 */
interface NetworkUpdateAction {
    /**
     * Invoke this action.
     */
    fun invoke()

    /**
     * Provide a String description of this update method.
     *
     * @return the update description
     */
    val description: String?

    /**
     * Provide a longer description for tooltips, etc.
     *
     * @return the update description
     */
    val longDescription: String?
}
