package com.soulinc.stockhpcomposestarter.data.broadcast

import com.soulinc.stockhpcomposestarter.data.widgets.SparkHomeBaseModel
import java.util.concurrent.ConcurrentHashMap

interface Broadcastable {
    val broadcastRequest: List<BroadcastRequest>

    fun parseBroadcast(
        model: SparkHomeBaseModel,
        broadcastResponse: ConcurrentHashMap<String, PriceTickResponse>
    ): SparkHomeBaseModel {
        return model
    }
}