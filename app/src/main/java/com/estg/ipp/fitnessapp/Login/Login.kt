package com.estg.ipp.fitnessapp.Login


import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.estg.ipp.fitnessapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class Login : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        mAuth = FirebaseAuth.getInstance()
        binding.buttonBack.setOnClickListener {
            onBackPressed()
        }

        binding.signOutButton.setOnClickListener {
            signOut()
        }
        binding.emailCreateAccountButton.setOnClickListener {
            createUser(binding.fieldEmail.text.toString(), binding.fieldPassword.text.toString())
        }
        binding.emailSignInButton.setOnClickListener {
            signInUser(binding.fieldEmail.text.toString(), binding.fieldPassword.text.toString())
        }
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        val currentUser = mAuth.currentUser
        updateUI(currentUser)
    }

    private fun validateForm(): Boolean {
        var valid = true

        val email = binding.fieldEmail.text.toString()
        if (email.isEmpty()) {
            binding.fieldEmail.error = "Campo obrigatório."
            valid = false
        } else {
            binding.fieldEmail.error = null
        }

        val password = binding.fieldPassword.text.toString()
        if (password.isEmpty()) {
            binding.fieldPassword.error = "Campo obrigatório."
            valid = false
        } else {
            binding.fieldPassword.error = null
        }

        return valid
    }


    private fun signOut() {
        mAuth.signOut()
        updateUI(null)
    }

    private fun createUser(email: String, password: String) {
        if (!validateForm()) {
            Log.d("Email", email)
            Log.d("password", password)
            return
        }

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    updateUI(user)
                } else {
                    Toast.makeText(
                        this, "Não é possivel criar o user.",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }
            }
    }

    private fun signInUser(email: String, password: String) {
        if (!validateForm()) {
            Log.d("Email", email)
            Log.d("password", password)
            return
        }
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    updateUI(user)
                } else {
                    Toast.makeText(
                        this, "Não foi possivel logar o user.",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }

            }

    }


    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            binding.status.text = "Logado"
            binding.emailPasswordButtons.visibility = View.GONE
            binding.emailPasswordFields.visibility = View.GONE
            binding.signedInButtons.visibility = View.VISIBLE
        } else {
            binding.status.text = "Deslogado"
            binding.emailPasswordButtons.visibility = View.VISIBLE
            binding.emailPasswordFields.visibility = View.VISIBLE
            binding.signedInButtons.visibility = View.GONE
        }
    }
}