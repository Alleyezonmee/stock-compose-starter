package com.soulinc.stockhpcomposestarter.data.widgets

abstract class SparkHomeBaseModel {
    abstract val widget: SparkWidgetModel

    fun getBroadcastIdentifier(segmentId: String, tokenId: String) = "${segmentId}_${tokenId}"
}