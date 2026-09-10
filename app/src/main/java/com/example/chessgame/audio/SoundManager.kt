package com.example.chessgame.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.exp
import kotlin.math.sin

object SoundManager {
    private var isEnabled = true
    private val scope = CoroutineScope(Dispatchers.Default)

    fun setSoundEnabled(enabled: Boolean) {
        isEnabled = enabled
    }

    fun isSoundEnabled(): Boolean = isEnabled

    private fun playPcm(samples: ShortArray, sampleRate: Int = 22050) {
        if (!isEnabled) return
        scope.launch {
            try {
                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(samples.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.write(samples, 0, samples.size)
                track.play()
                // Clean up after playback
                kotlinx.coroutines.delay((samples.size * 1000L / sampleRate) + 50)
                track.release()
            } catch (e: Exception) {
                // Ignore audio errors gracefully
            }
        }
    }

    fun playMove() {
        val sampleRate = 22050
        val duration = 0.07 // 70ms
        val totalSamples = (sampleRate * duration).toInt()
        val buffer = ShortArray(totalSamples)

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val progress = t / duration
            val freq = 320.0 * (1.0 - progress) + 120.0 * progress
            val envelope = exp(-progress * 5.0)
            val sample = sin(2.0 * Math.PI * freq * t) * envelope
            buffer[i] = (sample * 16000).toInt().coerceIn(-32767, 32767).toShort()
        }
        playPcm(buffer, sampleRate)
    }

    fun playMove(isCapture: Boolean) {
        if (isCapture) playCapture() else playMove()
    }

    fun playCapture() {
        val sampleRate = 22050
        val duration = 0.11 // 110ms
        val totalSamples = (sampleRate * duration).toInt()
        val buffer = ShortArray(totalSamples)

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val progress = t / duration
            val freq = 440.0 * (1.0 - progress) + 80.0 * progress
            val envelope = exp(-progress * 4.0)
            val sample = sin(2.0 * Math.PI * freq * t) * envelope
            buffer[i] = (sample * 22000).toInt().coerceIn(-32767, 32767).toShort()
        }
        playPcm(buffer, sampleRate)
    }

    fun playCastle() {
        playMove()
        scope.launch {
            kotlinx.coroutines.delay(110)
            playMove()
        }
    }

    fun playCheck() {
        val sampleRate = 22050
        val duration = 0.26
        val totalSamples = (sampleRate * duration).toInt()
        val buffer = ShortArray(totalSamples)

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val progress = t / duration
            val envelope = exp(-progress * 3.0)
            val tone1 = sin(2.0 * Math.PI * 520.0 * t)
            val tone2 = sin(2.0 * Math.PI * 680.0 * t)
            val sample = ((tone1 + tone2) * 0.5) * envelope
            buffer[i] = (sample * 18000).toInt().coerceIn(-32767, 32767).toShort()
        }
        playPcm(buffer, sampleRate)
    }

    fun playVictory() {
        scope.launch {
            val notes = doubleArrayOf(523.25, 659.25, 783.99, 1046.5) // C5, E5, G5, C6
            for (freq in notes) {
                val sampleRate = 22050
                val duration = 0.22
                val totalSamples = (sampleRate * duration).toInt()
                val buffer = ShortArray(totalSamples)

                for (i in 0 until totalSamples) {
                    val t = i.toDouble() / sampleRate
                    val progress = t / duration
                    val envelope = exp(-progress * 2.5)
                    val sample = sin(2.0 * Math.PI * freq * t) * envelope
                    buffer[i] = (sample * 16000).toInt().coerceIn(-32767, 32767).toShort()
                }
                playPcm(buffer, sampleRate)
                kotlinx.coroutines.delay(100)
            }
        }
    }

    fun playDefeat() {
        scope.launch {
            val notes = doubleArrayOf(440.0, 392.0, 349.23, 293.66) // A4, G4, F4, D4
            for (freq in notes) {
                val sampleRate = 22050
                val duration = 0.25
                val totalSamples = (sampleRate * duration).toInt()
                val buffer = ShortArray(totalSamples)

                for (i in 0 until totalSamples) {
                    val t = i.toDouble() / sampleRate
                    val progress = t / duration
                    val envelope = exp(-progress * 3.0)
                    val sample = sin(2.0 * Math.PI * freq * t) * envelope
                    buffer[i] = (sample * 14000).toInt().coerceIn(-32767, 32767).toShort()
                }
                playPcm(buffer, sampleRate)
                kotlinx.coroutines.delay(120)
            }
        }
    }

    fun playDraw() {
        val sampleRate = 22050
        val duration = 0.3
        val totalSamples = (sampleRate * duration).toInt()
        val buffer = ShortArray(totalSamples)

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val progress = t / duration
            val envelope = exp(-progress * 2.8)
            val tone1 = sin(2.0 * Math.PI * 440.0 * t)
            val tone2 = sin(2.0 * Math.PI * 554.37 * t)
            val sample = ((tone1 + tone2) * 0.5) * envelope
            buffer[i] = (sample * 14000).toInt().coerceIn(-32767, 32767).toShort()
        }
        playPcm(buffer, sampleRate)
    }

    fun playClick() {
        val sampleRate = 22050
        val duration = 0.04
        val totalSamples = (sampleRate * duration).toInt()
        val buffer = ShortArray(totalSamples)

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val progress = t / duration
            val freq = 800.0 * (1.0 - progress) + 400.0 * progress
            val envelope = exp(-progress * 7.0)
            val sample = sin(2.0 * Math.PI * freq * t) * envelope
            buffer[i] = (sample * 12000).toInt().coerceIn(-32767, 32767).toShort()
        }
        playPcm(buffer, sampleRate)
    }
}
