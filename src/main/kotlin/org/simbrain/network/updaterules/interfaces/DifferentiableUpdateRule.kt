/*
 * Part of Simbrain--a java-based neural network kit Copyright (C) 2005,2007 The
 * Authors. See http://www.simbrain.net/credits This program is free software;
 * you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You
 * should have received a copy of the GNU General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 59 Temple Place
 * - Suite 330, Boston, MA 02111-1307, USA.
 */
package org.simbrain.network.updaterules.interfaces

import smile.math.matrix.Matrix

/**
 * Indicates that an update rule is differentiable, and has a getDerivative
 * function. Used by backprop.
 *
 * @author jyoshimi
 */
interface DifferentiableUpdateRule {
    /**
     * The derivative of the activation function.
     *
     * @param val the value being sent through the neuron's derivative
     * @return the derivative of the neuron's activation function with respect
     * to val.
     */
    fun getDerivative(`val`: Double): Double

    /**
     * Array based derivative. By default forwards to scalar derivative.
     */
    fun getDerivative(input: Matrix): Matrix {
        val derivatives = Matrix(input.nrow(), 1)
        for (i in 0 until derivatives.nrow()) {
            derivatives[i, 0] = getDerivative(input[i, 0])
        }
        return derivatives
    }
}
