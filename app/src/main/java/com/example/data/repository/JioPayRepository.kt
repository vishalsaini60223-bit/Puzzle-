package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.model.BuyPlan
import com.example.data.model.Order
import com.example.data.model.TransactionRecord
import com.example.data.model.User
import com.example.data.model.WithdrawalRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.UUID

class JioPayRepository(private val database: AppDatabase) {
    private val userDao = database.userDao()
    private val planDao = database.buyPlanDao()
    private val orderDao = database.orderDao()
    private val txDao = database.transactionDao()
    private val withdrawalDao = database.withdrawalDao()

    // ---------------- AUTHENTICATION ----------------

    suspend fun loginWithPassword(phone: String, pass: String): Result<User> = withContext(Dispatchers.IO) {
        val user = userDao.getUserByPhone(phone.trim())
            ?: return@withContext Result.failure(Exception("Mobile number not registered"))

        if (user.isBlocked) {
            return@withContext Result.failure(Exception("Your account has been blocked by Admin. Contact Telegram @Sainishaab9076"))
        }

        if (user.password != pass.trim()) {
            return@withContext Result.failure(Exception("Incorrect password"))
        }

        Result.success(user)
    }

    suspend fun loginWithOtp(phone: String, otp: String): Result<User> = withContext(Dispatchers.IO) {
        // Any 4-digit code works in simulation / prototype, standard default is 1234
        if (otp.trim().length != 4) {
            return@withContext Result.failure(Exception("Please enter a valid 4-digit OTP"))
        }

        var user = userDao.getUserByPhone(phone.trim())
        if (user == null) {
            // Auto register on first OTP login
            val newUserId = userDao.insertUser(
                User(
                    name = "User ${phone.takeLast(4)}",
                    phone = phone.trim(),
                    password = "jio" + phone.takeLast(4),
                    balance = 0.0,
                    isAdmin = false,
                    isBlocked = false
                )
            )
            user = userDao.getUserByIdSync(newUserId)
        }

        if (user != null && user.isBlocked) {
            return@withContext Result.failure(Exception("Your account has been blocked by Admin. Contact Telegram @Sainishaab9076"))
        }

        if (user != null) {
            Result.success(user)
        } else {
            Result.failure(Exception("Failed to sign in with OTP"))
        }
    }

    suspend fun registerUser(name: String, phone: String, pass: String): Result<User> = withContext(Dispatchers.IO) {
        if (name.isBlank() || phone.isBlank() || pass.isBlank()) {
            return@withContext Result.failure(Exception("Please fill all required fields"))
        }
        val existing = userDao.getUserByPhone(phone.trim())
        if (existing != null) {
            return@withContext Result.failure(Exception("Mobile number is already registered. Please login."))
        }

        val newId = userDao.insertUser(
            User(
                name = name.trim(),
                phone = phone.trim(),
                password = pass.trim(),
                balance = 0.0,
                isAdmin = false,
                isBlocked = false
            )
        )
        val created = userDao.getUserByIdSync(newId)
        if (created != null) Result.success(created) else Result.failure(Exception("Registration failed"))
    }

    suspend fun loginAsAdmin(pinOrPass: String): Result<User> = withContext(Dispatchers.IO) {
        val admin = userDao.getAdminUser()
            ?: return@withContext Result.failure(Exception("Admin account not found"))

        if (pinOrPass.trim() == admin.password || (admin.password == "admin" && (pinOrPass.trim() == "admin" || pinOrPass.trim() == "admin123" || pinOrPass.trim() == "9999"))) {
            Result.success(admin)
        } else {
            Result.failure(Exception("Incorrect Admin Password! Access Denied. Only Admin Vishal Kumar can access this portal."))
        }
    }

    suspend fun updateAdminPassword(newPass: String): Result<Unit> = withContext(Dispatchers.IO) {
        val admin = userDao.getAdminUser()
            ?: return@withContext Result.failure(Exception("Admin account not found"))
        if (newPass.trim().length < 4) {
            return@withContext Result.failure(Exception("Password must be at least 4 characters"))
        }
        userDao.updateUser(admin.copy(password = newPass.trim()))
        Result.success(Unit)
    }

    fun observeUser(userId: Long): Flow<User?> = userDao.getUserById(userId)

    suspend fun getAdminUser(): User? = withContext(Dispatchers.IO) {
        userDao.getAdminUser()
    }

    // ---------------- PLANS & MARKETPLACE ----------------

    fun getAllActivePlans(): Flow<List<BuyPlan>> = planDao.getAllActivePlans()

    suspend fun createSellPlan(sellerId: Long, amount: Double, slots: Int): Result<BuyPlan> = withContext(Dispatchers.IO) {
        val seller = userDao.getUserByIdSync(sellerId)
            ?: return@withContext Result.failure(Exception("Seller not found"))

        if (seller.upiId.isBlank()) {
            return@withContext Result.failure(Exception("Please add your UPI ID first before selling money"))
        }

        val totalSellAmount = amount * slots
        if (!seller.isAdmin && seller.balance < totalSellAmount) {
            return@withContext Result.failure(Exception("Insufficient wallet balance to sell ₹$totalSellAmount"))
        }

        if (!seller.isAdmin) {
            userDao.updateBalance(sellerId, seller.balance - totalSellAmount)
        }

        val plan = BuyPlan(
            amount = amount,
            totalSlots = slots,
            remainingSlots = slots,
            commissionPercent = 8.0,
            sellerId = seller.id,
            sellerName = seller.name,
            sellerUpi = seller.upiId
        )
        val planId = planDao.insertPlan(plan)
        val saved = planDao.getPlanById(planId)
        if (saved != null) Result.success(saved) else Result.failure(Exception("Failed to create sell plan"))
    }

    // ---------------- BUY ORDER & PAYMENT SUBMISSION ----------------

    suspend fun submitBuyOrder(buyerId: Long, planId: Long, utr: String): Result<Order> = withContext(Dispatchers.IO) {
        val buyer = userDao.getUserByIdSync(buyerId)
            ?: return@withContext Result.failure(Exception("Buyer not found"))
        if (buyer.isBlocked) {
            return@withContext Result.failure(Exception("Account is blocked"))
        }

        val plan = planDao.getPlanById(planId)
            ?: return@withContext Result.failure(Exception("Plan not found"))

        if (plan.remainingSlots <= 0) {
            return@withContext Result.failure(Exception("No slots remaining for this plan"))
        }

        if (utr.trim().length < 6) {
            return@withContext Result.failure(Exception("Please enter a valid 12-digit UPI UTR / Reference number"))
        }

        // Decrement slot
        planDao.updateRemainingSlots(planId, plan.remainingSlots - 1)

        val orderNum = "JIO-" + (100000 + (Math.random() * 900000).toInt())
        val order = Order(
            orderNumber = orderNum,
            planId = plan.id,
            buyerId = buyer.id,
            buyerName = buyer.name,
            buyerPhone = buyer.phone,
            sellerId = plan.sellerId,
            sellerName = plan.sellerName,
            sellerUpi = plan.sellerUpi,
            amount = plan.amount,
            profitAmount = plan.profitAmount,
            totalReturn = plan.totalReturn,
            utr = utr.trim(),
            status = "PENDING"
        )
        val orderId = orderDao.insertOrder(order)

        // Log pending transaction for buyer
        txDao.insertTransaction(
            TransactionRecord(
                userId = buyer.id,
                type = "BUY_PLAN",
                amount = plan.amount,
                isCredit = false,
                title = "Buy Plan ₹${plan.amount.toInt()} (8% Profit)",
                subtitle = "UTR: ${utr.trim()} - Awaiting Verification",
                utr = utr.trim(),
                status = "PENDING"
            )
        )

        val created = orderDao.getOrderById(orderId)
        if (created != null) Result.success(created) else Result.failure(Exception("Failed to create order"))
    }

    // ---------------- VERIFICATION & ADMIN TRANSFERS ----------------

    fun getAllOrders(): Flow<List<Order>> = orderDao.getAllOrders()
    fun getPendingOrders(): Flow<List<Order>> = orderDao.getPendingOrders()
    fun getUserOrders(buyerId: Long): Flow<List<Order>> = orderDao.getOrdersByBuyer(buyerId)

    suspend fun confirmPaymentAndTransfer(orderId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        val order = orderDao.getOrderById(orderId)
            ?: return@withContext Result.failure(Exception("Order not found"))

        if (order.status != "PENDING") {
            return@withContext Result.failure(Exception("Order already processed (${order.status})"))
        }

        val buyer = userDao.getUserByIdSync(order.buyerId)
            ?: return@withContext Result.failure(Exception("Buyer not found"))
        val seller = userDao.getUserByIdSync(order.sellerId)
            ?: return@withContext Result.failure(Exception("Seller not found"))

        // Credit buyer with totalReturn (Amount + 8% profit)
        val newBuyerBalance = buyer.balance + order.totalReturn
        userDao.updateBalance(buyer.id, newBuyerBalance)

        // Debit seller by order amount (e.g. Admin 50k balance)
        val newSellerBalance = seller.balance - order.amount
        userDao.updateBalance(seller.id, newSellerBalance)

        // Update Order to VERIFIED
        orderDao.updateOrder(
            order.copy(
                status = "VERIFIED",
                verifiedAt = System.currentTimeMillis()
            )
        )

        // 1. Log Successful Credit for Buyer
        txDao.insertTransaction(
            TransactionRecord(
                userId = buyer.id,
                type = "PROFIT_COMMISSION",
                amount = order.totalReturn,
                isCredit = true,
                title = "Payment Verified & 8% Profit Added",
                subtitle = "₹${order.amount.toInt()} + ₹${order.profitAmount.toInt()} Profit (Order: ${order.orderNumber})",
                utr = order.utr,
                status = "SUCCESSFUL"
            )
        )

        // 2. Log Successful Transfer for Seller (e.g. Admin Vishal Kumar)
        txDao.insertTransaction(
            TransactionRecord(
                userId = seller.id,
                type = "ADMIN_TRANSFER",
                amount = order.amount,
                isCredit = false,
                title = "P2P Payment Transfer Confirmed",
                subtitle = "Transferred to ${buyer.name} (${buyer.phone}) - Order ${order.orderNumber}",
                utr = order.utr,
                status = "SUCCESSFUL"
            )
        )

        Result.success(Unit)
    }

    suspend fun rejectPaymentOrder(orderId: Long, reason: String = "Payment verification failed / Invalid UTR"): Result<Unit> = withContext(Dispatchers.IO) {
        val order = orderDao.getOrderById(orderId)
            ?: return@withContext Result.failure(Exception("Order not found"))

        if (order.status != "PENDING") {
            return@withContext Result.failure(Exception("Order already processed (${order.status})"))
        }

        // Restore plan slot
        val plan = planDao.getPlanById(order.planId)
        if (plan != null) {
            planDao.updateRemainingSlots(plan.id, plan.remainingSlots + 1)
        }

        // Update Order to REJECTED
        orderDao.updateOrder(
            order.copy(
                status = "REJECTED",
                verifiedAt = System.currentTimeMillis()
            )
        )

        // Log Failed Transaction for tracking in admin panel
        txDao.insertTransaction(
            TransactionRecord(
                userId = order.buyerId,
                type = "BUY_PLAN",
                amount = order.amount,
                isCredit = false,
                title = "Buy Payment Rejected",
                subtitle = "$reason (Order: ${order.orderNumber})",
                utr = order.utr,
                status = "FAILED"
            )
        )

        Result.success(Unit)
    }

    // ---------------- UPI & WITHDRAWAL ----------------

    suspend fun updateUserUpi(userId: Long, upiId: String, bankName: String): Result<Unit> = withContext(Dispatchers.IO) {
        if (upiId.isBlank() || !upiId.contains("@")) {
            return@withContext Result.failure(Exception("Please enter a valid UPI ID (e.g., username@bank)"))
        }
        userDao.updateUpi(userId, upiId.trim(), bankName.trim())
        Result.success(Unit)
    }

    suspend fun requestWithdrawal(userId: Long, amount: Double, upiId: String): Result<Unit> = withContext(Dispatchers.IO) {
        val user = userDao.getUserByIdSync(userId)
            ?: return@withContext Result.failure(Exception("User not found"))

        if (user.isBlocked) {
            return@withContext Result.failure(Exception("Account is blocked"))
        }

        if (amount <= 0) {
            return@withContext Result.failure(Exception("Please enter a valid withdrawal amount"))
        }

        if (user.balance < amount) {
            return@withContext Result.failure(Exception("Insufficient wallet balance (Available: ₹${user.balance.toInt()})"))
        }

        val targetUpi = if (upiId.isNotBlank()) upiId.trim() else user.upiId
        if (targetUpi.isBlank() || !targetUpi.contains("@")) {
            return@withContext Result.failure(Exception("Please provide a valid destination UPI ID"))
        }

        // Debit wallet
        userDao.updateBalance(userId, user.balance - amount)

        // Save withdrawal request
        withdrawalDao.insertWithdrawal(
            WithdrawalRequest(
                userId = user.id,
                userName = user.name,
                userPhone = user.phone,
                amount = amount,
                upiId = targetUpi,
                status = "COMPLETED" // Instant simulated withdrawal clearance
            )
        )

        // Log successful withdrawal transaction
        txDao.insertTransaction(
            TransactionRecord(
                userId = user.id,
                type = "WITHDRAWAL",
                amount = amount,
                isCredit = false,
                title = "Wallet Withdrawal",
                subtitle = "Transferred to UPI: $targetUpi",
                utr = "WTH-" + (100000 + (Math.random() * 900000).toInt()),
                status = "SUCCESSFUL"
            )
        )

        Result.success(Unit)
    }

    // ---------------- ADMIN CONTROLS ----------------

    fun getAllRegularUsers(): Flow<List<User>> = userDao.getAllRegularUsers()

    suspend fun setUserBlockStatus(userId: Long, isBlocked: Boolean): Result<Unit> = withContext(Dispatchers.IO) {
        userDao.updateBlockStatus(userId, isBlocked)
        Result.success(Unit)
    }

    // ---------------- TRANSACTIONS & LOGS ----------------

    fun getAllTransactions(): Flow<List<TransactionRecord>> = txDao.getAllTransactions()
    fun getUserTransactions(userId: Long): Flow<List<TransactionRecord>> = txDao.getTransactionsByUser(userId)
    fun getTransactionsByStatus(status: String): Flow<List<TransactionRecord>> = txDao.getTransactionsByStatus(status)
}
