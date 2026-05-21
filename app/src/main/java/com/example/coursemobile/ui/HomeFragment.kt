package com.example.coursemobile.ui

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coursemobile.R
import com.example.coursemobile.data.HourlyData
import com.example.coursemobile.databinding.HomeFragmentBinding
import com.example.coursemobile.ui.screens.WeatherViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneOffset

class HomeFragment : Fragment() {
    private val viewModel: WeatherViewModel by activityViewModels()
    private var _binding: HomeFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var hourly: HourlyData
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


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.map { it.responseState }.distinctUntilChanged().collect { state ->
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
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.map { it.forecast }.collect { response ->
                    if (response == null) return@collect

                    hourly = response.hourly
                    days = viewModel.groupByDay(hourly)
                    sortedDates = days.keys.sorted()

                    val date = LocalDateTime.now(
                        ZoneOffset.ofTotalSeconds(response.utcOffsetSeconds)
                    )
                    val currentDay = viewModel.uiState.value.selectedDay
                    val targetDate = sortedDates[currentDay]
                    firstIndices = days[targetDate]!!

                    recyclerViewToday.adapter =
                        TodayHourlyForecastAdapter(hourly, firstIndices,
                        onItemClick = { position ->
                            viewModel.selectHour(position)
                        })
                    recyclerViewWeek.adapter =
                        WeekForecastAdapter(hourly, sortedDates, date,
                            onItemClick = { position ->
                                viewModel.selectHour(0)
                                viewModel.selectDay(position)
                            })
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.map { it.selectedDay }.distinctUntilChanged().collect { day ->
                    if (::sortedDates.isInitialized) {
                        val targetDate = sortedDates[day]
                        firstIndices = days[targetDate]!!
                        recyclerViewToday.adapter =
                            TodayHourlyForecastAdapter(
                                hourly, firstIndices,
                                onItemClick = { position ->
                                    viewModel.selectHour(position)
                                })

                        recyclerViewToday.scrollToPosition(
                            viewModel.uiState.value.selectedHour
                        )
                    }
                }
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}