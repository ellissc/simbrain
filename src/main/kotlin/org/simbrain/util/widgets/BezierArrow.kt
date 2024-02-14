package org.simbrain.util.widgets

import org.piccolo2d.PNode
import org.piccolo2d.nodes.PArea
import org.piccolo2d.nodes.PPath
import org.simbrain.util.*
import java.awt.BasicStroke
import java.awt.Color
import java.awt.geom.CubicCurve2D
import java.awt.geom.Line2D

/**
 * Represents a Bezier curved with a single arrow at its end. Where it is located on its
 * source can be set, but it's target location is automatically determined.
 *
 * Terminology: a source and target [LocatableModel] are connected by a directed bezier
 * curve with a tail and tip.
 *
 * @param thickness thickness of the arrow in pixels
 * @param color color the arrow
 * @param lateralOffset where on the source edge the tail of the arrow is located. Starting is 0 and end is 1.
 *
 * @author Zoë Tosi
 * @author Leo Yulin Li
 */
class BezierArrow(template: BezierArrowTemplate) : PNode() {

    private val thickness = template.thickness
    private val color: Color = template.color
    private val headPadding = template.padding.head
    private val tailPadding = template.padding.tail
    private val _lateralOffset = template.lateralOffset
    private val lateralOffset get() = _lateralOffset()
    private val updateEvent = template.updateEvent

    /**
     * The triangle at the tip of the arrow. This triangle is constructed only once, and during [layout] the this
     * triangle will be placed onto the correct location
     */
    private val arrowTip = listOf(point(0.0, -sin60deg), point(0.5, 0.0), point(-0.5, 0.0))
            .map { it * (thickness * 2.0) }.toPolygon()
            .let { polygon -> PArea(polygon, null) }
            .apply {
                paint = color
                transparency = 0.5f
            }

    /**
     * Update the shape of the arrow base on the outlines of source and target.
     *
     * @return the updated curve model
     */
    fun layout(sourceOutlines: RectangleSides, targetOutlines: RectangleSides, bidirectional: Boolean) {

        // 0. clear old arrow
        removeAllChildren()

        // 1. for each source and target, find a side of the outline to let the arrow connect
        val (deltaVector, sourceSide, targetSide) = (sourceOutlines.toList() cartesianProduct targetOutlines.toList())
                .map { (source, target) -> Triple(target.headOffset - source.tailOffset, source, target) }
                .filter { (line, source, target) ->
                    // make sure the curve does not bend backward
                    line.norm dot source.unitNormal > 0.3 && line.norm dot target.unitNormal < -0.3
                }
                .let {
                    if (bidirectional) {
                        it.maxByOrNull { (line, source, _) -> line dot source.normal }
                    } else {
                        it.minByOrNull { (_, source, target) -> source.midPoint distanceSqTo target.midPoint }
                    }
                }.also { if (it == null) updateEvent(null) } ?: return

        // 2. put the arrow tip at the right location and orient it at the right angle
        arrowTip.getTransformReference(true).apply {
            setToTranslation(targetSide.headOffset.x, targetSide.headOffset.y)
            val theta = targetSide.normalTheta
            rotate(theta)
        }
        arrowTip.invalidateFullBounds();

        // 3. compute the curve
        val curveModel = cubicBezier(
                sourceSide.tailOffset,
                sourceSide.tailOffset + deltaVector.abs * sourceSide.unitNormal * 0.5,
                targetSide.headOffset + deltaVector.abs * targetSide.unitNormal * 0.5,
                targetSide.headOffset
        )

        // 4. create the curve PNode
        val curveView = PPath.Double(curveModel, BasicStroke(thickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER)).apply {
            paint = null
            strokePaint = color
            transparency = 0.5f
        }

        // 5. add shape to node
        addChild(curveView)
        addChild(arrowTip)

        // 6. call back
        updateEvent(curveModel)

    }

    /**
     * Given a side of a rectangle bound, find the location of where an arrow tail would go.
     */
    private val Line2D.tailOffset
        get() = p(lateralOffset) + (unitNormal * tailPadding)

    /**
     * Given a side of a rectangle bound, find the location of where an arrow head would go.
     */
    private val Line2D.headOffset
        get() = p(1 - lateralOffset) + (unitNormal * headPadding)
}

@DslMarker
annotation class BezierArrowMaker

@BezierArrowMaker
class BezierArrowTemplate {

    // Default color for all arrows
    var color: Color = Color.GREEN

    var thickness = 20.0f

    val padding = PaddingBuilder()

    var updateEvent: (CubicCurve2D?) -> Unit = {}
        private set

    var lateralOffset: () -> Double = { 0.5 }
        private set

    @BezierArrowMaker
    inner class PaddingBuilder {
        operator fun invoke(init: PaddingBuilder.() -> Unit) {
            init()
        }

        val default get() = 35.0
        val arrowSize get() = this@BezierArrowTemplate.thickness * 2 * sin60deg
        val defaultHead get() = default + arrowSize
        val defaultTail get() = default
        var head = defaultHead
        var tail = defaultTail
    }

    fun lateralOffset(block: () -> Double) {
        lateralOffset = block
    }

    fun onUpdated(block: (CubicCurve2D?) -> Unit) {
        updateEvent = block
    }
}

/**
 * Builder function for a bezier arrow
 */
fun bezierArrow(init: BezierArrowTemplate.() -> Unit) = BezierArrow(BezierArrowTemplate().apply(init))