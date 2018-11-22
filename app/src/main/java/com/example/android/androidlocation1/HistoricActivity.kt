package com.example.android.androidlocation1


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.IO
import kotlinx.coroutines.experimental.launch


class HistoricActivity : AppCompatActivity() {

    private lateinit var speedViewModel: SpeedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_main)

        val actionbar = supportActionBar
        actionbar!!.title = "Historic"
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
            GlobalScope.launch(Dispatchers.IO) {
                db.speedDao().dropDataBase()
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
