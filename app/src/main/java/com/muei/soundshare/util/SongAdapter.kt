package com.muei.soundshare.util

import android.view.View
import com.muei.soundshare.R
import com.muei.soundshare.databinding.LayoutSongBinding
import com.muei.soundshare.entities.Song

class SongAdapter(
    songs: List<Song>
) : BaseAdapter<Song>(songs, R.layout.layout_song) {

    override fun bindItem(view: View, item: Song) {
        val binding = LayoutSongBinding.bind(view)

        binding.apply {
            songName.text = item.title
            artistName.text = item.artist
            // songImage.setImageDrawable(item.songImage)
        }
    }

    override fun onItemClick(item: Song) {
        TODO("Not yet implemented")
    }

    override fun onAddButtonClick(item: Song) {
        TODO("Not yet implemented")
    }
}