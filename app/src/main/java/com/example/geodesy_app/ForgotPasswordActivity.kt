package com.example.geodesy_app

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.geodesy_app.viewmodel.ForgotPasswordViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ForgotPasswordActivity : AppCompatActivity() {

    private val viewModel: ForgotPasswordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val llStep1 = findViewById<LinearLayout>(R.id.llForgotStep1)
        val llStep2 = findViewById<LinearLayout>(R.id.llForgotStep2)
        val progressBar = findViewById<ProgressBar>(R.id.forgotProgressBar)

        val etEmail = findViewById<EditText>(R.id.etForgotEmail)
        val etNewPassword = findViewById<EditText>(R.id.etForgotNewPassword)
        val btnNext = findViewById<Button>(R.id.btnForgotNext)

        val etConfirmCode = findViewById<EditText>(R.id.etForgotConfirmCode)
        val btnVerify = findViewById<Button>(R.id.btnForgotVerify)

        btnNext.setOnClickListener {
            val email = etEmail.text.toString()
            val newPass = etNewPassword.text.toString()
            if (email.isNotEmpty() && newPass.isNotEmpty()) {
                progressBar.visibility = View.VISIBLE
                viewModel.forgotPasswordStep1(email, newPass)
            } else {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }

        btnVerify.setOnClickListener {
            val code = etConfirmCode.text.toString()
            if (code.length == 6) {
                progressBar.visibility = View.VISIBLE
                viewModel.verifyStep2(code)
            } else {
                Toast.makeText(this, "Введите 6-значный код", Toast.LENGTH_SHORT).show()
            }
        }

        lifecycleScope.launch {
            viewModel.isStep2.collectLatest { isStep2 ->
                if (isStep2) {
                    llStep1.visibility = View.GONE
                    llStep2.visibility = View.VISIBLE
                }
            }
        }

        lifecycleScope.launch {
            viewModel.uiState.collectLatest { message ->
                progressBar.visibility = View.GONE
                message?.let {
                    Toast.makeText(this@ForgotPasswordActivity, it, Toast.LENGTH_LONG).show()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.success.collectLatest { success ->
                if (success) {
                    finish()
                }
            }
        }
    }
}
