package com.example.projemanag.activities


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projemanag.R
import com.example.projemanag.adapters.BoardItemsAdapter
import com.example.projemanag.firebase.FireStore
import com.example.projemanag.models.User
import com.example.projemanag.models.Board
import com.example.projemanag.utils.Constants
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var nav_view : NavigationView
    private lateinit var drawer_layout : DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var auth: FirebaseAuth
    private lateinit var fab_create_board : FloatingActionButton
    private lateinit var rv_boards_list: RecyclerView
    private lateinit var tv_no_boards_available : TextView

    private lateinit var mUserName : String
    private lateinit var mSharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        nav_view = findViewById(R.id.nav_view)
        drawer_layout = findViewById(R.id.drawer_layout)
        toolbar = findViewById(R.id.toolbar_main_activity)
        fab_create_board = findViewById(R.id.fab_create_board)
        rv_boards_list = findViewById(R.id.rv_boards_list)
        tv_no_boards_available = findViewById(R.id.tv_noBoards_available)

        auth = FirebaseAuth.getInstance()

        setupActionBar()

        // Assign the NavigationView.OnNavigationItemSelectedListener to navigation view.
        nav_view.setNavigationItemSelectedListener(this)


        mSharedPreferences =
            this.getSharedPreferences(Constants.PROJEMANAG_PREFERENCE, Context.MODE_PRIVATE)

        val tokenUpdated = mSharedPreferences.getBoolean(Constants.FCM_TOKEN_UPDATE, false)

       if(tokenUpdated){
            showProgressDialog()
            FireStore().loadUserData(this@MainActivity, true)
       }else {
            FirebaseMessaging.getInstance()
                .token.addOnSuccessListener(this@MainActivity) { token ->
                updateFcmToken(token)
            }
       }



        fab_create_board.setOnClickListener{
            val intent = Intent(this@MainActivity, CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME, mUserName)
            startActivityForResult(intent, CREATE_BOARD_REQUEST_CODE)
        }

    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            // A double back press function is added in Base Activity.
            doubleBackToExit()
        }
    }


    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.nav_my_profile -> {
                startActivityForResult(Intent(this@MainActivity, ProfileActivity::class.java), MY_PROFILE_REQUEST_CODE)
            }

            R.id.nav_sign_out-> {
                // Here sign outs the user from firebase in this device.
                auth.signOut()

                mSharedPreferences.edit().clear().apply()

                // Send the user to the intro screen of the application.
                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK
            && requestCode == MY_PROFILE_REQUEST_CODE ){
            FireStore().loadUserData(this@MainActivity)
        }else if(resultCode == Activity.RESULT_OK
            && requestCode == CREATE_BOARD_REQUEST_CODE ){
            FireStore().getBoardsList(this@MainActivity) //updating boards list when a new board is created
        }
        else{
            Log.e("Cancelled", "Cancelled")
        }
    }


    private fun setupActionBar() {

        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        toolbar.setNavigationOnClickListener {
            toggleDrawer()
        }
    }


    private fun toggleDrawer() {

        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            drawer_layout.openDrawer(GravityCompat.START)
        }
    }

    fun updateNavigationUserDetails(user: User, readBoardList: Boolean){
        hideProgressDialog()
        mUserName = user.name

        // The instance of the header view of the navigation view.
        val headerView = nav_view.getHeaderView(0)

        // The instance of the user image of the navigation view.
        val navUserImage = headerView.findViewById<ImageView>(R.id.nav_user_image)

        //glide is a third party library that helps to quickly access/load images, pictures..
        Glide
            .with(this@MainActivity)
            .load(user.image)// URL of the image
            .centerCrop()// Scale type of the image.
            .placeholder(R.drawable.ic_user_place_holder)// A default place holder image
            .into(navUserImage)// the view in which the image will be loaded.

        // The instance of the user name TextView of the navigation view.
        val navUsername = headerView.findViewById<TextView>(R.id.tv_username)
        navUsername.text =  user.name

        if(readBoardList){
            showProgressDialog()
            FireStore().getBoardsList(this@MainActivity)
        }
    }

    private fun updateFcmToken(token:String){
        val userHashMap = HashMap<String, Any>()
        userHashMap[Constants.FCM_TOKEN] = token
        showProgressDialog()
        FireStore().updateUserProfileData(this@MainActivity, userHashMap)
    }


    fun populateBoardsListToUI(boardsList: ArrayList<Board>){
        hideProgressDialog()

        if(boardsList.size>0){
            rv_boards_list.visibility = View.VISIBLE
            tv_no_boards_available.visibility = View.GONE

            rv_boards_list.layoutManager = LinearLayoutManager(this@MainActivity)
            rv_boards_list.setHasFixedSize(true)

            val adapter = BoardItemsAdapter(this@MainActivity, boardsList)
            rv_boards_list.adapter = adapter

            adapter.setOnClickListener(object :
                BoardItemsAdapter.OnClickListener{
                override fun onClick(position: Int, model:Board) {
                    val intent = Intent(this@MainActivity, TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID, model.documentId)
                    startActivity(intent)
                }
            })
        }else{
            rv_boards_list.visibility = View.GONE
            tv_no_boards_available.visibility = View.VISIBLE
        }
    }

    fun tokenUpdateSuccess(){
        hideProgressDialog()
        val editor : SharedPreferences.Editor = mSharedPreferences.edit()
        editor.putBoolean(Constants.FCM_TOKEN_UPDATE, true)
        editor.apply()
        showProgressDialog()
        FireStore().loadUserData(this@MainActivity, true)

    }

    companion object{
        const val MY_PROFILE_REQUEST_CODE: Int = 11
        const val CREATE_BOARD_REQUEST_CODE: Int = 12
    }

}
