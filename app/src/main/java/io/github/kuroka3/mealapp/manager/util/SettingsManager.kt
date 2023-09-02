package io.github.kuroka3.mealapp.manager.util

import io.github.kuroka3.mealapp.MainActivity
import org.json.simple.JSONObject

object SettingsManager {

    private val file: JSONFile = JSONFile("${MainActivity.filesdir.path}/settings.json")

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

    fun load() {
        if(!file.isFile || file.isEmpty) {
            file.createNewFile()

            val tmpobj = JSONObject()
            tmpobj["edu"] = null
            tmpobj["sch"] = null
            tmpobj["show_cal"] = false
            tmpobj["show_ntr"] = false

            file.saveJSONFile(tmpobj)
        }

        val obj = file.jsonObject!!

        edu = obj["edu"] as String?
        sch = obj["sch"] as String?
        show_cal = obj["show_cal"] as Boolean
        show_ntr = obj["show_ntr"] as Boolean
    }

    fun save() {
        val tmpobj = JSONObject()
        tmpobj["edu"] = edu
        tmpobj["sch"] = sch
        tmpobj["show_cal"] = show_cal
        tmpobj["show_ntr"] = show_ntr

        file.saveJSONFile(tmpobj)
    }
}