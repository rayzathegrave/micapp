package com.example.micapp.viewmodel

import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.File
import java.io.FileOutputStream
import kotlin.math.log10
import kotlin.math.sqrt

class AudioRecorderViewModel : ViewModel() {
    private var mediaRecorder: MediaRecorder? = null
    private val _isRecording = MutableLiveData<Boolean>()
    val isRecording: LiveData<Boolean> get() = _isRecording

    private val _decibelLevel = MutableLiveData<Float>()
    val decibelLevel: LiveData<Float> get() = _decibelLevel

    private var outputFilePath: String? = null
    private var audioRecord: AudioRecord? = null
    private val handler = Handler(Looper.getMainLooper())

    fun startRecording(context: Context) {
        val outputDir = context.getExternalFilesDir(null)
        outputFilePath = File(outputDir, "recorded_audio.3gp").absolutePath

        mediaRecorder = MediaRecorder(context).apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(outputFilePath)

            try {
                prepare()
                start()
                _isRecording.postValue(true)
                startDecibelMeter(context)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            reset()
            release()
        }
        mediaRecorder = null
        _isRecording.postValue(false)

        audioRecord?.apply {
            stop()
            release()
        }
        audioRecord = null
    }

    private fun startDecibelMeter(context: Context) {
        val sampleRate = 44100
        val bufferSize = AudioRecord.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize
            )

            try {
                audioRecord?.startRecording()

                handler.post(object : Runnable {
                    override fun run() {
                        val buffer = ShortArray(bufferSize)
                        val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0

                        if (read > 0) {
                            val amplitude = buffer.take(read).map { it.toDouble() * it.toDouble() }
                            val rms = sqrt(amplitude.average())
                            var decibel = 20 * log10(rms)

                            if (decibel < 0) {
                                decibel = 0.0
                            }

                            _decibelLevel.postValue(decibel.toFloat())
                            Log.d("DecibelMeter", "Decibel Level: $decibel dB")
                        }

                        if (_isRecording.value == true) {
                            handler.postDelayed(this, 500)
                        }
                    }
                })
            } catch (e: SecurityException) {
                Log.e("DecibelMeter", "Permission denied for recording audio", e)
            }
        } else {
            Log.e("DecibelMeter", "RECORD_AUDIO permission not granted")
        }
    }

    fun updateRecording(context: Context, newFileName: String) {
        stopRecording()
        outputFilePath = File(context.getExternalFilesDir(null), newFileName).absolutePath
        startRecording(context)
    }

    fun writeAudioFileExternalStorage(context: Context, audioData: ByteArray, filename: String) {
        val state = Environment.getExternalStorageState()
        if (Environment.MEDIA_MOUNTED != state) {
            return
        }

        val file = File(context.getExternalFilesDir(null), filename)

        var outputStream: FileOutputStream? = null
        try {
            file.createNewFile()
            outputStream = FileOutputStream(file, true)
            outputStream.write(audioData)
            outputStream.flush()
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            outputStream?.close()
        }
    }
}