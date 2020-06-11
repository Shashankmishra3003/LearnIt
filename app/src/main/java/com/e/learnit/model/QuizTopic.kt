package com.e.learnit.model

class QuizTopic {
    var id: String? = null
    var Name: String? = null
    var Description: String? = null

    constructor():this(" ","","") {

    }


    constructor(id:String?, Name: String?, Description: String?) {
        this.id = id
        this.Name = Name
        this.Description =  Description
    }
}