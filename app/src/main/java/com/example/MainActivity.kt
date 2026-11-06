package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.data.local.AppDatabase
import com.example.data.local.DatabaseSeeder
import com.example.data.repository.JioPayRepository
import com.example.ui.components.AddUpiModal
import com.example.ui.components.JioPayHeader
import com.example.ui.components.PaymentModal
import com.example.ui.components.WithdrawModal
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.AppStrings
import com.example.ui.i18n.LocalAppLanguage
import com.example.ui.screens.AdminPanelScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.BuyPlansScreen
import com.example.ui.screens.CustomerSupportScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SellMoneyScreen
import com.example.ui.screens.TransactionsScreen
import com.example.ui.theme.DangerRed
import com.example.ui.theme.JioAccentBlue
import com.example.ui.theme.JioDarkBlue
import com.example.ui.theme.JioLightBlueSurface
import com.example.ui.theme.JioPrimaryBlue
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PendingAmber
import com.example.ui.theme.ProfitGreen
import com.example.ui.viewmodel.JioPayViewModel
import com.example.ui.viewmodel.JioPayViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var database: AppDatabase
    private lateinit var repository: JioPayRepository

    private val viewModel: JioPayViewModel by viewModels {
        JioPayViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        database = AppDatabase.getDatabase(this)
        repository = JioPayRepository(database)

        // Seed default Admin Vishal Kumar (50k balance, vishalkumar90@upi) & buy plans
        lifecycleScope.launch {
            DatabaseSeeder.seedDatabase(database)
        }

        setContent {
            MyApplicationTheme {
                JioPayApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun JioPayApp(viewModel: JioPayViewModel) {
    val language by viewModel.language.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val adminUser by viewModel.adminUser.collectAsStateWithLifecycle()
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val snackbarMsg by viewModel.snackbarMessage.collectAsStateWithLifecycle()

    val activePlans by viewModel.activePlans.collectAsStateWithLifecycle()
    val userTransactions by viewModel.userTransactions.collectAsStateWithLifecycle()
    val allTransactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    val pendingOrders by viewModel.pendingOrders.collectAsStateWithLifecycle()
    val allOrders by viewModel.allOrders.collectAsStateWithLifecycle()
    val regularUsers by viewModel.regularUsers.collectAsStateWithLifecycle()

    val selectedPlanForBuy by viewModel.selectedPlanForBuy.collectAsStateWithLifecycle()
    val showAddUpiDialog by viewModel.showAddUpiDialog.collectAsStateWithLifecycle()
    val showWithdrawDialog by viewModel.showWithdrawDialog.collectAsStateWithLifecycle()

    var showProfileDialog by remember { mutableStateOf(false) }
    var showAdminPassChallenge by remember { mutableStateOf(false) }
    var adminChallengePin by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(snackbarMsg) {
        snackbarMsg?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    CompositionLocalProvider(LocalAppLanguage provides language) {
        // Handle Back Button navigation
        if (currentUser != null && currentScreen != "HOME") {
            BackHandler {
                viewModel.setScreen("HOME")
            }
        }

        if (currentUser == null) {
            // Unauthenticated Flow
            AuthScreen(
                language = language,
                onLoginWithPassword = { phone, pass ->
                    viewModel.loginWithPassword(phone, pass) {}
                },
                onLoginWithOtp = { phone, otp ->
                    viewModel.loginWithOtp(phone, otp) {}
                },
                onRegister = { name, phone, pass ->
                    viewModel.register(name, phone, pass) {}
                },
                onAdminLogin = { pin ->
                    viewModel.loginAsAdmin(pin) {}
                }
            )
        } else {
            // Main Authenticated Application
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    JioPayHeader(
                        language = language,
                        currentUser = currentUser,
                        onToggleLanguage = { viewModel.toggleLanguage() },
                        onNavigateToSupport = { viewModel.setScreen("SUPPORT") },
                        onNavigateToAdmin = {
                            if (currentUser?.isAdmin == true) {
                                viewModel.setScreen("ADMIN")
                            } else {
                                adminChallengePin = ""
                                showAdminPassChallenge = true
                            }
                        },
                        onProfileClick = { showProfileDialog = true }
                    )
                },
                bottomBar = {
                    NavigationBar(
                        containerColor = Color.White,
                        tonalElevation = 6.dp,
                        modifier = Modifier
                            .navigationBarsPadding()
                            .testTag("bottom_nav_bar")
                    ) {
                        NavigationBarItem(
                            selected = currentScreen == "HOME",
                            onClick = { viewModel.setScreen("HOME") },
                            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                            label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = JioPrimaryBlue,
                                selectedTextColor = JioPrimaryBlue,
                                indicatorColor = JioLightBlueSurface
                            ),
                            modifier = Modifier.testTag("nav_item_home")
                        )

                        NavigationBarItem(
                            selected = currentScreen == "BUY",
                            onClick = { viewModel.setScreen("BUY") },
                            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Buy") },
                            label = { Text("Buy", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = JioPrimaryBlue,
                                selectedTextColor = JioPrimaryBlue,
                                indicatorColor = JioLightBlueSurface
                            ),
                            modifier = Modifier.testTag("nav_item_buy")
                        )

                        NavigationBarItem(
                            selected = currentScreen == "SELL",
                            onClick = { viewModel.setScreen("SELL") },
                            icon = { Icon(Icons.Default.ArrowUpward, contentDescription = "Sell") },
                            label = { Text("Sell", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = JioPrimaryBlue,
                                selectedTextColor = JioPrimaryBlue,
                                indicatorColor = JioLightBlueSurface
                            ),
                            modifier = Modifier.testTag("nav_item_sell")
                        )

                        NavigationBarItem(
                            selected = currentScreen == "TRANSACTIONS",
                            onClick = { viewModel.setScreen("TRANSACTIONS") },
                            icon = { Icon(Icons.Default.History, contentDescription = "History") },
                            label = { Text("History", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = JioPrimaryBlue,
                                selectedTextColor = JioPrimaryBlue,
                                indicatorColor = JioLightBlueSurface
                            ),
                            modifier = Modifier.testTag("nav_item_history")
                        )

                        NavigationBarItem(
                            selected = currentScreen == "ADMIN",
                            onClick = {
                                if (currentUser?.isAdmin == true) {
                                    viewModel.setScreen("ADMIN")
                                } else {
                                    adminChallengePin = ""
                                    showAdminPassChallenge = true
                                }
                            },
                            icon = {
                                if (pendingOrders.isNotEmpty()) {
                                    BadgedBox(
                                        badge = {
                                            Badge(containerColor = PendingAmber) {
                                                Text(text = "${pendingOrders.size}", color = Color.White)
                                            }
                                        }
                                    ) {
                                        Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin")
                                    }
                                } else {
                                    Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin")
                                }
                            },
                            label = { Text("Admin", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = JioPrimaryBlue,
                                selectedTextColor = JioPrimaryBlue,
                                indicatorColor = JioLightBlueSurface
                            ),
                            modifier = Modifier.testTag("nav_item_admin")
                        )

                        NavigationBarItem(
                            selected = currentScreen == "SUPPORT",
                            onClick = { viewModel.setScreen("SUPPORT") },
                            icon = { Icon(Icons.Default.HeadsetMic, contentDescription = "Support") },
                            label = { Text("Support", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = JioPrimaryBlue,
                                selectedTextColor = JioPrimaryBlue,
                                indicatorColor = JioLightBlueSurface
                            ),
                            modifier = Modifier.testTag("nav_item_support")
                        )
                    }
                }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    when (currentScreen) {
                        "HOME" -> HomeScreen(
                            currentUser = currentUser,
                            plans = activePlans,
                            transactions = userTransactions,
                            language = language,
                            onPlanBuyClick = { plan -> viewModel.selectPlanForBuy(plan) },
                            onNavigateToBuy = { viewModel.setScreen("BUY") },
                            onNavigateToSell = { viewModel.setScreen("SELL") },
                            onOpenWithdraw = { viewModel.openWithdrawDialog() },
                            onOpenAddUpi = { viewModel.openAddUpiDialog() },
                            onNavigateToHistory = { viewModel.setScreen("TRANSACTIONS") },
                            onNavigateToSupport = { viewModel.setScreen("SUPPORT") }
                        )

                        "BUY" -> BuyPlansScreen(
                            plans = activePlans,
                            language = language,
                            onPlanBuyClick = { plan -> viewModel.selectPlanForBuy(plan) }
                        )

                        "SELL" -> SellMoneyScreen(
                            currentUser = currentUser,
                            language = language,
                            onOpenAddUpi = { viewModel.openAddUpiDialog() },
                            onCreateSellOrder = { amt, slots ->
                                viewModel.createSellOrder(amt, slots) {
                                    viewModel.setScreen("BUY")
                                }
                            }
                        )

                        "TRANSACTIONS" -> TransactionsScreen(
                            transactions = userTransactions,
                            language = language
                        )

                        "ADMIN" -> {
                            if (currentUser?.isAdmin != true) {
                                viewModel.setScreen("HOME")
                            } else {
                                AdminPanelScreen(
                                    adminUser = adminUser,
                                    pendingOrders = pendingOrders,
                                    allOrders = allOrders,
                                    allTransactions = allTransactions,
                                    users = regularUsers,
                                    language = language,
                                    onConfirmTransfer = { orderId -> viewModel.confirmOrderTransfer(orderId) },
                                    onRejectOrder = { orderId -> viewModel.rejectOrder(orderId) },
                                    onToggleUserBlock = { userId, blocked -> viewModel.toggleUserBlock(userId, blocked) },
                                    onUpdatePassword = { newPass -> viewModel.updateAdminPassword(newPass) }
                                )
                            }
                        }

                        "SUPPORT" -> CustomerSupportScreen(language = language)
                    }
                }
            }

            // ---------------- MODAL DIALOGS ----------------

            // 1. BUY PAYMENT PROOF & QR MODAL
            selectedPlanForBuy?.let { plan ->
                PaymentModal(
                    plan = plan,
                    language = language,
                    onDismiss = { viewModel.dismissBuyDialog() },
                    onSubmitPayment = { utr ->
                        viewModel.submitBuyPayment(utr)
                    }
                )
            }

            // 2. ADD / UPDATE UPI MODAL
            if (showAddUpiDialog) {
                AddUpiModal(
                    currentUpi = currentUser?.upiId ?: "",
                    currentBank = currentUser?.bankName ?: "",
                    language = language,
                    onDismiss = { viewModel.closeAddUpiDialog() },
                    onSaveUpi = { upi, bank ->
                        viewModel.saveUserUpi(upi, bank)
                    }
                )
            }

            // 3. WITHDRAWAL MODAL
            if (showWithdrawDialog) {
                WithdrawModal(
                    availableBalance = currentUser?.balance ?: 0.0,
                    savedUpi = currentUser?.upiId ?: "",
                    language = language,
                    onDismiss = { viewModel.closeWithdrawDialog() },
                    onRequestWithdrawal = { amt, upi ->
                        viewModel.requestWithdrawal(amt, upi)
                    }
                )
            }

            // 4. USER PROFILE & LOGOUT DIALOG
            if (showProfileDialog) {
                AlertDialog(
                    onDismissRequest = { showProfileDialog = false },
                    title = {
                        Text(
                            text = "Account Profile",
                            fontWeight = FontWeight.Bold,
                            color = JioDarkBlue
                        )
                    },
                    text = {
                        Column {
                            Text(text = "Name: ${currentUser?.name ?: ""}", fontWeight = FontWeight.SemiBold)
                            Text(text = "Phone: ${currentUser?.phone ?: ""}", color = Color.Gray)
                            Text(text = "Wallet Balance: ₹${currentUser?.balance?.toInt() ?: 0}", color = ProfitGreen, fontWeight = FontWeight.Bold)
                            Text(text = "UPI: ${if (currentUser?.upiId?.isNotBlank() == true) currentUser?.upiId else "None"}", color = JioPrimaryBlue)
                            if (currentUser?.isAdmin == true) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Status: Super Administrator", color = JioAccentBlue, fontWeight = FontWeight.Bold)
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showProfileDialog = false
                                viewModel.logout()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                        ) {
                            Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Logout")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showProfileDialog = false }) {
                            Text("Close")
                        }
                    }
                )
            }

            // 5. ADMIN MASTER PASSWORD CHALLENGE DIALOG
            if (showAdminPassChallenge) {
                AlertDialog(
                    onDismissRequest = { showAdminPassChallenge = false },
                    title = {
                        Text(
                            text = "Admin Verification Required",
                            fontWeight = FontWeight.Bold,
                            color = JioDarkBlue
                        )
                    },
                    text = {
                        Column {
                            Text(
                                text = "This console is strictly restricted to Admin Vishal Kumar. Enter your Master Admin Password:",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = adminChallengePin,
                                onValueChange = { adminChallengePin = it },
                                label = { Text("Master Admin Password") },
                                placeholder = { Text("Default: admin") },
                                visualTransformation = PasswordVisualTransformation(),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("admin_challenge_input")
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.loginAsAdmin(adminChallengePin.trim()) {
                                    showAdminPassChallenge = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ProfitGreen),
                            modifier = Modifier.testTag("confirm_admin_challenge_button")
                        ) {
                            Text("Verify & Unlock")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showAdminPassChallenge = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}
