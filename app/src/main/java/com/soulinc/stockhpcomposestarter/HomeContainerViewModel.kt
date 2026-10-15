package com.soulinc.stockhpcomposestarter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soulinc.stockhpcomposestarter.data.broadcast.BroadcastMimic
import com.soulinc.stockhpcomposestarter.data.broadcast.Broadcastable
import com.soulinc.stockhpcomposestarter.data.broadcast.PriceTickResponse
import com.soulinc.stockhpcomposestarter.data.widgets.ScripItemVertical
import com.soulinc.stockhpcomposestarter.data.widgets.SparkHomeBaseModel
import com.soulinc.stockhpcomposestarter.data.widgets.SparkHomeCommonStockWidgetModel
import com.soulinc.stockhpcomposestarter.data.widgets.SparkHomeCommonStockWithFilterWidgetModel
import com.soulinc.stockhpcomposestarter.data.widgets.WidgetGenerator
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale


class HomeContainerViewModel: ViewModel() {

    private val widgetGenerator = WidgetGenerator()
    private val broadcastMimic = BroadcastMimic()
    private var broadcastJob: Job? = null

    private var _currentList = MutableStateFlow<List<SparkHomeBaseModel>>(emptyList())
    val currentList = _currentList.asStateFlow()

    init {
        loadWidgets()
    }

    private fun loadWidgets() {
        _currentList.value = List(WIDGET_COUNT) { index ->
            if (index % 2 == 0) {
                widgetGenerator.createCommonStockWidget(index, "common_stock_$index")
            } else {
                widgetGenerator.createCommonStockWidgetWithoutBroadcast(index, "common_stock_$index")
            }
        }
        startBroadcast()
    }

    /**
     * Subscribes to the mimic feed for every scrip currently on screen. Each tick snapshot is
     * applied to the list through [Broadcastable.parseBroadcast] and the updated list is emitted.
     */
    private fun startBroadcast() {
        broadcastJob?.cancel()
        val broadcastRequestList = buildBroadcastRequestList(_currentList.value)
        broadcastJob = viewModelScope.launch {
            broadcastMimic.ticks(broadcastRequestList).collect { ticks ->
                _currentList.update { models ->
                    models.map { model ->
                        if (model is Broadcastable) model.parseBroadcast(model, ticks) else model
                    }
                }
            }
        }
    }

    private fun buildBroadcastRequestList(models: List<SparkHomeBaseModel>): List<PriceTickResponse> {
        return models
            .filter { it is Broadcastable }
            .flatMap { model ->
                when (model) {
                    is SparkHomeCommonStockWidgetModel -> model.widget.stocks
                    is SparkHomeCommonStockWithFilterWidgetModel -> model.widget.stockList
                    else -> emptyList()
                }
            }
            .filterIsInstance<ScripItemVertical>()
            .distinctBy { it.segmentId to it.tokenId }
            .map {
                PriceTickResponse(
                    lastPrice = it.ltp,
                    changePer = it.changePer,
                    change = String.format(Locale.US, "%.2f", it.change),
                    segmentId = it.segmentId,
                    tokenId = it.tokenId
                )
            }
            .toList()
    }

    private companion object {
        const val WIDGET_COUNT = 3
    }
}
