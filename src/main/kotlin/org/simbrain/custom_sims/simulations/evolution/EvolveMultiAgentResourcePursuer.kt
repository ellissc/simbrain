package org.simbrain.custom_sims.simulations
//
// import kotlinx.coroutines.*
// import org.simbrain.custom_sims.addNetworkComponent
// import org.simbrain.custom_sims.addOdorWorldComponent
// import org.simbrain.custom_sims.couplingManager
// import org.simbrain.custom_sims.newSim
// import org.simbrain.network.NetworkComponent
// import org.simbrain.network.core.Neuron
// import org.simbrain.network.core.activations
// import org.simbrain.network.core.getModelByLabel
// import org.simbrain.network.core.labels
// import org.simbrain.network.layouts.GridLayout
// import org.simbrain.network.layouts.LineLayout
// import org.simbrain.network.updaterules.DecayRule
// import org.simbrain.network.updaterules.interfaces.BoundedUpdateRule
// import org.simbrain.network.util.BiasedScalarData
// import org.simbrain.util.*
// import org.simbrain.util.geneticalgorithms.*
// import org.simbrain.util.piccolo.GridCoordinate
// import org.simbrain.util.piccolo.toPixelCoordinate
// import org.simbrain.util.widgets.ProgressWindow
// import org.simbrain.workspace.Workspace
// import org.simbrain.world.odorworld.OdorWorld
// import org.simbrain.world.odorworld.OdorWorldComponent
// import org.simbrain.world.odorworld.entities.EntityType
// import org.simbrain.world.odorworld.entities.OdorWorldEntity
// import kotlin.math.abs
// import kotlin.math.pow
// import kotlin.random.Random
//
//
// val evolveMultiAgentResourcePursuer = newSim {
//
//     val scope = MainScope()
//
//     /**
//      * Max generation to run before giving up
//      */
//     val maxGenerations = 50
//
//     /**
//      * Iterations to run for each simulation. If < 3000 success is usually by luck.
//      */
//     val iterationsPerRun = 4000
//
//     val thirstThreshold = 5.0
//
//     fun createEvolution(): Evaluator {
//         val evolutionarySimulation = evolutionarySimulation(1) {
//
//             class NetworkGenotype {
//                 val inputs = chromosome(3) {
//                     nodeGene()
//                 }
//
//                 val metrics = chromosome(
//                     nodeGene {
//                         label = "Thirst"
//                         isClamped = true
//                         activation = 0.5
//                     }
//                 )
//
//                 val hiddens = chromosome(8) {
//                     nodeGene {
//                         updateRule = DecayRule().apply {
//                             decayFraction = .01
//                         }
//                     }
//                 }
//
//                 val outputs = chromosome(3) { index ->
//                     nodeGene {
//                         updateRule.let {
//                             if (it is BoundedUpdateRule) {
//                                 it.lowerBound = -10.0
//                                 it.upperBound = 10.0
//                             }
//                         }
//                         dataHolder.let {
//                             if (it is BiasedScalarData) {
//                                 it.bias = 1.0
//                             }
//                         }
//                     }
//                 }
//
//                 // Pre-populate with a few connections
//                 val connections = chromosome(
//                     connectionGene(inputs[0], hiddens[0]),
//                     connectionGene(inputs[1], hiddens[1]),
//                     connectionGene(inputs[1], hiddens[0]),
//                     connectionGene(inputs[1], hiddens[1]),
//                     connectionGene(hiddens[1], outputs[0]),
//                     connectionGene(hiddens[1], outputs[1]),
//                     connectionGene(hiddens[2], outputs[0]),
//                     connectionGene(hiddens[2], outputs[1])
//                 )
//             }
//
//             val networkItems1 = NetworkGenotype()
//             val networkItems2 = NetworkGenotype()
//
//             val evolutionWorkspace = EvolutionWorkspace()
//
//             fun makeNetwork(name: String) = evolutionWorkspace { addNetworkComponent(name) }.network
//
//             val network1 = makeNetwork("Agent 1")
//             val network2 = makeNetwork("Agent 2")
//
//             // TODO: A way to consolidate the code a bit?
//
//             val odorworldComponent = evolutionWorkspace {
//                 addOdorWorldComponent("World")
//             }
//
//             // TODO
//             // withGui {
//             //     place(odorworldComponent) {
//             //         location = point(410, 0)
//             //         width = 400
//             //         height = 400
//             //     }
//             // }
//
//             val odorworld = odorworldComponent.world.apply {
//                 isObjectsBlockMovement = true
//                 wrapAround = false
//                 // tileMap.updateMapSize(40, 40);
//                 // Grass = 5+1, Water = 0+1, Berries = 537+1
//                 // tileMap.editTile(10, 10, 6)
//             }
//
//             class MouseThing(name: String) {
//                 val sensors = chromosome(3) {
//                     tileSensorGene {
//                         tileType = "water"
//                         theta = it * 2 * 60.0 - 60.0
//                         radius = 64.0
//                         decayFunction.dispersion = 200.0
//                         showDispersion = true
//                     }
//                 }
//
//                 val straightMovement = chromosome(1) {
//                     straightMovementGene()
//                 }
//
//                 val turning = chromosome(
//                     turningGene { direction = -1.0 },
//                     turningGene { direction = 1.0 }
//                 )
//
//                 val mouse = odorworld.addEntity(EntityType.MOUSE).apply {
//                     this.name = name
//                     location = point(200.0, 200.0)
//                 }
//             }
//
//             val mouse1 = MouseThing("mouse 1")
//             val mouse2 = MouseThing("mouse 2")
//
//             // Take the current chromosomes,and express them via an agent in a world.
//             // Everything needed to build one generation
//             // Called once for each genome at each generation
//             onBuild { visible ->
//
//                 suspend fun NetworkGeneticsContext.bindItemsToNetwork(items: NetworkGenotype) = coroutineScope {
//                     with(items) {
//                         if (visible) {
//                             val inputGroup = +inputs.asNeuronCollection {
//                                 label = "Input"
//                                 layout(LineLayout())
//                                 location = point(250, 280)
//                             }
//                             inputGroup.neuronList.labels = listOf("center", "left", "right")
//                             +metrics
//                             +hiddens.asNeuronCollection {
//                                 label = "Hidden"
//                                 layout(GridLayout())
//                                 location = point(0, 100)
//                             }
//                             val outputGroup = +outputs.asNeuronCollection {
//                                 label = "Output"
//                                 layout(LineLayout())
//                                 location = point(250, 40)
//                                 setNeuronType(this@with.outputs[0].template.updateRule)
//                             }
//                             outputGroup.neuronList.labels = listOf("straight", "left", "right")
//                         } else {
//                             // This is update when graphics are off
//                             +inputs
//                             +metrics
//                             +hiddens
//                             +outputs
//                         }
//                         +connections
//                     }
//                 }
//
//                 network1 {
//                     bindItemsToNetwork(networkItems1)
//                 }
//                 network2 {
//                     bindItemsToNetwork(networkItems2)
//                 }
//                 mouse1.mouse {
//                     with(mouse1) {
//                         +sensors
//                         +straightMovement
//                         +turning
//                     }
//                 }
//                 mouse2.mouse {
//                     with(mouse2) {
//                         +sensors
//                         +straightMovement
//                         +turning
//                     }
//                 }
//
//                 evolutionWorkspace {
//                     runBlocking {
//                         couplingManager.apply {
//                             listOf(networkItems1 to mouse1, networkItems2 to mouse2).forEach { (n, m) ->
//                                 with(n) {
//                                     with(m) {
//                                         val (straightNeuron, leftNeuron, rightNeuron) = outputs.getProducts()
//                                         val (straightConsumer) = straightMovement.getProducts()
//                                         val (left, right) = turning.getProducts()
//
//                                         sensors.getProducts() couple inputs.getProducts()
//                                         straightNeuron couple straightConsumer
//                                         leftNeuron couple left
//                                         rightNeuron couple right
//                                     }
//                                 }
//                             }
//                         }
//                     }
//                 }
//             }
//
//             //
//             // Mutate the chromosomes. Specify what things are mutated at each generation.
//             //
//             onMutate {
//                 listOf(networkItems1, networkItems2).forEach {
//                     with(it) {
//                         hiddens.forEach {
//                             it.mutate {
//                                 dataHolder.let {
//                                     if (it is BiasedScalarData) it.bias += random.nextDouble(-0.2, 0.2)
//                                 }
//                             }
//                         }
//                         if (Random.nextDouble() > 0.5) {
//                             hiddens.add(nodeGene())
//                         }
//
//                         connections.forEach {
//                             it.mutate {
//                                 strength += random.nextDouble(-0.5, 0.5)
//                             }
//                         }
//
//                         if (Random.nextDouble() > 0.5) {
//                             // Random source neuron
//                             val source = (inputs + hiddens + metrics).selectRandom()
//                             // Random target neuron
//                             val target = (outputs + hiddens).selectRandom()
//                             // Add the connection
//                             connections += connectionGene(source, target) {
//                                 strength = random.nextDouble(-10.0, 10.0)
//                             }
//                         }
//                     }
//                 }
//
//             }
//
//             fun Workspace.onReachingWater(waterLocations: MutableSet<GridCoordinate>, thirstNeuron: Neuron, world: OdorWorld, entity: OdorWorldEntity, good: () -> Unit, bad: (thirst: Double) -> Unit) {
//
//                 fun randomTileCoordinate() = GridCoordinate(
//                     random.nextInt(world.tileMap.width).toDouble(),
//                     random.nextInt(world.tileMap.height).toDouble()
//                 )
//
//                 fun setTile(coordinate: GridCoordinate, tileId: Int) {
//                     val (x, y) = coordinate
//                     world.tileMap.setTile(x.toInt(), y.toInt(), tileId)
//                 }
//
//                 List(3) { randomTileCoordinate() }.forEach {
//                     waterLocations.add(it)
//                     setTile(it, 3)
//                 }
//
//                 addUpdateAction("location check") {
//                     with(world.tileMap) {
//                         waterLocations.toList().forEach { currentWaterLocation ->
//                             val distance = currentWaterLocation.toPixelCoordinate().distanceTo(entity.location)
//                             if (distance < entity.width / 2) {
//                                 good()
//                                 thirstNeuron.activation = 0.0
//
//                                 setTile(currentWaterLocation, 0)
//                                 waterLocations.remove(currentWaterLocation)
//
//                                 val newLocation = randomTileCoordinate()
//                                 waterLocations.add(newLocation)
//                                 setTile(newLocation, 3)
//                             }
//                         }
//                     }
//                 }
//
//                 addUpdateAction("update thirst") {
//                     thirstNeuron.activation = thirstNeuron.activation + 0.005
//                     if (thirstNeuron.activation > thirstThreshold) {
//                         bad(thirstNeuron.activation)
//                     }
//                 }
//             }
//
//             //
//             // Evaluate the current generation.
//             //
//             onEval {
//                 var fitness = 0.0
//
//                 networkItems1.metrics.getProducts()
//
//                 suspend fun evalAgent(networkNetworkGenotype: NetworkGenotype, mouseThing: MouseThing) {
//
//                     val sharedSet = HashSet<GridCoordinate>()
//
//                     evolutionWorkspace.onReachingWater(sharedSet, networkNetworkGenotype.metrics.getProducts().first(), odorworld, mouseThing.mouse, {
//                         // fitness += 1
//                     }) {
//                         fitness -= (it - thirstThreshold) * (20.0 / iterationsPerRun)
//                     }
//
//                     with(networkNetworkGenotype) {
//                         with (mouseThing) {
//                             evolutionWorkspace.onReachingWater(sharedSet, metrics.getProducts().first(), odorworld, mouseThing.mouse, {
//                                 // fitness += 1
//                             }) {
//                                 fitness -= (it - thirstThreshold) * (20.0 / iterationsPerRun)
//                             }
//
//                             evolutionWorkspace.addUpdateAction("update thirst fitness") {
//                                 val (thirst) = metrics.getProducts()
//                                 if (thirst.activation < thirstThreshold) {
//                                     fitness += (10.0 / iterationsPerRun)
//                                 }
//                             }
//
//                             evolutionWorkspace.addUpdateAction("update energy") {
//                                 val (thirst) = metrics.getProducts()
//                                 val outputsActivations = outputs.getProducts().activations.sumOf { 1.2.pow(if (it < 0) it * -2 else it) - 1 }
//                                 val allActivations = (inputs + hiddens).getProducts().activations.sumOf { abs(it) } * 2
//                                 thirst.activation += (outputsActivations + allActivations) * (1 / iterationsPerRun)
//                             }
//
//                             evolutionWorkspace.apply {
//                                 iterateSuspend(iterationsPerRun)
//                             }
//                         }
//                     }
//                 }
//
//                 evalAgent(networkItems1, mouse1)
//                 evalAgent(networkItems2, mouse2)
//
//                 fitness
//             }
//
//             // Called when evolution finishes. evolutionWorkspace is the "winning" sim.
//             onPeek {
//                 workspace.openFromZipData(evolutionWorkspace.zipData)
//                 val worldComponent = workspace.componentList.filterIsInstance<OdorWorldComponent>().first()
//                 val world = worldComponent.world
//                 val newMice = world.entityList.filter { it.entityType == EntityType.MOUSE }
//                 val networkComponents = workspace.componentList.filterIsInstance<NetworkComponent>()
//                 val networks = networkComponents.map { it.network }
//                 val thirstNeurons = networks.map { it.getModelByLabel<Neuron>("Thirst") }
//                 val sharedSet = HashSet<GridCoordinate>()
//                 (newMice zip thirstNeurons).forEach { (newMouse, thirstNeuron) ->
//                     workspace.onReachingWater(sharedSet, thirstNeuron, world, newMouse, { println("hit") }) {
//                         println("bad")
//                     }
//                 }
//             }
//         }
//
//         return evaluator(evolutionarySimulation) {
//             populationSize = 100
//             eliminationRatio = 0.5
//             optimizationMethod = Evaluator.OptimizationMethod.MAXIMIZE_FITNESS
//             runUntil { generation == maxGenerations || fitness > 12 }
//         }
//     }
//
//     scope.launch {
//         workspace.clearWorkspace()
//
//         val progressWindow = if (desktop != null) {
//             ProgressWindow(maxGenerations, "Fitness")
//         } else {
//             null
//         }
//
//         launch(Dispatchers.Default) {
//
//             val generations = createEvolution().start().onEachGenerationBest { agent, gen ->
//                 if (progressWindow == null) {
//                     println("[$gen] Fitness: ${agent.fitness.format(2)}")
//                 } else {
//                     progressWindow.value = gen
//                     progressWindow.text = "Fitness: ${agent.fitness.format(2)}"
//                 }
//             }
//
//             val (best, _) = generations.best
//
//             // println(best)
//
//             val build = best.visibleBuild()
//
//             build.peek()
//
//             progressWindow?.close()
//
//         }
//     }
//
// }
//
// suspend fun main() {
//     evolveMultiAgentResourcePursuer.run()
// }