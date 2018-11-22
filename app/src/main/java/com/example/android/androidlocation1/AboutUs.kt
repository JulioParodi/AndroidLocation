package com.example.android.androidlocation1

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toolbar


class AboutUsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)

        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "About Us"

        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        //actionbar.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }



}
