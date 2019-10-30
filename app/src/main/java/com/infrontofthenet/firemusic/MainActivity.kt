package com.infrontofthenet.firemusic

import android.content.Intent
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
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {

    // connect to firestore
    var db = FirebaseFirestore.getInstance()

    override fun onStart() {
        super.onStart()

        // check for authenticated user
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            val intent = Intent(applicationContext, SignInActivity::class.java)
            startActivity(intent)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonList.setOnClickListener {
            val intent = Intent(applicationContext, ArtistList::class.java)
            startActivity(intent)
        }

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
                var url: String? = null
                // create and populate new Artist object
                val artist = Artist(id, name, genre, url)

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

        fabExit.setOnClickListener {
            // log user out
            AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener {
                    // redirect to SignInActivity
                    val intent = Intent(applicationContext, SignInActivity::class.java)
                    startActivity(intent)
                }
        }
    }
}
