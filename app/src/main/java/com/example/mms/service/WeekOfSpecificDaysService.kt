package com.example.mms.service

import com.example.mms.model.HourWeight

class WeekOfSpecificDaysService {

    // liste publique pour les tests
    val week = MutableList<MutableList<HourWeight>>(7) { mutableListOf() }

    fun addInDay(day: Int, hourWeight: HourWeight) {
        this.week[day].add(hourWeight)
    }

    fun setDay(day: Int, hoursWeights: MutableList<HourWeight>) {
        this.week[day] = hoursWeights
    }

    fun changeTime(day: Int, position: Int, newTime: String) {
        this.week[day][position].hour = newTime
    }

    fun getDay(day: Int): MutableList<HourWeight> {
        return this.week[day]
    }

    fun getFlat(): MutableList<HourWeight> {
        val flat = mutableListOf<HourWeight>()
        for (day in this.week) {
            flat.addAll(day)
        }
        return flat
    }
}
