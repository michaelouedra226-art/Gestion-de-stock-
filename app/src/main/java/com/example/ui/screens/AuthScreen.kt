package com.example.ui.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.example.R
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.LocalBrandColor
import com.example.ui.theme.RedDanger
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletBrand
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private fun Context.findActivity(): Activity? {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    var isLoading by remember { mutableStateOf(false) }
    var authErrorDialog by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val brand = LocalBrandColor.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg),
        contentAlignment = Alignment.Center
    ) {
        // Abstract glowing geometric backdrop
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Radial soft glows
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(brand.copy(alpha = 0.12f), Color.Transparent),
                    center = Offset(w * 0.25f, h * 0.2f),
                    radius = w * 0.6f
                ),
                radius = w * 0.6f,
                center = Offset(w * 0.25f, h * 0.2f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(VioletBrand.copy(alpha = 0.10f), Color.Transparent),
                    center = Offset(w * 0.8f, h * 0.75f),
                    radius = w * 0.7f
                ),
                radius = w * 0.7f,
                center = Offset(w * 0.8f, h * 0.75f)
            )

            // Dynamic isometric line cube
            val path = Path().apply {
                moveTo(w * 0.5f, h * 0.16f)
                lineTo(w * 0.82f, h * 0.28f)
                lineTo(w * 0.82f, h * 0.48f)
                lineTo(w * 0.5f, h * 0.60f)
                lineTo(w * 0.18f, h * 0.48f)
                lineTo(w * 0.18f, h * 0.28f)
                close()
                moveTo(w * 0.5f, h * 0.60f)
                lineTo(w * 0.5f, h * 0.38f)
                moveTo(w * 0.18f, h * 0.28f)
                lineTo(w * 0.5f, h * 0.38f)
                lineTo(w * 0.82f, h * 0.28f)
            }
            drawPath(path, color = Color(0x225EEAD4), style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round))
            drawCircle(color = brand, radius = 5.dp.toPx(), center = Offset(w * 0.5f, h * 0.16f))
        }

        // Glassmorphism login card
        Column(
            modifier = Modifier
                .fillMaxWidth(0.90f)
                .clip(RoundedCornerShape(24.dp))
                .background(DarkSurface.copy(alpha = 0.88f))
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(brand.copy(alpha = 0.35f), DarkBorder)
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Badge
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(brand, VioletBrand)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "SF",
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    color = Color(0xFF0A0E1A)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "StockFlow",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Plateforme premium de gestion des stocks B2B",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Interactive Google Sign-In Button
            Button(
                onClick = {
                    isLoading = true
                    executeGoogleSignIn(
                        context = context,
                        credentialManager = credentialManager,
                        coroutineScope = coroutineScope,
                        onSuccess = {
                            isLoading = false
                            onAuthSuccess()
                        },
                        onError = { message ->
                            isLoading = false
                            authErrorDialog = message
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(message)
                            }
                        },
                        onCancelled = {
                            isLoading = false
                        }
                    )
                },
                enabled = !isLoading,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF0F172A)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("google_sign_in_button")
            ) {
                if (isLoading) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color(0xFF0F172A),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Connexion en cours…",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // Clean 'G' mark
                        Text(
                            text = "G",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = Color(0xFF4285F4)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Continuer avec Google",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Accès multi-tenant sécurisé par entreprise",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextMuted,
                    fontSize = 11.sp
                )
            )
        }

        if (authErrorDialog != null) {
            AlertDialog(
                onDismissRequest = { authErrorDialog = null },
                title = {
                    Text(
                        text = "Information de connexion",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                },
                text = {
                    Text(
                        text = authErrorDialog ?: "",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextSecondary,
                            lineHeight = 20.sp
                        )
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { authErrorDialog = null },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = brand,
                            contentColor = Color(0xFF0A0E1A)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Compris", fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = DarkSurfaceCard,
                shape = RoundedCornerShape(16.dp)
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}

private fun executeGoogleSignIn(
    context: Context,
    credentialManager: CredentialManager,
    coroutineScope: CoroutineScope,
    onSuccess: () -> Unit,
    onError: (String) -> Unit,
    onCancelled: () -> Unit
) {
    val activity = context.findActivity()
    if (activity == null) {
        Log.e("Auth", "Unable to locate host Activity from context: $context")
        onError("Impossible d'associer la fenêtre d'authentification Google (Activity manquante).")
        return
    }

    val serverClientId = try {
        context.getString(R.string.default_web_client_id)
    } catch (e: Exception) {
        Log.e("Auth", "Failed to retrieve default_web_client_id", e)
        onError("Configuration Google Sign-In introuvable (default_web_client_id manquant).")
        return
    }

    val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = serverClientId).build()
    val request = GetCredentialRequest.Builder()
        .addCredentialOption(signInOption)
        .build()

    coroutineScope.launch {
        try {
            val result = credentialManager.getCredential(activity, request)
            val credential = result.credential
            if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                Firebase.auth.signInWithCredential(authCredential).await()
                onSuccess()
            } else {
                onError("Type d'identifiant Google inattendu (${credential.type}).")
            }
        } catch (e: GetCredentialCancellationException) {
            Log.w("Auth", "Google Sign-In cancelled by user: ${e.message}")
            onCancelled()
        } catch (e: Exception) {
            Log.e("Auth", "Google Sign-In failed", e)
            val rawMsg = e.message ?: e.localizedMessage ?: ""
            val userMsg = when {
                rawMsg.contains("10") || rawMsg.contains("DEVELOPER_ERROR") ->
                    "Erreur Google (Code 10 : DEVELOPER_ERROR).\n\nL'application n'est pas autorisée par Firebase pour cette signature. Assurez-vous d'utiliser l'APK officiel signé avec l'empreinte SHA-1 enregistrée (9D:D2:70:BD:58:5B:EF:15:21:25:92:53:DE:93:4E:81:69:30:08:0F)."
                rawMsg.contains("16") || rawMsg.contains("CANCELLED") ->
                    "Sélection de compte annulée ou aucun compte Google valide disponible sur cet appareil."
                rawMsg.contains("network", ignoreCase = true) || rawMsg.contains("timeout", ignoreCase = true) ->
                    "Connexion réseau impossible. Vérifiez votre connexion Internet."
                else ->
                    "Échec de connexion Google : ${e.localizedMessage ?: "erreur inconnue"}"
            }
            onError(userMsg)
        }
    }
}
