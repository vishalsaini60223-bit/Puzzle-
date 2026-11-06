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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BuyPlan
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.AppStrings
import com.example.ui.theme.JioAccentBlue
import com.example.ui.theme.JioDarkBlue
import com.example.ui.theme.JioPrimaryBlue
import com.example.ui.theme.JioVibrantCyan
import com.example.ui.theme.ProfitGreen
import com.example.ui.theme.ProfitGreenLight

@Composable
fun PlanCard(
    plan: BuyPlan,
    language: AppLanguage,
    onBuyClick: (BuyPlan) -> Unit,
    modifier: Modifier = Modifier
) {
    val isSoldOut = plan.remainingSlots <= 0

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("plan_card_${plan.amount.toInt()}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row: Plan Amount & 8% Profit Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(JioPrimaryBlue, JioAccentBlue)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CurrencyRupee,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "₹${plan.amount.toInt()}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = JioDarkBlue
                        )
                        Text(
                            text = "Jio Pay P2P Plan",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }

                // 8% Commission Profit Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(ProfitGreenLight)
                        .border(1.dp, ProfitGreen.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ElectricBolt,
                            contentDescription = null,
                            tint = ProfitGreen,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "+8% Profit",
                            color = ProfitGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Middle Return Breakdown Row
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF7FAFC))
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = AppStrings.profitAmount(language),
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "+₹${plan.profitAmount.toInt()}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = ProfitGreen
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = AppStrings.totalReturn(language),
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "₹${plan.totalReturn.toInt()}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = JioPrimaryBlue
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom Row: Slots remaining & Buy button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Slots Left indicator
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isSoldOut) Color.Gray else ProfitGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isSoldOut) {
                            AppStrings.soldOut(language)
                        } else {
                            AppStrings.slotsLeft(language, plan.remainingSlots)
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isSoldOut) Color.Gray else JioDarkBlue
                    )
                }

                // Buy Action Button
                Button(
                    onClick = { onBuyClick(plan) },
                    enabled = !isSoldOut,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = JioPrimaryBlue,
                        disabledContainerColor = Color(0xFFE2E8F0)
                    ),
                    modifier = Modifier.testTag("buy_button_${plan.amount.toInt()}")
                ) {
                    Text(
                        text = if (isSoldOut) AppStrings.soldOut(language) else AppStrings.buyNow(language),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    if (!isSoldOut) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}
