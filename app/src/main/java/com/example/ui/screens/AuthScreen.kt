package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.i18n.AppLanguage
import com.example.ui.theme.JioAccentBlue
import com.example.ui.theme.JioDarkBlue
import com.example.ui.theme.JioPrimaryBlue
import com.example.ui.theme.JioVibrantCyan
import com.example.ui.theme.ProfitGreen
import com.example.ui.theme.ProfitGreenLight

@Composable
fun AuthScreen(
    language: AppLanguage,
    onLoginWithPassword: (phone: String, pass: String) -> Unit,
    onLoginWithOtp: (phone: String, otp: String) -> Unit,
    onRegister: (name: String, phone: String, pass: String) -> Unit,
    onAdminLogin: (pin: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedAuthTab by remember { mutableIntStateOf(0) } // 0: OTP Login, 1: Password Login, 2: Register
    var showAdminDialog by remember { mutableStateOf(false) }

    // Form inputs
    var phoneInput by remember { mutableStateOf("9988776655") }
    var passwordInput by remember { mutableStateOf("1234") }
    var otpInput by remember { mutableStateOf("1234") }
    var nameInput by remember { mutableStateOf("") }
    var otpSent by remember { mutableStateOf(false) }
    var adminPinInput by remember { mutableStateOf("admin") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF6F8FC))
            .statusBarsPadding()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Logo & Brand Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(JioPrimaryBlue, JioAccentBlue)
                    )
                )
                .padding(vertical = 32.dp, horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(2.dp, JioVibrantCyan, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_jiopay_logo),
                        contentDescription = "Jio Pay Logo",
                        modifier = Modifier.size(64.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Jio", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Pay", color = JioVibrantCyan, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                }

                Text(
                    text = "P2P Money Buy & Sell • 8% Commission Profit",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Main Auth Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .testTag("auth_card")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Auth Tab selector
                TabRow(
                    selectedTabIndex = selectedAuthTab,
                    containerColor = Color(0xFFF1F5F9),
                    contentColor = JioPrimaryBlue,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedAuthTab == 0,
                        onClick = { selectedAuthTab = 0 },
                        text = { Text("OTP Login", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedAuthTab == 1,
                        onClick = { selectedAuthTab = 1 },
                        text = { Text("Password", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedAuthTab == 2,
                        onClick = { selectedAuthTab = 2 },
                        text = { Text("Register", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ---------------- TAB 0: OTP LOGIN ----------------
                if (selectedAuthTab == 0) {
                    Text(
                        text = "Sign in via Mobile OTP",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = JioDarkBlue
                    )
                    Text(
                        text = "Enter your mobile number to receive a verification OTP",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = phoneInput,
                        onValueChange = { phoneInput = it },
                        label = { Text("Mobile Number") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = JioPrimaryBlue) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = JioPrimaryBlue,
                            focusedLabelColor = JioPrimaryBlue
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("otp_phone_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (!otpSent) {
                        Button(
                            onClick = { otpSent = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = JioPrimaryBlue),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("send_otp_button")
                        ) {
                            Icon(imageVector = Icons.Default.Sms, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Get OTP", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        // OTP Input with helper pill
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(ProfitGreenLight)
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "OTP sent! Simulation code is 1234",
                                color = ProfitGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = otpInput,
                            onValueChange = { otpInput = it },
                            label = { Text("Enter 4-Digit OTP") },
                            leadingIcon = { Icon(imageVector = Icons.Default.Key, contentDescription = null, tint = JioPrimaryBlue) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = JioPrimaryBlue,
                                focusedLabelColor = JioPrimaryBlue
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("otp_code_input")
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { onLoginWithOtp(phoneInput.trim(), otpInput.trim()) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = JioPrimaryBlue),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("verify_otp_button")
                        ) {
                            Text(text = "Verify & Login", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }

                // ---------------- TAB 1: PASSWORD LOGIN ----------------
                if (selectedAuthTab == 1) {
                    Text(
                        text = "Sign in with Password",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = JioDarkBlue
                    )
                    Text(
                        text = "Demo user: 9988776655 / Password: 1234",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = phoneInput,
                        onValueChange = { phoneInput = it },
                        label = { Text("Mobile Number") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = JioPrimaryBlue) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = JioPrimaryBlue,
                            focusedLabelColor = JioPrimaryBlue
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("pass_phone_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it },
                        label = { Text("Password") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = JioPrimaryBlue) },
                        visualTransformation = PasswordVisualTransformation(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = JioPrimaryBlue,
                            focusedLabelColor = JioPrimaryBlue
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("pass_password_input")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { onLoginWithPassword(phoneInput.trim(), passwordInput.trim()) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = JioPrimaryBlue),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("login_password_button")
                    ) {
                        Text(text = "Login to Jio Pay", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }

                // ---------------- TAB 2: REGISTER ----------------
                if (selectedAuthTab == 2) {
                    Text(
                        text = "Create New Account",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = JioDarkBlue
                    )
                    Text(
                        text = "Join Jio Pay P2P marketplace and start earning 8% profit",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Full Name") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = JioPrimaryBlue) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = JioPrimaryBlue,
                            focusedLabelColor = JioPrimaryBlue
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_name_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = phoneInput,
                        onValueChange = { phoneInput = it },
                        label = { Text("Mobile Number") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = JioPrimaryBlue) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = JioPrimaryBlue,
                            focusedLabelColor = JioPrimaryBlue
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_phone_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it },
                        label = { Text("Create Password") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = JioPrimaryBlue) },
                        visualTransformation = PasswordVisualTransformation(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = JioPrimaryBlue,
                            focusedLabelColor = JioPrimaryBlue
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_pass_input")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { onRegister(nameInput.trim(), phoneInput.trim(), passwordInput.trim()) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = JioPrimaryBlue),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("submit_register_button")
                    ) {
                        Text(text = "Create Account & Start", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Admin Access Card / Shortcut
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1E36)),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clickable { showAdminDialog = !showAdminDialog }
                .testTag("admin_login_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(ProfitGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Admin Portal (Vishal Kumar)",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Verify payments, ₹50k balance & user block controls",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 11.sp
                            )
                        }
                    }

                    Text(
                        text = if (showAdminDialog) "Close" else "Enter",
                        color = JioVibrantCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (showAdminDialog) {
                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = adminPinInput,
                        onValueChange = { adminPinInput = it },
                        label = { Text("Admin PIN / Password", color = Color.White.copy(alpha = 0.8f)) },
                        placeholder = { Text("default: admin", color = Color.Gray) },
                        visualTransformation = PasswordVisualTransformation(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = JioVibrantCyan,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_pin_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { onAdminLogin(adminPinInput.trim()) },
                        colors = ButtonDefaults.buttonColors(containerColor = ProfitGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("admin_login_submit_button")
                    ) {
                        Text(text = "Login as Admin (Vishal Kumar)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
