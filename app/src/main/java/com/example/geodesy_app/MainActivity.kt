package com.example.geodesy_app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.geodesy_app.viewmodel.LoginViewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPass = findViewById<EditText>(R.id.etPassword)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val btnForgot = findViewById<Button>(R.id.btnForgotPassword)
        val tvError = findViewById<TextView>(R.id.tvError)

        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }

        btnForgot.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        btnSubmit.setOnClickListener {
            val email = etEmail.text.toString()
            val pass = etPass.text.toString()

            tvError.visibility = View.GONE

            viewModel.login(email, pass) { success, result ->
                if (success) {
                    val intent = Intent(this, CardActivity::class.java).apply {
                        putExtra("EXTRA_TOKEN", result)
                    }
                    startActivity(intent)
                    finish() // Закрываем экран входа
                } else {
                    tvError.text = getString(R.string.error_invalid_credentials)
                    tvError.visibility = View.VISIBLE
                }
            }
        }
    }
}