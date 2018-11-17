package com.example.android.androidlocation1

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_welcome.*


var db: AppDatabase? = null
var speedDao: SpeedDao? = null


class WelcomeActivity : AppCompatActivity() {



    private val MY_PERMISSION_FINE_LOCATION = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        db = AppDatabase.getAppDataBase(context = this)
        speedDao = db?.speedDao()


        bttnLaunchMainActivity.setOnClickListener {
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }

        }


        bttnLaunchHistoricActivity.setOnClickListener {
            val intent = Intent(this, HistoricActivity::class.java)
            startActivity(intent)
        }

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

        }else{
            //request permissions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISSION_FINE_LOCATION)
            }
        }
    }



}
