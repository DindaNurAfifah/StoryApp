package com.dicoding.storyapplast.ui.home

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storyapplast.databinding.ActivityHomeBinding
import com.dicoding.storyapplast.ui.ViewModelFactory
import com.dicoding.storyapplast.ui.home.map.MapsActivity
import com.dicoding.storyapplast.ui.home.upload.UpStoriesActivity
import com.dicoding.storyapplast.ui.main.WelcomeActivity

class HomeActivity : AppCompatActivity() {

    companion object {
        const val TAG: String = "HomeActivity"
        fun getLaunchService(from: Context) = Intent(from, WelcomeActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }

    private lateinit var binding: ActivityHomeBinding
    private lateinit var Hadapter: HmAdapter
    private val HomeViewModel by viewModels<HomeViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showLoading(true)
        Hadapter = HmAdapter()
        setrv()

        HomeViewModel.getToken().observe(this) {shared ->
            val getToken = shared.token
            if (shared.isLogin) {
                showLoading(false)
                Toast.makeText(this@HomeActivity, "Success to retrieve data", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Success to retrieve data")
                setData(getToken)
            } else {
                showLoading(false)
                Toast.makeText(this@HomeActivity, "Failed to retrieve data", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Failed to retrieve data")
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }

        binding.btnlogout.setOnClickListener{
            startActivity(Intent(this, LogoutActivity::class.java))
        }

        binding.btnadd.setOnClickListener {
            startActivity(Intent(this, UpStoriesActivity::class.java))
        }

        binding.btnmap.setOnClickListener{
            startActivity(Intent(this, MapsActivity::class.java))
        }

    }

    private fun setrv() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvstory.layoutManager = layoutManager
        val item = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvstory.addItemDecoration(item)
    }

    private fun setData(token: String) {
        Hadapter = HmAdapter()
        binding.rvstory.adapter = Hadapter
        HomeViewModel.getStories(token).observe(this) {
            Hadapter.submitData(lifecycle, it)
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