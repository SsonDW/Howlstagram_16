package com.example.howlstagram_16.navigation

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.howlstagram_16.R
import com.example.howlstagram_16.navigation.model.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_detail.view.*
import kotlinx.android.synthetic.main.item_detail.view.*

class DetailViewFragment : Fragment() {
    var uid:String?=null
    var firestore : FirebaseFirestore? = null
    override fun onCreateView(inflater: LayoutInflater, container:  ViewGroup?, savedInstanceState: Bundle?): View?{

        val view= LayoutInflater.from(activity).inflate(R.layout.fragment_detail, container, false)
        firestore = FirebaseFirestore.getInstance()
        uid=FirebaseAuth.getInstance().currentUser?.uid

        view.detailviewfragment_recyclerview.adapter = DetailViewRecyclerViewAdapter()
        view.detailviewfragment_recyclerview.layoutManager=LinearLayoutManager(activity)
        return view
    }
    @SuppressLint("NotifyDataSetChanged")
    inner class DetailViewRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        var contentDTOs : ArrayList<ContentDTO> = arrayListOf()
        var contentUidList : ArrayList<String> = arrayListOf()

        init {
            firestore?.collection("images")?.orderBy("timestamp")
                ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    contentDTOs.clear()
                    contentUidList.clear()
                    for (snapshot in querySnapshot!!.documents) {
                        var item = snapshot.toObject(ContentDTO::class.java)
                        contentDTOs.add(item!!)
                        contentUidList.add(snapshot.id)

                    }
                    notifyDataSetChanged()
                }
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(p0.context).inflate(R.layout.item_detail,p0,false)
            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
            val viewholder= (p0 as CustomViewHolder).itemView

            //userId
            viewholder.detailviewitem_profile_textview.text = contentDTOs!![p1].userId
            //Image
            Glide.with(p0.itemView.context).load(contentDTOs!![p1].imageUrl).into(viewholder.detailviewitem_imageview_content)
            //Explain of content
            viewholder.detailviewitem_explain_textview.text=contentDTOs!![p1].explain
            //likes
            viewholder.detailviewitem_favoritecounter_textview.text="Likes"+contentDTOs!![p1].favoriteCount

            //좋아요버튼 누를때
            viewholder.detailviewitem_favorite_imageview.setOnClickListener{
                favoriteEvent(p1)
            }
            if(contentDTOs!![p1].favorites.containsKey(uid)){//uid가 포함되어잇는 경우->좋아요누른경우
                viewholder.detailviewitem_favorite_imageview.setImageResource(R.drawable.ic_favorite)//좋아요 하트 색칠
            }
            else{
                viewholder.detailviewitem_favorite_imageview.setImageResource(R.drawable.ic_favorite_border)//빈 하트
            }
        }

        //버튼이 눌리면 실행


        fun favoriteEvent(position:Int){
            var tsDoc =firestore?.collection("images")?.document(contentUidList[position])
            firestore?.runTransaction { transaction->

                var contentDTO=transaction.get(tsDoc!!).toObject(ContentDTO::class.java)

                if(contentDTO!!.favorites.containsKey(uid)){
                    //좋아요가 눌려져있을때
                    //좋아요 취소
                    contentDTO.favoriteCount = contentDTO.favoriteCount - 1 //좋아요 수 감소
                    contentDTO.favorites.remove(uid); //uid 제거

                }
                else{
                    //좋아요가 안눌러져있을때
                    contentDTO.favoriteCount = contentDTO.favoriteCount + 1//좋아요 수 증가
                    contentDTO.favorites[uid!!] = true //uid 추가
                }
                transaction.set(tsDoc,contentDTO) //트랜잭션을 서버로 돌려준다
            }
        }

    }
}