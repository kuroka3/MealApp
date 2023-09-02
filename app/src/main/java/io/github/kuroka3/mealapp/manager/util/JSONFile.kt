package io.github.kuroka3.mealapp.manager.util

import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.json.simple.parser.ParseException
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException

class JSONFile(pathname: String) : File(pathname) {
    private var jobj: JSONObject? = null
    private val parser = JSONParser()

    @get:Throws(IOException::class)
    val isEmpty: Boolean
        get() {
            val a = jsonObject
            return a == null
        }

    @get:Throws(IOException::class)
    val jsonObject: JSONObject?
        get() {
            val reader = FileReader(this)
            jobj = try {
                parser.parse(reader) as JSONObject
            } catch (e: ParseException) {
                return null
            }
            return jobj
        }

    @Throws(IOException::class)
    fun updatejsonObject() {
        val reader = FileReader(this)
        jobj = try {
            parser.parse(reader) as JSONObject
        } catch (e: ParseException) {
            null
        }
    }

    @Throws(IOException::class)
    fun saveJSONFile(obj: JSONObject?) {
        val writer = FileWriter(this)
        if (obj != null) {
            jobj = obj
        }
        writer.write(obj!!.toJSONString())
        writer.flush()
        writer.close()
    }
}
