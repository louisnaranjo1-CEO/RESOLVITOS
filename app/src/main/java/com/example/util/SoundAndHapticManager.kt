package com.example.util

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

enum class ResolvitosSoundEvent(val code: String, val label: String) {
    ALERT_LOOP("alert_loop", "Nuevo pedido / Oferta de viaje"),
    CHAT_POP("chat_pop", "Nuevo mensaje en chat"),
    SOS_SIREN("sos_siren", "Alerta SOS Emergencia"),
    TICKET_BELL("ticket_bell", "Misión desbloqueada"),
    PUSH_CHIME("push_chime", "Notificación general"),
    CYCLE_ACCEPT("cycle_accept", "Pedido aceptado"),
    CYCLE_READY("cycle_ready", "Pedido listo para despacho"),
    CYCLE_DRIVER("cycle_driver", "Moto Taxi asignado"),
    CYCLE_TRANSIT("cycle_transit", "En camino al destino"),
    CYCLE_ARRIVED("cycle_arrived", "Conductor en puerta"),
    DONE_SUCCESS("done_success", "Entrega / Viaje completado"),
    DONE_CASH("done_cash", "Pago verificado / Caja")
}

object SoundAndHapticManager {
    var soundEnabled: Boolean = true
    var hapticEnabled: Boolean = true

    fun trigger(context: Context, event: ResolvitosSoundEvent) {
        if (hapticEnabled) {
            triggerHaptic(context, event)
        }
        if (soundEnabled) {
            playTone(event)
        }
    }

    private fun triggerHaptic(context: Context, event: ResolvitosSoundEvent) {
        try {
            val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                manager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
            val durationMs = when (event) {
                ResolvitosSoundEvent.SOS_SIREN -> 280L
                ResolvitosSoundEvent.ALERT_LOOP, ResolvitosSoundEvent.DONE_SUCCESS -> 140L
                else -> 55L
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (_: Exception) {
            // Ignore in headless/test environments
        }
    }

    private fun playTone(event: ResolvitosSoundEvent) {
        try {
            val toneGen = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 80)
            val (toneType, durationMs) = when (event) {
                ResolvitosSoundEvent.ALERT_LOOP -> ToneGenerator.TONE_PROP_PROMPT to 220
                ResolvitosSoundEvent.CHAT_POP -> ToneGenerator.TONE_PROP_BEEP to 90
                ResolvitosSoundEvent.SOS_SIREN -> ToneGenerator.TONE_CDMA_EMERGENCY_RINGBACK to 350
                ResolvitosSoundEvent.TICKET_BELL -> ToneGenerator.TONE_PROP_ACK to 200
                ResolvitosSoundEvent.PUSH_CHIME -> ToneGenerator.TONE_PROP_BEEP2 to 150
                ResolvitosSoundEvent.CYCLE_ACCEPT -> ToneGenerator.TONE_PROP_ACK to 140
                ResolvitosSoundEvent.CYCLE_READY -> ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD to 180
                ResolvitosSoundEvent.CYCLE_DRIVER -> ToneGenerator.TONE_PROP_BEEP2 to 160
                ResolvitosSoundEvent.CYCLE_TRANSIT -> ToneGenerator.TONE_PROP_BEEP to 120
                ResolvitosSoundEvent.CYCLE_ARRIVED -> ToneGenerator.TONE_CDMA_PIP to 220
                ResolvitosSoundEvent.DONE_SUCCESS -> ToneGenerator.TONE_PROP_ACK to 240
                ResolvitosSoundEvent.DONE_CASH -> ToneGenerator.TONE_PROP_PROMPT to 180
            }
            toneGen.startTone(toneType, durationMs)
        } catch (_: Exception) {
            // Ignore audio hardware limitations in headless environments
        }
    }
}
