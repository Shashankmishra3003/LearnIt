package com.e.learnit.model


class CourseTopic {
    var Name: String? = null
    var Description: String? = null
    var Video: String? = null
    var FilePath:String? = null

    constructor():this("","","","") {

    }


    constructor(Name: String?, Description: String?,Video:String?,FilePath:String?) {
        this.Name = Name
        this.Description =  Description
        this.Video = Video
        this.FilePath = FilePath
    }
}