package io.github.kuroka3.mealapp.utils

import android.app.Dialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import io.github.kuroka3.mealapp.manager.util.SettingsManager

class TimePickerFragment : DialogFragment(), OnTimeSetListener {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val hour = SettingsManager.alarm_h
        val minute = SettingsManager.alarm_m
        return TimePickerDialog(requireActivity(), this, hour, minute, false)
    }

    override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
        SettingsManager.alarm_h = p1
        SettingsManager.alarm_m = p2
    }
}