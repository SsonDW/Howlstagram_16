package com.example.howlstagram_16.navigation

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.howlstagram_16.R
import com.example.howlstagram_16.navigation.model.AlarmDTO
import com.example.howlstagram_16.navigation.model.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.howl.howlstagram_f16.navigation.util.FcmPush
import kotlinx.android.synthetic.main.activity_comment.*
import kotlinx.android.synthetic.main.item_comment.view.*

class CommentActivity : AppCompatActivity() {
    var contentUid :String?=null
    var destinationUid:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)
        contentUid=intent.getStringExtra("contentUid")
        destinationUid=intent.getStringExtra("destinationUid")


        comment_recyclerview.adapter=CommentRecyclerviewAdapter()
        comment_recyclerview.layoutManager=LinearLayoutManager(this)

        comment_btn_send?.setOnClickListener{
            var comment =ContentDTO.Comment()
            comment.userId=FirebaseAuth.getInstance().currentUser?.email
            comment.uid=FirebaseAuth.getInstance().currentUser?.uid
            comment.comment=comment_edit_message.text.toString()
            comment.timestamp=System.currentTimeMillis()

            FirebaseFirestore.getInstance().collection("images").document(contentUid!!).collection("comments").document().set(comment)
            commentAlarm(destinationUid!!,comment_edit_message.text.toString())
            comment_edit_message.setText("")
        }
    }
    fun commentAlarm(destinationUid:String,message:String){
        var alarmDTO=AlarmDTO()
        alarmDTO.destinationUid=destinationUid
        alarmDTO.userId=FirebaseAuth.getInstance().currentUser?.email
        alarmDTO.uid=FirebaseAuth.getInstance().currentUser?.uid
        alarmDTO.kind=1
        alarmDTO.timestamp=System.currentTimeMillis()
        alarmDTO.message=message
        FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)

        var msg = FirebaseAuth.getInstance().currentUser?.email + " " + getString(R.string.alarm_comment) + " of " + message
        FcmPush.instance.sendMessage(destinationUid, "Instagram-immmitaion", msg)
    }

    @SuppressLint("NotifyDataSetChanged")
    inner class CommentRecyclerviewAdapter:RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        var comments:ArrayList<ContentDTO.Comment> = arrayListOf()
        init {
            FirebaseFirestore.getInstance()
                .collection("images")
                .document(contentUid!!)
                .collection("comments")
                .orderBy("timestamp")
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    comments.clear()
                    if(querySnapshot==null)return@addSnapshotListener

                    for(snapshot in querySnapshot.documents){
                        comments.add(snapshot.toObject(ContentDTO.Comment::class.java)!!)


                    }
                    notifyDataSetChanged()
                }
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view =LayoutInflater.from(parent.context).inflate(R.layout.item_comment,parent,false)
            return CustomViewHolder(view)
        }

        private inner class CustomViewHolder(view: View):RecyclerView.ViewHolder(view)

        override fun getItemCount():Int{
            return comments.size
        }
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            //영상에서는 holder 대신에 p0, position 대신에 p1 사용
            var view =holder.itemView
            view.commentviewitem_textview_comment.text=comments[position].comment
            view.commentviewitem_textview_profile.text=comments[position].userId

            FirebaseFirestore.getInstance()
                .collection("profileImages")
                .document(comments[position].uid!!)
                .get()
                .addOnCompleteListener{task->
                    if (task.isSuccessful){
                        var url=task.result!!["image"]
                        Glide.with(holder.itemView.context).load(url).apply(RequestOptions().circleCrop()).into(view.commentviewitem_imageview_profile)
                    }
                }


        }




    }
}