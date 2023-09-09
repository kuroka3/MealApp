package io.github.kuroka3.mealapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.github.kuroka3.mealapp.manager.api.APIManager
import io.github.kuroka3.mealapp.manager.api.APIResult
import io.github.kuroka3.mealapp.manager.classes.Meal
import io.github.kuroka3.mealapp.manager.util.SettingsManager
import io.github.kuroka3.mealapp.manager.util.ThreadManager
import java.time.LocalDate

class MealAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        println("받음")
        if(intent.getIntExtra("code", 0) == MainActivity.ALARM && SettingsManager.set_alarm) {
            ThreadManager.runInAnotherThread {
                val result = APIManager.reqMeal(LocalDate.now())

                if (result.result == APIResult.RESULT_OK) {
                    val body = result.body as Meal

                    MainActivity.getInstance()?.notificationMeal(body.toMealString(showCal = false, showNtr = false))
                    MainActivity.getInstance()?.registerNotif(SettingsManager.alarm_h, SettingsManager.alarm_m)
                }
            }
        }
    }
}