package com.dicoding.storyapplast.ui.home.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.dicoding.storyapplast.data.response.ListStoryItem
import com.dicoding.storyapplast.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_STORY = "EXTRA_STORY"
    }

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showLoading(true)


    val listStoryItem = intent.getParcelableExtra<ListStoryItem>(EXTRA_STORY)

        if (listStoryItem != null) {
            setDetail(listStoryItem)
            /*showLoading(false)
            binding.detailtitle.text = listStoryItem.name
            binding.detaildesc.text = listStoryItem.description
            Glide.with(this)
                .load(listStoryItem.photoUrl)
                .into(binding.detailstoriesimg)
            Log.d(TAG,"name(tittle): ${listStoryItem.name}")
            Log.d(TAG,"description(desc): ${listStoryItem.description}")
            Log.d(TAG,"photoUrl(image): ${listStoryItem.photoUrl}")*/
        } else {
            showLoading(false)
            Toast.makeText(this@DetailActivity, "Failed to retrieve data", Toast.LENGTH_SHORT).show()
        }
    }

    private fun  setDetail(listStoryItem: ListStoryItem) {
        showLoading(false)
        binding.detailtitle.text = listStoryItem.name
        binding.detaildesc.text = listStoryItem.description
        Glide.with(this)
            .load(listStoryItem.photoUrl)
            .into(binding.detailstoriesimg)
        Log.e("name(tittle)", "${listStoryItem.name}")
        Log.e("description(desc)", "${listStoryItem.description}")
        Log.e("photoUrln(image)", "${listStoryItem.photoUrl}")
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}