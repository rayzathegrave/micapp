package com.example.micapp.viewmodel

    import android.content.Context
    import android.media.MediaRecorder
    import android.os.Environment
    import androidx.lifecycle.LiveData
    import androidx.lifecycle.MutableLiveData
    import androidx.lifecycle.ViewModel
    import java.io.File
    import java.io.FileOutputStream

    class AudioRecorderViewModel : ViewModel() {
        private var mediaRecorder: MediaRecorder? = null
        private val _isRecording = MutableLiveData<Boolean>()
        val isRecording: LiveData<Boolean> get() = _isRecording

        private var outputFilePath: String? = null

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
        //hello
    }