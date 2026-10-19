package com.soulinc.stockhpcomposestarter.data.broadcast

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.round
import kotlin.random.Random

/**
 * Mimics a live price feed. Starting from the [PriceTickResponse]s it is given, it repeatedly
 * moves the price of a random subset of scrips and emits a snapshot of every tick.
 *
 * Keys follow the same `segmentId_tokenId` format as `SparkHomeBaseModel.getBroadcastIdentifier`.
 */
class BroadcastMimic(private val random: Random = Random.Default) {

    /**
     * Cold flow: every collection starts a fresh feed from [initialTicks]; cancelling the
     * collector stops it. Each emission is a snapshot of the scrips that changed on that tick,
     * which the consumer merges into its own broadcast cache.
     */
    fun ticks(initialTicks: List<PriceTickResponse>): Flow<ConcurrentHashMap<String, PriceTickResponse>> = flow {
        // Previous close is fixed for the "day": change = ltp - prevClose.
        val prevCloses = HashMap<String, Double>()
        val ltps = HashMap<String, Double>()
        val ticks = ConcurrentHashMap<String, PriceTickResponse>()

        initialTicks.forEach { tick ->
            val ltp = tick.lastPrice.toDoubleOrNull() ?: return@forEach
            val change = tick.change.toDoubleOrNull() ?: return@forEach
            val prevClose = ltp - change
            if (prevClose <= 0.0) return@forEach
            val key = keyOf(tick.segmentId, tick.tokenId)
            prevCloses[key] = prevClose
            ltps[key] = ltp
            ticks[key] = tick
        }
        if (ticks.isEmpty()) return@flow

        val keys = ticks.keys.toList()
        while (true) {
            delay(random.nextLong(MIN_DELAY_MS, MAX_DELAY_MS))

            val updateCount = random.nextInt(1, maxOf(2, keys.size * UPDATE_FRACTION_PERCENT / 100 + 1))
            keys.shuffled(random).take(updateCount).forEach { key ->
                val prevClose = prevCloses.getValue(key)
                val movePercent = random.nextDouble(-MAX_MOVE_PERCENT, MAX_MOVE_PERCENT)
                val newLtp = round2((ltps.getValue(key) * (1 + movePercent / 100)).coerceAtLeast(MIN_PRICE))
                ltps[key] = newLtp

                // Derive change and changePer from the rounded ltp so the three values always agree.
                val change = round2(newLtp - prevClose)
                val changePer = round2(change / prevClose * 100)
                val old = ticks.getValue(key)
                ticks[key] = old.copy(
                    lastPrice = format(newLtp),
                    change = format(change),
                    changePer = format(changePer)
                )
            }

            // Hand out a copy so consumers never see the map mutate under them.
            emit(ConcurrentHashMap(ticks))
        }
    }

    private fun keyOf(segmentId: String, tokenId: String) = "${segmentId}_${tokenId}"

    private fun round2(value: Double) = round(value * 100) / 100

    private fun format(value: Double) = String.format(Locale.US, "%.2f", value)

    private companion object {
        const val MIN_DELAY_MS = 500L
        const val MAX_DELAY_MS = 1500L
        const val UPDATE_FRACTION_PERCENT = 30
        const val MAX_MOVE_PERCENT = 0.5
        const val MIN_PRICE = 0.05
    }
}
