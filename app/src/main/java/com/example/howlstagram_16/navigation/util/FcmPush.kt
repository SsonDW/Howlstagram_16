package com.howl.howlstagram_f16.navigation.util

import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.howl.howlstagram_f16.navigation.model.PushDTO
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException

class FcmPush {

    var JSON = "application/json; charset=utf-8".toMediaTypeOrNull()
    var url = "https://fcm.googleapis.com/fcm/send"
    var serverKey = "AAAAkESG5Nw:APA91bFUSNJZmYT2SSBf5kjFXLPPXwgTSh2w1vPIb0J9nOLRsVPya9TxRCJcsyV2tCszUzZ2ghOxa26CN2QVZbdD4D0aSS4akjhPAF1uP8T41ltFUFH1-41w4jJqafwfw6md4CNNeA9R"
    var gson : Gson? = null
    var okHttpClient : OkHttpClient? = null
    companion object{
        var instance = FcmPush()
    }

    init {
        gson = Gson()
        okHttpClient = OkHttpClient()
    }
    fun sendMessage(destinationUid : String, title : String, message : String){
        FirebaseFirestore.getInstance().collection("pushtokens").document(destinationUid).get().addOnCompleteListener {
                task ->
            if(task.isSuccessful) {
                var token = task.result?.get("pushToken").toString()

                var pushDTO = PushDTO()
                pushDTO.to = token
                pushDTO.notification.title = title
                pushDTO.notification.body = message

                var body = gson?.toJson(pushDTO)!!.toRequestBody(JSON)
                var request = Request.Builder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "key=" + serverKey)
                    .url(url)
                    .post(body)
                    .build()

                okHttpClient?.newCall(request)?.enqueue(object : Callback {
                    //실패했을 때
                    override fun onFailure(call: Call, e: IOException) {

                    }

                    //성공했을 때
                    override fun onResponse(call: Call, response: Response) {
                        println(response.body.string())
                    }

                })
            }
        }
    }

}