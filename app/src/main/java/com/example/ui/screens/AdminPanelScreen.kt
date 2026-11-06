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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Order
import com.example.data.model.TransactionRecord
import com.example.data.model.User
import com.example.ui.components.TransactionItem
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.AppStrings
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DangerRedLight
import com.example.ui.theme.JioAccentBlue
import com.example.ui.theme.JioDarkBlue
import com.example.ui.theme.JioLightBlueSurface
import com.example.ui.theme.JioPrimaryBlue
import com.example.ui.theme.JioVibrantCyan
import com.example.ui.theme.PendingAmber
import com.example.ui.theme.ProfitGreen
import com.example.ui.theme.ProfitGreenLight
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminPanelScreen(
    adminUser: User?,
    pendingOrders: List<Order>,
    allOrders: List<Order>,
    allTransactions: List<TransactionRecord>,
    users: List<User>,
    language: AppLanguage,
    onConfirmTransfer: (orderId: Long) -> Unit,
    onRejectOrder: (orderId: Long) -> Unit,
    onToggleUserBlock: (userId: Long, currentBlocked: Boolean) -> Unit,
    onUpdatePassword: (newPass: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Verifications, 1: Transactions, 2: Users, 3: Security
    var newAdminPass by remember { mutableStateOf("") }
    var txFilter by remember { mutableStateOf("ALL") }

    val filteredTx = remember(allTransactions, txFilter) {
        when (txFilter) {
            "SUCCESSFUL" -> allTransactions.filter { it.status == "SUCCESSFUL" }
            "FAILED" -> allTransactions.filter { it.status == "FAILED" }
            "PENDING" -> allTransactions.filter { it.status == "PENDING" }
            else -> allTransactions
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF6F8FC)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. ADMIN 50K BALANCE & STATUS HERO CARD
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_stats_card")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(JioDarkBlue, JioPrimaryBlue, Color(0xFF0C2461))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
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
                                        .background(ProfitGreen),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AdminPanelSettings,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = adminUser?.name ?: "Vishal Kumar (Admin)",
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "UPI: ${adminUser?.upiId ?: "vishalkumar90@upi"}",
                                        color = JioVibrantCyan,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "SUPER ADMIN",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Text(
                            text = "Admin Liquidity Reserve Balance",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "₹${adminUser?.balance?.toInt() ?: 50000}",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Auto transfers to buyer wallet upon verification with +8% profit",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 10.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Stats Summary Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "Pending Approvals", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
                                Text(
                                    text = "${pendingOrders.size}",
                                    color = if (pendingOrders.isNotEmpty()) PendingAmber else Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Column {
                                Text(text = "Total Users", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
                                Text(text = "${users.size}", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text(text = "Total Orders", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
                                Text(text = "${allOrders.size}", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // 2. TAB SELECTOR (Verifications, All Transactions, User Management)
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = JioPrimaryBlue,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "Verifications (${pendingOrders.size})",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "All Logs",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = {
                        Text(
                            text = "Users (${users.size})",
                            fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 11.sp
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = {
                        Text(
                            text = "Security",
                            fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 11.sp
                        )
                    }
                )
            }
        }

        // ---------------- TAB 0: PENDING VERIFICATIONS ----------------
        if (selectedTab == 0) {
            if (pendingOrders.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(36.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = ProfitGreen,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No pending payments waiting for verification!",
                                    color = Color.Gray,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            } else {
                items(pendingOrders) { order ->
                    AdminOrderVerificationCard(
                        order = order,
                        language = language,
                        onConfirm = { onConfirmTransfer(order.id) },
                        onReject = { onRejectOrder(order.id) }
                    )
                }
            }
        }

        // ---------------- TAB 1: ALL TRANSACTIONS LOGS ----------------
        if (selectedTab == 1) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("ALL", "SUCCESSFUL", "FAILED", "PENDING").forEach { status ->
                        val isSelected = txFilter == status
                        FilterChip(
                            selected = isSelected,
                            onClick = { txFilter = status },
                            label = { Text(status, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = JioPrimaryBlue,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("admin_tx_filter_$status")
                        )
                    }
                }
            }

            if (filteredTx.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "No transactions found", color = Color.Gray, fontSize = 13.sp)
                        }
                    }
                }
            } else {
                items(filteredTx) { tx ->
                    TransactionItem(transaction = tx, language = language)
                }
            }
        }

        // ---------------- TAB 2: USER MANAGEMENT (BLOCK / UNBLOCK) ----------------
        if (selectedTab == 2) {
            item {
                Text(
                    text = "User Accounts & Access Control",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = JioDarkBlue
                )
            }

            items(users) { user ->
                AdminUserCard(
                    user = user,
                    language = language,
                    onToggleBlock = { onToggleUserBlock(user.id, user.isBlocked) }
                )
            }
        }

        // ---------------- TAB 3: SECURITY & ADMIN ACCESS ----------------
        if (selectedTab == 3) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(ProfitGreenLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VerifiedUser,
                                    contentDescription = null,
                                    tint = ProfitGreen,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Admin Access Exclusivity",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = JioDarkBlue
                                )
                                Text(
                                    text = "Only Admin Vishal Kumar has access to this portal",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Ordinary users cannot view or access this panel. All sensitive actions—including confirming buyer orders, transferring funds from the ₹50k reserve, and blocking/unblocking users—require your master admin authentication.",
                            fontSize = 12.sp,
                            color = Color(0xFF4A5568),
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Change Password / PIN section
                        Text(
                            text = "Change Admin Master Password",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = JioDarkBlue
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = newAdminPass,
                            onValueChange = { newAdminPass = it },
                            label = { Text("Enter New Admin Password") },
                            placeholder = { Text("e.g. MySecretPin#99") },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("new_admin_password_input")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                if (newAdminPass.trim().isNotBlank()) {
                                    onUpdatePassword(newAdminPass.trim())
                                    newAdminPass = ""
                                }
                            },
                            enabled = newAdminPass.trim().length >= 4,
                            colors = ButtonDefaults.buttonColors(containerColor = JioPrimaryBlue),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("save_admin_password_button")
                        ) {
                            Text(text = "Save New Master Password", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminOrderVerificationCard(
    order: Order,
    language: AppLanguage,
    onConfirm: () -> Unit,
    onReject: () -> Unit
) {
    val dateFormatter = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val formattedDate = dateFormatter.format(Date(order.createdAt))

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_order_card_${order.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Order #${order.orderNumber}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = JioDarkBlue
                    )
                    Text(
                        text = formattedDate,
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFFF0B3))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "PENDING APPROVAL",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB78103)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Buyer details & UTR
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF8FAFC))
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Buyer:", fontSize = 12.sp, color = Color.Gray)
                        Text(
                            text = "${order.buyerName} (${order.buyerPhone})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = JioDarkBlue
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Plan Amount:", fontSize = 12.sp, color = Color.Gray)
                        Text(text = "₹${order.amount.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "8% Profit Added:", fontSize = 12.sp, color = ProfitGreen)
                        Text(
                            text = "+₹${order.profitAmount.toInt()}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ProfitGreen
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Wallet Transfer Credit:", fontSize = 12.sp, color = JioPrimaryBlue, fontWeight = FontWeight.Bold)
                        Text(
                            text = "₹${order.totalReturn.toInt()}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = JioPrimaryBlue
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Provided UTR / Ref:", fontSize = 12.sp, color = Color.Gray)
                        Text(
                            text = order.utr,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFC05621)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons: Confirm & Transfer vs Reject
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onReject,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("admin_reject_button_${order.id}")
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Reject", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(containerColor = ProfitGreen),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1.8f)
                        .testTag("admin_confirm_button_${order.id}")
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Verify & Transfer", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun AdminUserCard(
    user: User,
    language: AppLanguage,
    onToggleBlock: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (user.isBlocked) DangerRedLight else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_user_card_${user.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = user.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = JioDarkBlue
                    )
                    if (user.isBlocked) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "(BLOCKED)",
                            color = DangerRed,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
                Text(
                    text = "Phone: ${user.phone}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = "UPI: ${if (user.upiId.isNotBlank()) user.upiId else "None"} • Balance: ₹${user.balance.toInt()}",
                    fontSize = 11.sp,
                    color = JioPrimaryBlue
                )
            }

            Button(
                onClick = onToggleBlock,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (user.isBlocked) ProfitGreen else DangerRed
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("block_toggle_${user.id}")
            ) {
                Icon(
                    imageVector = if (user.isBlocked) Icons.Default.LockOpen else Icons.Default.Block,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (user.isBlocked) "Unblock" else "Block",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
