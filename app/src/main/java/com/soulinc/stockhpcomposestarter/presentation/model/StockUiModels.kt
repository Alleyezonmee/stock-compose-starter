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
