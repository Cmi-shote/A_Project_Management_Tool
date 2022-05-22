package com.example.projemanag.activities

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.projemanag.R
import com.example.projemanag.databinding.ActivitySignupBinding
import com.example.projemanag.firebase.FireStore
import com.example.projemanag.models.User
import com.google.firebase.auth.FirebaseAuth


class SignupActivity : BaseActivity() {
    private var binding : ActivitySignupBinding? = null
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        auth = FirebaseAuth.getInstance()

        setupActionBar()

        binding?.btnSignUp?.setOnClickListener {
            registerUser()
        }
    }


    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarSignUpActivity)

        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }

        binding?.toolbarSignUpActivity?.setNavigationOnClickListener { onBackPressed() }
    }

    private fun registerUser(){
        val name: String = binding?.etName?.text.toString().trim{ it <= ' '}
        val email: String = binding?.etEmail?.text.toString().trim{ it <= ' '}
        val password: String = binding?.etPassword?.text.toString().trim{ it <= ' '}

        if(validateForm(name, email, password)){
            showProgressDialog()

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener{ task ->

                // If the registration is successfully done
                if (task.isSuccessful) {
                    val firebaseUser = task.result!!.user!!
                    val registeredEmail = firebaseUser.email
                    val user = User(firebaseUser.uid, name, registeredEmail!!)
                    FireStore().registerUser(this@SignupActivity, user)//the firestore class in the file

                }else{
                    Toast.makeText(
                        this,
                        task.exception!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    private fun validateForm(name: String, email: String, password : String): Boolean{
        return when {
            TextUtils.isEmpty(name) -> {
                showErrorSnackBar("Please enter a name")
                false
            }
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please enter an email address")
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please enter a password")
                false
            }
            else -> {
                true
            }

        }
    }

    fun userRegisteredSuccess(){
        Toast.makeText(
            this,
            "you have successfully registered",
            Toast.LENGTH_LONG
        ).show()
        hideProgressDialog()
        auth.signOut()
        finish()
    }
}