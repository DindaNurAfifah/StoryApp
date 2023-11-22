package com.dicoding.storyapplast.helper

import com.dicoding.storyapplast.data.response.ListStoryItem

object DataDummy {

    fun generateDummyStoriesRespone(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val stories = ListStoryItem(
                "photoUrl + $i",
                "createdAt + $i",
                "name + $i",
                "description + $i",
                i.toDouble(),
                i.toString(),
                i.toDouble(),
            )
            items.add(stories)
        }
        return items
    }
}