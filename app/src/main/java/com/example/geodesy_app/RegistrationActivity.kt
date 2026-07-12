package com.example.geodesy_app

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.geodesy_app.data.RegistrationRequest
import com.example.geodesy_app.viewmodel.RegistrationViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RegistrationActivity : AppCompatActivity() {

    private val viewModel: RegistrationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        val llStep1 = findViewById<LinearLayout>(R.id.llStep1)
        val llStep2 = findViewById<LinearLayout>(R.id.llStep2)
        val progressBar = findViewById<ProgressBar>(R.id.regProgressBar)

        val etFirstName = findViewById<EditText>(R.id.etFirstName)
        val etSecondName = findViewById<EditText>(R.id.etSecondName)
        val etThirdName = findViewById<EditText>(R.id.etThirdName)
        val rgSex = findViewById<RadioGroup>(R.id.rgSex)
        val etEmail = findViewById<EditText>(R.id.etRegEmail)
        val etPassword = findViewById<EditText>(R.id.etRegPassword)
        val btnNext = findViewById<Button>(R.id.btnRegNext)

        val etConfirmCode = findViewById<EditText>(R.id.etConfirmCode)
        val btnVerify = findViewById<Button>(R.id.btnRegVerify)

        btnNext.setOnClickListener {
            val sex = if (findViewById<RadioButton>(R.id.rbMale).isChecked) "male" else "female"
            val request = RegistrationRequest(
                firstName = etFirstName.text.toString(),
                secondName = etSecondName.text.toString(),
                thirdName = etThirdName.text.toString(),
                sex = sex,
                email = etEmail.text.toString(),
                password = etPassword.text.toString()
            )
            progressBar.visibility = View.VISIBLE
            viewModel.registerStep1(request)
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
                    Toast.makeText(this@RegistrationActivity, it, Toast.LENGTH_LONG).show()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.registrationSuccess.collectLatest { success ->
                if (success) {
                    finish() // Возвращаемся на экран логина
                }
            }
        }
    }
}
