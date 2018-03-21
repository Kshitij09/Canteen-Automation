package com.fireaxe.canteenautomation.models

/**
 * Created by negativezer0 on 17/3/18.
 */

class MyUser {
    var id: String? = null
    var id_number: String? = null
    var phone: String? = null
    var name: String? = null
    var isVerified: Boolean = false
    var isManager :Boolean = false

    constructor() {
        //Important
    }

    constructor(id: String, isVerified: Boolean) {
        this.id = id
        this.isVerified = isVerified
    }

    constructor(id: String, id_number: String, phone: String, name: String, isVerified: Boolean) {
        this.id = id
        this.id_number = id_number
        this.phone = phone
        this.name = name
        this.isVerified = isVerified
    }


}
