package com.dicoding.storyapplast.ui.main

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import com.dicoding.storyapplast.databinding.ActivityWelcomeBinding
import com.dicoding.storyapplast.ui.Login.MainActivity
import com.dicoding.storyapplast.ui.ViewModelFactory
import com.dicoding.storyapplast.ui.home.HomeActivity
import com.dicoding.storyapplast.ui.home.HomeViewModel
import com.dicoding.storyapplast.ui.register.RegistrasiActivity

class WelcomeActivity : AppCompatActivity() {

    companion object {
        const val TAG = "isLogin: "
    }

    private lateinit var binding: ActivityWelcomeBinding
    private val HomeViewModel by viewModels<HomeViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showLoading(false)
        setupView()
        setupAction()
        playAnimation()

        HomeViewModel.getToken().observe(this) {shared ->
            if (shared.isLogin) {
                showLoading(false)
                Log.d(TAG, "true")
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            } else {
                showLoading(false)
                setContentView(binding.root)
            }
        }

    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            showLoading(true)
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.signupButton.setOnClickListener {
            showLoading(true)
            startActivity(Intent(this, RegistrasiActivity::class.java))
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}