package com.example

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.StockFlowRepository
import com.example.ui.screens.StockFlowApp
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.RedDanger
import com.example.ui.theme.StockFlowTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        try {
            StockFlowApplication.ensureFirebaseInitialized(applicationContext)
        } catch (e: Exception) {
            Log.e("MainActivity", "FirebaseApp init failure: ${e.message}", e)
        }

        setContent {
            StockFlowTheme {
                var initError by remember { mutableStateOf<String?>(null) }
                val repository = remember {
                    try {
                        StockFlowRepository(applicationContext)
                    } catch (e: Throwable) {
                        Log.e("MainActivity", "StockFlowRepository init error", e)
                        initError = e.localizedMessage ?: "Erreur d'initialisation des services"
                        null
                    }
                }

                if (repository != null) {
                    StockFlowApp(repository = repository)
                } else {
                    StartupErrorScreen(
                        message = initError ?: "Impossible d'initialiser les services applicatifs.",
                        onRetry = { recreate() }
                    )
                }
            }
        }
    }
}

@Composable
private fun StartupErrorScreen(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurface)
                .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "StockFlow",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5EEAD4)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Initialisation impossible",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF5EEAD4),
                    contentColor = Color(0xFF0A0E1A)
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Réessayer", fontWeight = FontWeight.Bold)
            }
        }
    }
}
