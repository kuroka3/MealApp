package io.github.kuroka3.mealapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.DialogFragment
import io.github.kuroka3.mealapp.manager.util.SettingsManager
import io.github.kuroka3.mealapp.utils.DatePickerFragment
import io.github.kuroka3.mealapp.utils.TimePickerFragment

class SettingsActivity : AppCompatActivity() {

    lateinit var show_cal: SwitchCompat
    lateinit var show_ntr: SwitchCompat
    lateinit var set_alarm: SwitchCompat
    lateinit var goto_sch: Button
    lateinit var save_ext: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setTitle(R.string.settings)

        show_cal = findViewById(R.id.show_cal)
        show_ntr = findViewById(R.id.show_ntr)
        set_alarm = findViewById(R.id.set_alarm)
        goto_sch = findViewById(R.id.school_settings)
        save_ext = findViewById(R.id.confirm_setting)

        show_cal.isChecked = SettingsManager.show_cal
        show_ntr.isChecked = SettingsManager.show_ntr
        set_alarm.isChecked = SettingsManager.set_alarm

        set_alarm.setOnClickListener {
            if (set_alarm.isChecked) {
                val dialogFragment: DialogFragment = TimePickerFragment()
                dialogFragment.show(supportFragmentManager, "timePicker")
            }
        }

        goto_sch.setOnClickListener {
            val intent = Intent(applicationContext, SchoolInfoActivity::class.java)
            startActivity(intent)
        }

        save_ext.setOnClickListener {
            SettingsManager.set_alarm = set_alarm.isChecked
            SettingsManager.show_cal = show_cal.isChecked
            SettingsManager.show_ntr = show_ntr.isChecked

            complete()
        }
    }

    private fun complete() {
        setResult(MainActivity.SETTINGS_COMPLETE)
        finish()
    }
}