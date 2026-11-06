package com.example.data.local

import com.example.data.model.BuyPlan
import com.example.data.model.TransactionRecord
import com.example.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DatabaseSeeder {
    suspend fun seedDatabase(database: AppDatabase) {
        withContext(Dispatchers.IO) {
            val userDao = database.userDao()
            val planDao = database.buyPlanDao()
            val txDao = database.transactionDao()

            if (userDao.getUsersCount() == 0) {
                // 1. Seed Admin user (Vishal Kumar, 50k balance, UPI: vishalkumar90@upi)
                val adminId = userDao.insertUser(
                    User(
                        name = "Vishal Kumar (Admin)",
                        phone = "9876543210",
                        password = "admin",
                        upiId = "vishalkumar90@upi",
                        bankName = "State Bank of India",
                        balance = 50000.0,
                        isAdmin = true,
                        isBlocked = false
                    )
                )

                // 2. Seed standard demo users
                val user1Id = userDao.insertUser(
                    User(
                        name = "Rahul Sharma",
                        phone = "9988776655",
                        password = "1234",
                        upiId = "rahul@okhdfcbank",
                        bankName = "HDFC Bank",
                        balance = 1200.0,
                        isAdmin = false,
                        isBlocked = false
                    )
                )

                val user2Id = userDao.insertUser(
                    User(
                        name = "Priya Verma",
                        phone = "9812345678",
                        password = "1234",
                        upiId = "priya@paytm",
                        bankName = "Paytm Payments Bank",
                        balance = 850.0,
                        isAdmin = false,
                        isBlocked = false
                    )
                )

                // Initial opening balance transaction for Admin
                txDao.insertTransaction(
                    TransactionRecord(
                        userId = adminId,
                        type = "ADMIN_ALLOCATION",
                        amount = 50000.0,
                        isCredit = true,
                        title = "Admin Initial Reserve Balance",
                        subtitle = "Seeded pool for P2P buyer transfers",
                        utr = "JIO-INIT-50000",
                        status = "SUCCESSFUL"
                    )
                )

                txDao.insertTransaction(
                    TransactionRecord(
                        userId = user1Id,
                        type = "PROFIT_COMMISSION",
                        amount = 40.0,
                        isCredit = true,
                        title = "8% Buy Profit Commission",
                        subtitle = "Plan ₹500 executed successfully",
                        utr = "UTR-998811223344",
                        status = "SUCCESSFUL"
                    )
                )
            }

            val admin = userDao.getAdminUser()
            val adminId = admin?.id ?: 1L
            val adminUpi = admin?.upiId ?: "vishalkumar90@upi"
            val adminName = admin?.name ?: "Vishal Kumar (Admin)"

            val allRequiredPlans = listOf(
                // Original initial plans
                BuyPlan(amount = 350.0, totalSlots = 10, remainingSlots = 10, commissionPercent = 8.0, sellerId = adminId, sellerName = adminName, sellerUpi = adminUpi),
                BuyPlan(amount = 500.0, totalSlots = 10, remainingSlots = 10, commissionPercent = 8.0, sellerId = adminId, sellerName = adminName, sellerUpi = adminUpi),
                BuyPlan(amount = 700.0, totalSlots = 5, remainingSlots = 5, commissionPercent = 8.0, sellerId = adminId, sellerName = adminName, sellerUpi = adminUpi),
                BuyPlan(amount = 850.0, totalSlots = 9, remainingSlots = 9, commissionPercent = 8.0, sellerId = adminId, sellerName = adminName, sellerUpi = adminUpi),
                BuyPlan(amount = 1000.0, totalSlots = 20, remainingSlots = 20, commissionPercent = 8.0, sellerId = adminId, sellerName = adminName, sellerUpi = adminUpi),

                // Newly requested plans
                BuyPlan(amount = 580.0, totalSlots = 25, remainingSlots = 25, commissionPercent = 8.0, sellerId = adminId, sellerName = adminName, sellerUpi = adminUpi),
                BuyPlan(amount = 650.0, totalSlots = 15, remainingSlots = 15, commissionPercent = 8.0, sellerId = adminId, sellerName = adminName, sellerUpi = adminUpi),
                BuyPlan(amount = 756.0, totalSlots = 25, remainingSlots = 25, commissionPercent = 8.0, sellerId = adminId, sellerName = adminName, sellerUpi = adminUpi),
                BuyPlan(amount = 800.0, totalSlots = 15, remainingSlots = 15, commissionPercent = 8.0, sellerId = adminId, sellerName = adminName, sellerUpi = adminUpi),
                BuyPlan(amount = 2000.0, totalSlots = 6, remainingSlots = 6, commissionPercent = 8.0, sellerId = adminId, sellerName = adminName, sellerUpi = adminUpi),
                BuyPlan(amount = 3000.0, totalSlots = 10, remainingSlots = 10, commissionPercent = 8.0, sellerId = adminId, sellerName = adminName, sellerUpi = adminUpi),
                BuyPlan(amount = 4000.0, totalSlots = 8, remainingSlots = 8, commissionPercent = 8.0, sellerId = adminId, sellerName = adminName, sellerUpi = adminUpi),
                BuyPlan(amount = 10000.0, totalSlots = 5, remainingSlots = 5, commissionPercent = 8.0, sellerId = adminId, sellerName = adminName, sellerUpi = adminUpi)
            )

            for (p in allRequiredPlans) {
                val existing = planDao.getPlanByAmount(p.amount)
                if (existing == null) {
                    planDao.insertPlan(p)
                }
            }
        }
    }
}
