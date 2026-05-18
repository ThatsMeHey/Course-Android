package com.example.coursemobile.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.coursemobile.data.HourlyData
import com.example.coursemobile.R
import com.example.coursemobile.data.WeatherUtils
import kotlin.math.roundToInt

class TodayHourlyForecastAdapter(private val hourly: HourlyData,
                                 private val indices: List<Int>,
                                 private val onItemClick: (Int) -> Unit) :
    RecyclerView.Adapter<TodayHourlyForecastAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val time: TextView = view.findViewById(R.id.todayForecastTime)
        val icon: ImageView = view.findViewById(R.id.weatherIcon)
        val temperature: TextView = view.findViewById(R.id.todayForecastTemperature)
        val humidity: TextView = view.findViewById(R.id.todayForecastHumidity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.today_hourly_forecast_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val time = hourly.time[indices[position]]
        val weathercode = hourly.weathercode[indices[position]]
        val temperature = hourly.temperature[indices[position]]
        val humidity = hourly.humidity[indices[position]]

        holder.time.text = time.substringAfter("T")
        holder.icon.setImageResource(WeatherUtils.getWeatherIcon(weathercode, time.substringAfter("T").substringBefore(":").toInt()))
        holder.temperature.text = holder.itemView.context.getString(R.string.temperature_format, temperature.roundToInt())
        holder.humidity.text = holder.itemView.context.getString(R.string.humidity_format, humidity)

        holder.itemView.setOnClickListener {
            onItemClick(position)
        }
    }

    override fun getItemCount() = indices.size
}