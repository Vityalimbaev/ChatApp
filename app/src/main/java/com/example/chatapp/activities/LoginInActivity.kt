package com.example.chatapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.example.chatapp.IntentData
import com.example.chatapp.R
import com.example.chatapp.models.User
import com.example.chatapp.exceptions.AppExceptions
import com.example.chatapp.exceptions.ExceptionMessages
import com.example.chatapp.firebase.FireBaseAccess
import com.example.chatapp.startNewActivity

class LoginInActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FireBaseAccess.auth.currentUser?.let {
            startNewActivity(this, UserListActivity::class.java)
            finish()
        }

        setContentView(R.layout.activity_loginin)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
    }

    fun onLogInButtonClick(view: View){
        val password = passwordEditText.text.toString().trim()
        val user = User()
        user.email = emailEditText.text.toString().trim()

        try {
            logInUser(validationUserData(user, password), password)
        } catch (exception: AppExceptions){
            Toast.makeText(this, exception.message,Toast.LENGTH_LONG).show()
        }
    }

    fun onSignInButtonClick(view: View){
        startNewActivity(this, SignInActivity::class.java)
    }

    private fun logInUser(user: User, password: String) {
        FireBaseAccess.auth.signInWithEmailAndPassword(user.email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("LogIN", "signInWithEmail:success")
                    startNewActivity(this@LoginInActivity, UserListActivity::class.java)
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("LogIN", "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        this, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun validationUserData(user: User, password:String) : User {

        if (user.email.isEmpty())
            throw AppExceptions(ExceptionMessages.EmptyEmail)

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(user.email).matches())
            throw AppExceptions(ExceptionMessages.InvalidEmail)

        if (password.isEmpty())
            throw AppExceptions(ExceptionMessages.EmptyPassword)

        return user
    }

}