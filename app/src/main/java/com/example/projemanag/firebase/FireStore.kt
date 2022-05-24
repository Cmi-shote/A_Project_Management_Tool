package com.example.projemanag.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.core.location.GnssStatusCompat
import com.example.projemanag.activities.*
import com.example.projemanag.models.User
import com.example.projemanag.models.Board
import com.example.projemanag.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FireStore {
    //instance of the FireStore
    private val mFireStore = FirebaseFirestore.getInstance()

    //creating a new user
    fun registerUser(activity: SignupActivity, userInfo: User){
        //creating a new collection called user
        mFireStore.collection(Constants.Users)
            .document(getCurrentUserID())
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                // Here call a function of base activity for transferring the result to it.
                activity.userRegisteredSuccess()
            }.addOnFailureListener { e ->
            activity.hideProgressDialog()
            Log.e(activity.javaClass.simpleName,
                "Error writing document", e)
            }
    }


    //function to get the user details from Firestore Database.
    fun loadUserData(activity: Activity, readBoardList : Boolean = false){
        // Here we pass the collection name from which we wants the data.
        mFireStore.collection(Constants.Users)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.toString())

                // Here we have received the document snapshot which is converted into the User Data model object.
                val loggedInUser = document.toObject(User::class.java)!!

                // Here call a function of base activity for transferring the result to it.
                when(activity){
                    is SigninActivity -> {
                        activity.signInSuccess(loggedInUser)
                    }
                    is MainActivity ->{
                        activity.updateNavigationUserDetails(loggedInUser, readBoardList)
                    }
                    is ProfileActivity ->{
                        activity.setUserDataInUI(loggedInUser)
                    }
                }

            }.addOnFailureListener { e->

                when(activity) {
                    is SigninActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MainActivity -> {
                        activity.hideProgressDialog()
                    }
                    is ProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(activity.javaClass.simpleName,
                    "Error while getting the loggedIn user details", e)
            }
    }

    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.Users)
            .document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName,"Data updated successfully!")

                when(activity){
                    is MainActivity ->{
                        activity.tokenUpdateSuccess()
                    }
                    is ProfileActivity ->{
                        activity.profileUpdateSuccess()
                    }
                }
            }.addOnFailureListener { e ->
                when (activity) {
                    is MainActivity -> {
                        activity.hideProgressDialog()
                    }
                    is ProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating a board", e
                )
            }
    }


    fun createBoard(activity: CreateBoardActivity, board: Board){
        mFireStore.collection(Constants.BOARDS)
            .document()
            .set(board, SetOptions.merge())
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "Board created successfully")
                Toast.makeText(activity, "Board created successfully", Toast.LENGTH_SHORT).show()
                activity.boardCreatedSuccessfully()
            }
            .addOnFailureListener { exception->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating a board",exception)
            }
    }

    fun getBoardsList(activity: MainActivity){
        mFireStore.collection(Constants.BOARDS)
            .whereArrayContains(Constants.ASSIGNED_TO, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())

                val boardList: ArrayList<Board> = ArrayList()

                for(i in document.documents){
                    val board = i.toObject(Board::class.java)!!
                    board.documentId = i.id
                    boardList.add(board)
                }

                activity.populateBoardsListToUI(boardList)
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating a board", e)
            }
    }


    fun getBoardDetails(activity: TaskListActivity, documentId: String){
        mFireStore.collection(Constants.BOARDS)
            .document(documentId)
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.toString())

                val board = document.toObject(Board::class.java)!!
                board.documentId = documentId
                activity.boardDetails(board)

            }.addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating a board", e)
            }
    }

    fun addUpdateTaskList(activity: Activity, board: Board){
        val taskListHashMap = HashMap<String, Any>()
        taskListHashMap[Constants.TASK_LIST] = board.taskList

        mFireStore.collection(Constants.BOARDS)
            .document(board.documentId)
            .update(taskListHashMap)
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "TaskList updated successfully")

                if(activity is TaskListActivity){
                    activity.addUpdateTaskListSucess()
                }else if(activity is CardDetailsActivity){
                    activity.addUpdateTaskListSuccess()
                }
            }
            .addOnFailureListener { e ->
                if(activity is TaskListActivity) {
                    activity.hideProgressDialog()
                }else if(activity is CardDetailsActivity){
                    activity.hideProgressDialog()
                }
                Log.e(activity.javaClass.simpleName, "error while creating a board", e)
            }
    }


    fun getAssignedMembersListDetails(activity: Activity, assignedTo: ArrayList<String>){
        mFireStore.collection(Constants.Users)
            .whereIn(Constants.ID, assignedTo)
            .get()
            .addOnSuccessListener { document->
                Log.e(activity.javaClass.simpleName, document.documents.toString())

                val userList: ArrayList<User> = ArrayList()

                for(i in document.documents){
                    val user = i.toObject(User::class.java)!!
                    userList.add(user)
                }

                if(activity is MembersActivity)
                    activity.setupMembersList(userList)
                else if(activity is TaskListActivity)
                    activity.boardMembersDetailsList(userList)

            }
            .addOnFailureListener {e->
                if(activity is MembersActivity)
                    activity.hideProgressDialog()
                else if(activity is TaskListActivity)
                    activity.hideProgressDialog()

                Log.e(activity.javaClass.simpleName,
                    "Error while creating a board", e)
            }
    }

    fun getMemberDetails(activity: MembersActivity, email: String){
        mFireStore.collection(Constants.Users)
            .whereEqualTo(Constants.EMAIL, email)
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())

                if(document.documents.size > 0){
                    val user = document.documents[0].toObject(User::class.java)!!
                    activity.memberDetails(user)
                }else{
                    activity.hideProgressDialog()
                    activity.showErrorSnackBar("No such member found")
                }
            }
            .addOnFailureListener { e->
                Log.e(activity.javaClass.simpleName,
                    "Error while getting user details", e)
            }
    }

    fun assignMemberToBoard(activity: MembersActivity, board: Board, user: User){
        val assignedToHashMap = HashMap<String, Any>()
        assignedToHashMap[Constants.ASSIGNED_TO] = board.assignedTo

        mFireStore.collection(Constants.BOARDS)
            .document(board.documentId)
            .update(assignedToHashMap)
            .addOnSuccessListener {
                activity.memberAssignSuccess(user)
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating board")
            }
    }

    fun getCurrentUserID(): String{
        // An Instance of currentUser using FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser

        // A variable to assign the currentUserId if it is not null or else it will be blank.
        var currentUserID = ""
        if(currentUser != null){
            currentUserID = currentUser.uid
        }
        return currentUserID

    }

}