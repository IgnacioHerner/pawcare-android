package com.ignaherner.pawcare.data.local

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.ignaherner.pawcare.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val MEDICATION_CHANNEL_ID = "medication_channel"
        const val MEDICATION_CHANNEL_NAME = "Recordatorios de medicamentos"
        const val VACCINE_CHANNEL_ID = "vaccine_channel"
        const val VACCINE_CHANNEL_NAME = "Recordatorios de vacunas"
    }

    init {
        craateNotificationChannels()
    }

    private fun craateNotificationChannels() {
        val notificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        // Canal de medicamentos
        NotificationChannel(
            MEDICATION_CHANNEL_ID,
            MEDICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).also { notificationManager.createNotificationChannel(it) }

        // Canal de vacunas
        NotificationChannel(
            VACCINE_CHANNEL_ID,
            VACCINE_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).also { notificationManager.createNotificationChannel(it) }
    }

    fun showMedicationNotification(
        notificationId: Int,
        petName: String,
        medicationName: String,
        dosis: String
    ) {
        val notification = NotificationCompat.Builder(context, MEDICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("💊 Medicamento para $petName")
            .setContentText("Es hora de dar $medicationName — $dosis")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val notificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        notificationManager.notify(notificationId, notification)
    }

    fun showVaccineNotification(
        notificationId: Int,
        petName: String,
        vaccineName: String,
        fecha: String
    ) {
        val notification = NotificationCompat.Builder(context, VACCINE_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("💉 Vacuna próxima para $petName")
            .setContentText("$vaccineName programada para el $fecha")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val notificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        )as NotificationManager

        notificationManager.notify(notificationId, notification)
    }

}