package com.example.coursemobile.data
import com.example.coursemobile.R

data class City(
    val key: String,
    val nameRes: Int
)

val cities = listOf(
    City("Moscow", R.string.city_moscow),
    City("Saint Petersburg", R.string.city_saint_petersburg),
    City("Izhevsk", R.string.city_izhevsk),
    City("Novosibirsk", R.string.city_novosibirsk),
    City("Yekaterinburg", R.string.city_yekaterinburg),
    City("London", R.string.city_london),
    City("Paris", R.string.city_paris),
    City("Berlin", R.string.city_berlin),
    City("Rome", R.string.city_rome),
    City("Amsterdam", R.string.city_amsterdam),
    City("Tokyo", R.string.city_tokyo)
)