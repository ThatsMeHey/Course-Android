package com.example.coursemobile.ui

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.coursemobile.data.City
import com.example.coursemobile.databinding.ActivityMainBinding
import com.example.coursemobile.ui.screens.WeatherViewModel
import com.google.gson.Gson
import java.util.Locale
import kotlin.getValue

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val json = assets.open("cities.json")
            .bufferedReader().readText()
        val cities = Gson().fromJson(json, Array<City>::class.java)
            .sortedBy { if (Locale.getDefault().language == "ru") it.nameRu else it.nameEn }


        val displayNames = cities.map {
            if (Locale.getDefault().language == "ru") it.nameRu else it.nameEn
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, displayNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.citySpinner.adapter = adapter

        binding.citySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedCity = cities[position]
                viewModel.loadCities(selectedCity.key)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        viewModel.cities.observe(this) { cityList ->
            val city = cityList.firstOrNull() ?: return@observe
            viewModel.loadForecast(city)
        }
    }
}