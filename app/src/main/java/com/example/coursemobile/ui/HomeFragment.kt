package com.example.coursemobile.ui

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coursemobile.R
import com.example.coursemobile.data.HourlyData
import com.example.coursemobile.databinding.HomeFragmentBinding
import com.example.coursemobile.ui.screens.WeatherViewModel
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

class HomeFragment : Fragment() {
    private val viewModel: WeatherViewModel by activityViewModels()
    private var _binding: HomeFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var hourly: HourlyData
    private lateinit var firstDate: String
    private lateinit var firstIndices: List<Int>
    private lateinit var days: Map<String, List<Int>>
    private lateinit var sortedDates: List<String>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HomeFragmentBinding.inflate(inflater, container, false)

        childFragmentManager.beginTransaction()
            .replace(R.id.detailedWeather, DetailedWeatherFragment())
            .commit()

        val recyclerViewToday = binding.todayHourlyForecast
        val recyclerViewWeek = binding.weekForecast
        val status = binding.status

        recyclerViewToday.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        recyclerViewWeek.layoutManager =
            LinearLayoutManager(requireContext())

        viewModel.responseState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is WeatherViewModel.ResponseState.Loading -> {
                    status.visibility = View.VISIBLE
                    status.text = getString(R.string.gettingForecast)
                    recyclerViewToday.visibility = View.GONE
                    recyclerViewWeek.visibility = View.GONE
                }
                is WeatherViewModel.ResponseState.Error -> {
                    status.text = getString(R.string.errorMessage)
                }
                is WeatherViewModel.ResponseState.Success -> {
                    status.visibility = View.GONE
                    recyclerViewToday.visibility = View.VISIBLE
                    recyclerViewWeek.visibility = View.VISIBLE
                }
            }
        }

        viewModel.forecast.observe(viewLifecycleOwner) { response ->
            hourly = response.hourly
            days = viewModel.groupByDay(hourly)
            sortedDates = days.keys.sorted()

            val date = LocalDateTime.now(
                ZoneOffset.ofTotalSeconds(response.utcOffsetSeconds)
            )

            val isFirstLoad = viewModel.selectedDay.value == null
            if (isFirstLoad) {
                viewModel.selectedDay.value = 0
                val hourFormatter = DateTimeFormatter.ofPattern("HH", Locale.getDefault())
                viewModel.selectedHour.value = date.format(hourFormatter).toInt()
            }

            val currentDay = viewModel.selectedDay.value ?: 0
            val targetDate = sortedDates[currentDay]
            firstIndices = days[targetDate]!!

            recyclerViewToday.adapter =
                TodayHourlyForecastAdapter(hourly, firstIndices, viewModel)
            recyclerViewWeek.adapter =
                WeekForecastAdapter(hourly, sortedDates, date, viewModel)

            recyclerViewToday.scrollToPosition(viewModel.selectedHour.value ?: 0)
            recyclerViewWeek.scrollToPosition(currentDay)
        }

        viewModel.selectedHour.observe(viewLifecycleOwner) { _ ->
            recyclerViewToday.adapter?.notifyDataSetChanged()
        }

        viewModel.selectedDay.observe(viewLifecycleOwner) { day ->
            if (!::sortedDates.isInitialized) return@observe

            val targetDate = sortedDates[day ?: 0]
            firstIndices = days[targetDate]!!
            recyclerViewToday.adapter =
                TodayHourlyForecastAdapter(hourly, firstIndices, viewModel)

            recyclerViewWeek.adapter?.notifyDataSetChanged()
            recyclerViewToday.adapter?.notifyDataSetChanged()

            recyclerViewToday.scrollToPosition(viewModel.selectedHour.value ?: 0)
            recyclerViewWeek.scrollToPosition(day ?: 0)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}