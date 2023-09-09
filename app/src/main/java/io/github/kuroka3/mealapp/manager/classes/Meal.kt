package io.github.kuroka3.mealapp.manager.classes

import io.github.kuroka3.mealapp.manager.util.SettingsManager

data class Meal(val names: String, val cal: String?, val ntr: String?) {
    fun toMealString(show_cal: Boolean = SettingsManager.show_cal, show_ntr: Boolean = SettingsManager.show_ntr): String {
        val sb = StringBuilder()

        sb.append(this.names)

        if (show_cal) {
            sb.append("\n\n=-=-=-=-=-=\n\n${this.cal ?: "칼로리 정보 없음"}")
        }

        if (show_ntr) {
            sb.append("\n\n=-=-=-=-=-=\n\n${this.ntr ?: "영양 정보 없음"}")
        }

        return sb.toString()
    }
}
