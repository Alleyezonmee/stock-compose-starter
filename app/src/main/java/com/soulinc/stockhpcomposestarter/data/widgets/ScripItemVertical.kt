package com.soulinc.stockhpcomposestarter.data.widgets

data class ScripItemVertical(
    override val id: String,
    override val analyticsId: String,
    val scripName: String,
    val scripIcon: String,
    val ltp: String,
    val change: Double,
    val changePer: String,
    val tokenId: String,
    val segmentId: String
) : SparkWidgetModel
