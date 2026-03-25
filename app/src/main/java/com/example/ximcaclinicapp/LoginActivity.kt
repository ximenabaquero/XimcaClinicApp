package com.example.ximcaclinicapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ximcaclinicapp.data.AppDatabase
import com.example.ximcaclinicapp.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Inicializar View Binding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initializing the database connection
        val database = AppDatabase.getDatabase(this)
        val usuarioDao = database.usuarioDao()

        // Defining the trigger mechanism for the Login button
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            // System check: No empty voids allowed
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Error: Incomplete parameters.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Asynchronous physics: We drop this query into a background thread
            lifecycleScope.launch {
                val user = usuarioDao.login(email, password)

                if (user != null) {
                    // Access Granted: Shift the user to the MainActivity
                    Toast.makeText(this@LoginActivity, "Access Granted: Welcome, ${user.nombre}", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Access Denied
                    Toast.makeText(this@LoginActivity, "Access Denied: Invalid credentials.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Defining the trigger for the Registration text
        binding.tvRegister.setOnClickListener {
            // Documento de solicitud para transicionar a la otra pantalla
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}