package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.User
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.AppStrings
import com.example.ui.theme.JioAccentBlue
import com.example.ui.theme.JioDarkBlue
import com.example.ui.theme.JioLightBlueSurface
import com.example.ui.theme.JioPrimaryBlue
import com.example.ui.theme.ProfitGreen
import com.example.ui.theme.ProfitGreenLight

@Composable
fun SellMoneyScreen(
    currentUser: User?,
    language: AppLanguage,
    onOpenAddUpi: () -> Unit,
    onCreateSellOrder: (amount: Double, slots: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var amountText by remember { mutableStateOf("500") }
    var slotsText by remember { mutableStateOf("2") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val hasUpi = currentUser?.upiId?.isNotBlank() == true
    val userBalance = currentUser?.balance ?: 0.0

    val calculatedTotal = remember(amountText, slotsText) {
        val amt = amountText.toDoubleOrNull() ?: 0.0
        val slots = slotsText.toIntOrNull() ?: 0
        amt * slots
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF6F8FC)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        item {
            Column {
                Text(
                    text = AppStrings.sell(language),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = JioDarkBlue
                )
                Text(
                    text = "Sell your wallet balance on the P2P marketplace to other buyers.",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        // UPI Account Status Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (hasUpi) ProfitGreenLight else Color(0xFFFFF0B3)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (hasUpi) Icons.Default.CheckCircle else Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = if (hasUpi) ProfitGreen else Color(0xFFB78103),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Text(
                                    text = if (hasUpi) "Linked UPI Account" else "UPI Not Linked",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = JioDarkBlue
                                )
                                Text(
                                    text = if (hasUpi) currentUser?.upiId ?: "" else "Add UPI to receive payment from buyers",
                                    fontSize = 12.sp,
                                    color = if (hasUpi) JioPrimaryBlue else Color.Gray
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = onOpenAddUpi,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("manage_upi_button")
                        ) {
                            Text(text = if (hasUpi) "Change" else "Add UPI", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Sell Order Creation Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Create Sell Order",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = JioDarkBlue
                    )
                    Text(
                        text = "Available Balance: ₹${userBalance.toInt()}",
                        fontSize = 12.sp,
                        color = ProfitGreen,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Plan Amount
                    OutlinedTextField(
                        value = amountText,
                        onValueChange = {
                            amountText = it
                            errorMessage = null
                        },
                        label = { Text("Price Per Slot (₹)") },
                        placeholder = { Text("e.g. 500") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.CurrencyRupee, contentDescription = null, tint = JioPrimaryBlue)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = JioPrimaryBlue,
                            focusedLabelColor = JioPrimaryBlue
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("sell_amount_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Number of slots
                    OutlinedTextField(
                        value = slotsText,
                        onValueChange = {
                            slotsText = it
                            errorMessage = null
                        },
                        label = { Text("Number of Available Slots") },
                        placeholder = { Text("e.g. 5") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = JioPrimaryBlue,
                            focusedLabelColor = JioPrimaryBlue
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("sell_slots_input")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Order Summary Breakdown
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(JioLightBlueSurface)
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Total Selling Value:", fontSize = 12.sp, color = JioDarkBlue)
                            Text(
                                text = "₹${calculatedTotal.toInt()}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = JioPrimaryBlue
                            )
                        }
                    }

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage ?: "",
                            color = Color.Red,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Publish Button
                    Button(
                        onClick = {
                            if (!hasUpi) {
                                errorMessage = "Please add your UPI ID first"
                            } else {
                                val amt = amountText.toDoubleOrNull()
                                val slots = slotsText.toIntOrNull()
                                if (amt == null || amt <= 0) {
                                    errorMessage = "Please enter a valid amount"
                                } else if (slots == null || slots <= 0) {
                                    errorMessage = "Please enter valid number of slots"
                                } else if (currentUser?.isAdmin != true && calculatedTotal > userBalance) {
                                    errorMessage = "Total selling value exceeds your wallet balance"
                                } else {
                                    onCreateSellOrder(amt, slots)
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = JioPrimaryBlue),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("list_sell_order_button")
                    ) {
                        Icon(imageVector = Icons.Default.ArrowUpward, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "List Sell Plan on Marketplace",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
