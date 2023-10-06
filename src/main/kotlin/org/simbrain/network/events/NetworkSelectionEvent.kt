package org.simbrain.network.events

import org.simbrain.network.gui.nodes.ScreenElement
import org.simbrain.util.Events

/**
 * Handles dragging and clicking to select network objects. Can think of this as an internal service  of
 * [NetworkPanel] but leaving it here in the event package anyway.
 *
 * @see [Events]
 */

class NetworkSelectionEvent: Events() {
    val selection = ChangedEvent<Set<ScreenElement>>()
    val sourceSelection = ChangedEvent<Set<ScreenElement>>()
}