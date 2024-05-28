package com.muei.soundshare.services

import com.muei.soundshare.BuildConfig
import com.muei.soundshare.entities.Song
import com.muei.soundshare.util.Constants
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class SpotifyClient {

    private var clientId = Constants.SPOTIFY_CLIENT_ID
    private var clientSecret = BuildConfig.SPOTIFY_CLIENT_SECRET

    private var accessToken: String? = null
    private var tokenExpirationTime: Long = 0

    private fun getAccessToken(callback: (String?) -> Unit) {
        if (accessToken == null || isTokenExpired()) {
            requestAccessToken { success ->
                if (success) {
                    callback(accessToken)
                } else {
                    callback(null)
                }
            }
        } else {
            callback(accessToken)
        }
    }

    private fun requestAccessToken(callback: (Boolean) -> Unit) {
        val formBody =
            FormBody.Builder().add("grant_type", "client_credentials").add("client_id", clientId)
                .add("client_secret", clientSecret).build()

        val request = Request.Builder().url(Constants.TOKEN_URL).post(formBody)
            .addHeader("Content-Type", "application/x-www-form-urlencoded").build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                callback(false)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body?.let { responseBody ->
                        val json = responseBody.string()
                        val jsonObject = JSONObject(json)
                        accessToken = jsonObject.getString("access_token")
                        val expiresIn = jsonObject.getLong("expires_in")
                        tokenExpirationTime = System.currentTimeMillis() + (expiresIn * 1000)
                        callback(true)
                    } ?: run {
                        callback(false)
                    }
                } else {
                    callback(false)
                }
            }
        })
    }

    private fun isTokenExpired(): Boolean {
        return System.currentTimeMillis() >= tokenExpirationTime
    }

    fun getTrack(trackId: String, callback: (Song?) -> Unit) {
        getAccessToken { token ->
            if (token == null) {
                callback(null)
                return@getAccessToken
            }

            val url = Constants.TRACKS_URL + "/$trackId"
            val request =
                Request.Builder().url(url).addHeader("Authorization", "Bearer $token").build()

            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    callback(null)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        response.body?.let { responseBody ->
                            val json = responseBody.string()
                            val jsonObject = JSONObject(json)

                            val title = jsonObject.getString("name")
                            val artist = jsonObject.getJSONArray("artists").getJSONObject(0)
                                .getString("name")
                            val songImage = jsonObject.getJSONObject("album").getJSONArray("images")
                                .getJSONObject(0).getString("url")

                            val song = Song(trackId, title, artist, songImage)
                            callback(song)
                        } ?: run {
                            callback(null)
                        }
                    } else {
                        callback(null)
                    }
                }
            })
        }
    }

    fun searchSongs(query: String, callback: (List<Song>) -> Unit) {
        getAccessToken { token ->
            if (token == null) {
                callback(emptyList())
                return@getAccessToken
            }

            val url = Constants.SEARCH_URL + "?q=$query&type=track&limit=20"
            val request =
                Request.Builder().url(url).addHeader("Authorization", "Bearer $token").build()

            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    callback(emptyList())
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        response.body?.let { responseBody ->
                            val json = responseBody.string()
                            val jsonObject = JSONObject(json)
                            val tracks = jsonObject.getJSONObject("tracks").getJSONArray("items")

                            val songs = mutableListOf<Song>()
                            for (i in 0 until tracks.length()) {
                                val track = tracks.getJSONObject(i)
                                val title = track.getString("name")
                                val artist =
                                    track.getJSONArray("artists").getJSONObject(0).getString("name")
                                val songImage = track.getJSONObject("album").getJSONArray("images")
                                    .getJSONObject(0).getString("url")
                                val songId = track.getString("id")
                                songs.add(Song(songId, title, artist, songImage))
                            }
                            callback(songs)
                        } ?: run {
                            callback(emptyList())
                        }
                    } else {
                        callback(emptyList())
                    }
                }
            })
        }
    }
}
