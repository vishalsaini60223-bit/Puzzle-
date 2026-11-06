package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransactionRecord
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.AppStrings
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DangerRedLight
import com.example.ui.theme.JioDarkBlue
import com.example.ui.theme.JioPrimaryBlue
import com.example.ui.theme.PendingAmber
import com.example.ui.theme.PendingAmberLight
import com.example.ui.theme.ProfitGreen
import com.example.ui.theme.ProfitGreenLight
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TransactionItem(
    transaction: TransactionRecord,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val dateFormatter = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val formattedDate = dateFormatter.format(Date(transaction.timestamp))

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Icon + Title & Details
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                val iconBg = if (transaction.isCredit) ProfitGreenLight else Color(0xFFF1F5F9)
                val iconTint = if (transaction.isCredit) ProfitGreen else JioPrimaryBlue

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (transaction.isCredit) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = transaction.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = JioDarkBlue
                    )
                    if (transaction.subtitle.isNotBlank()) {
                        Text(
                            text = transaction.subtitle,
                            fontSize = 11.sp,
                            color = Color.Gray,
                            maxLines = 1
                        )
                    }
                    Text(
                        text = formattedDate,
                        fontSize = 10.sp,
                        color = Color.LightGray
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Right: Amount + Status Badge
            Column(horizontalAlignment = Alignment.End) {
                val prefix = if (transaction.isCredit) "+₹" else "-₹"
                val amountColor = if (transaction.isCredit) ProfitGreen else JioDarkBlue

                Text(
                    text = "$prefix${transaction.amount.toInt()}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = amountColor
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Status chip
                val (chipBg, chipBorder, chipText, statusIcon) = when (transaction.status) {
                    "SUCCESSFUL" -> Quadruple(
                        ProfitGreenLight,
                        ProfitGreen.copy(alpha = 0.3f),
                        AppStrings.statusSuccessful(language),
                        Icons.Default.CheckCircle
                    )
                    "PENDING" -> Quadruple(
                        PendingAmberLight,
                        PendingAmber.copy(alpha = 0.3f),
                        AppStrings.statusPending(language),
                        Icons.Default.HourglassTop
                    )
                    else -> Quadruple(
                        DangerRedLight,
                        DangerRed.copy(alpha = 0.3f),
                        AppStrings.statusFailed(language),
                        Icons.Default.Error
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(chipBg)
                        .border(1.dp, chipBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 7.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = chipText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (transaction.status) {
                            "SUCCESSFUL" -> ProfitGreen
                            "PENDING" -> PendingAmber
                            else -> DangerRed
                        }
                    )
                }
            }
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
