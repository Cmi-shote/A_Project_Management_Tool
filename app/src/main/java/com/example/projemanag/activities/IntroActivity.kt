package com.example.projemanag.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.projemanag.databinding.ActivityIntroBinding

class IntroActivity : AppCompatActivity() {
    private var binding: ActivityIntroBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        binding?.btnSignInIntro?.setOnClickListener {
            startActivity(Intent(this, SigninActivity::class.java))
        }

        binding?.btnSignUpIntro?.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
}