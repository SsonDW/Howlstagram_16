package com.example.howlstagram_16.navigation

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.howlstagram_16.R
import com.example.howlstagram_16.navigation.model.ContentDTO
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_add_photo.*
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class AddPhotoActivity : AppCompatActivity() {
    var PICK_IMAGE_FROM_ALBUM =0
    var storage : FirebaseStorage? = null
    var photoUri : Uri? = null
    var auth : FirebaseAuth? = null
    var firestore : FirebaseFirestore? = null

//    private lateinit var binding : ActivityAddPhotoBinding
//    private lateinit var getResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        binding = ActivityAddPhotoBinding.inflate(layoutInflater)
//        setContentView(binding.root)
        setContentView(R.layout.activity_add_photo)

        //Initiate
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        //Open the album
        var photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent,PICK_IMAGE_FROM_ALBUM)

        //add image upload event
        addphoto_btn_upload.setOnClickListener {
            contentUpload()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data:Intent?){
        super.onActivityResult(requestCode, resultCode,data)
        if(requestCode == PICK_IMAGE_FROM_ALBUM){
            if(resultCode == Activity.RESULT_OK){
                photoUri = data?.data
                addphoto_image.setImageURI(photoUri)
            }
            else{
                finish()
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun contentUpload(){
        //Make filename
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName= "IMAGE_" + timestamp+"_.png"
        val storageRef=storage?.reference?.child("images")?.child(imageFileName)
        //Promise method
        storageRef?.putFile(photoUri!!)?.continueWithTask{ task: Task<UploadTask.TaskSnapshot> ->
            return@continueWithTask storageRef.downloadUrl
        }?.addOnSuccessListener { uri->
            var contentDTO = ContentDTO()

            //Insert downloadUri of image
            contentDTO.imageUrl = uri.toString()

            //Insert uid of user
            contentDTO.uid = auth?.currentUser?.uid

            //Insert userId
            contentDTO.userId = auth?.currentUser?.email

            //Insert explain of content
            contentDTO.explain = addphoto_edit_explain.text.toString()

            //Insert timestamp
            contentDTO.timestamp = System.currentTimeMillis()

            firestore?.collection("images")?.document()?.set(contentDTO)

            setResult(Activity.RESULT_OK)

            finish()
        }

        //Callback method
//        storageRef?.putFile(photoUri!!)?.addOnSuccessListener{
//            storageRef.downloadUrl.addOnSuccessListener { uri ->
//                var contentDTO = ContentDTO()
//
//                //Insert downloadUri of image
//                contentDTO.imageUrl = uri.toString()
//
//                //Insert uid of user
//                contentDTO.uid = auth?.currentUser?.uid
//
//                //Insert userId
//                contentDTO.userId = auth?.currentUser?.email
//
//                //Insert explain of content
//                contentDTO.explain = addphoto_edit_explain.text.toString()
//
//                //Insert timestamp
//                contentDTO.timestamp = System.currentTimeMillis()
//
//                firestore?.collection("images")?.document()?.set(contentDTO)
//
//                setResult(Activity.RESULT_OK)
//
//                finish()
//            }
//        }
    }
}