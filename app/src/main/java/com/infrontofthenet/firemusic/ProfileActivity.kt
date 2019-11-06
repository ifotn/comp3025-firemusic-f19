package com.infrontofthenet.firemusic

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.Menu
import android.view.MenuItem
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
//import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile.fabExit

class ProfileActivity : AppCompatActivity() {

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
                startActivity(Intent(applicationContext, ArtistList::class.java))
                return true
            }
            R.id.action_profile -> {
                //do nothing
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // toolbar
        setSupportActionBar(toolbar)

        // enable scrolling on terms textView since it only shows 10 lines at a time
        textViewTerms.movementMethod = ScrollingMovementMethod()

        val user = FirebaseAuth.getInstance().currentUser

        user?.let {
            val name = user.displayName
            val email = user.email

            textViewName.text = name
            textViewEmail.text = email
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

    override fun onStart() {
        super.onStart()

        // check for authenticated user
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            val intent = Intent(applicationContext, SignInActivity::class.java)
            startActivity(intent)
        }
    }
}
