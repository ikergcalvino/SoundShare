package com.muei.soundshare.ui.map

import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.muei.soundshare.R

class CustomInfoWindowAdapter(inflater: LayoutInflater) : GoogleMap.InfoWindowAdapter {

    private val mWindow: View = inflater.inflate(R.layout.layout_map, null)

    private fun renderWindowText(marker: Marker, view: View) {
        val infoContent = view.findViewById<TextView>(R.id.text_post)
        val infoSongId = view.findViewById<TextView>(R.id.song_name)

        val postInfo = marker.tag as? PostInfo

        if (postInfo != null) {
            infoContent.text = postInfo.content
        }
        if (postInfo != null) {
            infoSongId.text = postInfo.songId
        }

    }

    override fun getInfoWindow(marker: Marker): View {
        renderWindowText(marker, mWindow)
        return mWindow
    }

    override fun getInfoContents(marker: Marker): View {
        renderWindowText(marker, mWindow)
        return mWindow
    }

    data class PostInfo(val content: String, val songId: String)
}
