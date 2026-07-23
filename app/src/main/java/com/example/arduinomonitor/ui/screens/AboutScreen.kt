package com.example.arduinomonitor.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.arduinomonitor.ui.theme.*

private const val DEVELOPER_NAME = "مهندس شاهو احمدیان"
private const val DEVELOPER_EMAIL = "shahoahmdian@gmail.com"

@Composable
fun AboutScreen(onBack: () -> Unit) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpace)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "بازگشت", tint = TextPrimary)
            }
            Text("درباره سازنده", style = MaterialTheme.typography.headlineMedium)
        }

        Spacer(Modifier.height(28.dp))

        Box(
            modifier = Modifier
                .size(84.dp)
                .align(Alignment.CenterHorizontally)
                .clip(CircleShape)
                .background(CardDark),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Code, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(40.dp))
        }

        Spacer(Modifier.height(16.dp))

        Text(
            DEVELOPER_NAME,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardDark),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "این برنامه توسط مهندس شاهو احمدیان ساخته شده برای ساخت پروژه اردوینو.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(20.dp).fillMaxWidth()
            )
        }

        Spacer(Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardDark),
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:$DEVELOPER_EMAIL")
                    }
                    context.startActivity(intent)
                }
        ) {
            Row(
                modifier = Modifier.padding(18.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Email, contentDescription = null, tint = AccentPurple, modifier = Modifier.size(26.dp))
                Spacer(Modifier.width(14.dp))
                Column {
                    Text("ارتباط با سازنده", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(DEVELOPER_EMAIL, style = MaterialTheme.typography.bodyMedium, color = AccentCyan)
                }
            }
        }
    }
}