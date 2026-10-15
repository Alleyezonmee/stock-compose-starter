package com.soulinc.stockhpcomposestarter

object Utils {
    fun formatLtpChangePercentage(change: String?, changePer: String?): String {
        return "(${getSign(change)}$changePer"
    }

    fun getSign(change: String?): String {
        return when {
            (change?.toDoubleOrNull() ?: 0.0) > 0 -> "+"
            (change?.toDoubleOrNull() ?: 0.0) < 0 -> "-"
            else -> ""
        }
    }
}