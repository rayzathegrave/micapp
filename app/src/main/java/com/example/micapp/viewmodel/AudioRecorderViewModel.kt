package com.example.micapp.viewmodel

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Environment
import android.util.Log
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
    private var isRecordingDecibels = false

    fun startRecording(context: Context) {
        val outputDir = context.getExternalFilesDir(null)
        outputFilePath = File(outputDir, "recorded_audio.3gp").absolutePath

        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(outputFilePath)

            try {
                prepare()
                start()
                _isRecording.postValue(true)
                startDecibelMeter()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
        _isRecording.postValue(false)
        isRecordingDecibels = false
    }

    private fun startDecibelMeter() {
        val sampleRate = 44100
        val bufferSize = AudioRecord.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        val audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )

        audioRecord.startRecording()
        isRecordingDecibels = true

        Thread {
            val buffer = ShortArray(bufferSize)
            while (isRecordingDecibels) {
                val read = audioRecord.read(buffer, 0, buffer.size)
                val amplitude = buffer.take(read).map { it.toDouble() * it.toDouble() }
                val rms = sqrt(amplitude.average())
                val decibel = 20 * log10(rms)

                _decibelLevel.postValue(decibel.toFloat())
                Log.d("DecibelMeter", "Decibel Level: $decibel dB")

                Thread.sleep(500)
            }
            audioRecord.stop()
            audioRecord.release()
        }.start()
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
