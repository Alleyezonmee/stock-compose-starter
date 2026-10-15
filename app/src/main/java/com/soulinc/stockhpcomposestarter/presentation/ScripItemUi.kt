package com.soulinc.stockhpcomposestarter.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.soulinc.stockhpcomposestarter.data.widgets.ScripItemVertical
import com.soulinc.stockhpcomposestarter.ui.theme.StockHpComposeStarterTheme

private val PositiveColor = Color(0xFF1B9E4B)
private val NegativeColor = Color(0xFFD93025)

@Composable
fun ScripItemVerticalUi(item: ScripItemVertical, modifier: Modifier = Modifier) {
    val changeColor = if (item.change >= 0) PositiveColor else NegativeColor

    OutlinedCard(
        modifier = modifier.width(140.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            // No image loader in the project yet, so the icon is a placeholder with the scrip's initial.
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.scripName.take(1),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = item.scripName,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = item.ltp,
                color = changeColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(2.dp))

            Text(
                text = "${"%.2f".format(item.change)} (${item.changePer}%)",
                color = changeColor,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ScripItemVerticalUiPreview() {
    StockHpComposeStarterTheme {
        ScripItemVerticalUi(
            ScripItemVertical(
                id = "1",
                analyticsId = "a1",
                scripName = "Reliance Industries",
                scripIcon = "",
                ltp = "2450.35",
                change = -12.4,
                changePer = "-0.50",
                tokenId = "1000",
                segmentId = "1"
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}
