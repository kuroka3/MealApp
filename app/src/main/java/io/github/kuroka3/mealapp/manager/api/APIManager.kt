package io.github.kuroka3.mealapp.manager.api

import io.github.kuroka3.JSONFile
import io.github.kuroka3.mealapp.MainActivity
import io.github.kuroka3.mealapp.manager.classes.Meal
import io.github.kuroka3.mealapp.manager.classes.School
import io.github.kuroka3.mealapp.manager.util.SettingsManager
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.net.URL
import java.net.URLEncoder
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object APIManager {

    private const val key = "(YOUR KEY)"

    fun reqMeal(ld: LocalDate): APIResult { return reqMeal(ld.format(DateTimeFormatter.ofPattern("yyyyMMdd"))) }

    private fun reqMeal(ymd: String): APIResult {
        try {
            // 학교가 설정되어있는지 확인
            if (!SettingsManager.isCanBeRequested) {
                return APIResult(APIResult.RESULT_ERR, null, "학교가 설정되어있지 않음")
            }

            // 불러온 급식 정보가 캐시에 저장되어있는지 확인
            val cahce = JSONFile(MainActivity.cachedir, "meal.json")
            if(cahce.isFile) {
                cahce.jsonObject?.let {
                    if (it["edu"] == SettingsManager.edu && it["sch"] == SettingsManager.sch && it["date"] == ymd) {
                        println("불러옴")
                        return APIResult(APIResult.RESULT_OK, Meal(it["names"].toString(), it["cal"] as String?, it["ntr"] as String?), null)
                    }
                }
            }


            // API 요청
            val url =
                URL("https://open.neis.go.kr/hub/mealServiceDietInfo?KEY=$key&Type=json&ATPT_OFCDC_SC_CODE=${SettingsManager.edu}&SD_SCHUL_CODE=${SettingsManager.sch}&MLSV_YMD=$ymd")
            val req: JSONObject = JSONParser().parse(url.readText()) as JSONObject

            // API 요청 실패 거르기
            try {
                val result: JSONObject = req["RESULT"] as JSONObject
                val resultCode: String = result["CODE"] as String
                val resultMessage: String = result["MESSAGE"] as String

                if (resultCode.contains("ERROR") || resultCode == "INFO-300" || resultCode == "INFO-200") {
                    return APIResult(APIResult.RESULT_ERR, null, resultMessage)
                }
            } catch (e: NullPointerException) {
                // 무시
            }

            // 결과 처리
            val row: JSONObject =
                (((req["mealServiceDietInfo"] as JSONArray)[1] as JSONObject)["row"] as JSONArray)[0] as JSONObject
            val name = (row["DDISH_NM"] as String).replace("<br/>", "\n")
            val cal = (row["CAL_INFO"] as String).replace("<br/>", "\n")
            val ntr = (row["NTR_INFO"] as String).replace("<br/>", "\n")

            val meal = Meal(name, cal, ntr)

            val file = JSONFile(MainActivity.cachedir, "meal.json")
            //            file.saveJSON(JSONObjectBuilder()
            //                .put("edu", SettingsManager.edu)
            //                .put("sch", SettingsManager.sch)
            //                .put("date", ymd)
            //                .put("names", meal.names)
            //                .put("cal", meal.cal)
            //                .put("ntr", meal.ntr)
            //                .build()
            //            )
            file.saveJSON(JSONObject().also {
                it["edu"] = SettingsManager.edu
                it["sch"] = SettingsManager.sch
                it["date"] = ymd
                it["names"] = meal.names
                it["cal"] = meal.cal
                it["ntr"] = meal.ntr
            })

            println("요청함")

            return APIResult(APIResult.RESULT_OK, meal, null)
        } catch (e: Exception) {
            e.printStackTrace()
            return APIResult(APIResult.RESULT_EXCEPTION, null, e)
        }
    }

    fun reqSchools(name: String): APIResult {
        try {
            // 한글 대책으로 학교 이름을 인코딩
            val enc = URLEncoder.encode(name, "UTF-8")

            // API 요청
            val url = URL("https://open.neis.go.kr/hub/schoolInfo?KEY=$key&Type=json&SCHUL_NM=$enc")
            val req: JSONObject = JSONParser().parse(url.readText()) as JSONObject

            // API 요청 실패 거르기
            try {
                val result: JSONObject = req["RESULT"] as JSONObject
                val resultCode: String = result["CODE"] as String
                val resultMessage: String = result["MESSAGE"] as String

                if (resultCode.contains("ERROR") || resultCode == "INFO-300" || resultCode == "INFO-200") {
                    return APIResult(APIResult.RESULT_ERR, null, resultMessage)
                }
            } catch (e: NullPointerException) {
                // 무시
            }

            // 결과 처리
            val row: JSONArray = ((req["schoolInfo"] as JSONArray)[1] as JSONObject)["row"] as JSONArray

            val list: MutableList<School> = mutableListOf()

            for (o in row) {
                val obj = o as JSONObject

                val edu = obj["ATPT_OFCDC_SC_CODE"] as String
                val sch = obj["SD_SCHUL_CODE"] as String
                val schName = obj["SCHUL_NM"] as String
                val adr = obj["ORG_RDNMA"] as String

                val school = School.Builder()
                    .edu(edu)
                    .sch(sch)
                    .schName(schName)
                    .adr(adr)
                    .build()

                list.add(school)
            }

            return APIResult(APIResult.RESULT_OK, list, null)
        } catch (e: Exception) {
            e.printStackTrace()
            return APIResult(APIResult.RESULT_EXCEPTION, null, e)
        }
    }

    fun reqSchoolInfo(edu: String, sch: String): APIResult {
        try {
            // API 요청
            val url = URL("https://open.neis.go.kr/hub/schoolInfo?KEY=$key&Type=json&ATPT_OFCDC_SC_CODE=$edu&SD_SCHUL_CODE=$sch&pSize=1")
            val req: JSONObject = JSONParser().parse(url.readText()) as JSONObject

            // API 요청 실패 거르기
            try {
                val result: JSONObject = req["RESULT"] as JSONObject
                val resultCode: String = result["CODE"] as String
                val resultMessage: String = result["MESSAGE"] as String

                if (resultCode.contains("ERROR") || resultCode == "INFO-300" || resultCode == "INFO-200") {
                    return APIResult(APIResult.RESULT_ERR, null, resultMessage)
                }
            } catch (e: NullPointerException) {
                // 무시
            }

            // 결과 처리
            val row: JSONArray = ((req["schoolInfo"] as JSONArray)[1] as JSONObject)["row"] as JSONArray

            val obj = row[0] as JSONObject

            val schName = obj["SCHUL_NM"] as String
            val adr = obj["ORG_RDNMA"] as String

            val school = School.Builder()
                .edu(edu)
                .sch(sch)
                .schName(schName)
                .adr(adr)
                .build()

            return APIResult(APIResult.RESULT_OK, school, null)
        } catch (e: Exception) {
            e.printStackTrace()
            return APIResult(APIResult.RESULT_EXCEPTION, null, e)
        }
    }
}