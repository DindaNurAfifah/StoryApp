package com.dicoding.storyapplast.ui.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.dicoding.storyapplast.data.body.dataRegistrasi
import com.dicoding.storyapplast.databinding.ActivityRegistrasiBinding
import com.dicoding.storyapplast.ui.Login.MainActivity
import com.dicoding.storyapplast.ui.ViewModelFactory

class RegistrasiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrasiBinding
    private val registrasiViewModel by viewModels<RegistrasiViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrasiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showLoading(false)

        binding.registerbtn.setOnClickListener {
            if(binding.registerpassword.text.toString().isEmpty() || binding.registeremail.text.toString().isEmpty() || binding.registernama.text.toString().isEmpty()){
                Toast.makeText(this, "Fill all the data!", Toast.LENGTH_SHORT).show()
            }else{
                showLoading(true)
                val name = binding.registernama.text.toString()
                val email = binding.registeremail.text.toString()
                val password = binding.registerpassword.text.toString()

                registrasiViewModel.getRegister(dataRegistrasi(name, email, password))
            }
        }

        registrasiViewModel.isRegister.observe(this) {
            if (it.error) {
                showLoading(false)
                Toast.makeText(this@RegistrasiActivity, "Registration failed", Toast.LENGTH_SHORT).show()
                Toast.makeText(this@RegistrasiActivity, "This email may already used", Toast.LENGTH_SHORT).show()
            } else {
                showLoading(false)
                Toast.makeText(this@RegistrasiActivity, "Registration success", Toast.LENGTH_SHORT).show()
                startActivity(Intent(MainActivity.getLaunchService(this)))
            }
        }

        binding.registerpassword.transformationMethod = PasswordTransformationMethod.getInstance()
        binding.showPasswordCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.registerpassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                binding.registerpassword.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }

        binding.loginhere.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
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