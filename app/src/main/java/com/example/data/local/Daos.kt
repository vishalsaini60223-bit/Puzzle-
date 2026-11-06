package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.BuyPlan
import com.example.data.model.Order
import com.example.data.model.TransactionRecord
import com.example.data.model.User
import com.example.data.model.WithdrawalRequest
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :id")
    fun getUserById(id: Long): Flow<User?>

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserByIdSync(id: Long): User?

    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    suspend fun getUserByPhone(phone: String): User?

    @Query("SELECT * FROM users WHERE isAdmin = 1 LIMIT 1")
    suspend fun getAdminUser(): User?

    @Query("SELECT * FROM users WHERE isAdmin = 0 ORDER BY id DESC")
    fun getAllRegularUsers(): Flow<List<User>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(user: User)

    @Query("UPDATE users SET balance = :newBalance WHERE id = :id")
    suspend fun updateBalance(id: Long, newBalance: Double)

    @Query("UPDATE users SET isBlocked = :isBlocked WHERE id = :id")
    suspend fun updateBlockStatus(id: Long, isBlocked: Boolean)

    @Query("UPDATE users SET upiId = :upiId, bankName = :bankName WHERE id = :id")
    suspend fun updateUpi(id: Long, upiId: String, bankName: String)

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUsersCount(): Int
}

@Dao
interface BuyPlanDao {
    @Query("SELECT * FROM buy_plans WHERE isActive = 1 ORDER BY amount ASC")
    fun getAllActivePlans(): Flow<List<BuyPlan>>

    @Query("SELECT * FROM buy_plans WHERE id = :id")
    suspend fun getPlanById(id: Long): BuyPlan?

    @Query("SELECT * FROM buy_plans WHERE amount = :amount LIMIT 1")
    suspend fun getPlanByAmount(amount: Double): BuyPlan?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(plan: BuyPlan): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlans(plans: List<BuyPlan>)

    @Query("UPDATE buy_plans SET remainingSlots = :slots WHERE id = :id")
    suspend fun updateRemainingSlots(id: Long, slots: Int)

    @Query("SELECT COUNT(*) FROM buy_plans")
    suspend fun getPlansCount(): Int
}

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY id DESC")
    fun getAllOrders(): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE buyerId = :buyerId ORDER BY id DESC")
    fun getOrdersByBuyer(buyerId: Long): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE status = 'PENDING' ORDER BY id DESC")
    fun getPendingOrders(): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE id = :id")
    suspend fun getOrderById(id: Long): Order?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order): Long

    @Update
    suspend fun updateOrder(order: Order)
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY id DESC")
    fun getAllTransactions(): Flow<List<TransactionRecord>>

    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY id DESC")
    fun getTransactionsByUser(userId: Long): Flow<List<TransactionRecord>>

    @Query("SELECT * FROM transactions WHERE status = :status ORDER BY id DESC")
    fun getTransactionsByStatus(status: String): Flow<List<TransactionRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(tx: TransactionRecord): Long
}

@Dao
interface WithdrawalDao {
    @Query("SELECT * FROM withdrawals ORDER BY id DESC")
    fun getAllWithdrawals(): Flow<List<WithdrawalRequest>>

    @Query("SELECT * FROM withdrawals WHERE userId = :userId ORDER BY id DESC")
    fun getWithdrawalsByUser(userId: Long): Flow<List<WithdrawalRequest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWithdrawal(req: WithdrawalRequest): Long

    @Query("UPDATE withdrawals SET status = :status WHERE id = :id")
    suspend fun updateWithdrawalStatus(id: Long, status: String)
}
