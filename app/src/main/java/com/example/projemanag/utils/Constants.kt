package com.example.projemanag.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.example.projemanag.models.Board

object Constants {
    const val Users: String = "users"
    const val BOARDS: String = "boards"

    const val IMAGE: String = "image"
    const val NAME: String = "name"
    const val MOBILE: String = "mobile"
    const val ASSIGNED_TO : String = "assignedTo"
    const val READ_STORAGE_PERMISSION_CODE =1
    const val PICK_IMAGE_REQUEST_CODE = 2
    const val DOCUMENT_ID : String = "documentId"
    const val TASK_LIST: String = "taskList"
    const val BOARD_DETAIL : String = "board_detail"
    const val ID: String = "id"
    const val EMAIL: String = "email"
    const val TASK_LIST_ITEM_POSITION: String = "task_list_item_position"
    const val CARD_LIST_ITEM_POSITION: String = "card_list_item_position"
    const val BOARD_MEMBERS_LIST: String = "board_members_list"
    const val UN_SELECT : String = "UnSelect"
    const val SELECT : String = "Select"
    const val PROJEMANAG_PREFERENCE = "ProjemanagPrefs"
    const val FCM_TOKEN_UPDATE = "fcmTokenUpdated"
    const val FCM_TOKEN = "fcmToken"
    const val FCM_BASE_URL: String ="https://fcm.googleapis.com/fcm/send"
    const val FCM_AUTHORIZATION : String = "autorization"
    const val FCM_KEY : String = "key"
    const val FCM_SERVER_KEY : String = "AAAAPDPkrqA:APA91bH0k2GyMLZwNEKhTUbq8qm1YSWRrKStyl6Lq3i2ZI9wemXlWhFHPh5gKzcNSBf_RfYpUurxQkKTE9kWaPIgr8VK_F_IeQBRR9lChfhPTixZvA7RLPJ7FAFZfVqNSPtQZ2Ty4UHI"
    const val FCM_KEY_TITLE : String = "title"
    const val FCM_KEY_MESSAGE : String = "message"
    const val FCM_KEY_DATA : String = "data"
    const val FCM_KEY_TO : String = "to"

    fun showImageChooser(activity: Activity){
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    fun getFileExtension(activity: Activity, uri: Uri?): String?{
        //we use singleton to get an instance of MimeTypeMap
        //this function returns the extension of the selected image
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}