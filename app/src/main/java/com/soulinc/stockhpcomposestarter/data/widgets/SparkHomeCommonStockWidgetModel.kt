package com.soulinc.stockhpcomposestarter.data.widgets

import com.soulinc.stockhpcomposestarter.data.broadcast.BroadcastRequest
import com.soulinc.stockhpcomposestarter.data.broadcast.Broadcastable
import com.soulinc.stockhpcomposestarter.data.broadcast.PriceTickResponse
import java.util.concurrent.ConcurrentHashMap

data class SparkHomeCommonStockWidgetModel(
    override val widget: CommonStockWidget,
    override val broadcastRequest: List<BroadcastRequest>
): SparkHomeBaseModel(), Broadcastable {
    override fun parseBroadcast(
        model: SparkHomeBaseModel,
        broadcastResponse: ConcurrentHashMap<String, PriceTickResponse>
    ): SparkHomeBaseModel {
        return this.copy(
            widget = this.widget.copy(
                stocks = this.widget.stocks.map {
                    when (it) {
                        is ScripItemVertical -> {
                            val tickCache = broadcastResponse[getBroadcastIdentifier(it.segmentId, it.tokenId)]
                            if (tickCache == null) it else it.copy(
                                ltp = tickCache.lastPrice,
                                change = tickCache.change.toDoubleOrNull() ?: it.change,
                                changePer = tickCache.changePer
                            )
                        }
                        else -> it
                    }
                }
            )
        )
    }
}
