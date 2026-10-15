package com.soulinc.stockhpcomposestarter.data.widgets

data class SparkFilterTabItemModel(
    override val id: String,
    override val analyticsId: String,
    val name: String,
    val isSelected: Boolean
): SparkWidgetModel
