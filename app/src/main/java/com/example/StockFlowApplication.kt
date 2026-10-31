package com.example

import android.app.Application
import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

class StockFlowApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ensureFirebaseInitialized(this)
    }

    companion object {
        private const val TAG = "StockFlowApp"

        fun ensureFirebaseInitialized(context: Context) {
            try {
                val appContext = context.applicationContext ?: context
                if (FirebaseApp.getApps(appContext).isEmpty()) {
                    val options = FirebaseOptions.fromResource(appContext)
                    if (options != null) {
                        FirebaseApp.initializeApp(appContext, options)
                        Log.i(TAG, "FirebaseApp initialized from resources.")
                    } else {
                        // Project configuration fallback to prevent crashes if google-services.json was omitted
                        val fallback = FirebaseOptions.Builder()
                            .setApplicationId("1:405168602623:android:14cdfd476e3249df593bca")
                            .setApiKey("AIzaSyCN-4GwEy7g57lu2g8HiZNch_LugRy-tFM")
                            .setProjectId("gen-lang-client-0190245043")
                            .setStorageBucket("gen-lang-client-0190245043.firebasestorage.app")
                            .build()
                        FirebaseApp.initializeApp(appContext, fallback)
                        Log.i(TAG, "FirebaseApp initialized from fallback options.")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error ensuring FirebaseApp is initialized: ${e.message}", e)
            }
        }
    }
}
