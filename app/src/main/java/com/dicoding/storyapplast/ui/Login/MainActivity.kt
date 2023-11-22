package com.dicoding.storyapplast.ui.Login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.viewModels
import com.dicoding.storyapplast.data.body.dataLogin
import com.dicoding.storyapplast.databinding.ActivityMainBinding
import com.dicoding.storyapplast.ui.ViewModelFactory
import com.dicoding.storyapplast.ui.home.HomeActivity
import com.dicoding.storyapplast.ui.main.WelcomeActivity
import com.dicoding.storyapplast.ui.register.RegistrasiActivity
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "isLogin"
        fun getLaunchService(from: Context) = Intent(from, WelcomeActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }

    private lateinit var  binding: ActivityMainBinding
    private val MainViewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showLoading(false)

        binding.loginbtn.setOnClickListener {
            if (binding.loginemail.text.toString()
                    .isEmpty() || binding.loginpassword.text.toString().isEmpty()
            ) {
                Toast.makeText(this, "Please fill all the field", Toast.LENGTH_SHORT).show()
            } else {
                showLoading(true)
                val email = binding.loginemail.text.toString()
                val password = binding.loginpassword.text.toString()

                MainViewModel.getLogin(dataLogin(email, password))
            }
        }

        MainViewModel.isLogin.observe(this) {
            if (it.error) {
                showLoading(false)
                Toast.makeText(this@MainActivity, "Login failed", Toast.LENGTH_SHORT).show()
            } else {
                showLoading(false)
                Log.d(TAG, "true")
                Toast.makeText(this@MainActivity, "Login success", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        binding.loginpassword.transformationMethod = PasswordTransformationMethod.getInstance()
        binding.showPasswordCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.loginpassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                binding.loginpassword.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }

        binding.registerhere.setOnClickListener {
            val intent = Intent(this, RegistrasiActivity::class.java)
            startActivity(intent)
        }

    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}