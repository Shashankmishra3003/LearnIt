package com.e.learnit.model

class Notes {

    var data: String? = null
    var topicName: String? = null

    constructor():this("","") {

    }


    constructor(Data: String?,topicName: String?) {
        this.data = Data
        this.topicName = topicName

    }
}