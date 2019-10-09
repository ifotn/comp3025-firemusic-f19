package com.infrontofthenet.firemusic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore

class ArtistList : AppCompatActivity() {

    // connect to firestore
    var db = FirebaseFirestore.getInstance()

    // classes to store and pass query data
    //private var adapter: ArtistAdapter? = null

    // Kotlin equivalent of Java ArrayList class
    private var artistList = mutableListOf<Artist>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artist_list)
    }
}
