package com.example.howlstagram_16

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.google.android.material.navigation.NavigationBarView
import kotlinx.android.synthetic.main.activity_main.*
import navigation.DetailViewFragment

class MainActivity : AppCompatActivity() , NavigationBarView.OnItemSelectedListener{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottom_navigation.setOnItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
       when(item.itemId){
           R.id.action_home->{
               var detailViewFragment=DetailViewFragment()
               supportFragmentManager.beginTransaction().replace(R.id.main_content,detailViewFragment).commit()
               return true
           }
           R.id.action_search->{
               var gridFragmentFragment=DetailViewFragment()
               supportFragmentManager.beginTransaction().replace(R.id.main_content,gridFragmentFragment).commit()
               return true
           }
           R.id.action_add_photo->{

               return true
           }
           R.id.action_favorite_alarm->{
               var alarmFragment=DetailViewFragment()
               supportFragmentManager.beginTransaction().replace(R.id.main_content,alarmFragment).commit()
               return true
           }
           R.id.action_account->{
               var userFragment=DetailViewFragment()
               supportFragmentManager.beginTransaction().replace(R.id.main_content,userFragment).commit()
               return true
           }
       }
        return false
    }
}