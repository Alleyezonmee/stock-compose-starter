package com.soulinc.stockhpcomposestarter.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.soulinc.stockhpcomposestarter.HomeContainerViewModel
import com.soulinc.stockhpcomposestarter.data.widgets.SparkHomeCommonStockWidgetModel

@Composable
fun StockListScreen(vm: HomeContainerViewModel, modifier: Modifier = Modifier) {
    val list by vm.currentList.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        items(items = list, key = { it.widget.id }) { item ->
            when (item) {
                is SparkHomeCommonStockWidgetModel -> CommonStockWidgetUi(item.widget)
                else -> Unit
            }
        }
    }
}
