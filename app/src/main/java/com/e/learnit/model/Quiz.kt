package com.e.learnit.model

class Quiz {
    var Name: String? = null
    var Image: String? = null

    constructor():this("","") {

    }


    constructor(Name: String?, Image: String?) {
        this.Name = Name
        this.Image =  Image
    }
}