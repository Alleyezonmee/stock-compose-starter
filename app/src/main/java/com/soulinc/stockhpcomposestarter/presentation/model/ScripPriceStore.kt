package com.soulinc.stockhpcomposestarter.presentation.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.soulinc.stockhpcomposestarter.data.broadcast.PriceTickResponse
import com.soulinc.stockhpcomposestarter.data.widgets.ScripItemVertical
import java.util.concurrent.ConcurrentHashMap

/**
 * One [MutableState] per broadcast scrip (`segmentId_tokenId`). Every card showing the same scrip
 * shares the holder, and [update] only writes when the price really changed, so unchanged scrips
 * never invalidate anything. Not thread-safe: use from a single thread (the view model's).
 */
class ScripPriceStore {

    private val holders = HashMap<String, MutableState<PriceUiModel>>()

    fun holderFor(item: ScripItemVertical): MutableState<PriceUiModel> =
        holders.getOrPut(keyOf(item)) { mutableStateOf(item.toPriceUiModel()) }

    fun update(item: ScripItemVertical) {
        val holder = holders[keyOf(item)] ?: return
        val price = item.toPriceUiModel()
        if (holder.value != price) holder.value = price
    }

    /**
     * Refreshes every held scrip from [broadcastCache], the latest tick per `segmentId_tokenId`.
     * Only holders whose price actually changed are written, so unchanged scrips recompose nothing.
     */
    fun update(broadcastCache: ConcurrentHashMap<String, PriceTickResponse>) {
        holders.forEach { (key, holder) ->
            val tick = broadcastCache[key] ?: return@forEach
            val price = tick.toPriceUiModel()
            if (holder.value != price) holder.value = price
        }
    }

    fun clear() = holders.clear()

    // Same format as SparkHomeBaseModel.getBroadcastIdentifier.
    private fun keyOf(item: ScripItemVertical) = "${item.segmentId}_${item.tokenId}"
}
