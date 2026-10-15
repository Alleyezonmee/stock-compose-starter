package com.soulinc.stockhpcomposestarter.data.broadcast

data class PriceTickResponse(
    val lastPrice: String,
    val changePer: String,
    val change: String,
    val segmentId: String,
    val tokenId: String
)
