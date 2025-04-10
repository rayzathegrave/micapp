package com.example.micapp.viewmodel

 import android.content.Context
 import android.content.pm.PackageManager
 import android.media.AudioFormat
 import android.media.AudioRecord
 import android.media.MediaRecorder
 import android.os.Handler
 import android.os.Looper
 import android.util.Log
 import androidx.core.content.ContextCompat
 import androidx.lifecycle.LiveData
 import androidx.lifecycle.MutableLiveData
 import androidx.lifecycle.ViewModel
 import com.example.micapp.database.DatabaseRepository
 import java.io.File
 import kotlin.math.log10
 import kotlin.math.sqrt

class AudioRecorderViewModel : ViewModel() {
    // MediaRecorder voor het opnemen van audio
     private var mediaRecorder: MediaRecorder? = null
    // LiveData om de opname status bij te houden
     private val _isRecording = MutableLiveData<Boolean>()
     val isRecording: LiveData<Boolean> get() = _isRecording
    // LiveData om het decibelniveau bij te houden
     private val _decibelLevel = MutableLiveData<Float>()
     val decibelLevel: LiveData<Float> get() = _decibelLevel

     private var outputFilePath: String? = null
    // AudioRecord object voor real-time decibelmeting
     private var audioRecord: AudioRecord? = null
     private val handler = Handler(Looper.getMainLooper())

     private var dbRepository: DatabaseRepository? = null

     fun initDatabase(context: Context) {
         dbRepository = DatabaseRepository(context)
     }

    // Start een audio-opname
     fun startRecording(context: Context) {
         val outputDir = context.getExternalFilesDir(null)
         outputFilePath = File(outputDir, "recorded_audio.3gp").absolutePath

         mediaRecorder = MediaRecorder(context).apply {
             setAudioSource(MediaRecorder.AudioSource.MIC) // Gebruik de microfoon als audio bron
             setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP) // Outputformaat is 3GP
             setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB) // Encoder is AMR-NB
             setOutputFile(outputFilePath) // Opslaglocatie voor opname

             try {
                 prepare()
                 start()
                 _isRecording.postValue(true) // Zet opname status op true
                 startDecibelMeter(context) // Start de decibelmeter
             } catch (e: Exception) {
                 e.printStackTrace()
             }
         }
     }
    // Stop de audio-opname
     fun stopRecording() {
         mediaRecorder?.apply {
             stop()
             reset()
             release()
         }
         mediaRecorder = null
         _isRecording.postValue(false) // Zet opname status op false
// Stop de decibelmeter
         audioRecord?.apply {
             stop()
             release()
         }
         audioRecord = null
     }
    // Start real-time decibelmeting
     private fun startDecibelMeter(context: Context) {
         val sampleRate = 44100 // Sample rate in Hz
         val bufferSize = AudioRecord.getMinBufferSize(
             sampleRate,
             AudioFormat.CHANNEL_IN_MONO,
             AudioFormat.ENCODING_PCM_16BIT
         )
        // Controleer of de RECORD_AUDIO toestemming is verleend
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
                         // Bereken het RMS-gemiddelde van het audiosignaal
                         if (read > 0) {
                             val amplitude = buffer.take(read).map { it.toDouble() * it.toDouble() }
                             val rms = sqrt(amplitude.average())
                             var decibel = 20 * log10(rms)

                             if (decibel < 0) {
                                 decibel = 0.0 // Vermijd negatieve decibelwaarden
                             }

                             _decibelLevel.postValue(decibel.toFloat()) // Update LiveData
                             Log.d("DecibelMeter", "Decibel Level: $decibel dB")
                         }

                         if (_isRecording.value == true) {
                             handler.postDelayed(this, 500) // Herhaal elke 500ms
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
    // Ruim resources op wanneer de ViewModel wordt vernietigd
     override fun onCleared() {
         super.onCleared()
         dbRepository?.close()
     }
 }