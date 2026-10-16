package com.soulinc.stockhpcomposestarter.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.soulinc.stockhpcomposestarter.presentation.model.CommonStockWithFilterWidgetUiModel
import com.soulinc.stockhpcomposestarter.presentation.model.FilterTabUiModel

private val SelectedOutline = Color(0xFF1A73E8)
private val UnselectedOutline = Color(0xFFBDBDBD)

@Composable
fun CommonStockWithFilterWidgetUi(
    widget: CommonStockWithFilterWidgetUiModel,
    onFilterClick: (tabId: String) -> Unit,
    modifier: Modifier = Modifier,
    onViewAllClick: () -> Unit = {}
) {
    Column(modifier = modifier.fillMaxWidth()) {
        WidgetHeader(widget.title, widget.showViewAll, onViewAllClick)

        Spacer(Modifier.height(4.dp))

        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            widget.tabs.forEach { tab ->
                FilterCapsule(tab = tab, onClick = { onFilterClick(tab.id) })
            }
        }

        Spacer(Modifier.height(12.dp))

        ScripRow(widget.scrips)
    }
}

@Composable
private fun FilterCapsule(tab: FilterTabUiModel, onClick: () -> Unit) {
    val outline = if (tab.isSelected) SelectedOutline else UnselectedOutline

    Text(
        text = tab.name,
        style = MaterialTheme.typography.labelLarge,
        color = if (tab.isSelected) SelectedOutline else MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .clip(CircleShape)
            .border(BorderStroke(1.dp, outline), CircleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 6.dp)
    )
}
