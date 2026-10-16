package com.soulinc.stockhpcomposestarter.presentation.model

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.soulinc.stockhpcomposestarter.data.widgets.CommonStockWidget
import com.soulinc.stockhpcomposestarter.data.widgets.CommonStockWithFilterWidget
import com.soulinc.stockhpcomposestarter.data.widgets.ScripItemVertical
import com.soulinc.stockhpcomposestarter.data.widgets.SparkHomeBaseModel
import com.soulinc.stockhpcomposestarter.data.widgets.SparkHomeCommonStockWidgetModel
import com.soulinc.stockhpcomposestarter.data.widgets.SparkHomeCommonStockWithFilterWidgetModel
import com.soulinc.stockhpcomposestarter.data.widgets.SparkHomeCommonStockWithoutBroadcastWiget
import com.soulinc.stockhpcomposestarter.data.widgets.SparkWidgetModel

/** Every scrip held by this model, whatever the widget type. */
fun SparkHomeBaseModel.scripItems(): List<ScripItemVertical> = when (this) {
    is SparkHomeCommonStockWidgetModel -> widget.stocks
    is SparkHomeCommonStockWithFilterWidgetModel -> widget.stockList
    is SparkHomeCommonStockWithoutBroadcastWiget -> widget.stocks
    else -> emptyList()
}.filterIsInstance<ScripItemVertical>()

/** Returns null for widget types that have no UI yet. */
fun SparkHomeBaseModel.toUiModel(priceStore: ScripPriceStore): HomeItemUiModel? = when (this) {
    is SparkHomeCommonStockWidgetModel -> widget.toUiModel(priceStore::holderFor)
    is SparkHomeCommonStockWithFilterWidgetModel -> widget.toUiModel(priceStore::holderFor)
    // Not subscribed to the feed, so its prices get their own state that is never updated.
    is SparkHomeCommonStockWithoutBroadcastWiget -> widget.toUiModel { mutableStateOf(it.toPriceUiModel()) }
    else -> null
}

fun ScripItemVertical.toPriceUiModel() = PriceUiModel(
    ltp = ltp,
    changeText = "${"%.2f".format(change)} ($changePer%)",
    isPositive = change >= 0
)

private fun CommonStockWidget.toUiModel(
    priceOf: (ScripItemVertical) -> State<PriceUiModel>
) = CommonStockWidgetUiModel(
    id = id,
    title = title,
    showViewAll = showViewAll,
    scrips = stocks.toScripUiModels(priceOf)
)

private fun CommonStockWithFilterWidget.toUiModel(
    priceOf: (ScripItemVertical) -> State<PriceUiModel>
) = CommonStockWithFilterWidgetUiModel(
    id = id,
    title = title,
    showViewAll = showViewAll,
    tabs = tabs.list.map { FilterTabUiModel(id = it.id, name = it.name, isSelected = it.isSelected) },
    scrips = stockList.toScripUiModels(priceOf)
)

private fun List<SparkWidgetModel>.toScripUiModels(
    priceOf: (ScripItemVertical) -> State<PriceUiModel>
) = filterIsInstance<ScripItemVertical>().map {
    ScripUiModel(id = it.id, name = it.scripName, icon = it.scripIcon, price = priceOf(it))
}
