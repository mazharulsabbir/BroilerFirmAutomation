package tarmsbd.iot.automation.broilerfirm.utils

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import tarmsbd.iot.automation.broilerfirm.R
import tarmsbd.iot.automation.broilerfirm.ui.notification.view.NotificationActivity


private const val TAG = "FirebaseNotification"

class FirebaseNotificationService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            val title = it.title ?: "Notification" //get title
            val message = it.body ?: "Notification Body" //get message

            notify(title, message)
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }
    }

    private fun notify(title: String, messageBody: String) {
        val intent = Intent(this, NotificationActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(
            this,
            resources.getString(R.string.default_notification_channel_id)
        )
            .setAutoCancel(true)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(title)
            .setStyle(NotificationCompat.BigTextStyle().bigText(messageBody))
            .setContentText(messageBody)
            .setSound(defaultSoundUri)
            .setDefaults(Notification.DEFAULT_VIBRATE or Notification.DEFAULT_SOUND or Notification.FLAG_SHOW_LIGHTS)
            .setContentIntent(pendingIntent)

        val notificationManager = NotificationManagerCompat.from(this)

        val id = (Math.random() * 10).toInt() + 1
        notificationManager.notify(id, notificationBuilder.build())
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        sendRegistrationToServer(p0)
    }

    private fun sendRegistrationToServer(token: String) {
        val user = FirebaseAuth.getInstance().currentUser
        val ref: DatabaseReference = FirebaseDatabase.getInstance().reference

        if (user != null) {
            ref.child("user/${user.uid}/fcm_token").setValue(token)
        }
    }
}