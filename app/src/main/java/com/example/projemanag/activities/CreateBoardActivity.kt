package com.example.projemanag.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.projemanag.R
import com.example.projemanag.databinding.ActivityCreateBoardBinding
import com.example.projemanag.firebase.FireStore
import com.example.projemanag.models.Board
import com.example.projemanag.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class CreateBoardActivity : BaseActivity() {
    private var binding : ActivityCreateBoardBinding? = null
    private var mSelectedImageFileURi: Uri? = null

    private lateinit var mUserName: String
    private var mBoardImageUrl : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateBoardBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupActionBar()
        if(intent.hasExtra(Constants.NAME)){
            mUserName = intent.getStringExtra(Constants.NAME)!!
        }

        binding?.ivBoardImage?.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){

                Constants.showImageChooser(this@CreateBoardActivity)

            } else{
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }

        binding?.btnCreate?.setOnClickListener {
            if(mSelectedImageFileURi != null){
                uploadBoardImage()
            }else{
                showProgressDialog()
                createBoard()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == Constants.READ_STORAGE_PERMISSION_CODE){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this@CreateBoardActivity)
            }
        }else{
            Toast.makeText(this,
                "Oops you just denied permission. You can enable this at the settings",
                Toast.LENGTH_LONG).show()
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK
            && requestCode == Constants.PICK_IMAGE_REQUEST_CODE
            && data!!.data != null){
            mSelectedImageFileURi = data.data

            try{
                Glide
                    .with(this@CreateBoardActivity)
                    .load(Uri.parse(mSelectedImageFileURi.toString()))
                    .centerCrop()
                    .placeholder(R.drawable.ic_board_place_holder)
                    .into(binding!!.ivBoardImage)
            }catch (e: IOException){
                e.printStackTrace()
            }
        }
    }

    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarCreateBoardActivity)

        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_white_ios_24)
            actionBar.title = "Add New Board"
        }

        binding?.toolbarCreateBoardActivity?.setNavigationOnClickListener { onBackPressed() }
    }


    private fun uploadBoardImage(){
        showProgressDialog()
        //structure to store something in firebase
        val sRef : StorageReference = FirebaseStorage.getInstance().reference.child(
            "BOARD_IMAGE" + System.currentTimeMillis()
                    + "." + Constants.getFileExtension(this@CreateBoardActivity, mSelectedImageFileURi))

        sRef.putFile(mSelectedImageFileURi!!)
            .addOnSuccessListener { taskSnapshot->
                Log.e(
                    "Firebase image url",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )

                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri->
                        Log.i("Downloadable Image url", uri.toString())

                        // assign the image url to the variable.
                        mBoardImageUrl = uri.toString()

                        createBoard()

                    }
            }
            .addOnFailureListener{ exception ->
                Toast.makeText(this@CreateBoardActivity,
                    exception.message,
                    Toast.LENGTH_LONG).show()

                hideProgressDialog()
            }
        }

    private fun createBoard(){
        val assignedUserArrayList : ArrayList<String> = ArrayList()
        assignedUserArrayList.add(getCurrentUserId())

        val board = Board(
            binding?.etBoardName?.text.toString(),
            mBoardImageUrl,
            mUserName,
            assignedUserArrayList
        )

        FireStore().createBoard(this@CreateBoardActivity, board)
    }

    fun boardCreatedSuccessfully(){
        hideProgressDialog()

        setResult(Activity.RESULT_OK)
        finish()
    }

}