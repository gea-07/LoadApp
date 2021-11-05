package com.udacity

import android.os.Bundle
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val fileName = findViewById<TextView>(R.id.label_filename_value)
        val status = findViewById<TextView>(R.id.label_download_status_value)

        val fileNameValue = intent.getStringExtra(URL_KEY).toString()
        fileName.setText(fileNameValue)

        val statusValue = intent.getStringExtra(DOWNLOAD_STATUS_KEY).toString()
        status.setText(statusValue)
        when (statusValue) {
            applicationContext.getString(R.string.download_successful) ->
                status.setTextColor(applicationContext.getColor(R.color.black))
            applicationContext.getString(R.string.download_failed) ->
                status.setTextColor(applicationContext.getColor(R.color.red))
        }
    }
}
