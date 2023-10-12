package org.simbrain.util.geneticalgorithm

import org.simbrain.util.propertyeditor.CopyableObject

/**
 * The "gene product" that we are evolving. Must be a [CopyableObject].
 * Must take some mutable var property (here value) so that something can happen during mutation.
 */
class IntWrapper(var value: Int = 0) : CopyableObject {
    override fun toString(): String {
        return "" + value
    }

    override fun copy(): IntWrapper {
        return IntWrapper(value)
    }
}
//
// /**
//  * Builder function for [IntGene].
//  */
// inline fun intGene(initVal: IntWrapper.() -> Unit = { }): IntGene {
//     return IntGene(IntWrapper().apply(initVal))
// }
//
// /**
//  * The integer gene, which takes a template "product" ([IntWrapper]) as an argument and extends
//  * [Gene] and implements [TopLevelGene], which allows you to add this gene directly into an onBuild context.
//  * function.
//  */
// class IntGene(private val template: IntWrapper) : Gene<Int>(), TopLevelGene<Int> {
//
//     override val product = CompletableDeferred<Int>()
//
//     override fun copy(): IntGene {
//         return IntGene(template.copy())
//     }
//
//     override suspend fun TopLevelBuilderContext.build(): Int {
//         template.copy().also { product.complete(it.value) }
//         return product.await()
//     }
//
//     fun mutate(block: IntWrapper.() -> Unit) {
//         template.apply(block)
//     }
// }
//
// /**
//  * The main integer evolution simulation.
//  */
// fun main() {
//
//     val evolutionarySimulation = evolutionarySimulation {
//
//         /**
//          * Set number of integers per chromosome here
//          */
//         val intChromosome = chromosome(5) {
//             intGene { value = 1 }
//         }
//
//         onMutate {
//             intChromosome.forEach {
//                 it.mutate {
//                     value += random.nextInt(-5,5)
//                 }
//             }
//         }
//
//         onBuild {
//             +intChromosome
//         }
//
//         onEval {
//             val total = intChromosome.map { it.product.await() }.sumByDouble { it.toDouble() }
//             val targetSum = 10
//             abs(total - targetSum)
//         }
//
//         onPeek {
//             print("Integer genes:")
//             println(intChromosome.map { it.product.await() }.joinToString(", "))
//         }
//
//     }
//
//     val evolution = evaluator(evolutionarySimulation) {
//         populationSize = 100
//         eliminationRatio = 0.5
//         optimizationMethod = Evaluator.OptimizationMethod.MINIMIZE_FITNESS
//         runUntil { generation == 10000 || fitness < .2 }
//     }
//
//     val time = measureTimeMillis {
//         val (builder, fitness) = evolution.start().best
//         runBlocking { builder.build().peek() }
//         println("Fitness: $fitness")
//     }
//
//     println("Finished in ${time / 1000.0}s")
//
// }




