package com.example.chatapp.models

class MessageChatApp(
    var message: String,
    var name: String,
    var imageURL: String,
    var sender: String,
    var recipient: String
) {
    constructor() : this("", "", "", "", "") {}
}