package org.simbrain.custom_sims.simulations

//
// import kotlinx.coroutines.Dispatchers
// import kotlinx.coroutines.MainScope
// import kotlinx.coroutines.launch
// import kotlinx.coroutines.runBlocking
// import org.simbrain.custom_sims.addNetworkComponent
// import org.simbrain.custom_sims.addOdorWorldComponent
// import org.simbrain.custom_sims.couplingManager
// import org.simbrain.custom_sims.newSim
// import org.simbrain.network.core.Synapse
// import org.simbrain.network.core.activations
// import org.simbrain.network.updaterules.LinearRule
// import org.simbrain.network.util.BiasedScalarData
// import org.simbrain.util.format
// import org.simbrain.util.geneticalgorithms.*
// import org.simbrain.util.point
// import org.simbrain.util.widgets.ProgressWindow
// import org.simbrain.workspace.Workspace
// import org.simbrain.world.odorworld.entities.EntityType
// import kotlin.math.abs
//
// val evolveMouse = newSim {
//
//     val mainScope = MainScope()
//
//     fun createEvolution(): Evaluator {
//         val evolutionarySimulation = evolutionarySimulation(1) {
//
//             val inputs = chromosome(3) {
//                 nodeGene()
//             }
//
//             val hiddens = chromosome(8) {
//                 nodeGene()
//             }
//
//             val outputs = chromosome(3) {
//                 nodeGene {
//                     updateRule.let {
//                         if (it is LinearRule) {
//                             it.lowerBound = 0.0
//                         }
//                     }
//                 }
//             }
//
//             val connections = chromosome<Synapse, ConnectionGene>()
//
//             val evolutionWorkspace = Workspace()
//
//             val networkComponent = evolutionWorkspace { addNetworkComponent("Network") }
//             val network = networkComponent.network
//
//             val odorworldComponent = evolutionWorkspace { addOdorWorldComponent("Odor World") }
//             val odorworld = odorworldComponent.world
//
//             val sensors = chromosome(3) {
//                 objectSensorGene {
//                     setObjectType(EntityType.SWISS)
//                     theta = it * 2 * 60.0
//                     radius = 32.0
//                     decayFunction.dispersion = 200.0
//                 }
//             }
//
//             val straightMovement = chromosome(1) {
//                 straightMovementGene()
//             }
//
//             val turning = chromosome(
//                 turningGene { direction = -1.0 },
//                 turningGene { direction = 1.0 }
//             )
//
//             val mouse = odorworld.addEntity(EntityType.MOUSE).apply {
//                 location = point(50.0, 200.0)
//             }
//
//             fun createCheese() = odorworld.addEntity(EntityType.SWISS).apply {
//                 location = point(
//                     random.nextDouble(100.0, 300.0),
//                     random.nextDouble(0.0, 300.0)
//                 )
//             }
//
//             val cheeses = List(3) { createCheese() }
//
//             onBuild { visible ->
//                 network {
//                     if (visible) {
//                         +inputs.asGroup {
//                             label = "Input"
//                             location = point(0, 100)
//                         }
//                         +hiddens
//                         +outputs.asGroup {
//                             label = "Output"
//                             location = point(0, -100)
//                         }
//                     } else {
//                         +inputs
//                         +hiddens
//                         +outputs
//                     }
//                     +connections
//                 }
//                 mouse {
//                     +sensors
//                     +straightMovement
//                     +turning
//                 }
//                 evolutionWorkspace {
//                     runBlocking {
//                         couplingManager.apply {
//                             val (straightNeuron, leftNeuron, rightNeuron) = outputs.getProducts()
//                             val (straightConsumer) = straightMovement.getProducts()
//                             val (left, right) = turning.getProducts()
//
//                             sensors.getProducts() couple inputs.getProducts()
//                             straightNeuron couple straightConsumer
//                             leftNeuron couple left
//                             rightNeuron couple right
//                         }
//                     }
//                 }
//
//                 cheeses.forEach { cheese ->
//                     cheese.events.collided.on {
//                         cheese.location = point(
//                             random.nextDouble(100.0, 300.0),
//                             random.nextDouble(0.0, 300.0)
//                         )
//                     }
//                 }
//             }
//
//             onMutate {
//                 hiddens.forEach {
//                     it.mutate {
//                         dataHolder.let {
//                             if (it is BiasedScalarData) it.bias += random.nextDouble(-0.2, 0.2)
//                         }
//                     }
//                 }
//                 connections.forEach {
//                     it.mutate {
//                         strength += random.nextDouble(-0.2, 0.2)
//                     }
//                 }
//                  val source = (inputs + hiddens).selectRandom()
//                  val target = (outputs + hiddens).selectRandom()
//                  connections += connectionGene(source, target) {
//                      strength = random.nextDouble(-0.2, 0.2)
//                  }
//             }
//
//             onEval {
//                 var fitness = 0.0
//
//                 // cheeses.forEach {
//                 //     it.onCollide { other ->
//                 //         if (other === mouse) {
//                 //             score += 1
//                 //         }
//                 //     }
//                 // }
//
//                 evolutionWorkspace.apply {
//                     repeat(100) {
//                         simpleIterate()
//                         val energy = abs(outputs.getProducts().activations.sum()) + 5
//                         fitness += energy / 1000
//                     }
//                 }
//
//                 // val partial = cheeses.map { cheese -> 100 - mouse.getRadiusTo(cheese) }
//                 //     .maxOf { it }
//                 //     .let { if (it < 0) 0.0 else it } / 100
//
//                 fitness
//             }
//
//             onPeek {
//                 workspace.openFromZipData(evolutionWorkspace.zipData)
//             }
//
//         }
//
//         return evaluator(evolutionarySimulation) {
//             populationSize = 100
//             eliminationRatio = 0.5
//             runUntil { generation == 50 || fitness > 250 }
//         }
//     }
//
//     mainScope.launch {
//
//         workspace.clearWorkspace()
//
//         val generations = createEvolution().start()
//
//         launch(Dispatchers.Default) {
//
//             withGui {
//                 val progressWindow = ProgressWindow(200, "Error")
//                 generations.onEachGenerationBest { agent, gen ->
//                     progressWindow.value = gen
//                     progressWindow.text = "Error: ${agent.fitness.format(2)}"
//                 }
//                 progressWindow.close()
//             }
//
//             generations.onEachGenerationBest { agent, gen ->
//                 println("[$gen] Error: ${agent.fitness.format(6)}")
//             }
//
//             val (best, fitness) = generations.best
//
//             println("Winning fitness $fitness after generation ${generations.finalGenerationNumber}")
//
//             best.visibleBuild().peek()
//         }
//
//     }
//
// }