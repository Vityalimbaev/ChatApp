package com.example.chatapp.models

class User(var id: String, var email: String, var name:String, var avatarMockUpResource:Int?) {
    constructor() : this("", "", "", null  ){
    }
}