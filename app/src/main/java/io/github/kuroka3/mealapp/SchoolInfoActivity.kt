package io.github.kuroka3.mealapp

import android.content.Context
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import io.github.kuroka3.mealapp.manager.api.APIManager
import io.github.kuroka3.mealapp.manager.api.APIResult
import io.github.kuroka3.mealapp.manager.classes.School
import io.github.kuroka3.mealapp.manager.util.SettingsManager
import io.github.kuroka3.mealapp.manager.util.ThreadManager


class SchoolInfoActivity : AppCompatActivity() {

    private lateinit var schoolName: TextView
    private lateinit var schoolAddress: TextView
    lateinit var edtSearch: EditText
    lateinit var list: LinearLayout

    var loadThread: Thread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_school_info)

        schoolName = findViewById(R.id.school_name)
        schoolAddress = findViewById(R.id.school_address)
        edtSearch = findViewById(R.id.search_school)
        list = findViewById(R.id.schools)

        loadSch()

        edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // 필요없음
            }

            override fun afterTextChanged(p0: Editable?) {
                // 필요없음22
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                loadThread?.interrupt()

                loadThread = ThreadManager.runInAnotherThread {
                    try {
                        val result = APIManager.reqSchools(edtSearch.text.toString())

                        runOnUiThread { showSearchResult(result) }
                    } catch (e: InterruptedException) {
                        list.removeAllViews()
                    }
                }
            }
        })
    }

    private fun loadSch() {
        ThreadManager.runInAnotherThread {
            val result = APIManager.reqSchoolInfo(SettingsManager.edu.toString(), SettingsManager.sch.toString())

            if (result.result == APIResult.RESULT_ERR && result.err is String) {
                runOnUiThread { schoolName.text = result.err }
            } else if (result.result == APIResult.RESULT_EXCEPTION && result.err is Exception) {
                runOnUiThread {
                    schoolName.text = result.err.toString()
                    schoolAddress.text = result.err.message
                }
            } else {
                val school = result.body as School

                runOnUiThread {
                    schoolName.text = school.schName
                    schoolAddress.text = school.adr
                }
            }
        }
    }

    fun showSearchResult(result: APIResult) {
        if (result.result == APIResult.RESULT_ERR && result.err is String) {
            list.removeAllViews()
        } else if (result.result == APIResult.RESULT_EXCEPTION && result.err is Exception) {
            list.removeAllViews()
        } else {
            list.removeAllViews()

            val schools = result.body as List<*>

            for (sch in schools) { if (sch is School) {

                val wholeLayout = LinearLayout(applicationContext)
                wholeLayout.orientation = LinearLayout.HORIZONTAL
                val wholeLayoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
                )
                val tendip = dipToPixels(applicationContext).toInt()

                wholeLayoutParams.setMargins(0, tendip, 0, tendip)
                wholeLayout.layoutParams = wholeLayoutParams

                val subLayout = LinearLayout(applicationContext)
                subLayout.orientation = LinearLayout.VERTICAL
                val subLayoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
                )
                subLayoutParams.weight = 100F
                subLayout.layoutParams = subLayoutParams

                val resultSchName = TextView(applicationContext)
                resultSchName.text = sch.schName
                resultSchName.textSize = 20F
                resultSchName.paintFlags = resultSchName.paintFlags or Paint.FAKE_BOLD_TEXT_FLAG
                val resultSchNameParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
                )
                resultSchName.layoutParams = resultSchNameParams

                val resultSchAdr = TextView(applicationContext)
                resultSchAdr.text = sch.adr
                resultSchAdr.textSize = 13F
                val resultSchAdrParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
                )
                resultSchAdr.layoutParams = resultSchAdrParams

                val selectBtn = Button(applicationContext)
                selectBtn.text = "선택"
                val selectBtnParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
                )
                selectBtnParams.gravity = Gravity.CENTER
                selectBtnParams.weight = 1F
                selectBtn.layoutParams = selectBtnParams

                selectBtn.setOnClickListener {
                    changeSchTo(sch.edu, sch.sch)
                }

                subLayout.addView(resultSchName)
                subLayout.addView(resultSchAdr)

                wholeLayout.addView(subLayout)
                wholeLayout.addView(selectBtn)

                list.addView(wholeLayout)
            }}
        }
    }

    @Suppress("DEPRECATION")
    private fun changeSchTo(edu: String, sch: String) {
        SettingsManager.edu = edu
        SettingsManager.sch = sch

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

    private fun dipToPixels(context: Context): Float {
        val metrics = context.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10.0F, metrics)
    }
}