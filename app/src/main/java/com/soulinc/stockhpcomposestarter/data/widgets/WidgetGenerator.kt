package com.soulinc.stockhpcomposestarter.data.widgets

import com.soulinc.stockhpcomposestarter.data.broadcast.BroadcastRequest
import kotlin.random.Random

class WidgetGenerator {

    private val scripNames = listOf(
        "Reliance Industries", "TCS", "HDFC Bank", "Infosys", "ICICI Bank",
        "Hindustan Unilever", "ITC", "State Bank of India", "Bharti Airtel", "Kotak Mahindra Bank",
        "Larsen & Toubro", "Axis Bank", "Asian Paints", "Maruti Suzuki", "Bajaj Finance",
        "Sun Pharma", "Titan Company", "HCL Technologies", "UltraTech Cement", "Wipro",
        "Nestle India", "NTPC", "Power Grid", "Tata Motors", "Tata Steel",
        "JSW Steel", "Mahindra & Mahindra", "Tech Mahindra", "Bajaj Finserv", "Adani Enterprises",
        "Adani Ports", "Coal India", "ONGC", "IndusInd Bank", "Grasim Industries",
        "Cipla", "Dr Reddy's", "Divi's Laboratories", "Eicher Motors", "Hero MotoCorp",
        "Britannia", "Apollo Hospitals", "Hindalco", "BPCL", "SBI Life Insurance",
        "HDFC Life", "Tata Consumer", "UPL", "Shree Cement", "Zomato"
    )

    private val scripItems: List<ScripItemVertical> = createScripItems()

    /**
     * Builds a [SparkHomeCommonStockWidgetModel] holding 4 random items picked from the 50 scrips.
     */
    fun createCommonStockWidget(id: Int, name: String): SparkHomeBaseModel {
        val stocks = scripItems.shuffled().take(WIDGET_ITEM_COUNT)
        return SparkHomeCommonStockWidgetModel(
            widget = CommonStockWidget(
                id = name,
                analyticsId = "analytics_$name",
                title = "Widget Name $id",
                showViewAll = true,
                stocks = stocks
            ),
            broadcastRequest = stocks.map {
                BroadcastRequest(
                    segmentId = it.segmentId,
                    tokenId = it.tokenId
                )
            }
        )
    }

    fun createCommonStockWidgetWithoutBroadcast(id: Int, name: String): SparkHomeBaseModel {
        val stocks = scripItems.shuffled().take(WIDGET_ITEM_COUNT)
        return SparkHomeCommonStockWithoutBroadcastWiget(
            widget = CommonStockWidget(
                id = name,
                analyticsId = "analytics_$name",
                title = "Widget Name $id",
                showViewAll = true,
                stocks = stocks
            )
        )
    }

    fun createCommonStockWithFilterWidget(id: Int, name: String): SparkHomeBaseModel {
        val stocks = scripItems.shuffled().take(WIDGET_ITEM_COUNT)
        return SparkHomeCommonStockWithFilterWidgetModel(
            widget = CommonStockWithFilterWidget(
                id = name,
                analyticsId = "analytics_$name",
                title = "Widget Name $id",
                showViewAll = true,
                stockList = stocks,
                tabs = SparkFilterTabModel(
                    id = "filter_$name",
                    analyticsId = "",
                    list = listOf(
                        SparkFilterTabItemModel(
                            "filter_id_1",
                            "analytics_filter1",
                            "Filter 1",
                            true
                        ),
                        SparkFilterTabItemModel(
                            "filter_id_2",
                            "analytics_filter2",
                            "Filter 2",
                            true
                        ),
                        SparkFilterTabItemModel(
                            "filter_id_3",
                            "analytics_filter3",
                            "Filter 3",
                            true
                        )
                    )
                )
            ),
            broadcastRequest = stocks.map {
                BroadcastRequest(
                    segmentId = it.segmentId,
                    tokenId = it.tokenId
                )
            }
        )
    }

    private fun createScripItems(): List<ScripItemVertical> {
        val random = Random(SEED)
        return scripNames.mapIndexed { index, scripName ->
            val ltp = random.nextDouble(100.0, 5000.0)
            val change = random.nextDouble(-50.0, 50.0)
            ScripItemVertical(
                id = "ScripItem$index",
                analyticsId = "scrip_analytics_$index",
                scripName = scripName,
                scripIcon = "https://example.com/icons/scrip_$index.png",
                ltp = "%.2f".format(ltp),
                change = change,
                changePer = "%.2f".format(change / ltp * 100),
                tokenId = (1000 + index).toString(),
                segmentId = "1"
            )
        }
    }

    private companion object {
        const val WIDGET_ITEM_COUNT = 5
        const val SEED = 42
    }
}
