package com.e.learnit.model

class Question {
    var id: Int =0
    var questionText: String? = null
    var questionImage: String? = null
    var answerA: String? = null
    var answerB: String? = null
    var answerC: String? = null
    var answerD: String? = null
    var correctAnswer: String? = null
    var isImageQuestion: Boolean = false
    var categoryId: String? = null

    constructor() {}


}