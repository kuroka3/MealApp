package io.github.kuroka3.mealapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.github.kuroka3.mealapp.manager.api.APIManager
import io.github.kuroka3.mealapp.manager.api.APIResult
import io.github.kuroka3.mealapp.manager.classes.Meal
import io.github.kuroka3.mealapp.manager.util.SettingsManager
import io.github.kuroka3.mealapp.manager.util.ThreadManager
import io.github.kuroka3.mealapp.utils.DatePickerFragment
import java.io.File
import java.time.LocalDate

class MainActivity : AppCompatActivity() {

    companion object {
        lateinit var filesdir: File
        lateinit var instance: MainActivity
        const val SETTINGS_COMPLETE = 0
    }

    lateinit var seldate: Button
    lateinit var viewtext: TextView
    lateinit var settings: FloatingActionButton

    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setTitle(R.string.app_name)

        filesdir = filesDir
        instance = this

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            onResult(it)
        }

        SettingsManager.load()

        seldate = findViewById(R.id.select_date)
        viewtext = findViewById(R.id.meal_text)
        settings = findViewById(R.id.settings)

        // 첫 시작 불러오기
        loadMeals(LocalDate.now())

        seldate.setOnClickListener {
            val dialogFragment: DialogFragment = DatePickerFragment()
            dialogFragment.show(supportFragmentManager, "datePicker")
        }

        settings.setOnClickListener {
            val intent = Intent(this@MainActivity, SettingsActivity::class.java)
            activityResultLauncher.launch(intent)
        }
    }

    fun loadMeals(date: LocalDate) {
        viewtext.text = ""

        ThreadManager.runInAnotherThread {
            val result = APIManager.reqMeal(date)

            if (result.result == APIResult.RESULT_ERR && result.err is String) {
                runOnUiThread { viewtext.text = result.err }
            } else if (result.result == APIResult.RESULT_EXCEPTION && result.err is Exception) {
                runOnUiThread { viewtext.text = "${result.err}: ${result.err.message}" }
            } else {
                val body = result.body as Meal

                val sb = StringBuilder()

                sb.append(body.names)

                if (SettingsManager.show_cal) {
                    sb.append("\n\n=-=-=-=-=-=\n\n${body.cal ?: "칼로리 정보 없음"}")
                }

                if (SettingsManager.show_ntr) {
                    sb.append("\n\n=-=-=-=-=-=\n\n${body.ntr ?: "영양 정보 없음"}")
                }

                runOnUiThread { viewtext.text = sb }
            }
        }
    }

    fun onResult(result: ActivityResult) {
        val resultCode = result.resultCode

        if(resultCode == SETTINGS_COMPLETE) {
            finish()
            overridePendingTransition(0, 0)
            val intent = intent
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
    }
}