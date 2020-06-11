package com.e.learnit.Common

import com.e.learnit.model.CurrentQuestion
import com.e.learnit.model.Question
import com.e.learnit.ui.fragments.QuestionFragment
import java.lang.StringBuilder

object Common {
    val TOTAL_TIME = 20*60*1000 // 20 mins
    var answerSheetListFilterred:MutableList<CurrentQuestion> = ArrayList()
    var answerSheetList:MutableList<CurrentQuestion> = ArrayList()
    var questionList:MutableList<Question> = ArrayList()
    var fragmentList:MutableList<QuestionFragment> = ArrayList()

    val selected_values:MutableList<String> = ArrayList()

    var selectedCategory:String?=null

    var timer =0
    var right_answer_count = 0
    var wrong_answer_count = 0
    var no_answer_count = 0
    var data_question = StringBuilder()
    val  KEY_GO_TO_QUESTION:String? = "position_go_to"
    val  KEY_BACK_FROM_RESULY:String? = "back_from_result"


    enum class ANSWER_TYPE{
        NO_ANSWER,
        RIGHT_ANSWER,
        WRONG_ANSWER
    }
}