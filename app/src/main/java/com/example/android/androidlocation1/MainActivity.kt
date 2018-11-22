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


import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.IO
import kotlinx.coroutines.experimental.launch
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {


    //private var db: AppDatabase? = null
    //private var speedDao: SpeedDao? = null


    private var fusedLocationProviderClient : FusedLocationProviderClient?  = null
    private val MY_PERMISSION_FINE_LOCATION = 101
    private var locationRequest : LocationRequest? = null
    private var updatesOn = true
    private var locationCallback : LocationCallback? = null

    private var place = ArrayList<Location>()
    private var time = ArrayList<Long>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "Android Location"

        //set back button
        //actionbar.setDisplayHomeAsUpEnabled(true)


        //db = AppDatabase.getAppDataBase(context = this)
        //speedDao = db?.speedDao()

        locationRequest = LocationRequest()
        locationRequest!!.interval = 1000 // use 10 ou 15 in real app  | 7500 before
        locationRequest!!.fastestInterval = 500    // | 5000 before
        locationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY


        tb_LocationOnOff.setOnClickListener {
            if(tb_LocationOnOff.isChecked){
                Toast.makeText(this, "ON", Toast.LENGTH_SHORT)
                longitudeTag.visibility = View.VISIBLE
                tvLongitude.visibility = View.VISIBLE
                latitudeTag.visibility = View.VISIBLE
                tvLatitude.visibility = View.VISIBLE
                speedTag.visibility = View.VISIBLE
                tvSpeed.visibility = View.VISIBLE
                mySpeedTag.visibility = View.VISIBLE
                mySpeed.visibility = View.VISIBLE
                dWalkedTag.visibility = View.VISIBLE
                distanceWalk.visibility = View.VISIBLE
                tRelativeTag.visibility = View.VISIBLE
                timeRelative.visibility = View.VISIBLE
                //updatesOn = true
                //startLocationUpdates()
            }else{
                Toast.makeText(this, "OFF", Toast.LENGTH_SHORT)
                longitudeTag.visibility = View.INVISIBLE
                tvLongitude.visibility = View.INVISIBLE
                latitudeTag.visibility = View.INVISIBLE
                tvLatitude.visibility = View.INVISIBLE
                speedTag.visibility = View.INVISIBLE
                tvSpeed.visibility = View.INVISIBLE
                mySpeedTag.visibility = View.INVISIBLE
                mySpeed.visibility = View.INVISIBLE
                dWalkedTag.visibility = View.INVISIBLE
                distanceWalk.visibility = View.INVISIBLE
                tRelativeTag.visibility = View.INVISIBLE
                timeRelative.visibility = View.INVISIBLE
                //updatesOn = false
                //stopLocationUpdates()
            }
        }


        sendBttn.setOnClickListener {

            if (editNum.text.isBlank()) {
                //setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                val db = AppDatabase.getAppDataBase(context = this)

                val sdf = SimpleDateFormat("dd/M/yyyy \n hh:mm:ss")
                val currentDate = sdf.format(Date()).toString()
                val location : Location




                val speed1 = Speed(realSpeed = "${mySpeed.text}", mySpeed = "${editNum.text} km/h", currentDate = currentDate, longitude = tvLongitude.text.toString() , latitude = tvLatitude.text.toString() )
                GlobalScope.launch(Dispatchers.IO) { db.speedDao().insertSpeed(speed1) }
            }
           // finish()
            editNum.text = null
        }


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient!!.lastLocation.addOnSuccessListener { location ->
                if(location != null){
                    //update UI
                    tvLongitude.text = location.latitude.toString()
                    tvLatitude.text = location.longitude.toString()
                    if (location.hasSpeed()){
                        tvSpeed.text = location.speed.toString()
                    }else{
                        tvSpeed.text = "No Speed Available wtff"
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
                var seconds : Long
                var speed : Double
                val newTime = System.currentTimeMillis() / 1000
                //var located : Location
                for (location in p0!!.locations){
                    //update UI
                    //located = location
                    if(location != null){
                        //update UI
                        if(place.size == 0){
                            place.add(location)
                            time.add(System.currentTimeMillis() / 1000)
                        }
                        if (place.size > 0 && time[time.size-1] != newTime) { // era assim : (place.size > 0 && place[place.size-1] != location)
                            place.add(location)
                            time.add(System.currentTimeMillis() / 1000)
                        }
                        if(place.size > 2){
                            //Toast.makeText(applicationContext, "Getting Speed", Toast.LENGTH_SHORT).show()
                            seconds = time[time.size-1] - time[time.size-2]
                            timeRelative.text = seconds.toString() + "segundos"
                            speed = getSpeed(place[place.size-1], place[place.size-2], seconds)
                            mySpeed.text = speed.toInt().toString() + " km/h"
                            //Toast.makeText(applicationContext, "Getting Speed: "+speed.toString(), Toast.LENGTH_SHORT).show()
                        }

                        tvLongitude.text = location.latitude.toString()
                        tvLatitude.text = location.longitude.toString()
                        if (location.hasSpeed()){
                            tvSpeed.text = location.speed.toString()
                        }else{
                            tvSpeed.text = "No Speed Available"
                        }
                    }
                }
            }
            private fun getSpeed(location1 : Location, location2 : Location, seconds : Long) : Double{
                var speed: Double
                var distance = findDistanceTwoLocations(location1, location2)
                //distanceWalk.text = distance.toString() + "metros"
                //posInicial.text = location2.latitude.toString() + " / " + location2.longitude.toString()
                //posFinal.text = location1.latitude.toString() + " / " + location1.longitude.toString()
                speed = distance / seconds
                //speed = round(speed)
                if (speed < 1) {
                    speed = 0.0
                }
                speed = speed * 3.6

                return speed
            }
            private fun findDistanceTwoLocations(location1: Location, location2: Location): Double {


                val earthRadius = 6371
                var dLat = (location1.latitude - location2.latitude) * (Math.PI / 180.0)
                var dLon = (location1.longitude - location2.longitude) * (Math.PI / 180.0)
                var a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                        Math.cos(Math.toRadians(location1.latitude)) * Math.cos(Math.toRadians(location1.latitude)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2)
                var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
                var dist = (earthRadius * c)
                dist = dist * 1000
                return dist

            }
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
            val intent = Intent(this, AboutUsActivity::class.java)
            startActivity(intent)
        }


        return super.onOptionsItemSelected(item)

    }

//    override fun onSupportNavigateUp(): Boolean {
//        onBackPressed()
//        return true
//    }
//
//    companion object {
//        const val EXTRA_REPLY = "com.example.android.wordlistsql.REPLY"
//    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            MY_PERMISSION_FINE_LOCATION ->
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //permission granted, do nothing
                }else{
                    Toast.makeText(applicationContext, "Ative a Locatizacao,, eh necessario", Toast.LENGTH_SHORT)
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


