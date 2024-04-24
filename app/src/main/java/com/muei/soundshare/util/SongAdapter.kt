package com.muei.soundshare.util

import android.view.View
import com.muei.soundshare.R
import com.muei.soundshare.databinding.LayoutSongBinding
import com.muei.soundshare.entities.Song

class SongAdapter(
    private var songs: List<Song>,
    private val onSongSelected: (String) -> Unit // Callback para manejar la selección de canciones
) : BaseAdapter<Song>(songs, R.layout.layout_song) {

    private var filteredSongs: List<Song> = songs

    fun filter(query: String) {
        filteredSongs = if (query.isEmpty()) {
            songs
        } else {
            songs.filter { it.title.contains(query, ignoreCase = true) || it.artist.contains(query, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return filteredSongs.size
    }

    override fun bindItem(view: View, item: Song) {
        val binding = LayoutSongBinding.bind(view)

        binding.apply {
            songName.text = item.title
            artistName.text = item.artist
        }
    }

    override fun onItemClick(item: Song) {
        onSongSelected(item.songId)
    }

    override fun onAddButtonClick(item: Song) {
        TODO("Not yet implemented")
    }
}
