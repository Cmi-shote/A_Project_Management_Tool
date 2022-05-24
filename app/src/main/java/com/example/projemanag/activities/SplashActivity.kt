package com.example.projemanag.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.projemanag.databinding.ActivitySplashBinding
import com.example.projemanag.firebase.FireStore

class SplashActivity : AppCompatActivity() {
    private var binding : ActivitySplashBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        //move to intro screen after 2s
        Handler().postDelayed({
            val currentUserID = FireStore().getCurrentUserID()
            if(currentUserID.isNotEmpty()){
                startActivity(Intent(this, MainActivity::class.java))
            }else{
                startActivity(Intent(this, IntroActivity::class.java))
            }
            finish()
        }, 2000)

    }
}