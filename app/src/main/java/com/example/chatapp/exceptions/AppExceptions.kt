package com.example.chatapp.exceptions

import java.lang.Exception

class AppExceptions(exception: ExceptionMessages) : Exception(exception.message)

enum class ExceptionMessages(val message: String) {
    EmptyEmail("Enter your email"),
    InvalidEmail("Invalid email"),
    EmptyPassword("Enter your password"),
    EmptyRepeatPassword("Repeat your password"),
    PasswordIsTooShort("Password is too short, it must be longer than 7 characters"),
    PasswordsIsNotEquals("Password mismatch"),
    EmptyName("Enter your Name")
}