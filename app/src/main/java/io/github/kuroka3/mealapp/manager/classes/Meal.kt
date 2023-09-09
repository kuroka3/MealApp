package io.github.kuroka3.mealapp.manager.classes

import io.github.kuroka3.mealapp.manager.util.SettingsManager

data class Meal(val names: String, val cal: String?, val ntr: String?) {
    fun toMealString(showCal: Boolean = SettingsManager.show_cal, showNtr: Boolean = SettingsManager.show_ntr): String {
        val sb = StringBuilder()

        sb.append(this.names)

        if (showCal) {
            sb.append("\n\n=-=-=-=-=-=\n\n${this.cal ?: "칼로리 정보 없음"}")
        }

        if (showNtr) {
            sb.append("\n\n=-=-=-=-=-=\n\n${this.ntr ?: "영양 정보 없음"}")
        }

        return sb.toString()
    }
}
