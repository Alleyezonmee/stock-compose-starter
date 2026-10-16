package com.soulinc.stockhpcomposestarter.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.soulinc.stockhpcomposestarter.presentation.model.CommonStockWidgetUiModel
import com.soulinc.stockhpcomposestarter.presentation.model.ScripUiModel

@Composable
fun CommonStockWidgetUi(
    widget: CommonStockWidgetUiModel,
    modifier: Modifier = Modifier,
    onViewAllClick: () -> Unit = {}
) {
    Column(modifier = modifier.fillMaxWidth()) {
        WidgetHeader(widget.title, widget.showViewAll, onViewAllClick)
        ScripRow(widget.scrips)
    }
}

/** Title on the left, optional "View All" button on the right. */
@Composable
internal fun WidgetHeader(
    title: String,
    showViewAll: Boolean,
    onViewAllClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        if (showViewAll) {
            TextButton(onClick = onViewAllClick) {
                Text(text = "View All")
            }
        }
    }
}

@Composable
internal fun ScripRow(scrips: List<ScripUiModel>) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = scrips,
            key = { it.id }
        ) { scrip ->
            ScripItemVerticalUi(scrip)
        }
    }
}
