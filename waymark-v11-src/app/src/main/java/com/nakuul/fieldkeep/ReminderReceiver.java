package com.nakuul.fieldkeep;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.ContextCompat;

public class ReminderReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context c, Intent in) {
        if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(c, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return;

        String noteId = in.getStringExtra("note_id");
        String title = in.getStringExtra("title");
        String body = in.getStringExtra("body");
        if (title == null || title.trim().isEmpty()) title = "Waymark reminder";
        if (body == null || body.trim().isEmpty()) body = "Tap to open your note";

        String tone = AppPrefs.reminderTone(c);
        boolean vibrate = AppPrefs.reminderVibrate(c);
        String channelId = "waymark_reminders_" + tone.replaceAll("[^A-Za-z0-9]", "_") + (vibrate ? "_v" : "_q");
        NotificationManager nm = (NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm == null) return;

        Uri sound = null;
        if ("Alarm tone".equals(tone)) sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        else if (!"Silent".equals(tone)) sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationChannel ch = new NotificationChannel(channelId, "Waymark reminders", NotificationManager.IMPORTANCE_HIGH);
        ch.setDescription("Waymark Field Notes reminders");
        ch.enableVibration(vibrate);
        if (sound == null) ch.setSound(null, null);
        else {
            AudioAttributes attrs = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).build();
            ch.setSound(sound, attrs);
        }
        nm.createNotificationChannel(ch);

        Intent open = new Intent(c, NoteActivity.class);
        open.putExtra("note_id", noteId);
        open.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(c, noteId == null ? 0 : noteId.hashCode(), open, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Notification.Builder b = new Notification.Builder(c, channelId)
                .setSmallIcon(R.drawable.ic_lucide_file_text)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new Notification.BigTextStyle().bigText(body))
                .setAutoCancel(true)
                .setContentIntent(pi)
                .setCategory(Notification.CATEGORY_REMINDER);
        nm.notify(noteId == null ? 101 : (noteId.hashCode() & 0x7fffffff), b.build());
    }
}
