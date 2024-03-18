package com.example.safetify203v

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.safetify203v.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.loginButton.setOnClickListener{
            val email = binding.loginEmail.text.toString()
            val password = binding.loginPassword.text.toString()

            if(email.isNotEmpty() && password.isNotEmpty()){

                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener{
                    if(it.isSuccessful){
                        val user = firebaseAuth.currentUser
                        if (user != null && user.isEmailVerified) {
                            val intent = Intent(this, SecondActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, "Please verify your email", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            }

        }

        binding.forgotPassword.setOnClickListener{
            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.forgotpass, null)
            val userEmail = view.findViewById<EditText>(R.id.editBox)

            builder.setView(view)
            val dialog = builder.create()

            view.findViewById<Button>(R.id.btnReset).setOnClickListener{
                compareEmail(userEmail)
                dialog.dismiss()

            }

            view.findViewById<Button>(R.id.btnCancel).setOnClickListener{
                dialog.dismiss()
            }

            if (dialog.window != null){
                dialog.window!!.setBackgroundDrawable(ColorDrawable(0))
            }
            dialog.show()
        }

        binding.signupRedirectText.setOnClickListener{
            val signupIntent = Intent(this, SignUpActivity::class.java)
            startActivity(signupIntent)
        }

    }

    //Outside OnCreate
    private fun compareEmail(email: EditText){
        if(email.text.toString().isEmpty()){
            return
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()){
            return
        }
        firebaseAuth.sendPasswordResetEmail(email.text.toString()).addOnCompleteListener{ task ->
            if(task.isSuccessful){
                Toast.makeText(this, "Check your email", Toast.LENGTH_SHORT).show()
            }
        }
    }
}