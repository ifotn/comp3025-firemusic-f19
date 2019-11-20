package com.infrontofthenet.firemusic

import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.method.ScrollingMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
//import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile.fabExit
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ProfileActivity : AppCompatActivity() {

    // android default for permission granted
    val PERMISSION_REQUEST_CODE = 101

    // android default for taking a photo
    val REQUEST_IMAGE_CAPTURE = 1

    // path to the current photo taken
    var currentPath: String? = null

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

        // default profile image
        // set default image
        imageViewProfile.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_a_photo))

        imageViewProfile.setOnClickListener {
            //if (checkPermission()) {
                // permission already granted
                takePicture()
//            }
//            else {
//                // permission not granted so ask
//                requestPermission()
//            }
        }

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

    // has user already allowed the app to use camera & storage?
    private fun checkPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED)
    }

    // if permission not already granted, ask user if app can use camera & storage
    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(CAMERA, READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (!grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                            grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    takePicture()
                }
                else {
                    Toast.makeText(this, "Please grant permissions to set a profile photo", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    // start the camera
    private fun takePicture() {
        // this will use an explicit intent to invoke the camera
        //Toast.makeText(this, "Coming soon", Toast.LENGTH_LONG).show()
        val intent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val file: File = createImageFile()

        // get the location of this file
        val uri: Uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file)

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // if user invoked the camera and the activity was successful (RESULT_OK: -1)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = Uri.parse(currentPath)

            // display the image if we found one
            if (imageUri != null) {
                imageViewProfile.setImageURI(imageUri)
            }
        }
    }

    // create the image in storage
    private fun createImageFile() : File {
        // get timestamp to uniquely name the file
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())

        // set up to store in android photo gallery
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        // create the directory if it's not found
        if (!storageDir!!.exists()) {
            storageDir.mkdirs()
        }

        val image = File.createTempFile(timeStamp, ".jpg", storageDir)
        currentPath = image.absolutePath
        return image
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
