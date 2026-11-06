package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String,
    val password: String,
    val upiId: String = "",
    val bankName: String = "",
    val balance: Double = 0.0,
    val isAdmin: Boolean = false,
    val isBlocked: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "buy_plans")
data class BuyPlan(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,
    val totalSlots: Int,
    val remainingSlots: Int,
    val commissionPercent: Double = 8.0,
    val sellerId: Long,
    val sellerName: String,
    val sellerUpi: String,
    val isActive: Boolean = true
) {
    val profitAmount: Double get() = (amount * commissionPercent) / 100.0
    val totalReturn: Double get() = amount + profitAmount
}

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderNumber: String,
    val planId: Long,
    val buyerId: Long,
    val buyerName: String,
    val buyerPhone: String,
    val sellerId: Long,
    val sellerName: String,
    val sellerUpi: String,
    val amount: Double,
    val profitAmount: Double,
    val totalReturn: Double,
    val utr: String,
    val status: String, // PENDING, VERIFIED, REJECTED
    val createdAt: Long = System.currentTimeMillis(),
    val verifiedAt: Long? = null
)

@Entity(tableName = "transactions")
data class TransactionRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val type: String, // BUY_PLAN, PROFIT_COMMISSION, SELL_CREDIT, WITHDRAWAL, ADMIN_TRANSFER
    val amount: Double,
    val isCredit: Boolean,
    val title: String,
    val subtitle: String,
    val utr: String = "",
    val status: String, // SUCCESSFUL, PENDING, FAILED
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "withdrawals")
data class WithdrawalRequest(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val userName: String,
    val userPhone: String,
    val amount: Double,
    val upiId: String,
    val status: String, // PENDING, COMPLETED, REJECTED
    val timestamp: Long = System.currentTimeMillis()
)
