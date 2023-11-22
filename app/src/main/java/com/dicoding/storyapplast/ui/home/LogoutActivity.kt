package com.dicoding.storyapplast.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.dicoding.storyapplast.databinding.ActivityLogoutBinding
import com.dicoding.storyapplast.ui.ViewModelFactory
import com.dicoding.storyapplast.ui.main.WelcomeActivity

class LogoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLogoutBinding
    private val homeViewModel by viewModels<HomeViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnNo.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnYes.setOnClickListener {
            homeViewModel.logout()
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}