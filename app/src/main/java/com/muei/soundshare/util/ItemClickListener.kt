package com.muei.soundshare.util

interface ItemClickListener<T> {
    fun onItemClick(item: T)
    fun onAddFriendButtonClick(item: T)
    fun onRemoveSongButtonClick(item: T)
}