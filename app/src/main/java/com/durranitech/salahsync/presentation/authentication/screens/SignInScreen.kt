import android.view.RoundedCorner
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.durranitech.salahsync.domain.UserRole
import com.durranitech.salahsync.ui.theme.Dark_Green
import com.durranitech.salahsync.ui.theme.Darker_Indigo
import com.durranitech.salahsync.ui.theme.Light_Green_For_Card
import com.durranitech.salahsync.ui.theme.Medium_Blue
import com.durranitech.salahsync.ui.theme.Text_Dark_Green
import com.durranitech.salahsync.ui.theme.Text_Light_Green

@Composable
fun SignInScreen(
    role: UserRole,
    onBack: () -> Unit,
    onSwitchToSignUp: () -> Unit,
    paddingValues: PaddingValues
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val verticalScrol = rememberScrollState()

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
            .padding(24.dp)
            .verticalScroll(verticalScrol)
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        // Header Section
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(onClick = onBack, modifier = Modifier.align(Alignment.Start)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Text_Light_Green)
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
            Text(titleText, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Medium, color = Text_Dark_Green)
            Text(
                "Welcome back to SalahSync",
                fontSize = 14.sp,
                color = Text_Light_Green
            )
        }

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
                            MaterialTheme.colorScheme.errorContainer, RoundedCornerShape(8.dp)
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
                    focusedBorderColor = Text_Light_Green,
                    unfocusedBorderColor = Dark_Green
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
                    focusedBorderColor = Text_Light_Green,
                    unfocusedBorderColor = Dark_Green
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Button(
                onClick = {
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
                            ),
                            shape = MaterialTheme.shapes.medium
                        ),
                    contentAlignment = Alignment.Center
                ){
                    if (loading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically){
                            Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text("Sign In as ${role.name}")
                        }

                    }
                }

            }
        }

        // Demo Account
        Card(
            colors = CardDefaults.cardColors(containerColor = Light_Green_For_Card),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().border(width = 1.dp, color = Text_Light_Green, shape = RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Try Demo Account",
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "Use this demo to explore SalahSync",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            "Demo ${role.name}", fontSize = 12.sp, fontWeight = FontWeight.Medium
                        )
                        Text(
                            if (role == UserRole.IMAM) "imam@mosque.demo" else "muqtadi@mosque.demo",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    TextButton(onClick = {
                        email =
                            if (role == UserRole.MUQTADI) "imam@mosque.demo" else "muqtadi@mosque.demo"
                        password = "demo123"
                    }, shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Darker_Indigo)) {
                        Text("Use Demo", color = Color.White)
                    }
                }
            }
        }

        // Sign Up Switch
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            HorizontalDivider(
                Modifier.padding(vertical = 4.dp),
                DividerDefaults.Thickness,
                Text_Light_Green
            )
            TextButton(onClick = onSwitchToSignUp) {
                Text(
                    buildAnnotatedString {
                        append("Don’t have an account?")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Text_Dark_Green)) {
                            append(" Sign up as ${role.name}")
                        }

                    },
                    textAlign = TextAlign.Center,
                    color = Text_Light_Green

                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
private fun SignInScreenPre() {
    SignInScreen(
        role = UserRole.IMAM,
        onBack = {},
        onSwitchToSignUp = {},
        PaddingValues(16.dp)
    )
}