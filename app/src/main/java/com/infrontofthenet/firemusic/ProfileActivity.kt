package com.infrontofthenet.firemusic

import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
//import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile.fabExit
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class ProfileActivity : AppCompatActivity() {

    // android default for permission granted
    val PERMISSION_REQUEST_CODE = 101

    // android default for taking a photo
    val REQUEST_IMAGE_CAPTURE = 1

    // path to the current photo taken
    var currentPath: String? = null

    val user = FirebaseAuth.getInstance().currentUser

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

        // default profile image - try first to get from firebase profile
        var profilePhoto: Uri? = user!!.photoUrl

        if (profilePhoto != null) {
            var path = profilePhoto.path
            loadImageFromInternalStorage(path!!)
        }
        else {
            // set default image
            imageViewProfile.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_a_photo))
        }

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

            // try to save a copy to app's internal storage
            try {
                var bitmap: Bitmap = BitmapFactory.decodeFile(currentPath)
                var path = saveToInternalStorage(bitmap)

                // try saving to user's profile too by converting the local bitmap to a uri
                var builder = Uri.Builder()
                var localUri = builder.appendPath(path).build()
                saveProfilePhoto(localUri)
            }
            catch (e:IOException) {
                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
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

    // save a copy of photo to app's internal storage to avoid cross-application permission problems
    private fun saveToInternalStorage(bitmapImage: Bitmap) : String {
        // set up private image directory for our app
        var cw = ContextWrapper(getApplicationContext())
        var directory: File = cw.getDir("imageDir", Context.MODE_PRIVATE)

        // always save the local file copy as "profile.jpg"
        var path: File = File(directory, "profile.jpg")
        var fos: FileOutputStream? = null

        // use the stream to write a copy of the image file
        try {
            fos = FileOutputStream(path)
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos)
        }
        catch (e:Exception) {
            Toast.makeText(this, "Could not save file", Toast.LENGTH_LONG).show()
        }
        finally {
            try {
                fos!!.close()
            }
            catch (e:IOException) {
                Toast.makeText(this, "Could not save file", Toast.LENGTH_LONG).show()
            }
        }

        return directory.absolutePath
    }

    // save photo to Firebase profile
    private fun saveProfilePhoto(imageUri: Uri?) {

        // set up the profile update with the photo uri
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setPhotoUri(imageUri)
            .build()

        // commit the update to firebase
        user!!.updateProfile(profileUpdates)
            .addOnCompleteListener { object: OnCompleteListener<Void?> {
                override fun onComplete(p0: Task<Void?>) {
                    if (p0.isSuccessful()) {
                        Toast.makeText(applicationContext, "Image Saved to Firebase", Toast.LENGTH_LONG).show()
                    }
                }
            }}
    }

    // get local profile.jpg if any
    private fun loadImageFromInternalStorage(path: String) {
        try {
            var file: File = File(path, "profile.jpg")

            // convert to bitmap
            var bitmapImage = BitmapFactory.decodeStream(FileInputStream(file))

            // render in the imageview
            imageViewProfile.setImageBitmap(bitmapImage)
        }
        catch (e: FileNotFoundException) {
            Toast.makeText(this, "Could not load profile photo", Toast.LENGTH_LONG).show()
        }
    }
}
