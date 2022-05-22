package com.example.projemanag.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.projemanag.R
import com.example.projemanag.databinding.ActivitySigninBinding
import com.example.projemanag.firebase.FireStore
import com.example.projemanag.models.User
import com.google.firebase.auth.FirebaseAuth

class SigninActivity : BaseActivity() {

    private var binding : ActivitySigninBinding? = null

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setupActionBar()

        //init firebaseauth
        auth = FirebaseAuth.getInstance()

        binding?.btnSignIn?.setOnClickListener {
            signInRegisteredUser()
        }


    }

    fun signInSuccess(user: User){
        hideProgressDialog()
        startActivity(Intent(this@SigninActivity, MainActivity::class.java))
        finish()
    }

    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarSignInActivity)

        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }

        binding?.toolbarSignInActivity?.setNavigationOnClickListener { onBackPressed() }
    }

    private fun signInRegisteredUser(){
        val email: String = binding?.etEmail?.text.toString().trim{ it <= ' '}
        val password: String = binding?.etPassword?.text.toString().trim{ it <= ' '}

        if(validateForm(email, password)){
            showProgressDialog()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        FireStore().loadUserData(this@SigninActivity)
                        Toast.makeText(this@SigninActivity,  "successful",
                            Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@SigninActivity,  task.exception!!.message,
                            Toast.LENGTH_LONG).show()

                    }

        }
        }
    }

    private fun validateForm(email: String, password: String): Boolean {
        return if (TextUtils.isEmpty(email)) {
            showErrorSnackBar("Please enter email.")
            false
        } else if (TextUtils.isEmpty(password)) {
            showErrorSnackBar("Please enter password.")
            false
        } else {
            true
        }
    }


}