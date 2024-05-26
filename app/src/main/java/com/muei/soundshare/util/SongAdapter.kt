package com.muei.soundshare.util

import android.view.View
import com.bumptech.glide.Glide
import com.muei.soundshare.R
import com.muei.soundshare.databinding.LayoutSongBinding
import com.muei.soundshare.entities.Song
import org.koin.core.component.KoinComponent

class SongAdapter(
    songs: List<Song>, clickListener: ItemClickListener<Song>?
) : BaseAdapter<Song>(songs, clickListener, R.layout.layout_song), KoinComponent {

    override fun bindItem(view: View, item: Song) {
        val binding = LayoutSongBinding.bind(view)

        binding.songName.text = item.title
        binding.artistName.text = item.artist
        if (!item.songImage.isNullOrEmpty()) {
            Glide.with(binding.songImage.context).load(item.songImage).into(binding.songImage)
        }
    }
}