package com.soulinc.stockhpcomposestarter.data.widgets

data class CommonStockWithFilterWidget(
    override val id: String,
    override val analyticsId: String,
    val title: String,
    val stockList: List<SparkWidgetModel>,
    val tabs: SparkFilterTabModel,
    val showViewAll: Boolean
): SparkWidgetModel
