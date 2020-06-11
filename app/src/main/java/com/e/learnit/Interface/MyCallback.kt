package com.e.learnit.Interface

import com.e.learnit.model.Question

interface MyCallback {
    fun setQuestionList(questionList:List<Question>)
}