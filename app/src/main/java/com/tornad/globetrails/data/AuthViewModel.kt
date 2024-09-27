package com.tornad.globetrails.data

import android.content.Context
import android.widget.Toast
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.FirebaseDatabase
import com.tornad.globetrails.models.user
import com.tornad.globetrails.navigation.ROUTE_LOGIN

class AuthViewModel(private val navController: NavController, private val context: Context) {
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    // Updated login function with a callback to indicate success or failure
    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(context, "Email or Password cannot be empty", Toast.LENGTH_LONG).show()
            onResult(false) // Notify failure due to missing fields
            return
        }

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Login Successful", Toast.LENGTH_LONG).show()
                    onResult(true)  // Notify success
                } else {
                    Toast.makeText(context, "Login Failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    onResult(false) // Notify failure
                }
            }
    }

    // Function to handle user signup with a callback that returns a Boolean
    fun signup(
        firstname: String,
        lastname: String,
        country: String,
        email: String,
        password: String,
        onResult: (Boolean) -> Unit
    ) {
        if (firstname.isBlank() || lastname.isBlank() || country.isBlank() || email.isBlank() || password.isBlank()) {
            Toast.makeText(context, "Please fill all the fields.", Toast.LENGTH_LONG).show()
            onResult(false) // Notify failure due to missing fields
            return
        }

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUser = mAuth.currentUser
                    currentUser?.let {
                        val userData = user(firstname, lastname, country, email, password, it.uid)
                        saveUserToDatabase(userData, onResult)
                    } ?: run {
                        Toast.makeText(context, "Registration failed: User not found.", Toast.LENGTH_LONG).show()
                        onResult(false) // Notify failure due to user not being found
                    }
                } else {
                    handleAuthExceptions(task.exception)
                    onResult(false) // Notify failure due to registration error
                }
            }
    }

    // Function to save user data to Firebase Realtime Database with callback
    private fun saveUserToDatabase(userData: user, onResult: (Boolean) -> Unit) {
        val userRef = FirebaseDatabase.getInstance().getReference("Users").child(userData.uid)

        userRef.setValue(userData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "User successfully registered.", Toast.LENGTH_LONG).show()
                navController.navigate(ROUTE_LOGIN) // Navigate to login screen after success
                onResult(true) // Notify success
            } else {
                Toast.makeText(context, "Error saving user data: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                onResult(false) // Notify failure
            }
        }
    }

    // Function to handle FirebaseAuth-specific exceptions
    private fun handleAuthExceptions(exception: Exception?) {
        when (exception) {
            is FirebaseAuthUserCollisionException -> {
                Toast.makeText(context, "An account with this email already exists.", Toast.LENGTH_LONG).show()
            }
            is FirebaseAuthInvalidCredentialsException -> {
                Toast.makeText(context, "Invalid email or password format.", Toast.LENGTH_LONG).show()
            }
            else -> {
                Toast.makeText(context, "Authentication failed: ${exception?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun updateUserDetails(user: user) {
        val userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.uid)

        val userUpdates = mapOf(
            "firstname" to user.firstname,
            "lastname" to user.lastname,
            "country" to user.country,
            "email" to user.email,
            "password" to user.password,
        )

        userRef.updateChildren(userUpdates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "User details updated successfully.", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Error updating user details: ${task.exception?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

}
