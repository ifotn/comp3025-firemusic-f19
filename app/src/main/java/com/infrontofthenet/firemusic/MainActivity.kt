package com.infrontofthenet.firemusic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.text.TextUtils
import kotlinx.android.synthetic.main.activity_main.*
import androidx.annotation.NonNull
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.firestore.DocumentReference
import com.google.android.gms.tasks.OnSuccessListener
//import javax.swing.UIManager.put
import android.widget.Toast


class MainActivity : AppCompatActivity() {

    // connect to firestore
    var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonSave.setOnClickListener {
            // get the user inputs
            val name = editTextName.text.toString().trim()

            // validate name input
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(this, "Artist Name is Required", Toast.LENGTH_LONG).show()
            }
            else {
                // store selected Genre
                val genre = spinnerGenres.selectedItem.toString()

                // get new document with unique id
                val tbl = db.collection("artists")
                val id = tbl.document().getId()

                // create and populate new Artist object
                val artist = Artist(id, name, genre)

                // or call empty constructor and populate properties individually
//                var artist = Artist()
//                artist.artistId = id
//                artist.artistName = name
//                artist.artistGenre = genre

                // save to the db
                tbl.document(id).set(artist)

                // clean up
                editTextName.setText("")
                spinnerGenres.setSelection(0)
                Toast.makeText(this, "Artist Added", Toast.LENGTH_LONG).show()
            }

            // hardcoded example from firebase assistant
//            // Create a new user with a first and last name
//            val data = HashMap<String, Any>()
//            data.put("first", "Another")
//            data.put("last", "Person")
//            data.put("born", "1989")
//
//            // Add a new document with a generated ID
//            db.collection("members")
//                .add(data)
//                .addOnSuccessListener { documentReference ->
//                    Toast.makeText(this,
//                        "DocumentSnapshot added with ID: " + documentReference.id,
//                        Toast.LENGTH_LONG).show()
//                }
//                .addOnFailureListener { e ->
//                    Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
//                }
        }
    }
}
