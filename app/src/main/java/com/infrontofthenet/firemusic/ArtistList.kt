package com.infrontofthenet.firemusic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_artist_list.*

class ArtistList : AppCompatActivity() {

    // connect to firestore
    var db = FirebaseFirestore.getInstance()

    // classes to store and pass query data
    private var adapter: ArtistAdapter? = null

    // Kotlin equivalent of Java ArrayList class. We decided we don't need this after all
    //private var artistList = mutableListOf<Artist>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artist_list)

        // recycler will have linear layout
        recyclerViewArtists.setLayoutManager(LinearLayoutManager(this))

        // query
        val query = db.collection("artists").orderBy("artistName", Query.Direction.ASCENDING)

        // pass the query results to the recycler adapter for display
        val options = FirestoreRecyclerOptions.Builder<Artist>().setQuery(query, Artist::class.java).build()
        adapter = ArtistAdapter(options)

        // bind the adapter to the recyclerview (adapter means the datasource)
        recyclerViewArtists.adapter = adapter
    }

    // start listening for changes if the the activity starts / restarts
    override fun onStart() {
        super.onStart()
        adapter!!.startListening()
    }

    // stop listening for data changes if the activity gets stopped
    override fun onStop() {
        super.onStop()
        adapter!!.stopListening()
    }

    // inner classes needed to read and bind the data
    private inner class ArtistViewHolder internal constructor(private val view: View) :
        RecyclerView.ViewHolder(view) {

        internal fun displayArtist(artistName: String, artistGenre: String) {
            // populate the corresponding textViews in the layout template inflated when adapter created below
            val textViewName = view.findViewById<TextView>(R.id.textViewName)
            val textViewGenre = view.findViewById<TextView>(R.id.textViewGenre)

            textViewName.text = artistName
            textViewGenre.text = artistGenre
        }
    }

    private inner class ArtistAdapter internal constructor(options: FirestoreRecyclerOptions<Artist>) :
        FirestoreRecyclerAdapter<Artist,
                ArtistViewHolder>(options) {

        override fun onBindViewHolder(p0: ArtistViewHolder, p1: Int, p2: Artist) {
            // pass current Artist values to the display function above
            p0.displayArtist(p2.artistName.toString(), p2.artistGenre.toString())
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
            // when creating, instantiate the item_artist.xml template (only happens once)
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_artist, parent, false)
            return ArtistViewHolder(view)
        }
    }
}
