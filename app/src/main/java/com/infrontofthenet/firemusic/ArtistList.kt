package com.infrontofthenet.firemusic

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.Button
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // inflate the menu_main to add the items to the toolbar
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.action_main -> {
                 startActivity(Intent(applicationContext, MainActivity::class.java))
                return true
            }
            R.id.action_list -> {
                // do nothing, already on this activity
                return true
            }
            R.id.action_profile -> {
                startActivity(Intent(applicationContext, ProfileActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artist_list)

        // instantiate toolbar
        setSupportActionBar(toolbar)

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

        // not needed if we bind to the item template directly on the onBindViewHolder method below
//        internal fun displayArtist(artistName: String, artistGenre: String) {
//            // populate the corresponding textViews in the layout template inflated when adapter created below
//            val textViewName = view.findViewById<TextView>(R.id.textViewName)
//            val textViewGenre = view.findViewById<TextView>(R.id.textViewGenre)
//
//            textViewName.text = artistName
//            textViewGenre.text = artistGenre
//        }
    }

    private inner class ArtistAdapter internal constructor(options: FirestoreRecyclerOptions<Artist>) :
        FirestoreRecyclerAdapter<Artist,
                ArtistViewHolder>(options) {

        override fun onBindViewHolder(p0: ArtistViewHolder, p1: Int, p2: Artist) {
            // pass current Artist values to the display function above
            //p0.displayArtist(p2.artistName.toString(), p2.artistGenre.toString()) - replaced by 2 lines below
            p0.itemView.findViewById<TextView>(R.id.textViewName).text = p2.artistName
            p0.itemView.findViewById<TextView>(R.id.textViewGenre).text = p2.artistGenre

            p0.itemView.setOnClickListener {
                val url = p2.url.toString()
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
            // when creating, instantiate the item_artist.xml template (only happens once)
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_artist, parent, false)
            return ArtistViewHolder(view)
        }
    }
}
