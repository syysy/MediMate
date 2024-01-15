package com.example.mms.adapter.Interface

interface OnItemClickListener {
    fun onItemClick(position: Int)
}

interface CalendarAdapterInterface {
    fun onMonthYearChanged(month: String, year: String)
}
