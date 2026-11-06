package com.example

import com.example.data.model.BuyPlan
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testPlanProfitCalculation() {
        // Plan 350 -> 8% profit is 28, total return is 378
        val plan350 = BuyPlan(
            amount = 350.0,
            totalSlots = 10,
            remainingSlots = 10,
            commissionPercent = 8.0,
            sellerId = 1L,
            sellerName = "Vishal Kumar (Admin)",
            sellerUpi = "vishalkumar90@upi"
        )
        assertEquals(28.0, plan350.profitAmount, 0.001)
        assertEquals(378.0, plan350.totalReturn, 0.001)

        // Plan 500 -> 8% profit is 40, total return is 540
        val plan500 = BuyPlan(
            amount = 500.0,
            totalSlots = 10,
            remainingSlots = 10,
            commissionPercent = 8.0,
            sellerId = 1L,
            sellerName = "Vishal Kumar (Admin)",
            sellerUpi = "vishalkumar90@upi"
        )
        assertEquals(40.0, plan500.profitAmount, 0.001)
        assertEquals(540.0, plan500.totalReturn, 0.001)

        // Plan 700 -> 8% profit is 56, total return is 756
        val plan700 = BuyPlan(
            amount = 700.0,
            totalSlots = 5,
            remainingSlots = 5,
            commissionPercent = 8.0,
            sellerId = 1L,
            sellerName = "Vishal Kumar (Admin)",
            sellerUpi = "vishalkumar90@upi"
        )
        assertEquals(56.0, plan700.profitAmount, 0.001)
        assertEquals(756.0, plan700.totalReturn, 0.001)

        // Plan 850 -> 8% profit is 68, total return is 918
        val plan850 = BuyPlan(
            amount = 850.0,
            totalSlots = 9,
            remainingSlots = 9,
            commissionPercent = 8.0,
            sellerId = 1L,
            sellerName = "Vishal Kumar (Admin)",
            sellerUpi = "vishalkumar90@upi"
        )
        assertEquals(68.0, plan850.profitAmount, 0.001)
        assertEquals(918.0, plan850.totalReturn, 0.001)

        // Plan 1000 -> 8% profit is 80, total return is 1080
        val plan1000 = BuyPlan(
            amount = 1000.0,
            totalSlots = 20,
            remainingSlots = 20,
            commissionPercent = 8.0,
            sellerId = 1L,
            sellerName = "Vishal Kumar (Admin)",
            sellerUpi = "vishalkumar90@upi"
        )
        assertEquals(80.0, plan1000.profitAmount, 0.001)
        assertEquals(1080.0, plan1000.totalReturn, 0.001)

        // Plan 800 -> 8% profit is 64, total return is 864
        val plan800 = BuyPlan(amount = 800.0, totalSlots = 15, remainingSlots = 15, commissionPercent = 8.0, sellerId = 1L, sellerName = "Vishal Kumar (Admin)", sellerUpi = "vishalkumar90@upi")
        assertEquals(64.0, plan800.profitAmount, 0.001)
        assertEquals(864.0, plan800.totalReturn, 0.001)

        // Plan 4000 -> 8% profit is 320, total return is 4320
        val plan4000 = BuyPlan(amount = 4000.0, totalSlots = 8, remainingSlots = 8, commissionPercent = 8.0, sellerId = 1L, sellerName = "Vishal Kumar (Admin)", sellerUpi = "vishalkumar90@upi")
        assertEquals(320.0, plan4000.profitAmount, 0.001)
        assertEquals(4320.0, plan4000.totalReturn, 0.001)

        // Plan 10000 -> 8% profit is 800, total return is 10800
        val plan10000 = BuyPlan(amount = 10000.0, totalSlots = 5, remainingSlots = 5, commissionPercent = 8.0, sellerId = 1L, sellerName = "Vishal Kumar (Admin)", sellerUpi = "vishalkumar90@upi")
        assertEquals(800.0, plan10000.profitAmount, 0.001)
        assertEquals(10800.0, plan10000.totalReturn, 0.001)

        // Plan 2000 -> 8% profit is 160, total return is 2160
        val plan2000 = BuyPlan(amount = 2000.0, totalSlots = 6, remainingSlots = 6, commissionPercent = 8.0, sellerId = 1L, sellerName = "Vishal Kumar (Admin)", sellerUpi = "vishalkumar90@upi")
        assertEquals(160.0, plan2000.profitAmount, 0.001)
        assertEquals(2160.0, plan2000.totalReturn, 0.001)

        // Plan 3000 -> 8% profit is 240, total return is 3240
        val plan3000 = BuyPlan(amount = 3000.0, totalSlots = 10, remainingSlots = 10, commissionPercent = 8.0, sellerId = 1L, sellerName = "Vishal Kumar (Admin)", sellerUpi = "vishalkumar90@upi")
        assertEquals(240.0, plan3000.profitAmount, 0.001)
        assertEquals(3240.0, plan3000.totalReturn, 0.001)

        // Plan 650 -> 8% profit is 52, total return is 702
        val plan650 = BuyPlan(amount = 650.0, totalSlots = 15, remainingSlots = 15, commissionPercent = 8.0, sellerId = 1L, sellerName = "Vishal Kumar (Admin)", sellerUpi = "vishalkumar90@upi")
        assertEquals(52.0, plan650.profitAmount, 0.001)
        assertEquals(702.0, plan650.totalReturn, 0.001)

        // Plan 756 -> 8% profit is 60.48, total return is 816.48
        val plan756 = BuyPlan(amount = 756.0, totalSlots = 25, remainingSlots = 25, commissionPercent = 8.0, sellerId = 1L, sellerName = "Vishal Kumar (Admin)", sellerUpi = "vishalkumar90@upi")
        assertEquals(60.48, plan756.profitAmount, 0.001)
        assertEquals(816.48, plan756.totalReturn, 0.001)

        // Plan 580 -> 8% profit is 46.4, total return is 626.4
        val plan580 = BuyPlan(amount = 580.0, totalSlots = 25, remainingSlots = 25, commissionPercent = 8.0, sellerId = 1L, sellerName = "Vishal Kumar (Admin)", sellerUpi = "vishalkumar90@upi")
        assertEquals(46.4, plan580.profitAmount, 0.001)
        assertEquals(626.4, plan580.totalReturn, 0.001)
    }
}
