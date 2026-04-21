package com.example.coursemobile.ui

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coursemobile.R
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


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = HomeFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        val recyclerViewToday = view.findViewById<RecyclerView>(R.id.todayHourlyForecast)
        val recyclerViewWeek = view.findViewById<RecyclerView>(R.id.weekForecast)

        val todayDate = view.findViewById<TextView>(R.id.todayDate)
        recyclerViewToday.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerViewWeek.layoutManager = LinearLayoutManager(requireContext())

        viewModel.responseState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is WeatherViewModel.ResponseState.Loading -> {
                    todayDate.text = getString(R.string.gettingForecast)
                    recyclerViewToday.visibility = View.GONE
                    recyclerViewWeek.visibility = View.GONE
                }
                is WeatherViewModel.ResponseState.Error -> {
                    todayDate.text = getString(R.string.errorMessage)
                }
            }
        }
        viewModel.forecast.observe(viewLifecycleOwner) { response ->
            val hourly = response.hourly
            val days = viewModel.groupByDay(hourly)
            val sortedDates = days.keys.sorted()
            val firstDate = sortedDates.first()
            val firstIndices = days[firstDate]!!

            val date = LocalDateTime.now(ZoneOffset.ofTotalSeconds(response.utcOffsetSeconds))
            val formatter = DateTimeFormatter.ofPattern("d MMMM HH:mm", Locale.getDefault())
            todayDate.text = getString(R.string.today_date_format, date.format(formatter))

            recyclerViewToday.visibility = View.VISIBLE
            recyclerViewWeek.visibility = View.VISIBLE
            recyclerViewToday.adapter = TodayHourlyForecastAdapter(hourly, firstIndices)
            recyclerViewWeek.adapter = WeekForecastAdapter(hourly, sortedDates, date)
        }
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadForecastAgain()
            binding.swipeRefresh.isRefreshing = false
        }
        return view
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}