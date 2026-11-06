package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.BuyPlan
import com.example.data.model.Order
import com.example.data.model.TransactionRecord
import com.example.data.model.User
import com.example.data.repository.JioPayRepository
import com.example.ui.i18n.AppLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class JioPayViewModel(private val repository: JioPayRepository) : ViewModel() {

    private val _language = MutableStateFlow(AppLanguage.ENGLISH)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _adminUser = MutableStateFlow<User?>(null)
    val adminUser: StateFlow<User?> = _adminUser.asStateFlow()

    private val _currentScreen = MutableStateFlow("HOME") // HOME, BUY, SELL, TRANSACTIONS, ADMIN, SUPPORT
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    // Modals state
    private val _selectedPlanForBuy = MutableStateFlow<BuyPlan?>(null)
    val selectedPlanForBuy: StateFlow<BuyPlan?> = _selectedPlanForBuy.asStateFlow()

    private val _showAddUpiDialog = MutableStateFlow(false)
    val showAddUpiDialog: StateFlow<Boolean> = _showAddUpiDialog.asStateFlow()

    private val _showWithdrawDialog = MutableStateFlow(false)
    val showWithdrawDialog: StateFlow<Boolean> = _showWithdrawDialog.asStateFlow()

    // Active marketplace buy plans
    val activePlans: StateFlow<List<BuyPlan>> = repository.getAllActivePlans()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All orders for admin
    val allOrders: StateFlow<List<Order>> = repository.getAllOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Pending orders waiting for Admin/Seller confirmation
    val pendingOrders: StateFlow<List<Order>> = repository.getPendingOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All transactions for admin tracking
    val allTransactions: StateFlow<List<TransactionRecord>> = repository.getAllTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Regular users for admin block/unblock management
    val regularUsers: StateFlow<List<User>> = repository.getAllRegularUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Current user's transactions
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val userTransactions: StateFlow<List<TransactionRecord>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getUserTransactions(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadAdminUser()
    }

    private fun loadAdminUser() {
        viewModelScope.launch {
            val admin = repository.getAdminUser()
            _adminUser.value = admin
        }
    }

    fun toggleLanguage() {
        _language.value = if (_language.value == AppLanguage.ENGLISH) AppLanguage.HINDI else AppLanguage.ENGLISH
    }

    fun setScreen(screen: String) {
        _currentScreen.value = screen
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun showSnackbar(msg: String) {
        _snackbarMessage.value = msg
    }

    // ---------------- AUTHENTICATION ----------------

    fun loginWithPassword(phone: String, pass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val result = repository.loginWithPassword(phone, pass)
            result.onSuccess { user ->
                _currentUser.value = user
                _snackbarMessage.value = "Welcome back, ${user.name}!"
                onSuccess()
            }.onFailure { error ->
                _snackbarMessage.value = error.message ?: "Login failed"
            }
        }
    }

    fun loginWithOtp(phone: String, otp: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val result = repository.loginWithOtp(phone, otp)
            result.onSuccess { user ->
                _currentUser.value = user
                _snackbarMessage.value = "Logged in successfully with OTP!"
                onSuccess()
            }.onFailure { error ->
                _snackbarMessage.value = error.message ?: "OTP Login failed"
            }
        }
    }

    fun register(name: String, phone: String, pass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val result = repository.registerUser(name, phone, pass)
            result.onSuccess { user ->
                _currentUser.value = user
                _snackbarMessage.value = "Registration successful! Welcome, ${user.name}."
                onSuccess()
            }.onFailure { error ->
                _snackbarMessage.value = error.message ?: "Registration failed"
            }
        }
    }

    fun loginAsAdmin(pin: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val result = repository.loginAsAdmin(pin)
            result.onSuccess { admin ->
                _currentUser.value = admin
                _adminUser.value = admin
                _snackbarMessage.value = "Welcome Vishal Kumar (Admin). Balance: ₹${admin.balance.toInt()}"
                _currentScreen.value = "ADMIN"
                onSuccess()
            }.onFailure { error ->
                _snackbarMessage.value = error.message ?: "Admin verification failed"
            }
        }
    }

    fun updateAdminPassword(newPass: String) {
        viewModelScope.launch {
            val result = repository.updateAdminPassword(newPass)
            result.onSuccess {
                _snackbarMessage.value = "Admin password updated successfully! Only you can login."
                loadAdminUser()
            }.onFailure { error ->
                _snackbarMessage.value = error.message ?: "Failed to update password"
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _currentScreen.value = "HOME"
        _snackbarMessage.value = "Logged out successfully"
    }

    // ---------------- BUY PLAN FLOW ----------------

    fun selectPlanForBuy(plan: BuyPlan) {
        _selectedPlanForBuy.value = plan
    }

    fun dismissBuyDialog() {
        _selectedPlanForBuy.value = null
    }

    fun submitBuyPayment(utr: String) {
        val user = _currentUser.value ?: return
        val plan = _selectedPlanForBuy.value ?: return

        viewModelScope.launch {
            val result = repository.submitBuyOrder(user.id, plan.id, utr)
            result.onSuccess { order ->
                _selectedPlanForBuy.value = null
                _snackbarMessage.value = "Payment submitted! Order #${order.orderNumber} is pending Admin verification."
                refreshCurrentUserData()
            }.onFailure { error ->
                _snackbarMessage.value = error.message ?: "Failed to submit payment"
            }
        }
    }

    // ---------------- ADMIN VERIFICATION ----------------

    fun confirmOrderTransfer(orderId: Long) {
        viewModelScope.launch {
            val result = repository.confirmPaymentAndTransfer(orderId)
            result.onSuccess {
                _snackbarMessage.value = "Payment Verified! ₹ transferred and 8% profit credited to buyer."
                loadAdminUser()
                refreshCurrentUserData()
            }.onFailure { error ->
                _snackbarMessage.value = error.message ?: "Failed to verify order"
            }
        }
    }

    fun rejectOrder(orderId: Long) {
        viewModelScope.launch {
            val result = repository.rejectPaymentOrder(orderId)
            result.onSuccess {
                _snackbarMessage.value = "Order marked as REJECTED (Failed)"
                loadAdminUser()
                refreshCurrentUserData()
            }.onFailure { error ->
                _snackbarMessage.value = error.message ?: "Failed to reject order"
            }
        }
    }

    // ---------------- SELL MONEY ----------------

    fun createSellOrder(amount: Double, slots: Int, onSuccess: () -> Unit) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val result = repository.createSellPlan(user.id, amount, slots)
            result.onSuccess {
                _snackbarMessage.value = "Sell order listed successfully! Other users can now buy your plan."
                refreshCurrentUserData()
                onSuccess()
            }.onFailure { error ->
                _snackbarMessage.value = error.message ?: "Failed to create sell order"
            }
        }
    }

    // ---------------- UPI & WITHDRAWAL ----------------

    fun openAddUpiDialog() {
        _showAddUpiDialog.value = true
    }

    fun closeAddUpiDialog() {
        _showAddUpiDialog.value = false
    }

    fun saveUserUpi(upiId: String, bankName: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val result = repository.updateUserUpi(user.id, upiId, bankName)
            result.onSuccess {
                _showAddUpiDialog.value = false
                _snackbarMessage.value = "UPI ID updated successfully: $upiId"
                refreshCurrentUserData()
            }.onFailure { error ->
                _snackbarMessage.value = error.message ?: "Failed to update UPI"
            }
        }
    }

    fun openWithdrawDialog() {
        _showWithdrawDialog.value = true
    }

    fun closeWithdrawDialog() {
        _showWithdrawDialog.value = false
    }

    fun requestWithdrawal(amount: Double, upiId: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val result = repository.requestWithdrawal(user.id, amount, upiId)
            result.onSuccess {
                _showWithdrawDialog.value = false
                _snackbarMessage.value = "Withdrawal of ₹${amount.toInt()} processed to $upiId"
                refreshCurrentUserData()
            }.onFailure { error ->
                _snackbarMessage.value = error.message ?: "Withdrawal failed"
            }
        }
    }

    // ---------------- ADMIN CONTROLS ----------------

    fun toggleUserBlock(userId: Long, currentBlocked: Boolean) {
        viewModelScope.launch {
            val newStatus = !currentBlocked
            val result = repository.setUserBlockStatus(userId, newStatus)
            result.onSuccess {
                _snackbarMessage.value = if (newStatus) "User blocked successfully" else "User unblocked"
            }.onFailure { error ->
                _snackbarMessage.value = error.message ?: "Failed to update user status"
            }
        }
    }

    private fun refreshCurrentUserData() {
        val current = _currentUser.value ?: return
        viewModelScope.launch {
            val updated = repository.getAdminUser().takeIf { current.isAdmin }
                ?: repository.observeUser(current.id)
            repository.observeUser(current.id).collect { u ->
                if (u != null) {
                    _currentUser.value = u
                }
            }
        }
    }
}

class JioPayViewModelFactory(private val repository: JioPayRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JioPayViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return JioPayViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
