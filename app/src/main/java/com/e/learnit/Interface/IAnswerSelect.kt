package com.e.learnit.Interface

import com.e.learnit.model.CurrentQuestion

interface IAnswerSelect {
    fun selectedAnswer():CurrentQuestion
    fun showCorrectAnswer()
    fun disableAnswer()
    fun resetQuestion()
}