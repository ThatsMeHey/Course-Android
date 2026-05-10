package com.example.coursemobile.ui

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.coursemobile.R
import com.example.coursemobile.data.WeatherUtils
import com.example.coursemobile.databinding.DetailedWeatherFragmentBinding
import com.example.coursemobile.ui.screens.WeatherViewModel
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

class DetailedWeatherFragment : Fragment() {
    private val viewModel: WeatherViewModel by activityViewModels()
    private var _binding: DetailedWeatherFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DetailedWeatherFragmentBinding.inflate(inflater, container, false)

        viewModel.forecast.observe(viewLifecycleOwner) { updateUI() }
        viewModel.selectedDay.observe(viewLifecycleOwner) { updateUI() }
        viewModel.selectedHour.observe(viewLifecycleOwner) { updateUI() }

        viewModel.responseState.observe(viewLifecycleOwner) { state ->
            binding.root.visibility = when (state) {
                is WeatherViewModel.ResponseState.Loading -> View.GONE
                is WeatherViewModel.ResponseState.Error -> View.GONE
                is WeatherViewModel.ResponseState.Success -> View.VISIBLE
            }
        }

        return binding.root
    }

    private fun updateUI() {
        val response = viewModel.forecast.value ?: return
        val hour = viewModel.selectedHour.value ?: return

        val hourly = response.hourly
        val days = viewModel.groupByDay(hourly)
        val sortedDates = days.keys.sorted()
        val firstDate = sortedDates[viewModel.selectedDay.value ?: 0]
        val firstIndices = days[firstDate]!!

        val targetOffset = ZoneOffset.ofTotalSeconds(response.utcOffsetSeconds)
        val date = LocalDateTime.now(targetOffset)
            .plusDays((viewModel.selectedDay.value ?: 0).toLong())
            .withHour(hour)

        val formatter = DateTimeFormatter.ofPattern("d MMMM HH:00", Locale.getDefault())

        binding.todayDate.text = getString(R.string.today_date_format, date.format(formatter))
        binding.currentWeather.text = getString(WeatherUtils.getWeatherName(hourly.weathercode[firstIndices[hour]], hour))
        binding.currentTemperature.text = getString(R.string.temperature_format, hourly.temperature[firstIndices[hour]].roundToInt())
        binding.windSpeed.text = getString(R.string.wind_speed, hourly.windspeed[firstIndices[hour]])
        binding.windDirection.text = getString(WeatherUtils.getWindDirection(hourly.winddirection[firstIndices[hour]]))
        binding.pressure.text = getString(R.string.pressure, hourly.pressure[firstIndices[hour]].roundToInt())
        binding.currentHumidity.text = getString(R.string.humidity_format, hourly.humidity[firstIndices[hour]])
        binding.windDirectionIcon.rotation = hourly.winddirection[firstIndices[hour]].toFloat()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}