package com.infrontofthenet.firemusic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
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
            // Create a new user with a first and last name
            val data = HashMap<String, Any>()
            data.put("first", "Another")
            data.put("last", "Person")
            data.put("born", "1989")

            // Add a new document with a generated ID
            db.collection("members")
                .add(data)
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(this,
                        "DocumentSnapshot added with ID: " + documentReference.id,
                        Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
                }
        }
    }
}
