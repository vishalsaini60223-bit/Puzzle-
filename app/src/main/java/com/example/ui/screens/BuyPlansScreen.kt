package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BuyPlan
import com.example.ui.components.PlanCard
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.AppStrings
import com.example.ui.theme.JioAccentBlue
import com.example.ui.theme.JioDarkBlue
import com.example.ui.theme.JioLightBlueSurface
import com.example.ui.theme.JioPrimaryBlue
import com.example.ui.theme.ProfitGreen
import com.example.ui.theme.ProfitGreenLight

@Composable
fun BuyPlansScreen(
    plans: List<BuyPlan>,
    language: AppLanguage,
    onPlanBuyClick: (BuyPlan) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("ALL") }

    val filteredPlans = remember(plans, selectedFilter) {
        if (selectedFilter == "ALL") plans
        else plans.filter { it.amount.toInt().toString() == selectedFilter }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF6F8FC)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = AppStrings.buy(language),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = JioDarkBlue
                )
                Text(
                    text = "Select a plan to buy, pay via UPI, and earn 8% commission profit.",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        // 8% Profit Rules Information Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = JioLightBlueSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(JioPrimaryBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ElectricBolt,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Guaranteed 8% Commission Model",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = JioDarkBlue
                        )
                        Text(
                            text = "Admin Vishal Kumar verifies payment and transfers capital + 8% profit instantly to your wallet.",
                            fontSize = 11.sp,
                            color = Color(0xFF4A5568)
                        )
                    }
                }
            }
        }

        // Filter Chips row (All, 350, 500, 700, 850, 1000)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filterOptions = remember(plans) {
                    listOf("ALL") + plans.map { it.amount.toInt().toString() }.distinct()
                }
                filterOptions.forEach { opt ->
                    val isSelected = selectedFilter == opt
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = opt },
                        label = {
                            Text(
                                text = if (opt == "ALL") "All Plans" else "₹$opt",
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = JioPrimaryBlue,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("filter_chip_$opt")
                    )
                }
            }
        }

        // Plans List
        items(filteredPlans) { plan ->
            PlanCard(
                plan = plan,
                language = language,
                onBuyClick = onPlanBuyClick
            )
        }
    }
}
