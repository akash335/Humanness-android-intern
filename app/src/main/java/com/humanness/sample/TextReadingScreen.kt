package com.humanness.sample

import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.humanness.sample.data.Task
import com.humanness.sample.data.TaskRepository
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TextReadingScreen(onBack: () -> Unit) {
    val context = LocalContext.current

    var recorder: MediaRecorder? by remember { mutableStateOf(null) }
    var mediaPlayer: MediaPlayer? by remember { mutableStateOf(null) }

    var isRecording by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }

    var audioPath by remember { mutableStateOf<String?>(null) }
    var duration by remember { mutableStateOf(0) }

    val timestamp = SimpleDateFormat("dd MMM yyyy | HH:mm", Locale.getDefault())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Read the passage aloud in your native language.")

        Spacer(modifier = Modifier.height(20.dp))

        // -------- RECORD BUTTON --------
        Button(
            onClick = {
                if (!isRecording) {
                    val file = File(context.filesDir, "rec_${System.currentTimeMillis()}.3gp")
                    audioPath = file.absolutePath

                    recorder = MediaRecorder().apply {
                        setAudioSource(MediaRecorder.AudioSource.MIC)
                        setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                        setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                        setOutputFile(audioPath)
                        prepare()
                        start()
                    }

                    isRecording = true
                } else {
                    recorder?.stop()
                    recorder?.release()
                    recorder = null
                    isRecording = false
                    duration = 5 // mock (you can calculate real duration)
                }
            }
        ) {
            Text(if (isRecording) "Stop Recording" else "Start Recording")
        }

        Spacer(modifier = Modifier.height(15.dp))

        // -------- PLAYBACK --------
        if (audioPath != null) {
            Button(
                onClick = {
                    if (!isPlaying) {
                        mediaPlayer = MediaPlayer().apply {
                            setDataSource(audioPath)
                            prepare()
                            start()
                        }
                        isPlaying = true

                        mediaPlayer?.setOnCompletionListener {
                            isPlaying = false
                        }
                    } else {
                        mediaPlayer?.pause()
                        isPlaying = false
                    }
                }
            ) {
                Text(if (isPlaying) "Pause" else "Play Recording")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // -------- SUBMIT BUTTON --------
        if (audioPath != null) {
            Button(onClick = {
                TaskRepository.addTask(
                    context,
                    Task(
                        id = System.currentTimeMillis(),
                        taskType = "text",
                        audioPath = audioPath!!,
                        durationSec = duration,
                        timestamp = timestamp.format(Date())
                    )
                )
                onBack()
            }) {
                Text("Submit")
            }
        }
    }
}
