package com.joaohhenriq.kotlintictactoefirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*

class Login : AppCompatActivity() {

    private var mAuth : FirebaseAuth? = null
    private var dataBase = FirebaseDatabase.getInstance()
    private var myRef = dataBase.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()
        loadMain()
    }

    fun btnLoginEvent(view: View){
        loginToFirebase(edtEmail.text.toString(), edtPassword.text.toString())
    }

    private fun loginToFirebase(email: String, password: String) {
        mAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){
                if(it.isSuccessful) {
                    Toast.makeText(applicationContext, "Successfuly loged", Toast.LENGTH_LONG).show()

                    val currentUser = mAuth!!.currentUser

                    if(currentUser != null) {
                        myRef.child("users").child(currentUser.email.split("@")[0]).child("Request").setValue(currentUser.uid)
                    }

                    loadMain()
                } else {
                    Toast.makeText(applicationContext, "Failed to login", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun loadMain() {
        val currentUser = mAuth!!.currentUser

        if(currentUser != null) {
            var intent = Intent(this, MainActivity::class.java)
            intent.putExtra("email", currentUser.email)
            intent.putExtra("uid", currentUser.uid)

            startActivity(intent)
        }
    }
}