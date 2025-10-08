import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.durranitech.salahsync.R
import com.durranitech.salahsync.domain.UserRole
import com.durranitech.salahsync.ui.theme.Dark_Green
import com.durranitech.salahsync.ui.theme.Darker_Indigo
import com.durranitech.salahsync.ui.theme.Light_Green_For_Card
import com.durranitech.salahsync.ui.theme.Medium_Blue
import com.durranitech.salahsync.ui.theme.Text_Dark_Green
import com.durranitech.salahsync.ui.theme.Text_Light_Green

@Composable
fun SignInScreen(
    role: UserRole, onBack: () -> Unit, onSwitchToSignUp: (UserRole) -> Unit, paddingValues: PaddingValues,onSwitchToImamDashboard: () -> Unit,
    onSwitchToMuqtadiDashboard: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val (gradientColors, titleText) = when (role) {
        UserRole.IMAM -> listOf(
            Medium_Blue, Darker_Indigo
        ) to "Imam Sign In"

        UserRole.MUQTADI -> listOf(
            Medium_Blue, Darker_Indigo
        ) to "Muqtadi Sign In"
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            )
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Image(
                    painterResource(R.drawable.main_icon),
                    contentDescription = "Main Icon",
                    Modifier.height(96.dp)
                )
            }
        }
        Text(
            "SalahSync",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = Text_Light_Green
        )
        Text(
            "Prayer Time Management for Mosques",
            style = MaterialTheme.typography.labelMedium,
            color = Text_Light_Green
        )

        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // Header Section
            Column(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 12.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceContainer,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextButton(onClick = onBack, modifier = Modifier.align(Alignment.Start)) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Text_Light_Green
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Back to role selection", color = Text_Light_Green, fontSize = 16.sp)
                }

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            brush = Brush.linearGradient(gradientColors), shape = CircleShape
                        ), contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Role Icon",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(96.dp),
                    )
                }

                Spacer(Modifier.height(12.dp))
                Text(
                    titleText,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Medium,
                    color = Text_Dark_Green
                )
                Text(
                    "Welcome back to SalahSync", fontSize = 14.sp, color = Text_Light_Green
                )


                // Sign In Form
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {

                    if (error != null) {
                        Text(
                            text = error!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.errorContainer,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(8.dp)
                        )
                    }

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Text_Light_Green, unfocusedBorderColor = Dark_Green
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        singleLine = true,
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = Text_Light_Green
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Text_Light_Green, unfocusedBorderColor = Dark_Green
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(Modifier.height(4.dp))
                    Button(
                        onClick = {
                            if (role == UserRole.IMAM) onSwitchToImamDashboard() else onSwitchToMuqtadiDashboard()
                            loading = true
                            error = null
                        },
                        enabled = !loading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        contentPadding = (PaddingValues(0.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(Medium_Blue, Darker_Indigo)
                                    ), shape = MaterialTheme.shapes.medium
                                ), contentAlignment = Alignment.Center
                        ) {
                            if (loading) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null)
                                    Spacer(Modifier.width(6.dp))
                                    Text("Sign In as ${role.name}")
                                }

                            }
                        }

                    }
                }

                // Demo Account
                Spacer(Modifier.height(16.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = Light_Green_For_Card),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = Text_Light_Green,
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Try Demo Account",
                            fontWeight = FontWeight.Medium,
                            color = Text_Dark_Green
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Use this demo to explore SalahSync",
                            fontSize = 12.sp,
                            color = Text_Light_Green
                        )

                        Spacer(Modifier.height(12.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    "Demo ${role.name}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Text_Light_Green
                                )
                                Text(
                                    if (role == UserRole.IMAM) "imam@mosque.demo" else "muqtadi@mosque.demo",
                                    fontSize = 12.sp,
                                    color = Text_Light_Green
                                )
                            }
                            TextButton(
                                onClick = {
                                    email =
                                        if (role == UserRole.MUQTADI) "imam@mosque.demo" else "muqtadi@mosque.demo"
                                    password = "demo123"
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Darker_Indigo)
                            ) {
                                Text("Use Demo", color = Color.White)
                            }
                        }
                    }
                }
            }

            // Sign Up Switch
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                HorizontalDivider(
                    Modifier.padding(18.dp), DividerDefaults.Thickness, Text_Light_Green
                )
                TextButton(onClick = {onSwitchToSignUp(role)}) {
                    Text(
                        buildAnnotatedString {
                            append("Don’t have an account?")
                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Bold, color = Text_Dark_Green
                                )
                            ) {
                                append(" Sign up as ${role.name}")
                            }

                        }, textAlign = TextAlign.Center, color = Text_Light_Green

                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Text(
                "Empowering Masajid, strengthening our Ummah",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = Text_Light_Green.copy(0.6f),
                letterSpacing = 0.2.sp,
                textAlign = TextAlign.Center
            )
        }

    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
private fun SignInScreenPre() {
    SignInScreen(
        role = UserRole.IMAM, onBack = {}, onSwitchToSignUp = {}, PaddingValues(16.dp),
        onSwitchToImamDashboard = {}, onSwitchToMuqtadiDashboard = {}
    )
}