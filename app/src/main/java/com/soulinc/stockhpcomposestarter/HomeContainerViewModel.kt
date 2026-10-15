package com.soulinc.stockhpcomposestarter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soulinc.stockhpcomposestarter.data.broadcast.BroadcastMimic
import com.soulinc.stockhpcomposestarter.data.broadcast.Broadcastable
import com.soulinc.stockhpcomposestarter.data.broadcast.PriceTickResponse
import com.soulinc.stockhpcomposestarter.data.widgets.SparkHomeBaseModel
import com.soulinc.stockhpcomposestarter.data.widgets.WidgetGenerator
import com.soulinc.stockhpcomposestarter.presentation.model.HomeItemUiModel
import com.soulinc.stockhpcomposestarter.presentation.model.ScripPriceStore
import com.soulinc.stockhpcomposestarter.presentation.model.scripItems
import com.soulinc.stockhpcomposestarter.presentation.model.toUiModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale


class HomeContainerViewModel: ViewModel() {

    private val widgetGenerator = WidgetGenerator()
    private val broadcastMimic = BroadcastMimic()
    private val priceStore = ScripPriceStore()
    private var broadcastJob: Job? = null

    private var _currentList = MutableStateFlow<List<SparkHomeBaseModel>>(emptyList())
    val currentList = _currentList.asStateFlow()

    // What the UI collects. Emitted when the widgets load, not on ticks: prices flow through priceStore.
    private val _uiList = MutableStateFlow<List<HomeItemUiModel>>(emptyList())
    val uiList = _uiList.asStateFlow()

    init {
        loadWidgets()
    }

    private fun loadWidgets() {
        _currentList.value = List(WIDGET_COUNT) { index ->
            if (index % 3 != 0) {
                widgetGenerator.createCommonStockWidget(index, "common_stock_$index")
            } else {
                widgetGenerator.createCommonStockWidgetWithoutBroadcast(index, "common_stock_$index")
            }
        }
        priceStore.clear()
        _uiList.value = _currentList.value.mapNotNull { it.toUiModel(priceStore) }
        startBroadcast()
    }

    /**
     * Subscribes to the mimic feed for every scrip currently on screen. Each tick snapshot is
     * applied to the list through [Broadcastable.parseBroadcast], and the scrips whose price
     * changed are pushed into [priceStore] so only their price text recomposes.
     */
    private fun startBroadcast() {
        broadcastJob?.cancel()
        val broadcastRequestList = buildBroadcastRequestList(_currentList.value)
        broadcastJob = viewModelScope.launch {
            broadcastMimic.ticks(broadcastRequestList).collect { ticks ->
                val updated = _currentList.value.map { model ->
                    if (model is Broadcastable) model.parseBroadcast(model, ticks) else model
                }
                _currentList.value = updated
                // Flatten to unique scrips so a scrip shown in several widgets is updated once.
                updated
                    .filter { it is Broadcastable }
                    .flatMap { it.scripItems() }
                    .distinctBy { it.segmentId to it.tokenId }
                    .forEach(priceStore::update)
            }
        }
    }

    private fun buildBroadcastRequestList(models: List<SparkHomeBaseModel>): List<PriceTickResponse> {
        return models
            .filter { it is Broadcastable }
            .flatMap { it.scripItems() }
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
    }

    private companion object {
        const val WIDGET_COUNT = 20
    }
}
