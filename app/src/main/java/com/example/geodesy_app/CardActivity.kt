package com.example.geodesy_app

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.geodesy_app.data.StatusField
import com.example.geodesy_app.data.TypeOfSignField
import com.example.geodesy_app.data.repository.CardData
import com.example.geodesy_app.data.repository.CardRepository
import com.example.geodesy_app.network.RetrofitClient
import com.example.geodesy_app.utils.Constants
import com.example.geodesy_app.utils.toFile
import com.example.geodesy_app.viewmodel.CardViewModel
import com.google.gson.Gson
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class CardActivity : AppCompatActivity() {

    private val viewModel: CardViewModel by viewModels {
        CardViewModel.Factory(CardRepository(RetrofitClient.cardApi))
    }

    private val selectedPhotos = mutableListOf<Uri>()
    private val gson = Gson()

    private lateinit var progressBar: ProgressBar

    private val photoPicker = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        selectedPhotos.clear()
        selectedPhotos.addAll(uris)
        findViewById<TextView>(R.id.tvPhotosCount).text = "Выбрано: ${selectedPhotos.size}"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)

        progressBar = findViewById(R.id.progressBar)

        val token = intent.getStringExtra("EXTRA_TOKEN")
        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Ошибка: Токен отсутствует", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupSpinners()

        findViewById<Button>(R.id.btnPickPhotos).setOnClickListener {
            photoPicker.launch("image/*")
        }

        findViewById<Button>(R.id.btnSend).setOnClickListener {
            sendCard(token)
        }

        lifecycleScope.launch {
            viewModel.uiState.collectLatest { message ->
                progressBar.visibility = View.GONE
                message?.let {
                    Toast.makeText(this@CardActivity, it, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setupSpinners() {
        val savedUnsaved = arrayOf("saved", "unsaved")
        val typeOptions = arrayOf("signal", "pyramid", "tripod", "tur", "no_sign")
        val coveredUncovered = arrayOf("covered", "uncovered")
        val trenchOptions = arrayOf("readable", "unreadable")
        val pillarOptions = arrayOf("detected", "undetected")
        val satOptions = arrayOf("possible", "conditionally_possible", "impossible")

        fun setAdapter(spinner: Spinner, options: Array<String>) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        setAdapter(findViewById(R.id.spOutdoorSign), savedUnsaved)
        setAdapter(findViewById(R.id.spMonolithOne), savedUnsaved)
        setAdapter(findViewById(R.id.spMonolithTwo), coveredUncovered)
        setAdapter(findViewById(R.id.spMonolithThreeFour), coveredUncovered)
        setAdapter(findViewById(R.id.spTrench), trenchOptions)
        setAdapter(findViewById(R.id.spIdentificationPillar), pillarOptions)
        setAdapter(findViewById(R.id.spOrpOne), savedUnsaved)
        setAdapter(findViewById(R.id.spOrpTwo), savedUnsaved)
        setAdapter(findViewById(R.id.spSatelliteSurveillance), satOptions)

        // Federal Subjects Spinner
        val fedSpinner = findViewById<Spinner>(R.id.spFederalSubject)
        setAdapter(fedSpinner, Constants.FEDERAL_SUBJECTS)

        // Type of Sign Spinner with dynamic properties
        val typeSpinner = findViewById<Spinner>(R.id.spTypeOfSign)
        setAdapter(typeSpinner, typeOptions)
        typeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateSignPropertiesUI(typeOptions[position])
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun updateSignPropertiesUI(type: String) {
        val tv1 = findViewById<TextView>(R.id.tvProp1)
        val sp1 = findViewById<Spinner>(R.id.spProp1)
        val tv2 = findViewById<TextView>(R.id.tvProp2)
        val sp2 = findViewById<Spinner>(R.id.spProp2)

        fun setProp(tv: TextView, sp: Spinner, label: String, options: Array<String>) {
            tv.text = label
            tv.visibility = View.VISIBLE
            sp.visibility = View.VISIBLE
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            sp.adapter = adapter
        }

        listOf(tv1, sp1, tv2, sp2).forEach { it.visibility = View.GONE }

        when (type) {
            "pyramid" -> {
                setProp(tv1, sp1, "Материал", arrayOf("metalic", "wood"))
                setProp(tv2, sp2, "Геометрия", arrayOf("tetrahedron", "trihedron"))
            }
            "signal" -> {
                setProp(tv1, sp1, "Тип", arrayOf("simple", "complex"))
            }
            "tripod" -> {
                setProp(tv1, sp1, "Материал", arrayOf("metalic", "wood"))
                setProp(tv2, sp2, "Геометрия", arrayOf("tetrahedron", "trihedron"))
            }
            "tur" -> {
                setProp(tv1, sp1, "Столб", arrayOf("concrete", "stone", "brick"))
            }
        }
    }

    private fun validateInputs(): Boolean {
        val lat = findViewById<EditText>(R.id.etLat).text.toString().toDoubleOrNull()
        val lon = findViewById<EditText>(R.id.etLon).text.toString().toDoubleOrNull()
        val height = findViewById<EditText>(R.id.etSignHeight).text.toString().toDoubleOrNull()
        val date = findViewById<EditText>(R.id.etExecuteDate).text.toString()

        if (lat == null || lat !in -90.0..90.0) {
            Toast.makeText(this, "Широта должна быть от -90 до 90", Toast.LENGTH_SHORT).show()
            return false
        }
        if (lon == null || lon !in -180.0..180.0) {
            Toast.makeText(this, "Долгота должна быть от -180 до 180", Toast.LENGTH_SHORT).show()
            return false
        }
        if (height == null || height < 0) {
            Toast.makeText(this, "Высота знака должна быть ≥ 0", Toast.LENGTH_SHORT).show()
            return false
        }
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            sdf.isLenient = false
            sdf.parse(date)
        } catch (e: Exception) {
            Toast.makeText(this, "Неверный формат даты (ГГГГ-ММ-ДД)", Toast.LENGTH_SHORT).show()
            return false
        }

        if (selectedPhotos.size < 2) {
            Toast.makeText(this, "Нужно выбрать минимум 2 фото", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun sendCard(token: String) {
        if (!validateInputs()) return

        progressBar.visibility = View.VISIBLE
        val files = selectedPhotos.mapNotNull { it.toFile(this) }
        
        fun packStatus(spinnerId: Int) = StatusField(
            value = findViewById<Spinner>(spinnerId).selectedItem.toString(),
            recommendation = "string",
            comment = "string"
        )

        val data = CardData(
            latitude = findViewById<EditText>(R.id.etLat).text.toString().toDouble(),
            longitude = findViewById<EditText>(R.id.etLon).text.toString().toDouble(),
            federalSubject = findViewById<Spinner>(R.id.spFederalSubject).selectedItem.toString(),
            signHeight = findViewById<EditText>(R.id.etSignHeight).text.toString().toDouble(),
            signHeightAbove = findViewById<EditText>(R.id.etSignHeightAbove).text.toString().toDoubleOrNull() ?: 0.0,
            executeDate = findViewById<EditText>(R.id.etExecuteDate).text.toString(),
            outdoorSignJson = gson.toJson(packStatus(R.id.spOutdoorSign)),
            typeOfSignJson = generateTypeOfSignJson(),
            monolithOne = gson.toJson(packStatus(R.id.spMonolithOne)),
            monolithTwo = gson.toJson(packStatus(R.id.spMonolithTwo)),
            monolithThreeFour = gson.toJson(packStatus(R.id.spMonolithThreeFour)),
            trench = gson.toJson(packStatus(R.id.spTrench)),
            identificationPillar = gson.toJson(packStatus(R.id.spIdentificationPillar)),
            orpOne = gson.toJson(packStatus(R.id.spOrpOne)),
            orpTwo = gson.toJson(packStatus(R.id.spOrpTwo)),
            satelliteSurveillance = gson.toJson(packStatus(R.id.spSatelliteSurveillance))
        )

        viewModel.createCard(token, data, files)
    }

    private fun generateTypeOfSignJson(): String {
        val type = findViewById<Spinner>(R.id.spTypeOfSign).selectedItem.toString()
        val properties = mutableMapOf<String, String>()
        
        when (type) {
            "pyramid", "tripod" -> {
                properties["material"] = findViewById<Spinner>(R.id.spProp1).selectedItem.toString()
                properties["geometry"] = findViewById<Spinner>(R.id.spProp2).selectedItem.toString()
            }
            "signal" -> {
                properties["type"] = findViewById<Spinner>(R.id.spProp1).selectedItem.toString()
            }
            "tur" -> {
                properties["pillar"] = findViewById<Spinner>(R.id.spProp1).selectedItem.toString()
            }
        }

        return gson.toJson(TypeOfSignField(
            value = type,
            properties = if (properties.isEmpty()) null else properties,
            recommendation = "string",
            comment = "string"
        ))
    }
}
