package com.soulinc.stockhpcomposestarter.data.widgets

data class SparkFilterTabModel(
    override val id: String,
    override val analyticsId: String,
    val list: List<SparkFilterTabItemModel>
): SparkWidgetModel
