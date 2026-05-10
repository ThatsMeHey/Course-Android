package com.example.coursemobile.ui
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.coursemobile.data.HourlyData
import com.example.coursemobile.R
import com.example.coursemobile.data.WeatherUtils
import com.example.coursemobile.ui.screens.WeatherViewModel
import com.google.android.material.card.MaterialCardView
import java.time.LocalDateTime

class WeekForecastAdapter(private val hourly: HourlyData, private val sortedDates: List<String>, private val localTodayDate: LocalDateTime, private val viewModel: WeatherViewModel) :
    RecyclerView.Adapter<WeekForecastAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dayOfWeek: TextView = view.findViewById(R.id.weekForecastDayOfWeek)
        val humidity: TextView = view.findViewById(R.id.weekForecastHumidity)
        val iconDay: ImageView = view.findViewById(R.id.weatherDayIcon)
        val iconNight: ImageView = view.findViewById(R.id.weatherNightIcon)
        val temperatureDay: TextView = view.findViewById(R.id.weekForecastTemperatureDay)
        val temperatureNight: TextView = view.findViewById(R.id.weekForecastTemperatureNight)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.week_forecast_card, parent, false)
        return ViewHolder(view)
    }

    val firstDay = sortedDates.first();
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val daySummary = WeatherUtils.getDailySummary(hourly, sortedDates[position], localTodayDate.plusDays(position.toLong()).toString())

        if (position == 0) holder.dayOfWeek.text = holder.itemView.context.getString(R.string.today)
        else holder.dayOfWeek.text = daySummary.dayOfWeek

        holder.humidity.text = holder.itemView.context.getString(R.string.humidity_format, daySummary.humidity)
        holder.iconDay.setImageResource(WeatherUtils.getWeatherIcon(daySummary.dayWeatherCode, 13))
        holder.iconNight.setImageResource(WeatherUtils.getWeatherIcon(daySummary.nightWeatherCode, 1))
        holder.temperatureDay.text = holder.itemView.context.getString(R.string.temperature_format, daySummary.maxTemp)
        holder.temperatureNight.text = holder.itemView.context.getString(R.string.temperature_format, daySummary.minTemp)

        holder.itemView.setOnClickListener {
            viewModel.selectedHour.value = 0
            viewModel.selectedDay.value = position
        }

        val isSelected = position == viewModel.selectedDay.value
        val card = holder.itemView as MaterialCardView
        val color = ContextCompat.getColor(
            holder.itemView.context,
            if (isSelected) R.color.spinnerBackground
            else R.color.cardBackground
        )
        card.setCardBackgroundColor(color)
    }

    override fun getItemCount() = sortedDates.size
}