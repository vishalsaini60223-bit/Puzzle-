package com.example.ui.i18n

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf

enum class AppLanguage(val code: String, val displayName: String, val flag: String) {
    ENGLISH("en", "English", "🇬🇧"),
    HINDI("hi", "हिन्दी", "🇮🇳")
}

val LocalAppLanguage = compositionLocalOf { AppLanguage.ENGLISH }

object AppStrings {
    fun appTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.HINDI -> "जियो पे"
        AppLanguage.ENGLISH -> "Jio Pay"
    }

    fun appTagline(lang: AppLanguage) = when (lang) {
        AppLanguage.HINDI -> "P2P मनी बाय-सेल और 8% कमीशन मुनाफा"
        AppLanguage.ENGLISH -> "P2P Money Buy-Sell & 8% Profit"
    }

    fun walletBalance(lang: AppLanguage) = when (lang) {
        AppLanguage.HINDI -> "वॉलेट बैलेंस"
        AppLanguage.ENGLISH -> "Wallet Balance"
    }

    fun buy(lang: AppLanguage) = when (lang) {
        AppLanguage.HINDI -> "खरीदें (Buy)"
        AppLanguage.ENGLISH -> "Buy Money"
    }

    fun sell(lang: AppLanguage) = when (lang) {
        AppLanguage.HINDI -> "बेचें (Sell)"
        AppLanguage.ENGLISH -> "Sell Money"
    }

    fun withdraw(lang: AppLanguage) = when (lang) {
        AppLanguage.HINDI -> "निकासी (Withdraw)"
        AppLanguage.ENGLISH -> "Withdraw"
    }

    fun addUpi(lang: AppLanguage) = when (lang) {
        AppLanguage.HINDI -> "UPI जोड़ें"
        AppLanguage.ENGLISH -> "Add UPI"
    }

    fun history(lang: AppLanguage) = when (lang) {
        AppLanguage.HINDI -> "लेनदेन रिकॉर्ड"
        AppLanguage.ENGLISH -> "History"
    }

    fun support(lang: AppLanguage) = when (lang) {
        AppLanguage.HINDI -> "कस्टमर सर्विस"
        AppLanguage.ENGLISH -> "Support"
    }

    fun adminPanel(lang: AppLanguage) = when (lang) {
        AppLanguage.HINDI -> "एडमिन पैनल"
        AppLanguage.ENGLISH -> "Admin Panel"
    }

    fun profitBannerTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.HINDI -> "हर खरीद पर 8% कमीशन मुनाफा"
        AppLanguage.ENGLISH -> "Earn 8% Guaranteed Profit on Every Buy"
    }

    fun profitBannerDesc(lang: AppLanguage) = when (lang) {
        AppLanguage.HINDI -> "पेमेंट वेरिफाई होते ही आपके वॉलेट में 8% अतिरिक्त राशि जुड़ जाएगी"
        AppLanguage.ENGLISH -> "Extra 8% credited automatically to your wallet upon payment confirmation"
    }

    fun availablePlans(lang: AppLanguage) = when (lang) {
        AppLanguage.HINDI -> "उपलब्ध प्लान्स"
        AppLanguage.ENGLISH -> "Available Buy Plans"
    }

    fun slotsLeft(lang: AppLanguage, count: Int) = when (lang) {
        AppLanguage.HINDI -> "$count स्लॉट बाकी"
        AppLanguage.ENGLISH -> "$count slots left"
    }

    fun totalReturn(lang: AppLanguage) = when (lang) {
        AppLanguage.HINDI -> "कुल प्राप्ति"
        AppLanguage.ENGLISH -> "Total Return"
    }

    fun profitAmount(lang: AppLanguage) = when (lang) {
        AppLanguage.HINDI -> "8% मुनाफा"
        AppLanguage.ENGLISH -> "8% Profit"
    }

    fun buyNow(lang: AppLanguage) = when (lang) {
        AppLanguage.HINDI -> "अभी खरीदें"
        AppLanguage.ENGLISH -> "Buy Now"
    }

    fun soldOut(lang: AppLanguage) = when (lang) {
        AppLanguage.HINDI -> "समाप्त"
        AppLanguage.ENGLISH -> "Sold Out"
    }

    fun recentTransactions(lang: AppLanguage) = when (lang) {
        AppLanguage.HINDI -> "हाल के लेनदेन"
        AppLanguage.ENGLISH -> "Recent Transactions"
    }

    fun viewAll(lang: AppLanguage) = when (lang) {
        AppLanguage.HINDI -> "सभी देखें"
        AppLanguage.ENGLISH -> "View All"
    }

    fun paymentVerification(lang: AppLanguage) = when (lang) {
        AppLanguage.HINDI -> "पेमेंट सत्यापन और ट्रांसफर"
        AppLanguage.ENGLISH -> "Payment Verification"
    }

    fun scanQrPay(lang: AppLanguage) = when (lang) {
        AppLanguage.HINDI -> "QR कोड स्कैन करें या UPI कॉपी करें"
        AppLanguage.ENGLISH -> "Scan QR or Copy UPI ID to Pay"
    }

    fun copyUpi(lang: AppLanguage) = when (lang) {
        AppLanguage.HINDI -> "UPI कॉपी करें"
        AppLanguage.ENGLISH -> "Copy UPI ID"
    }

    fun enterUtr(lang: AppLanguage) = when (lang) {
        AppLanguage.HINDI -> "12 अंकों का UTR / रेफ़रेंस नंबर दर्ज करें"
        AppLanguage.ENGLISH -> "Enter 12-digit UTR / Reference No."
    }

    fun submitPayment(lang: AppLanguage) = when (lang) {
        AppLanguage.HINDI -> "पेमेंट सबमिट करें"
        AppLanguage.ENGLISH -> "Submit Payment Proof"
    }

    fun pendingVerification(lang: AppLanguage) = when (lang) {
        AppLanguage.HINDI -> "सत्यापन लंबित"
        AppLanguage.ENGLISH -> "Pending Verification"
    }

    fun confirmTransfer(lang: AppLanguage) = when (lang) {
        AppLanguage.HINDI -> "पुष्टि करें और वॉलेट में जोड़ें"
        AppLanguage.ENGLISH -> "Confirm & Transfer to Wallet"
    }

    fun reject(lang: AppLanguage) = when (lang) {
        AppLanguage.HINDI -> "अस्वीकार करें"
        AppLanguage.ENGLISH -> "Reject"
    }

    fun blockUser(lang: AppLanguage) = when (lang) {
        AppLanguage.HINDI -> "ब्लॉक करें"
        AppLanguage.ENGLISH -> "Block User"
    }

    fun unblockUser(lang: AppLanguage) = when (lang) {
        AppLanguage.HINDI -> "अनब्लॉक करें"
        AppLanguage.ENGLISH -> "Unblock User"
    }

    fun statusSuccessful(lang: AppLanguage) = when (lang) {
        AppLanguage.HINDI -> "सफल"
        AppLanguage.ENGLISH -> "Successful"
    }

    fun statusPending(lang: AppLanguage) = when (lang) {
        AppLanguage.HINDI -> "लंबित"
        AppLanguage.ENGLISH -> "Pending"
    }

    fun statusFailed(lang: AppLanguage) = when (lang) {
        AppLanguage.HINDI -> "असफल"
        AppLanguage.ENGLISH -> "Failed"
    }

    fun customerSupportTelegram(lang: AppLanguage) = when (lang) {
        AppLanguage.HINDI -> "टेलीग्राम पर संपर्क करें (@Sainishaab9076)"
        AppLanguage.ENGLISH -> "Contact Support on Telegram (@Sainishaab9076)"
    }
}
