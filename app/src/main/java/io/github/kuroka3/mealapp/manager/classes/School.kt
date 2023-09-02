package io.github.kuroka3.mealapp.manager.classes

import io.github.kuroka3.mealapp.manager.exceptions.RequiredArgumentIsNullException

class School(builder: Builder) {
    val edu: String
    val sch: String

    val sch_name: String
    val adr: String

    class Builder {
        var edu: String? = null
        var sch: String? = null

        var sch_name: String? = null
        var adr: String? = null

        fun edu(value: String): Builder { edu = value; return this }
        fun sch(value: String): Builder { sch = value; return this }
        fun sch_name(value: String): Builder { sch_name = value; return this }
        fun adr(value: String): Builder { adr = value; return this }

        fun build(): School {
            when (null) {
                edu -> throw RequiredArgumentIsNullException()
                sch -> throw RequiredArgumentIsNullException()
                sch_name -> throw RequiredArgumentIsNullException()
                adr -> throw RequiredArgumentIsNullException()
            }

            return School(this)
        }
    }

    init {
        this.edu = builder.edu!!
        this.sch = builder.sch!!
        this.sch_name = builder.sch_name!!
        this.adr = builder.adr!!
    }
}
