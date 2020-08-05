package com.example.chatapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.chatapp.IntentData
import com.example.chatapp.KEY_USER_NAME
import com.example.chatapp.R
import com.example.chatapp.models.User
import com.example.chatapp.exceptions.AppExceptions
import com.example.chatapp.exceptions.ExceptionMessages
import com.example.chatapp.firebase.FireBaseAccess
import com.example.chatapp.firebase.FireBaseAccess.usersDataBaseReference
import com.example.chatapp.startNewActivity
import com.google.firebase.auth.FirebaseUser

class SignInActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var repeatPasswordEditText: EditText
    private lateinit var nameEditText: EditText
    private lateinit var signInButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        emailEditText = findViewById(R.id.emailSignInEditText)
        passwordEditText = findViewById(R.id.passwordSignInEditText)
        repeatPasswordEditText = findViewById(R.id.repeatPasswordSignInEditText)
        nameEditText = findViewById(R.id.nameSignInEditText)
        signInButton = findViewById(R.id.signInSignInButton)
    }

    fun onSignInButton(view: View) {
        val user = User()
        user.email = emailEditText.text.toString().trim()
        user.name = nameEditText.text.toString().trim()

        val password = passwordEditText.text.toString().trim()
        try {
            signUpUser(validationUserData(user, password), password)
        } catch (exception: AppExceptions){
            Toast.makeText(this, exception.message,Toast.LENGTH_LONG).show()
        }
    }

    fun onLogInButton(view: View){
        startNewActivity(this, LoginInActivity::class.java)
    }

    private fun signUpUser(user: User, password:String) {
        FireBaseAccess.auth.createUserWithEmailAndPassword(user.email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("SignUP", "createUserWithEmail:success")
                    FireBaseAccess.auth.currentUser?.let { createUser(it) }
                    startNewActivity(this, LoginInActivity::class.java, IntentData(KEY_USER_NAME, user.name))
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("SignUP", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        this, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun createUser(firebaseUser: FirebaseUser){
        val user = User()
        user.id = firebaseUser.uid
        user.email = firebaseUser.email.toString()
        user.name = nameEditText.text.toString().trim()

        usersDataBaseReference.push().setValue(user)
    }

    private fun validationUserData(user: User, password: String) : User {

        if (user.email.isEmpty())
            throw AppExceptions(ExceptionMessages.EmptyEmail)

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(user.email).matches())
            throw AppExceptions(ExceptionMessages.InvalidEmail)

        if (password.isEmpty())
            throw AppExceptions(ExceptionMessages.EmptyPassword)

        if (password.length < 7)
            throw AppExceptions(ExceptionMessages.PasswordIsTooShort)

        if (repeatPasswordEditText.text.toString().trim().isEmpty())
            throw AppExceptions(ExceptionMessages.EmptyRepeatPassword)

        if (password!= repeatPasswordEditText.text.toString().trim())
            throw AppExceptions(ExceptionMessages.PasswordsIsNotEquals)

        if(user.name.isEmpty())
            throw AppExceptions(ExceptionMessages.EmptyName)

        return user
    }


}