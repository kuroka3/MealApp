package io.github.kuroka3.mealapp

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.github.kuroka3.mealapp.manager.api.APIManager
import io.github.kuroka3.mealapp.manager.api.APIResult
import io.github.kuroka3.mealapp.manager.classes.Meal
import io.github.kuroka3.mealapp.manager.util.SettingsManager
import io.github.kuroka3.mealapp.manager.util.ThreadManager
import io.github.kuroka3.mealapp.utils.DatePickerFragment
import io.github.kuroka3.mealapp.utils.StatusManager
import java.io.File
import java.lang.ref.WeakReference
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity() {

    companion object {
        lateinit var filesdir: File
        lateinit var cachedir: File
        lateinit var weakReference: WeakReference<MainActivity>
        const val SETTINGS_COMPLETE = 0
        const val ALARM = 1
        const val PERMISSION_REQUEST_CODE = 112
        const val NOTIFICATION_CHANNEL = "meal_notif"

        fun getInstance(): MainActivity? {
            return weakReference.get()
        }
    }

    private lateinit var seldate: Button
    private lateinit var viewtext: TextView
    private lateinit var settings: FloatingActionButton

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!StatusManager.checkNetworkState(applicationContext)) {
            Toast.makeText(applicationContext, "네트워크에 연결되어있지 않음", Toast.LENGTH_SHORT).show()
            exitProcess(1)
        }

        setTitle(R.string.app_name)

        if (Build.VERSION.SDK_INT > 32) {
            if (!shouldShowRequestPermissionRationale(PERMISSION_REQUEST_CODE.toString())) {
                getNotificationPermission()
            }
        }

        filesdir = filesDir
        cachedir = cacheDir
        weakReference = WeakReference(this)

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            onResult(it)
        }

        SettingsManager.load()

        seldate = findViewById(R.id.select_date)
        viewtext = findViewById(R.id.meal_text)
        settings = findViewById(R.id.settings)

        // 첫 시작 불러오기
        loadMeals(LocalDate.now())


        if (SettingsManager.set_alarm) {
            registerNotif(SettingsManager.alarm_h, SettingsManager.alarm_m)
        }


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
                val text = "${result.err}: ${result.err.message}"
                runOnUiThread { viewtext.text = text }
            } else {
                val body = result.body as Meal

                runOnUiThread { viewtext.text = body.toMealString() }
            }
        }
    }

    fun registerNotif(hour: Int, min: Int) {
        val alarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val pendingIntent = Intent(applicationContext, MealAlarmReceiver::class.java).let {
            it.putExtra("code", ALARM)
            PendingIntent.getBroadcast(applicationContext, ALARM, it, PendingIntent.FLAG_IMMUTABLE)
        }

        val now = LocalDateTime.now()

        var todayAtTime = now.with(LocalTime.of(hour, min))

        if (now.isAfter(todayAtTime)) {
            todayAtTime = todayAtTime.plusDays(1)
        }

        val duration = Duration.between(now, todayAtTime)

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + duration.toMillis(),
            pendingIntent
        )

        println("등록됨 ${duration.toMillis()}")
    }

    fun notificationMeal(string: String) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notif = Notification.Builder(applicationContext, NOTIFICATION_CHANNEL)
            .setSmallIcon(R.drawable.ic_fork_and_knife)
            .setShowWhen(false)
            .setContentTitle("오늘의 급식 메뉴")
            .setContentText("확장해서 메뉴 확인하기")
            .setStyle(Notification.BigTextStyle().bigText(string))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(0, notif)
    }

    @Suppress("DEPRECATION")
    private fun onResult(result: ActivityResult) {
        val resultCode = result.resultCode

        if(resultCode == SETTINGS_COMPLETE) {
            finish()
            if (Build.VERSION.SDK_INT >= 34) {
                overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, 0, 0)
            } else {
                overridePendingTransition(0, 0)
            }
            val intent = intent
            startActivity(intent)
            if (Build.VERSION.SDK_INT >= 34) {
                overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, 0, 0)
            } else {
                overridePendingTransition(0, 0)
            }
        }
    }

    private fun getNotificationPermission() {
        try {
            if (Build.VERSION.SDK_INT > 32) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    PERMISSION_REQUEST_CODE
                )
            }
        } catch (_: Exception) {
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

                    val channelId = NOTIFICATION_CHANNEL
                    val channelName = "급식 알림"
                    val channelDescription = "지정된 시각에 급식 정보에 대한 알림을 전송하는 채널"
                    val importance = NotificationManager.IMPORTANCE_HIGH

                    val notificationChannel = NotificationChannel(channelId, channelName, importance).apply {
                        description = channelDescription
                        enableVibration(true)
                        vibrationPattern = longArrayOf(100L, 200L, 300L)
                        enableVibration(true)
                        enableLights(false)
                    }

                    notificationManager.createNotificationChannel(notificationChannel)

                    StatusManager.alarmPerm = true
                } else {
                    StatusManager.alarmPerm = false
                    SettingsManager.set_alarm = false
                }
                return
            }
        }
    }
}