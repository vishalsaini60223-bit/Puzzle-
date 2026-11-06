package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.i18n.AppLanguage
import com.example.ui.theme.JioDarkBlue
import com.example.ui.theme.JioPrimaryBlue

@Composable
fun AddUpiModal(
    currentUpi: String,
    currentBank: String,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSaveUpi: (upiId: String, bankName: String) -> Unit
) {
    var upiId by remember { mutableStateOf(currentUpi) }
    var bankName by remember { mutableStateOf(currentBank) }
    var isError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (currentUpi.isBlank()) "Add UPI Account" else "Update UPI Account",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = JioDarkBlue
                        )
                        Text(
                            text = "To receive payments when you sell or withdraw",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // UPI ID input
                OutlinedTextField(
                    value = upiId,
                    onValueChange = {
                        upiId = it
                        if (it.contains("@")) isError = false
                    },
                    label = { Text("UPI ID") },
                    placeholder = { Text("e.g. mobile@upi or name@okaxis") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.QrCode, contentDescription = null, tint = JioPrimaryBlue)
                    },
                    isError = isError,
                    supportingText = {
                        if (isError) Text("Please enter a valid UPI ID with '@'")
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = JioPrimaryBlue,
                        focusedLabelColor = JioPrimaryBlue
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("upi_id_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Bank Name input
                OutlinedTextField(
                    value = bankName,
                    onValueChange = { bankName = it },
                    label = { Text("Bank / Provider Name") },
                    placeholder = { Text("e.g. State Bank of India / Paytm") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.AccountBalance, contentDescription = null, tint = JioPrimaryBlue)
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = JioPrimaryBlue,
                        focusedLabelColor = JioPrimaryBlue
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("bank_name_input")
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Save button
                Button(
                    onClick = {
                        if (!upiId.contains("@") || upiId.isBlank()) {
                            isError = true
                        } else {
                            onSaveUpi(upiId.trim(), bankName.trim())
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = JioPrimaryBlue),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("save_upi_button")
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Save UPI Account", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}
