package org.simbrain.network.updaterules.interfaces

import org.simbrain.util.UserParameter

/**
 * An interface for updates rules that make use of an upper and lower bound,
 * either for clipping or for setting intrinsic bounds.
 *
 * @author Zoë Tosi
 * @author Jeff Yoshimi
 */
interface BoundedUpdateRule {
    /**
     * Sets the upper bound of this neuron update rule's activation.
     */
    @UserParameter(
        label = "Upper Bound",
        description = "Upper bound that determines the maximum level of activity of a node.",
        order = -20
    )
    var upperBound: Double

    /**
     * Sets the lower bound of this neuron update rule's activation.
     */
    @UserParameter(
        label = "Lower Bound",
        description = "Lower bound that determines the minimum level of activity of a node.",
        order = -10
    )
    var lowerBound: Double

}