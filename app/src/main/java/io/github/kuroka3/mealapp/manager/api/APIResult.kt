package io.github.kuroka3.mealapp.manager.api

data class APIResult(val result: Int, val body: Any?, val err: Any?) {
    companion object {
        const val RESULT_OK = 0
        const val RESULT_ERR = 1
        const val RESULT_EXCEPTION = 2
    }
}