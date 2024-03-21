package com.paulklauser.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.actionCodeSettings
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.paulklauser.myapplication.ui.theme.FirebaseAuthTheme
import timber.log.Timber

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree())

        val emailLink = intent.data.toString()
        val auth = Firebase.auth
        // Confirm the link is a sign-in with email link.
        if (auth.isSignInWithEmailLink(emailLink)) {
            // Retrieve this from wherever you stored it
            val email = "ert342@gmail.com"

            // The client SDK will parse the code from the link for you.
            auth.signInWithEmailLink(email, emailLink)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Timber.d("Successfully signed in with email link!")
                        val result = task.result
                        // You can access the new user via result.getUser()
                        // Additional user info profile *not* available via:
                        // result.getAdditionalUserInfo().getProfile() == null
                        // You can check if the user is new or existing:
                        // result.getAdditionalUserInfo().isNewUser()
                    } else {
                        Timber.d("Error signing in with email link", task.exception)
                    }
                }
        }

        setContent {
            FirebaseAuthTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Column {
        var email by remember { mutableStateOf("ert342@gmail.com") }
        var password by remember { mutableStateOf("") }
        TextField(value = email, onValueChange = { email = it })
        TextField(value = password, onValueChange = { password = it })
        Button(onClick = {
            Firebase.auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Timber.d("Sign in successful")
                    } else {
                        Timber.d("Sign in failed")
                    }
                }
        }) {
            Text(text = "Sign in with email & password")
        }
        Button(onClick = {
            val actionCodeSettings = actionCodeSettings {
                // URL you want to redirect back to. The domain (www.example.com) for this
                // URL must be whitelisted in the Firebase Console.
                url = "https://authtest-e6440.firebaseapp.com"
                // This must be true
                handleCodeInApp = true
//                setIOSBundleId("com.example.ios")
                setAndroidPackageName(
                    "com.paulklauser.firebaseauth",
                    true, // installIfNotAvailable
                    "0", // minimumVersion
                )
            }

            Firebase.auth.sendSignInLinkToEmail(email, actionCodeSettings)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Timber.d("Sign in link sent")
                    } else {
                        Timber.d("Sign in link failed")
                    }
                }
        }) {
            Text(text = "Create account with email magic link")
        }

        Button(onClick = {
            // reset password
            Firebase.auth.sendPasswordResetEmail(email)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Timber.d("Password reset email sent")
                    } else {
                        Timber.d("Password reset email failed")
                    }
                }
        }) {
            Text(text = "reset password")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FirebaseAuthTheme {
        Greeting("Android")
    }
}