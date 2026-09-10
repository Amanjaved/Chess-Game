package com.example.chessgame.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import com.example.chessgame.engine.PieceColor
import com.example.chessgame.engine.PieceType
import java.util.Locale

/**
 * 100% Offline native voice commentary engine using Android TextToSpeech.
 * Provides clear, professional chess announcer commentary for checks,
 * checkmates, stalemates, castles, and promotions.
 */
object VoiceAnnouncer : TextToSpeech.OnInitListener {
    private const val TAG = "VoiceAnnouncer"
    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private var isVoiceEnabled = true

    fun init(context: Context) {
        if (tts == null) {
            tts = TextToSpeech(context.applicationContext, this)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.setLanguage(Locale.getDefault())
            }
            // Dignified, composed grandmaster tournament speech cadence
            tts?.setPitch(0.95f)
            tts?.setSpeechRate(0.92f)
            isInitialized = true
            Log.d(TAG, "TextToSpeech initialized successfully.")
        } else {
            Log.e(TAG, "TextToSpeech initialization failed with status $status")
            isInitialized = false
        }
    }

    fun setVoiceEnabled(enabled: Boolean) {
        isVoiceEnabled = enabled
        if (!enabled) {
            stop()
        }
    }

    fun isVoiceEnabled(): Boolean = isVoiceEnabled

    fun speak(text: String, queueMode: Int = TextToSpeech.QUEUE_FLUSH) {
        if (!isVoiceEnabled || !isInitialized) return
        try {
            tts?.speak(text, queueMode, null, "CHESS_TTS_${System.currentTimeMillis()}")
        } catch (e: Exception) {
            Log.e(TAG, "Error speaking text: $text", e)
        }
    }

    fun stop() {
        try {
            tts?.stop()
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping TTS", e)
        }
    }

    fun announceCheck() {
        speak("Check!")
    }

    fun announceCheckmate(winner: PieceColor?, humanColor: PieceColor, isAIMode: Boolean) {
        if (winner == null) {
            speak("Checkmate! Game over.")
            return
        }
        if (isAIMode) {
            if (winner == humanColor) {
                speak("Checkmate! Victory is yours, well played!")
            } else {
                speak("Checkmate! The AI wins the match.")
            }
        } else {
            val winnerName = if (winner == PieceColor.WHITE) "White" else "Black"
            speak("Checkmate! $winnerName wins the game!")
        }
    }

    fun announceStalemate() {
        speak("Stalemate! The game is drawn.")
    }

    fun announceDraw(reason: String) {
        when (reason.lowercase()) {
            "fifty_move_rule" -> speak("Draw declared by the fifty move rule.")
            "threefold_repetition" -> speak("Draw by threefold repetition.")
            "insufficient_material" -> speak("Draw due to insufficient mating material.")
            else -> speak("The match has ended in a draw.")
        }
    }

    fun announceCastle(isKingside: Boolean) {
        if (isKingside) {
            speak("Kingside castle.")
        } else {
            speak("Queenside castle.")
        }
    }

    fun announcePromotion(type: PieceType) {
        val name = when (type) {
            PieceType.QUEEN -> "Queen"
            PieceType.ROOK -> "Rook"
            PieceType.BISHOP -> "Bishop"
            PieceType.KNIGHT -> "Knight"
            else -> "piece"
        }
        speak("Pawn promoted to $name!")
    }

    fun announceGameStart(isAIMode: Boolean) {
        if (isAIMode) {
            speak("Match commenced. Make your move.")
        } else {
            speak("Match commenced. White to move.")
        }
    }

    fun announceResignation(resignedColor: PieceColor) {
        val name = if (resignedColor == PieceColor.WHITE) "White" else "Black"
        speak("$name has resigned. Victory declared.")
    }

    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
            tts = null
            isInitialized = false
        } catch (e: Exception) {
            Log.e(TAG, "Error shutting down TTS", e)
        }
    }
}
