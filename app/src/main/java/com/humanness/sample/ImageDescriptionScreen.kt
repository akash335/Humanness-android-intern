package com.humanness.sample

import android.Manifest
import android.app.Activity
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.humanness.sample.data.Task
import com.humanness.sample.data.TaskRepository
import kotlinx.coroutines.delay
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ImageDescriptionScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val activity = context as Activity

    // Photo state
    var showCamera by remember { mutableStateOf(false) }
    var photoFile by remember { mutableStateOf<File?>(null) }

    // Audio state
    var isRecording by remember { mutableStateOf(false) }
    var duration by remember { mutableStateOf(0) }
    var hasRecording by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var recorder: MediaRecorder? by remember { mutableStateOf(null) }
    var audioTempFile by remember { mutableStateOf<File?>(null) }

    var isPlaying by remember { mutableStateOf(false) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    // Permissions for camera + mic
    LaunchedEffect(Unit) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO),
            2001
        )
    }

    // Timer
    LaunchedEffect(isRecording) {
        while (isRecording) {
            delay(1000)
            duration++
        }
    }

    // Clean up when leaving
    DisposableEffect(Unit) {
        onDispose {
            recorder?.release()
            mediaPlayer?.release()
        }
    }

    fun startRecording() {
        if (photoFile == null) {
            error = "Take a photo first."
            return
        }

        error = null
        duration = 0

        val file = File(context.filesDir, "img_desc_audio_${System.currentTimeMillis()}.m4a")
        audioTempFile = file

        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(file.absolutePath)
            try {
                prepare()
                start()
                isRecording = true
            } catch (e: Exception) {
                error = "Recording failed: ${e.message}"
                release()
            }
        }
    }

    fun stopRecording() {
        try {
            recorder?.stop()
        } catch (_: Exception) { }
        recorder?.release()
        recorder = null
        isRecording = false
        hasRecording = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            "Describe what you see in your native language.",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        Spacer(Modifier.height(16.dp))

        // PHOTO AREA
        if (showCamera) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                CameraCapture(
                    modifier = Modifier.fillMaxSize(),
                    onImageCaptured = { file ->
                        photoFile = file
                        showCamera = false
                    },
                    onError = {
                        error = "Camera error: ${it.message}"
                        showCamera = false
                    },
                    onCancel = {
                        showCamera = false
                    }
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                contentAlignment = Alignment.Center
            ) {
                if (photoFile != null) {
                    AsyncImage(
                        model = photoFile!!.toUri(),
                        contentDescription = "Captured image",
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text("No photo yet. Tap \"Take Photo\" below.")
                }
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = { showCamera = true },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(if (photoFile == null) "Take Photo" else "Retake Photo")
            }
        }

        Spacer(Modifier.height(16.dp))

        // AUDIO CONTROLS
        Button(
            onClick = {
                if (!isRecording) startRecording() else stopRecording()
            },
            enabled = !showCamera && photoFile != null,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(if (isRecording) "Stop Recording" else "Start Recording")
        }

        Spacer(Modifier.height(8.dp))
        Text("Duration: ${duration}s")

        if (hasRecording && !isRecording) {
            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    if (!isPlaying) {
                        val file = audioTempFile ?: return@Button
                        try {
                            val player = MediaPlayer()
                            mediaPlayer = player
                            player.setDataSource(file.absolutePath)
                            player.prepare()
                            player.start()
                            isPlaying = true

                            player.setOnCompletionListener {
                                isPlaying = false
                                player.release()
                                mediaPlayer = null
                            }
                        } catch (e: Exception) {
                            isPlaying = false
                            mediaPlayer?.release()
                            mediaPlayer = null
                            Toast.makeText(context, "Playback failed", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        mediaPlayer?.stop()
                        mediaPlayer?.release()
                        mediaPlayer = null
                        isPlaying = false
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(if (isPlaying) "⏹ Stop Playback" else "▶ Playback Recording")
            }
        }

        error?.let {
            Spacer(Modifier.height(8.dp))
            Text(it)
        }

        Spacer(Modifier.height(16.dp))

        // SUBMIT
        if (photoFile != null && hasRecording && !isRecording) {
            Button(
                onClick = {
                    val now = SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss",
                        Locale.getDefault()
                    ).format(Date())

                    val finalAudio = File(
                        context.filesDir,
                        "image_desc_${System.currentTimeMillis()}.m4a"
                    )
                    audioTempFile?.copyTo(finalAudio, overwrite = true)

                    val task = Task(
                        id = System.currentTimeMillis(),
                        taskType = "image_description",
                        imagePath = photoFile!!.absolutePath,
                        audioPath = finalAudio.absolutePath,
                        durationSec = duration,
                        timestamp = now
                    )

                    TaskRepository.addTask(context, task)
                    Toast.makeText(context, "Task saved", Toast.LENGTH_SHORT).show()
                    onBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Submit")
            }
        }
    }
}
