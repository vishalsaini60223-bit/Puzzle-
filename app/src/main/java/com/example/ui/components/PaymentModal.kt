package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.BuyPlan
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.AppStrings
import com.example.ui.theme.JioAccentBlue
import com.example.ui.theme.JioDarkBlue
import com.example.ui.theme.JioLightBlueSurface
import com.example.ui.theme.JioPrimaryBlue
import com.example.ui.theme.JioVibrantCyan
import com.example.ui.theme.ProfitGreen
import com.example.ui.theme.ProfitGreenLight

@Composable
fun PaymentModal(
    plan: BuyPlan,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSubmitPayment: (utr: String) -> Unit
) {
    val context = LocalContext.current
    var utrInput by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    // UPI Deep Link URI
    val upiUri = remember(plan) {
        "upi://pay?pa=${plan.sellerUpi}&pn=${Uri.encode(plan.sellerName)}&am=${plan.amount}&cu=INR&tn=JioPayPlan"
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with close icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = AppStrings.paymentVerification(language),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = JioDarkBlue
                        )
                        Text(
                            text = "Plan ₹${plan.amount.toInt()} • Seller: ${plan.sellerName}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 8% Profit Highlight Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(ProfitGreenLight)
                        .border(1.dp, ProfitGreen.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Pay: ₹${plan.amount.toInt()}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = JioDarkBlue
                            )
                            Text(
                                text = "8% Profit: +₹${plan.profitAmount.toInt()}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = ProfitGreen
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Wallet Credit",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = "₹${plan.totalReturn.toInt()}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = JioPrimaryBlue
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Dynamic QR Code Canvas
                QrCodeView(
                    payload = upiUri,
                    size = 180.dp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = AppStrings.scanQrPay(language),
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Seller UPI Card with Copy button
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = JioLightBlueSurface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Seller UPI ID (Official)",
                                fontSize = 10.sp,
                                color = JioAccentBlue,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = plan.sellerUpi,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = JioDarkBlue
                            )
                        }

                        // Copy UPI ID button
                        Box(
                            modifier = Modifier
                                .testTag("copy_upi_button")
                                .clip(RoundedCornerShape(8.dp))
                                .background(JioPrimaryBlue)
                                .clickable {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("UPI ID", plan.sellerUpi))
                                    Toast.makeText(context, "UPI ID copied: ${plan.sellerUpi}", Toast.LENGTH_SHORT).show()
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy",
                                    tint = Color.White,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Copy",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Optional "Open in UPI App" launcher button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.dp, JioPrimaryBlue, RoundedCornerShape(10.dp))
                        .clickable {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(upiUri))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "No UPI app found. Please copy the UPI ID.", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .padding(vertical = 9.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = null,
                            tint = JioPrimaryBlue,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Pay via GPay / PhonePe / Paytm",
                            color = JioPrimaryBlue,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // UTR / Reference Number Input
                OutlinedTextField(
                    value = utrInput,
                    onValueChange = {
                        utrInput = it
                        if (it.length >= 6) isError = false
                    },
                    label = { Text(AppStrings.enterUtr(language)) },
                    placeholder = { Text("e.g. 432189012345") },
                    isError = isError,
                    supportingText = {
                        if (isError) {
                            Text("Please enter the 12-digit UPI UTR number from your payment app")
                        } else {
                            Text("Payment confirmation will be processed by Admin Vishal Kumar")
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = JioPrimaryBlue,
                        focusedLabelColor = JioPrimaryBlue
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("utr_input_field")
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Submit Payment Button
                Button(
                    onClick = {
                        if (utrInput.trim().length < 6) {
                            isError = true
                        } else {
                            onSubmitPayment(utrInput.trim())
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = JioPrimaryBlue),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_payment_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = AppStrings.submitPayment(language),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
