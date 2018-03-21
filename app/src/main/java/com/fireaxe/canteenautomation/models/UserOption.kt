package com.fireaxe.canteenautomation.models

class UserOption{
    var user_id : String?
    var item_id : String?
    var priority: Int
    var time : Long



    constructor(){
        user_id = null
        item_id = null
        priority = 0
        time =0
    }
    constructor(user_id: String, item_id: String, priority: Int, time: Long) {
        this.user_id = user_id
        this.item_id = item_id
        this.priority = priority
        this.time = time
    }

}