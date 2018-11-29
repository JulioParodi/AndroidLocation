package com.example.android.androidlocation1

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.facebook.stetho.Stetho


import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.IO
import kotlinx.coroutines.experimental.launch
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {


    private var fusedLocationProviderClient : FusedLocationProviderClient?  = null
    private val MY_PERMISSION_FINE_LOCATION = 101
    private var locationRequest : LocationRequest? = null
    private var updatesOn = true
    private var locationCallback : LocationCallback? = null

//    private var place = ArrayList<Location>()
//    private var time = ArrayList<Long>()

    var realSpeed : String = "0"
    var longitude : String = "0"
    var latitude : String = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val actionbar = supportActionBar
        actionbar!!.title = "What Speed Is It"

        locationRequest = LocationRequest()
        locationRequest!!.interval = 1000 // use 10 ou 15 in real app  | 7500 before
        locationRequest!!.fastestInterval = 500    // | 5000 before
        locationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY


//        tb_LocationOnOff.setOnClickListener {
//            if(tb_LocationOnOff.isChecked){
//                Toast.makeText(this, "ON", Toast.LENGTH_SHORT)
//                longitudeTag.visibility = View.VISIBLE
//                tvLongitude.visibility = View.VISIBLE
//                latitudeTag.visibility = View.VISIBLE
//                tvLatitude.visibility = View.VISIBLE
//                speedTag.visibility = View.VISIBLE
//                tvSpeed.visibility = View.VISIBLE
//                mySpeedTag.visibility = View.VISIBLE
//                mySpeed.visibility = View.VISIBLE
//                dWalkedTag.visibility = View.VISIBLE
//                distanceWalk.visibility = View.VISIBLE
//                tRelativeTag.visibility = View.VISIBLE
//                timeRelative.visibility = View.VISIBLE
//
//            }else{
//                Toast.makeText(this, "OFF", Toast.LENGTH_SHORT)
//                longitudeTag.visibility = View.INVISIBLE
//                tvLongitude.visibility = View.INVISIBLE
//                latitudeTag.visibility = View.INVISIBLE
//                tvLatitude.visibility = View.INVISIBLE
//                speedTag.visibility = View.INVISIBLE
//                tvSpeed.visibility = View.INVISIBLE
//                mySpeedTag.visibility = View.INVISIBLE
//                mySpeed.visibility = View.INVISIBLE
//                dWalkedTag.visibility = View.INVISIBLE
//                distanceWalk.visibility = View.INVISIBLE
//                tRelativeTag.visibility = View.INVISIBLE
//                timeRelative.visibility = View.INVISIBLE
//
//            }
//        }


        sendBttn.setOnClickListener {

            if (editNum.text.isBlank()) {

            } else {
                val db = AppDatabase.getAppDataBase(context = this)
                val sdf = SimpleDateFormat("dd/M/yyyy-hh:mm:ss")
                val currentDate = sdf.format(Date()).toString()
                val speed1 = Speed(realSpeed = "${this.realSpeed} km/h", mySpeed = "${editNum.text} km/h", currentDate = currentDate, longitude = this.longitude , latitude = this.latitude )
                GlobalScope.launch(Dispatchers.IO) { db.speedDao().insertSpeed(speed1) }
            }

            editNum.text = null
        }


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient!!.lastLocation.addOnSuccessListener { location ->
                if(location != null){
                    //update UI
//                    tvLongitude.text = location.latitude.toString()
//                    tvLatitude.text = location.longitude.toString()
                    longitude = location.longitude.toString()
                    latitude = location.latitude.toString()

                    if (location.hasSpeed()){
                        tvSpeed.text = location.speed.toString()
                        this.realSpeed = location.speed.toString()
                    }else{
                        tvSpeed.text = "0"
                    }
                }
            }
        }else{
            //request permissions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISSION_FINE_LOCATION)
            }
        }

        locationCallback = object : LocationCallback(){
            override fun onLocationResult(p0: LocationResult?) {
                super.onLocationResult(p0)
                for (location in p0!!.locations){
                    if(location != null){
//                        tvLongitude.text = location.latitude.toString()
//                        tvLatitude.text = location.longitude.toString()
                        longitude = location.longitude.toString()
                        latitude = location.latitude.toString()
                        if (location.hasSpeed()){
                            tvSpeed.text = (location.speed * 3.6).toInt().toString() + " km/h"
                            realSpeed = (location.speed * 3.6).toInt().toString()

                        }else{
                            tvSpeed.text = "0" + " km/h"
                        }
                    }
                }
            }
//
        }
        startLocationUpdates()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here.
        val id = item.getItemId()

        if (id == R.id.action_one) {
            val intent = Intent(this, HistoricActivity::class.java)
            startActivity(intent)
        }
        if (id == R.id.action_two) {
            val intent = Intent(this, HelpActivity::class.java)
            startActivity(intent)
        }
        if (id == R.id.action_three) {
                if (speedTag.visibility == View.VISIBLE){
                    speedTag.visibility = View.INVISIBLE
                    tvSpeed.visibility = View.INVISIBLE


                } else {
                    tvSpeed.visibility = View.VISIBLE
                    speedTag.visibility = View.VISIBLE
                }
//
        }


        return super.onOptionsItemSelected(item)

    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            MY_PERMISSION_FINE_LOCATION ->
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //permission granted, do nothing
                }else{
                    Toast.makeText(applicationContext, "Ative a Locatizacao, eh necessario", Toast.LENGTH_SHORT)
                    finish()
                }
        }
    }

    override fun onResume() {
        super.onResume()
        if (updatesOn) startLocationUpdates()
    }

    private fun startLocationUpdates() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient!!.requestLocationUpdates(locationRequest, locationCallback, null)
        }else{
            //request permissions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISSION_FINE_LOCATION)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        fusedLocationProviderClient!!.removeLocationUpdates(locationCallback)
    }

}


