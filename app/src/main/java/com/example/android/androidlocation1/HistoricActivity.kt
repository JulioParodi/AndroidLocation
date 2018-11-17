package com.example.android.androidlocation1


import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_historic.*
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.launch


class HistoricActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historic)


        var text = "historic111"

        db = AppDatabase.getAppDataBase(this)
        speedDao = db?.speedDao()

        GlobalScope.launch {
            text = speedDao?.getSpeeds()?.first()?.mySpeed.toString()
            text = "thread"
        }

        textView.text = text

    }



}
