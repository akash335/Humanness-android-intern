package com.humanness.sample

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Paint
import android.media.MediaRecorder
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.delay
import kotlin.math.*

@Composable
fun NoiseTestScreen(
    onBack: () -> Unit = {},
    onPassed: () -> Unit = {}
) {
    val context = LocalContext.current

    // Use a fixed blue like the design screenshot
    val primaryBlue = Color(0xFF1976D2)
    val grayText = Color(0xFF808999)

    // ------- permission -------
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
    }

    // ------- recorder + state -------
    var isTesting by remember { mutableStateOf(false) }
    var decibels by remember { mutableStateOf(0f) }

    // show result text after a run
    var testFinished by remember { mutableStateOf(false) }
    var isPass by remember { mutableStateOf(false) }

    val recorderState = remember { mutableStateOf<MediaRecorder?>(null) }

    fun stopTest() {
        isTesting = false
        recorderState.value?.run {
            try { stop() } catch (_: Exception) {}
            try { reset() } catch (_: Exception) {}
            release()
        }
        recorderState.value = null
    }

    fun startTest() {
        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            return
        }
        if (isTesting) return

        val outputFile = context.cacheDir.resolve("noise_test.3gp").absolutePath
        val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            MediaRecorder()
        }

        try {
            recorder.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(outputFile)
                prepare()
                start()
            }
            recorderState.value = recorder
            isTesting = true
            decibels = 0f
            testFinished = false
        } catch (_: Exception) {
            try {
                recorder.reset()
                recorder.release()
            } catch (_: Exception) {}
            recorderState.value = null
            isTesting = false
        }
    }

    // ------- sampling loop (5-second test) -------
    LaunchedEffect(isTesting) {
        if (!isTesting) return@LaunchedEffect

        var smoothDb = 0f
        var elapsed = 0L
        val durationMs = 5000L

        while (isTesting && elapsed < durationMs) {
            delay(250)
            elapsed += 250

            val rec = recorderState.value ?: break
            try {
                val amp = rec.maxAmplitude // 0..32767
                if (amp > 0) {
                    // Map amplitude → 0–60 dB instead of log10 (too noisy on real devices)
                    val normalized = (amp / 32767f).coerceIn(0f, 1f)
                    val rawDb = (normalized * 60f)

                    smoothDb = if (smoothDb == 0f) {
                        rawDb
                    } else {
                        smoothDb * 0.7f + rawDb * 0.3f
                    }
                    decibels = smoothDb.coerceIn(0f, 60f)
                }
            } catch (_: Exception) {
                break
            }
        }

        stopTest()

        // decide pass / fail according to PDF: < 40 = good :contentReference[oaicite:3]{index=3}
        testFinished = true
        val passed = decibels < 40f
        isPass = passed
        if (passed) {
            onPassed()
        }
    }

    // ------- UI -------
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .padding(horizontal = 24.dp)
            .padding(top = 32.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Test Ambient Noise Level",
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colors.onBackground,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Before you can start the call we will have to check your ambient noise level.",
            style = MaterialTheme.typography.body2,
            color = grayText,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(32.dp))

        NoiseGauge(
            decibels = decibels.toInt(),
            primaryBlue = primaryBlue,
            modifier = Modifier.fillMaxWidth()
        )

        // Status text (only after one full test)
        if (testFinished) {
            Spacer(Modifier.height(16.dp))
            Text(
                text = if (isPass) "Good to proceed" else "Please move to a quieter place",
                style = MaterialTheme.typography.body2,
                fontWeight = FontWeight.Medium,
                color = if (isPass) Color(0xFF388E3C) else Color(0xFFE53935),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = { startTest() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = primaryBlue,
                contentColor = Color.White
            ),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
        ) {
            Text(
                text = if (isTesting) "Testing..." else "Start Test",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun NoiseGauge(
    decibels: Int,
    primaryBlue: Color,
    modifier: Modifier = Modifier
) {
    val redZoneColor = Color(0xFFE53935)
    val trackColor = Color(0xFFE2E5EA)
    val labelColor = Color(0xFF808999)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
        ) {
            val strokeWidth = 28.dp.toPx()
            val sizeMin = min(size.width, size.height)
            val diameter = sizeMin - strokeWidth

            val topLeft = Offset(
                (size.width - diameter) / 2f,
                size.height - diameter
            )
            val arcRect = Rect(topLeft, Size(diameter, diameter))

            val startAngle = 180f + 20f
            val sweepAngle = 180f - 40f

            // grey background
            drawArc(
                color = trackColor,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // blue safe arc (75%)
            val safeSweep = sweepAngle * 0.75f
            drawArc(
                color = primaryBlue,
                startAngle = startAngle,
                sweepAngle = safeSweep,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // red loud arc
            drawArc(
                color = redZoneColor,
                startAngle = startAngle + safeSweep,
                sweepAngle = sweepAngle - safeSweep,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // labels 10,20,30,40,50
            val canvas = drawContext.canvas.nativeCanvas
            val textPaint = Paint().apply {
                color = android.graphics.Color.parseColor("#808999")
                textSize = 32f
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }

            val radius = arcRect.width / 2f
            val center = arcRect.center

            for (i in 1..5) {
                val label = (i * 10).toString()
                val angleDeg = startAngle + sweepAngle * (i / 5f)
                val angleRad = Math.toRadians(angleDeg.toDouble())
                val rLabel = radius - strokeWidth * 1.1f

                val x = center.x + rLabel * cos(angleRad)
                val y = center.y + rLabel * sin(angleRad)

                canvas.drawText(label, x.toFloat(), y.toFloat(), textPaint)
            }

            // needle
            val clamped = decibels.coerceIn(0, 60)
            val valueFraction = clamped / 60f
            val needleAngleDeg = startAngle + sweepAngle * valueFraction
            val needleAngleRad = Math.toRadians(needleAngleDeg.toDouble())
            val needleRadius = radius - strokeWidth * 0.5f

            val endX = center.x + needleRadius * cos(needleAngleRad)
            val endY = center.y + needleRadius * sin(needleAngleRad)

            drawLine(
                color = Color(0xFFDEE2E8),
                start = center,
                end = Offset(endX.toFloat(), endY.toFloat()),
                strokeWidth = 10f,
                cap = StrokeCap.Round
            )
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = "${decibels.coerceAtLeast(0)}",
            style = MaterialTheme.typography.h4,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colors.onBackground
        )
        Text(
            text = "db",
            style = MaterialTheme.typography.body2,
            color = labelColor
        )
    }
}
