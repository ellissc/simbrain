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
package org.simbrain.network.subnetworks

import org.simbrain.network.core.WeightMatrix
import org.simbrain.network.core.XStreamConstructor
import org.simbrain.network.trainers.LMSTrainer
import org.simbrain.network.trainers.MatrixDataset
import org.simbrain.network.trainers.SupervisedNetwork
import org.simbrain.network.trainers.createDiagonalDataset
import org.simbrain.network.updaterules.LinearRule
import org.simbrain.util.UserParameter
import org.simbrain.util.propertyeditor.EditableObject
import org.simbrain.util.stats.ProbabilityDistribution
import java.awt.geom.Point2D

/**
 * LMS network.
 *
 * @author Jeff Yoshimi
 */
class LMSNetwork : FeedForward, SupervisedNetwork {

    override lateinit var trainingSet: MatrixDataset

    override val trainer = LMSTrainer()

    constructor(nInputs: Int, nOutputs: Int, initialPosition: Point2D? = null): super(intArrayOf(nInputs, nOutputs), initialPosition) {
        layerList.forEach { it.updateRule = LinearRule() }
        trainingSet = createDiagonalDataset(nInputs, nOutputs)
        label = "LMS"
    }

    @XStreamConstructor
    private constructor() : super()

    val weightMatrix: WeightMatrix get() = inputLayer.outgoingConnectors.first() as WeightMatrix

    override fun randomize(randomizer: ProbabilityDistribution?) {
        weightMatrix.randomize(randomizer)
        // TODO
        // outputLayer.randomizeBiases()
    }

    /**
     * Helper class for creating LMS Networks.
     */
    class LMSCreator(val initialPosition: Point2D?) : EditableObject {

        @UserParameter(label = "Number of inputs", order = 10)
        var nin = 5

        @UserParameter(label = "Number of outputs (classes)",  order = 20)
        var nout = 4

        //TODO: Node type

        override val name = "LMS Network"

        fun create(): LMSNetwork {
            return LMSNetwork(nin, nout, initialPosition)
        }

    }

}