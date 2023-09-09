package io.github.kuroka3.mealapp.manager.util

import io.github.kuroka3.JSONFile
import io.github.kuroka3.mealapp.MainActivity
import org.json.simple.JSONObject

object SettingsManager {

    private val file: JSONFile = JSONFile(MainActivity.filesdir, "settings.json")

    var edu: String? = null // 시도교육청코드
        set(value) {
            field = value
            save()
        }
    var sch: String? = null // 표준학교코드
        set(value) {
            field = value
            save()
        }

    val isCanBeRequested: Boolean
        get() {
            return edu != null && sch != null
        }

    var show_cal: Boolean = false // 칼로리정보 표시
        set(value) {
            field = value
            save()
        }
    var show_ntr: Boolean = false // 영양정보 표시
        set(value) {
            field = value
            save()
        }

    var set_alarm: Boolean = false // 알림 설정
        set(value) {
            field = value
            save()
        }

    var alarm_h: Int = 7
        set(value) {
            field = value
            save()
        }

    var alarm_m: Int = 0
        set(value) {
            field = value
            save()
        }

    fun load() {
        if(!file.isFile) {
            file.createNewFile()

            val tmpobj = JSONObject()
            tmpobj["edu"] = null
            tmpobj["sch"] = null
            tmpobj["show_cal"] = false
            tmpobj["show_ntr"] = false
            tmpobj["set_alarm"] = false
            tmpobj["alarm_h"] = 7
            tmpobj["alarm_m"] = 0

            file.saveJSON(tmpobj)
        }

        val obj = file.jsonObject!!

        edu = obj["edu"] as String?
        sch = obj["sch"] as String?
        show_cal = obj["show_cal"] as Boolean
        show_ntr = obj["show_ntr"] as Boolean
        set_alarm = obj["set_alarm"] as Boolean? ?: false
        alarm_h = (obj["alarm_h"] as Long? ?: 7).toInt()
        alarm_m = (obj["alarm_m"] as Long? ?: 0).toInt()
    }

    private fun save() {
        val tmpobj = JSONObject()
        tmpobj["edu"] = edu
        tmpobj["sch"] = sch
        tmpobj["show_cal"] = show_cal
        tmpobj["show_ntr"] = show_ntr
        tmpobj["set_alarm"] = set_alarm
        tmpobj["alarm_h"] = alarm_h
        tmpobj["alarm_m"] = alarm_m

        file.saveJSON(tmpobj)
    }
}