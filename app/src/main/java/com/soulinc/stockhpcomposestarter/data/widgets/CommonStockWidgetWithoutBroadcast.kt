package com.soulinc.stockhpcomposestarter.data.widgets

data class CommonStockWidgetWithoutBroadcast(
    override val id: String,
    override val analyticsId: String,
    val title: String,
    val stocks: List<SparkWidgetModel>,
    val showViewAll: Boolean
): SparkWidgetModel
