package com.soulinc.stockhpcomposestarter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soulinc.stockhpcomposestarter.data.broadcast.BroadcastMimic
import com.soulinc.stockhpcomposestarter.data.broadcast.BroadcastRequest
import com.soulinc.stockhpcomposestarter.data.broadcast.Broadcastable
import com.soulinc.stockhpcomposestarter.data.broadcast.PriceTickResponse
import com.soulinc.stockhpcomposestarter.data.widgets.SparkHomeBaseModel
import com.soulinc.stockhpcomposestarter.data.widgets.SparkHomeCommonStockWithFilterWidgetModel
import com.soulinc.stockhpcomposestarter.data.widgets.WidgetGenerator
import com.soulinc.stockhpcomposestarter.presentation.model.HomeItemUiModel
import com.soulinc.stockhpcomposestarter.presentation.model.ScripPriceStore
import com.soulinc.stockhpcomposestarter.presentation.model.scripItems
import com.soulinc.stockhpcomposestarter.presentation.model.toUiModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap


class HomeContainerViewModel: ViewModel() {

    private val widgetGenerator = WidgetGenerator()
    private val broadcastMimic = BroadcastMimic()
    private val priceStore = ScripPriceStore()
    private var broadcastJob: Job? = null

    // Single live price cache (latest tick per `segmentId_tokenId`). Every tick from the mimic is
    // merged into it, and the price store updates itself from it.
    private val broadcastCache = ConcurrentHashMap<String, PriceTickResponse>()

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
            when (index % 3) {
                0 -> widgetGenerator.createCommonStockWidgetWithoutBroadcast(index, "common_stock_$index")
                1 -> widgetGenerator.createCommonStockWidget(index, "common_stock_$index")
                else -> widgetGenerator.createCommonStockWithFilterWidget(index, "common_stock_filter_$index")
            }
        }
        priceStore.clear()
        _uiList.value = _currentList.value.mapNotNull { it.toUiModel(priceStore) }
        startBroadcast()
    }

    /**
     * Selects [tabId] on the filter widget [widgetId] and swaps in a fresh set of scrips. Only that
     * widget's item changes in [uiList]; the new scrips are then subscribed to the feed.
     */
    fun onFilterSelected(widgetId: String, tabId: String) {
        val model = _currentList.value.firstOrNull { it.widget.id == widgetId }
                as? SparkHomeCommonStockWithFilterWidgetModel ?: return
        val tab = model.widget.tabs.list.firstOrNull { it.id == tabId } ?: return
        if (tab.isSelected) return

        val scrips = widgetGenerator.randomScrips()
        val updated = model.copy(
            widget = model.widget.copy(
                stockList = scrips,
                tabs = model.widget.tabs.copy(
                    list = model.widget.tabs.list.map { it.copy(isSelected = it.id == tabId) }
                )
            ),
            broadcastRequest = scrips.map { BroadcastRequest(segmentId = it.segmentId, tokenId = it.tokenId) }
        )

        _currentList.update { list -> list.map { if (it.widget.id == widgetId) updated else it } }
        val updatedUi = updated.toUiModel(priceStore) ?: return
        _uiList.update { list -> list.map { if (it.id == widgetId) updatedUi else it } }
        // The feed was seeded with the old scrips; restart it so the new ones receive ticks too.
        startBroadcast()
    }

    /**
     * Subscribes to the mimic feed for every scrip currently on screen. Each tick snapshot is
     * merged into [broadcastCache], and [priceStore] then updates itself from that cache so only
     * the scrips whose price text changed recompose.
     */
    private fun startBroadcast() {
        broadcastJob?.cancel()
        val broadcastRequestList = buildBroadcastRequestList(_currentList.value)
        broadcastCache.clear()
        broadcastJob = viewModelScope.launch {
            broadcastMimic.ticks(broadcastRequestList).collect { ticks ->
                // Everything coming from the mimic updates the single broadcast cache,
                broadcastCache.putAll(ticks)
                // then the price store updates itself from that cache.
                priceStore.update(broadcastCache)
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
