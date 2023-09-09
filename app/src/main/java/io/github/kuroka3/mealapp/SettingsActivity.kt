package io.github.kuroka3.mealapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.DialogFragment
import io.github.kuroka3.mealapp.manager.util.SettingsManager
import io.github.kuroka3.mealapp.utils.TimePickerFragment

class SettingsActivity : AppCompatActivity() {

    private lateinit var showCal: SwitchCompat
    private lateinit var showNtr: SwitchCompat
    private lateinit var setAlarm: SwitchCompat
    private lateinit var gotoSch: Button
    private lateinit var saveExt: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setTitle(R.string.settings)

        showCal = findViewById(R.id.show_cal)
        showNtr = findViewById(R.id.show_ntr)
        setAlarm = findViewById(R.id.set_alarm)
        gotoSch = findViewById(R.id.school_settings)
        saveExt = findViewById(R.id.confirm_setting)

        showCal.isChecked = SettingsManager.show_cal
        showNtr.isChecked = SettingsManager.show_ntr
        setAlarm.isChecked = SettingsManager.set_alarm

        setAlarm.setOnClickListener {
            if (setAlarm.isChecked) {
                val dialogFragment: DialogFragment = TimePickerFragment()
                dialogFragment.show(supportFragmentManager, "timePicker")
            }
        }

        gotoSch.setOnClickListener {
            val intent = Intent(applicationContext, SchoolInfoActivity::class.java)
            startActivity(intent)
        }

        saveExt.setOnClickListener {
            SettingsManager.set_alarm = setAlarm.isChecked
            SettingsManager.show_cal = showCal.isChecked
            SettingsManager.show_ntr = showNtr.isChecked

            complete()
        }
    }

    private fun complete() {
        setResult(MainActivity.SETTINGS_COMPLETE)
        finish()
    }
}