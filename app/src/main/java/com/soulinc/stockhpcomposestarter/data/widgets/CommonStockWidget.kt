package com.soulinc.stockhpcomposestarter.data.widgets

data class CommonStockWidget(
    override val id: String,
    override val analyticsId: String,
    val title: String,
    val showViewAll: Boolean,
    val stocks: List<SparkWidgetModel>
): SparkWidgetModel
