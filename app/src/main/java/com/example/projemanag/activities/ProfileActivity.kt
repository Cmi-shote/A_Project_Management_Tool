package com.example.projemanag.activities


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.projemanag.R
import com.example.projemanag.databinding.ActivityProfileBinding
import com.example.projemanag.firebase.FireStore
import com.example.projemanag.models.User
import com.example.projemanag.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException


class ProfileActivity : BaseActivity() {

    private var binding: ActivityProfileBinding? = null

    private var mSelectedImageFileURi : Uri? = null
    private lateinit var mUserDetails: User
    private var mProfileImageUrl : String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupActionBar()
        FireStore().loadUserData(this@ProfileActivity)

        binding?.ivProfileUserImage?.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){

                Constants.showImageChooser(this@ProfileActivity)

            } else{
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }

        binding?.btnUpdate?.setOnClickListener {
            if(mSelectedImageFileURi != null){
                uploadUserImage()
            }
            else{
                showProgressDialog()

                updatedUserProfileData()
            }

        }
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK &&
            requestCode == Constants.PICK_IMAGE_REQUEST_CODE &&
            data!!.data != null){
            mSelectedImageFileURi = data.data!!

            try{
            Glide
                .with(this@ProfileActivity)
                .load(Uri.parse(mSelectedImageFileURi.toString()))
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(binding!!.ivProfileUserImage)
            }catch (e: IOException){
                e.printStackTrace()
            }
        }
    }

    //the onRequestPermissionsResult identifies the result after the user allows or deny permission based on the unique code.
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this@ProfileActivity)
            } else {
                Toast.makeText(
                    this, "Oops you just denied permission. You can enable this at the settings",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarMyProfileActivity)

        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_white_ios_24)
            actionBar.title = resources.getString(R.string.my_profile)
        }

        binding?.toolbarMyProfileActivity?.setNavigationOnClickListener { onBackPressed() }
    }


    fun setUserDataInUI(user: User){

        mUserDetails = user

        Glide
            .with(this@ProfileActivity)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(binding!!.ivProfileUserImage)

        binding?.etName?.setText(user.name)
        binding?.etEmail?.setText(user.email)
        if(user.mobile != 0L) binding?.etMobile?.setText(user.mobile.toString())
    }



    //A function to upload the selected user image to firebase cloud storage.
    private fun uploadUserImage(){
        showProgressDialog()

        if(mSelectedImageFileURi != null){

            //structure to store something in firebase
            val sRef : StorageReference = FirebaseStorage.getInstance().reference.child(
                "USER_IMAGE" + System.currentTimeMillis()
                    + "." + Constants.getFileExtension(this@ProfileActivity, mSelectedImageFileURi))

            sRef.putFile(mSelectedImageFileURi!!)
                .addOnSuccessListener { taskSnapshot->
                Log.e(
                    "firebase image url",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )

                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri->
                    Log.i("Downloadable Image url", uri.toString())

                    // assign the image url to the variable.
                    mProfileImageUrl = uri.toString()

                    updatedUserProfileData()
                }
            }.addOnFailureListener{ exception ->
                Toast.makeText(this@ProfileActivity,
                    exception.message, Toast.LENGTH_LONG).show()

                hideProgressDialog()
            }
        }
    }


    private fun updatedUserProfileData(){
        //this function updates the user profile details into the database
        val userHashMap = HashMap<String, Any>()

        if(mProfileImageUrl.isNotEmpty() && mProfileImageUrl != mUserDetails.image){
            userHashMap[Constants.IMAGE] = mProfileImageUrl
        }
        if(binding?.etName.toString() != mUserDetails.name){
            userHashMap[Constants.NAME] = binding?.etName?.text.toString()
        }

        if(binding?.etMobile.toString() != mUserDetails.name){
            userHashMap[Constants.MOBILE] = binding?.etMobile?.text.toString().toLong()
        }

        FireStore().updateUserProfileData(this@ProfileActivity, userHashMap)
    }


    fun profileUpdateSuccess(){
        //this will notify the user, the profile is updated successfully
        hideProgressDialog()

        Toast.makeText(this@ProfileActivity, "Profile updated successfully!", Toast.LENGTH_SHORT).show()

        setResult(Activity.RESULT_OK)
        finish()
    }


}