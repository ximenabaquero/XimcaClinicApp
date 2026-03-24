package com.example.ximcaclinicapp

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ximcaclinicapp.data.Usuario
import com.example.ximcaclinicapp.data.UsuarioDao
import com.example.ximcaclinicapp.databinding.ActivityRegisterBinding
import com.example.ximcaclinicapp.utils.PasswordUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

// RegisterActivity es la pantalla donde el médico crea su cuenta.
// Solo se llega aquí desde el link en LoginActivity.
@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    // Hilt inyecta el DAO automáticamente, igual que en LoginActivity
    @Inject
    lateinit var usuarioDao: UsuarioDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegistrar.setOnClickListener {
            val nombre = binding.etNombre.text.toString().trim()
            val email = binding.etEmailReg.text.toString().trim()
            val password = binding.etPasswordReg.text.toString().trim()

            // --- VALIDACIONES ---
            // Estas validaciones cumplen con el requisito mínimo del proyecto:
            // email no vacío + contraseña de al menos 6 caracteres.

            if (nombre.isEmpty()) {
                binding.tilNombre.error = "El nombre es obligatorio"
                return@setOnClickListener
            } else binding.tilNombre.error = null

            // Verifico que el email tenga @ para asegurar que es un correo real
            if (email.isEmpty() || !email.contains("@")) {
                binding.tilEmailReg.error = "Ingresa un correo electrónico válido"
                return@setOnClickListener
            } else binding.tilEmailReg.error = null

            // Requisito del proyecto: contraseña mínimo 6 caracteres
            if (password.length < 6) {
                binding.tilPasswordReg.error = "La contraseña debe tener al menos 6 caracteres"
                return@setOnClickListener
            } else binding.tilPasswordReg.error = null

            // Mostrar indicador de carga
            binding.btnRegistrar.isEnabled = false
            binding.progressBar.visibility = View.VISIBLE

            // Todo válido: creo el objeto Usuario y lo guardo en la base de datos
            lifecycleScope.launch {
                // Hasheo la contraseña con SHA-256 antes de guardarla.
                // Así aunque alguien vea la base de datos, no ve la contraseña en texto plano.
                val hashedPassword = PasswordUtils.hashPassword(password)
                val nuevoUsuario = Usuario(
                    nombre = nombre,
                    email = email,
                    password = hashedPassword
                    // rol se queda en "MÉDICO" por defecto (está definido en la clase Usuario)
                )
                usuarioDao.registrarUsuario(nuevoUsuario)

                // Volver al hilo principal para actualizar UI
                runOnUiThread {
                    binding.btnRegistrar.isEnabled = true
                    binding.progressBar.visibility = View.GONE

                    Toast.makeText(
                        this@RegisterActivity,
                        "Cuenta creada exitosamente. Inicia sesión.",
                        Toast.LENGTH_SHORT
                    ).show()

                    // finish() cierra esta pantalla y regresa automáticamente al Login
                    finish()
                }
            }
        }

        // El link de "ya tengo cuenta" también cierra esta pantalla y vuelve al Login
        binding.tvVolverLogin.setOnClickListener { finish() }
    }
}
