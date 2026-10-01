package com.nakuul.fieldkeep;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import org.json.JSONArray;
import org.json.JSONObject;

public final class ReminderUtil {
    private ReminderUtil() {}

    private static int requestCode(String id) {
        return id == null ? 0 : (id.hashCode() & 0x7fffffff);
    }

    private static PendingIntent pending(Context c, JSONObject note) {
        Intent i = new Intent(c, ReminderReceiver.class);
        i.setAction("com.nakuul.waymark.REMINDER." + note.optString("id"));
        i.putExtra("note_id", note.optString("id"));
        i.putExtra("title", note.optString("title", "Waymark reminder"));
        i.putExtra("body", note.optString("body", ""));
        return PendingIntent.getBroadcast(c, requestCode(note.optString("id")), i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    public static void schedule(Context c, JSONObject note) {
        if (note == null) return;
        long when = note.optLong("reminderAt", 0);
        if (when <= System.currentTimeMillis()) return;
        AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;
        try {
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, when, pending(c, note));
        } catch (Exception ignored) {
            try { am.set(AlarmManager.RTC_WAKEUP, when, pending(c, note)); } catch (Exception ignored2) {}
        }
    }

    public static void cancel(Context c, JSONObject note) {
        if (note == null) return;
        AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        if (am != null) {
            try { am.cancel(pending(c, note)); } catch (Exception ignored) {}
        }
    }

    public static void clear(Context c, JSONObject note) {
        if (note == null) return;
        cancel(c, note);
        note.remove("reminderAt");
        try { note.put("updatedAt", System.currentTimeMillis()); } catch (Exception ignored) {}
        Storage.upsertNote(c, note);
    }

    public static void rescheduleAll(Context c) {
        JSONArray notes = Storage.notes(c);
        long now = System.currentTimeMillis();
        for (int i = 0; i < notes.length(); i++) {
            JSONObject n = notes.optJSONObject(i);
            if (n != null && n.optLong("deletedAt", 0) == 0 && n.optLong("reminderAt", 0) > now) schedule(c, n);
        }
    }
}
