package com.example.micapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.example.micapp.viewmodel.AudioRecorderViewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: AudioRecorderViewModel by viewModels()
    private lateinit var btnStart: Button
    private lateinit var btnStop: Button
    private lateinit var txtStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnStart = findViewById(R.id.btn_start)
        btnStop = findViewById(R.id.btn_stop)
        txtStatus = findViewById(R.id.txt_status)

        requestPermissions()

        viewModel.isRecording.observe(this, Observer { isRecording ->
            if (isRecording) {
                txtStatus.text = "Recording..."
                btnStart.visibility = Button.GONE
                btnStop.visibility = Button.VISIBLE
            } else {
                txtStatus.text = "Press Start to Record"
                btnStart.visibility = Button.VISIBLE
                btnStop.visibility = Button.GONE
            }
        })

        btnStart.setOnClickListener { viewModel.startRecording(this) }
        btnStop.setOnClickListener { viewModel.stopRecording() }
    }

    private fun requestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        if (!permissions.all { checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED }) {
            ActivityCompat.requestPermissions(this, permissions, 0)
        }
    }
}