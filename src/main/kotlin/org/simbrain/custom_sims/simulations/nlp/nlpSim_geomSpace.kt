package org.simbrain.custom_sims.simulations

import org.simbrain.custom_sims.addProjectionPlot2
import org.simbrain.custom_sims.addTextWorld
import org.simbrain.custom_sims.couplingManager
import org.simbrain.custom_sims.newSim
import org.simbrain.util.Utils.FS
import org.simbrain.util.place
import org.simbrain.util.point
import org.simbrain.util.projection.PCAProjection2
import java.io.File

/**
 * Todo
 *
 */
val nlpSim_geomSpace = newSim {

    // 2. "Geometric thinking and vector spaces"
    //
    // Start with a pre-trained dictionary.
    // Multiple dimensionality reduction methods:
    // - 2d vector space
    // - 3d vector space
    // - Nd vector space
    // Illustrate how the word are embedded into a high dimensional space
    //
    // Second point: distance and similarity
    // Show how distance would be calculated in the various vector spaces
    // Comparison of Euclidean distance vs cosine distance

    workspace.clearWorkspace()

    // Text World
    val twc = addTextWorld("Text World")
    val textWorld = twc.world
    val text = File("simulations" + FS + "texts" + FS + "mlk.txt").readText()
    textWorld.loadDictionary(text)
    textWorld.text = text

    withGui {
        place(twc) {
            location = point(0, 0)
            width = 400
            height = 500
        }
    }

    // Network
    // val networkComponent = addNetworkComponent("Network")
    // val network = networkComponent.network
    // val nc = network.createNeuronCollection(textWorld.tokenVectorMap.size).apply {
    //     label = "Vector Embeddings for Word Tokens"
    //     location = point(0, 0)
    //     layout(GridLayout())
    // }

    // withGui {
    //     place(networkComponent) {
    //         location = point(450, 0)
    //         width = 400
    //         height = 400
    //     }
    // }

    // Location of the projection in the desktop
    val projectionPlot = addProjectionPlot2("Activations")
    projectionPlot.projector.tolerance = .2
    projectionPlot.projector.projectionMethod = PCAProjection2()
    withGui {
        place(projectionPlot) {
            location = point(450, 0)
            width = 500
            height = 500
        }
    }

    // Couple the text world to neuron collection
    with(couplingManager) {
        // createCoupling(
        //     textWorld.getProducer("getCurrentVector"),
        //     nc.getConsumer("addInputs")
        // )
        createCoupling(
            textWorld.getProducer("getCurrentVector"),
            projectionPlot.getConsumer("addPoint")
        )
        createCoupling(
            textWorld.getProducer("getCurrentToken"),
            projectionPlot.getConsumer("setLabel")
        )
    }

}