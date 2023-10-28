package com.example.safetify_test

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.safetify_test.ui.theme.Safetify_TestTheme
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = Firebase.firestore
        val ref = db.collection("user_name").document("username_1")
        var hahaName: String = ""
        ref.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    hahaName= document.data?.getValue("Test").toString()
                    setContent {
                        Safetify_TestTheme {
                            // A surface container using the 'background' color from the theme
                            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                                Greeting(hahaName)
                            }
                        }
                    }
                } else {
                    Log.d(TAG, "No such document")
                    hahaName = "no"
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "No such trash")
                hahaName="ahjsdnl"
            }
        Log.d(TAG, hahaName)

    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
            text = "Hello $name!",
            modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Safetify_TestTheme {
        Greeting("Android")
    }
}