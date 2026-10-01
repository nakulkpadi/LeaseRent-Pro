package com.nakuul.fieldkeep;

import android.content.Context;
import android.content.SharedPreferences;

public final class AppPrefs {
    private static final String NAME="waymark_camera_prefs";
    private AppPrefs(){}
    private static SharedPreferences p(Context c){return c.getSharedPreferences(NAME,Context.MODE_PRIVATE);}

    public static String photoQuality(Context c){return p(c).getString("photo_quality","High");}
    public static void photoQuality(Context c,String v){p(c).edit().putString("photo_quality",v).apply();}
    public static String videoQuality(Context c){return p(c).getString("video_quality","HD");}
    public static void videoQuality(Context c,String v){p(c).edit().putString("video_quality",v).apply();}
    public static String volumeAction(Context c){return p(c).getString("volume_action","Photo Capture");}
    public static void volumeAction(Context c,String v){p(c).edit().putString("volume_action",v).apply();}
    public static String tapAction(Context c){return p(c).getString("tap_action","Focus");}
    public static void tapAction(Context c,String v){p(c).edit().putString("tap_action",v).apply();}
    public static boolean qr(Context c){return p(c).getBoolean("qr",false);}
    public static void qr(Context c,boolean v){p(c).edit().putBoolean("qr",v).apply();}
    public static String stampPosition(Context c){return p(c).getString("stamp_position","Bottom");}
    public static void stampPosition(Context c,String v){p(c).edit().putString("stamp_position",v).apply();}
    public static boolean showLogo(Context c){return p(c).getBoolean("show_logo",true);}
    public static void showLogo(Context c,boolean v){p(c).edit().putBoolean("show_logo",v).apply();}
    public static boolean cameraIndicator(Context c){return p(c).getBoolean("camera_indicator",false);}
    public static void cameraIndicator(Context c,boolean v){p(c).edit().putBoolean("camera_indicator",v).apply();}
    public static boolean saveOriginal(Context c){return p(c).getBoolean("save_original",false);}
    public static void saveOriginal(Context c,boolean v){p(c).edit().putBoolean("save_original",v).apply();}
    public static boolean sharePhoto(Context c){return p(c).getBoolean("share_photo",false);}
    public static void sharePhoto(Context c,boolean v){p(c).edit().putBoolean("share_photo",v).apply();}
    public static boolean reports(Context c){return p(c).getBoolean("reports",true);}
    public static void reports(Context c,boolean v){p(c).edit().putBoolean("reports",v).apply();}
    public static String template(Context c){return p(c).getString("template","Advanced");}
    public static void template(Context c,String v){p(c).edit().putString("template",v).apply();}

    public static boolean addNewBottom(Context c){return p(c).getBoolean("add_new_bottom",true);}
    public static void addNewBottom(Context c,boolean v){p(c).edit().putBoolean("add_new_bottom",v).apply();}
    public static boolean moveCheckedBottom(Context c){return p(c).getBoolean("move_checked_bottom",true);}
    public static void moveCheckedBottom(Context c,boolean v){p(c).edit().putBoolean("move_checked_bottom",v).apply();}
    public static boolean richLinks(Context c){return p(c).getBoolean("rich_links",true);}
    public static void richLinks(Context c,boolean v){p(c).edit().putBoolean("rich_links",v).apply();}
    public static boolean textDefault(Context c){return p(c).getBoolean("text_default",false);}
    public static void textDefault(Context c,boolean v){p(c).edit().putBoolean("text_default",v).apply();}
    public static boolean sharing(Context c){return p(c).getBoolean("sharing",true);}
    public static void sharing(Context c,boolean v){p(c).edit().putBoolean("sharing",v).apply();}

    public static String appTheme(Context c){return p(c).getString("app_theme","System default");}
    public static void appTheme(Context c,String v){p(c).edit().putString("app_theme",v).apply();}

    public static String reminderTone(Context c){return p(c).getString("reminder_tone","Default notification");}
    public static void reminderTone(Context c,String v){p(c).edit().putString("reminder_tone",v).apply();}
    public static boolean reminderVibrate(Context c){return p(c).getBoolean("reminder_vibrate",true);}
    public static void reminderVibrate(Context c,boolean v){p(c).edit().putBoolean("reminder_vibrate",v).apply();}
}
