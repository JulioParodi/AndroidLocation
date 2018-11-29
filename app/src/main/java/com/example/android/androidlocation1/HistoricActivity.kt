package com.example.android.androidlocation1


import android.Manifest
import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.IO
import kotlinx.coroutines.experimental.launch
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Toast
import java.io.*
import android.content.Intent
import java.text.SimpleDateFormat
import java.util.*


class HistoricActivity : AppCompatActivity() {

    private lateinit var speedViewModel: SpeedViewModel
    val REQUEST_PERMISSION = 123
    val TAG: String = "TESTE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_main)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Requirin permission!")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISSION)
        } else {
            Log.d(TAG, "You have already permission!")

        }

        val actionbar = supportActionBar
        actionbar!!.title = getString(R.string.titleHistoricActivity)
        actionbar.setDisplayHomeAsUpEnabled(true)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = SpeedListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        speedViewModel = ViewModelProviders.of(this).get(SpeedViewModel::class.java)

        speedViewModel.allSpeeds.observe(this, Observer { speeds ->
            // Update the cached copy of the words in the adapter.
            speeds?.let { adapter.setSpeeds(it) }
        })
        val db = AppDatabase.getAppDataBase(context = this)

        bttnDeleteDataBase.setOnClickListener {
            Log.d("TESTE", "To dentro")
            // Initialize a new instance of
            val builder = AlertDialog.Builder(this)
            // Set the alert dialog title
            builder.setTitle(getString(R.string.titleAlertHistoricActivity))
            // Display a message on alert dialog
            builder.setMessage(getString(R.string.messageAlertHistoricActivity))
            // Set a positive button and its click listener on alert dialog
            builder.setPositiveButton(getString(R.string.titleButtnAlertHistoricActivity)){ dialog, which ->
                // Do something when user press the positive button
                Toast.makeText(applicationContext,getString(R.string.afterPositiveAlertHistoricActivity),Toast.LENGTH_SHORT).show()
                // Change the app background color
                GlobalScope.launch(Dispatchers.IO) {
                    db.speedDao().dropDataBase()
                }
            }
            // Display a negative button on alert dialog
            builder.setNeutralButton(getString(R.string.titleBttnNegativeAlertHistoricActivity)){ dialog, which ->
                Toast.makeText(applicationContext,getString(R.string.afterNegativeAlertHistoricActivity),Toast.LENGTH_SHORT).show()
            }

//            // Display a neutral button on alert dialog
//            builder.setNeutralButton("Cancel"){_,_ ->
//                Toast.makeText(applicationContext,"You cancelled the dialog.",Toast.LENGTH_SHORT).show()
//            }
            // Finally, make the alert dialog using builder
            val dialog: AlertDialog = builder.create()
            // Display the alert dialog on app interface
            dialog.show()

        }
        bttnExportDataBase.setOnClickListener {
            writeToFile(adapter)

        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun writeToFile(adapter: SpeedListAdapter) {

        //Define ruta en almacenamiento externo y si deseas un directorio.
        val path = File(Environment.getExternalStorageDirectory(),"/androidLocation/")
        var success = true
        //Si el path no existe, trata de crear el directorio.
        if (!path.exists()) {
            success = path.mkdir()
            Log.d(TAG, "Directory " + path + " was created: "  +success)
        }

        //Si el path existe o creo directorio sin problemas ahora crea archivo.
        if (success) {
            Log.d(TAG, "Directory exist, proceed to create the file.")
            var text = "ID;currentDate;realSpeed;mySpeed;Longitude;Latitude\n"
            for (speed in adapter.speeds){
                text = "$text${speed.id};"
                text = "$text${speed.currentDate};"
                text = "$text${speed.realSpeed};"
                text = "$text${speed.mySpeed};"
                text = "$text${speed.longitude};"
                text = "$text${speed.latitude}\n"


            }
            //Write text to file!
            
            File(path, "relatorioWhatSpeedIsIt.csv").writeText(text)


            Toast.makeText(this, "A message was written to your file!", Toast.LENGTH_SHORT).show()

        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSION -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permissions were accepted!")
            }
        }
    }
}
