package com.example.safetify203v

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.safetify203v.databinding.ActivitySignUpBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)

        FirebaseApp.initializeApp(this)
        firebaseAuth = FirebaseAuth.getInstance()

        binding.signupButton.setOnClickListener {
            val email = binding.signupEmail.text.toString()
            val password = binding.signupPassword.text.toString()
            val confirmPassword = binding.signupConfirm.text.toString()

            if(email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()){
                if(password == confirmPassword){
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{
                        if(it.isSuccessful){
                            val intent  = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                        }else{
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                // Create the AlertDialog
                val builder = AlertDialog.Builder(this)
                val view = layoutInflater.inflate(R.layout.fieldcannotbeempty, null)

                builder.setView(view)
                val dialog = builder.create()

                dialog.show()

                dialog.findViewById<View>(android.R.id.content)?.setOnClickListener {
                    dialog.dismiss()
                }
            } else {


                if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                    if (password == confirmPassword) {
                        firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {

                                    FirebaseAuth.getInstance().currentUser?.sendEmailVerification()
                                        ?.addOnCompleteListener { task ->
                                            if (task.isSuccessful) {

                                                val builder = AlertDialog.Builder(this)
                                                val view = layoutInflater.inflate(R.layout.fragment_verification, null)

                                                builder.setView(view)
                                                val dialog = builder.create()

                                                dialog.show()

                                                view.findViewById<Button>(R.id.btnVerify).setOnClickListener{
                                                    dialog.dismiss()
                                                }

                                                view.findViewById<Button>(R.id.btnCancel1).setOnClickListener{
                                                    dialog.dismiss()
                                                }

                                                Toast.makeText(this, "User registered successfully. Please verify your email address.", Toast.LENGTH_SHORT).show()
                                                binding.signupEmail.setText("")
                                                binding.signupName.setText("")
                                                binding.signupPassword.setText("")
                                                binding.signupConfirm.setText("")

                                            } else {
                                                // There was an error sending the verification email
                                                // Show an error message to the user
                                                Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                } else {
                                    Toast.makeText(
                                        this,
                                        it.exception.toString(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    } else {
                        Toast.makeText(this, "Password does not matched", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.loginRedirectText.setOnClickListener{
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
        setContentView(binding.root)
    }
}