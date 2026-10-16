package com.soulinc.stockhpcomposestarter.presentation.model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State

/** The part of a scrip that changes on every tick. Pre-formatted so composables only draw it. */
@Immutable
data class PriceUiModel(
    val ltp: String,
    val changeText: String,
    val isPositive: Boolean
)

/**
 * Static scrip details plus a [State] holding its live price. The price is only read inside the
 * price composable, so a tick invalidates just that scope and not the card or the widget.
 */
@Stable
data class ScripUiModel(
    val id: String,
    val name: String,
    val icon: String,
    val price: State<PriceUiModel>
)

sealed interface HomeItemUiModel {
    val id: String
}

@Stable
data class CommonStockWidgetUiModel(
    override val id: String,
    val title: String,
    val showViewAll: Boolean,
    val scrips: List<ScripUiModel>
) : HomeItemUiModel

@Immutable
data class FilterTabUiModel(
    val id: String,
    val name: String,
    val isSelected: Boolean
)

/** Replaced in the UI list as a whole when a filter is picked, since its scrips change. */
@Stable
data class CommonStockWithFilterWidgetUiModel(
    override val id: String,
    val title: String,
    val showViewAll: Boolean,
    val tabs: List<FilterTabUiModel>,
    val scrips: List<ScripUiModel>
) : HomeItemUiModel
