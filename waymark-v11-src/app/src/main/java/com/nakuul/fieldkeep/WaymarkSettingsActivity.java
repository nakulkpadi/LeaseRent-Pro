package com.nakuul.fieldkeep;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.ComponentActivity;

public class WaymarkSettingsActivity extends ComponentActivity {
    private LinearLayout root;

    @Override protected void onCreate(Bundle b){
        super.onCreate(b);Ui.prepareWindow(this,true);
        FrameLayout page=new FrameLayout(this);page.setBackgroundColor(Ui.BG);Ui.applySystemBarInsets(page,4,10);
        ScrollView scroll=new ScrollView(this);scroll.setClipToPadding(false);scroll.setFillViewport(true);page.addView(scroll,new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
        root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(Ui.dp(this,20),Ui.dp(this,10),Ui.dp(this,20),Ui.dp(this,40));scroll.addView(root);

        LinearLayout top=new LinearLayout(this);top.setGravity(Gravity.CENTER_VERTICAL);
        TextView back=Ui.iconButton(this,"←");back.setTextSize(30);top.addView(back,new LinearLayout.LayoutParams(Ui.dp(this,52),Ui.dp(this,58)));
        TextView ttl=new TextView(this);ttl.setText("Settings");ttl.setTextColor(Ui.INK);ttl.setTextSize(24);ttl.setGravity(Gravity.CENTER_VERTICAL);top.addView(ttl,new LinearLayout.LayoutParams(0,Ui.dp(this,58),1));root.addView(top);back.setOnClickListener(v->finish());

        heading("Appearance");
        row("Theme",AppPrefs.appTheme(this),this::chooseTheme);

        heading("Reminders");
        row("Reminder tone",AppPrefs.reminderTone(this),this::chooseReminderTone);
        toggle("Vibrate with reminders",AppPrefs.reminderVibrate(this),v->AppPrefs.reminderVibrate(this,v));

        heading("Sharing");
        toggle("Enable sharing",AppPrefs.sharing(this),v->AppPrefs.sharing(this,v));

        heading("Capture & reports");
        row("Camera & stamp settings","Photo, video, location stamp and templates",()->startActivity(new Intent(this,CameraSettingsActivity.class)));

        heading("About & privacy");
        row("About Waymark","Quick overview",this::showShortAbout);
        row("About Webnix Studio","Full About Us",()->openInfo("about"));
        row("Privacy Policy","Effective 1 October 2026",()->openInfo("privacy"));
        row("Contact support","waymarknote@gmail.com",this::emailSupport);
        row("App version","0.8 test build",null);
        setContentView(page);
    }

    private void heading(String s){TextView t=new TextView(this);t.setText(s);t.setTextColor(Ui.INK);t.setTextSize(22);t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);t.setPadding(0,Ui.dp(this,22),0,Ui.dp(this,8));root.addView(t);}

    private void toggle(String title,boolean checked,Toggle setter){
        LinearLayout r=rowBase();TextView a=new TextView(this);a.setText(title);a.setTextColor(Ui.INK);a.setTextSize(17);a.setGravity(Gravity.CENTER_VERTICAL);r.addView(a,new LinearLayout.LayoutParams(0,Ui.dp(this,62),1));
        Switch sw=new Switch(this);sw.setChecked(checked);sw.setOnCheckedChangeListener((b,v)->setter.set(v));r.addView(sw,new LinearLayout.LayoutParams(Ui.dp(this,70),Ui.dp(this,54)));root.addView(r);
    }

    private void row(String title,String value,Runnable action){
        LinearLayout r=rowBase();LinearLayout texts=new LinearLayout(this);texts.setOrientation(LinearLayout.VERTICAL);texts.setGravity(Gravity.CENTER_VERTICAL);TextView a=new TextView(this);a.setText(title);a.setTextColor(Ui.INK);a.setTextSize(17);TextView b=new TextView(this);b.setText(value);b.setTextColor(Ui.MUTED);b.setTextSize(13);texts.addView(a);texts.addView(b);r.addView(texts,new LinearLayout.LayoutParams(0,Ui.dp(this,70),1));if(action!=null){TextView v=new TextView(this);v.setText("›");v.setTextColor(Ui.MUTED);v.setTextSize(26);v.setGravity(Gravity.CENTER);r.addView(v,new LinearLayout.LayoutParams(Ui.dp(this,42),Ui.dp(this,60)));r.setOnClickListener(x->action.run());}root.addView(r);
    }

    private LinearLayout rowBase(){LinearLayout r=new LinearLayout(this);r.setOrientation(LinearLayout.HORIZONTAL);r.setGravity(Gravity.CENTER_VERTICAL);r.setPadding(Ui.dp(this,2),0,Ui.dp(this,2),0);return r;}

    private void chooseTheme(){
        String[] items={"Light","Dark","System default"};String cur=AppPrefs.appTheme(this);int checked=2;for(int i=0;i<items.length;i++)if(items[i].equals(cur))checked=i;final int[] choice={checked};
        new AlertDialog.Builder(this).setTitle("Choose theme").setSingleChoiceItems(items,checked,(d,w)->choice[0]=w).setNegativeButton("Cancel",null).setPositiveButton("Apply",(d,w)->{AppPrefs.appTheme(this,items[choice[0]]);Ui.applyTheme(this);restartForTheme();}).show();
    }

    private void restartForTheme(){
        Intent i=new Intent(this,MainActivity.class);i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);startActivity(i);finish();
    }

    private void chooseReminderTone(){
        String[] items={"Default notification","Alarm tone","Silent"};String cur=AppPrefs.reminderTone(this);int checked=0;for(int i=0;i<items.length;i++)if(items[i].equals(cur))checked=i;final int[] choice={checked};
        new AlertDialog.Builder(this).setTitle("Reminder tone").setSingleChoiceItems(items,checked,(d,w)->choice[0]=w).setNegativeButton("Cancel",null).setPositiveButton("Save",(d,w)->{AppPrefs.reminderTone(this,items[choice[0]]);Toast.makeText(this,"Reminder tone updated",Toast.LENGTH_SHORT).show();recreate();}).show();
    }

    private void showShortAbout(){new AlertDialog.Builder(this).setTitle("Waymark Field Notes").setMessage("Waymark Field Notes, created by Webnix Studio, brings site records, GPS locations, photos, and voice notes together in one app. Built to simplify fieldwork, Waymark helps you capture details, stay organized, and turn site observations into clear reports.").setPositiveButton("OK",null).show();}
    private void openInfo(String page){Intent i=new Intent(this,InfoActivity.class);i.putExtra("page",page);startActivity(i);}
    private void emailSupport(){try{Intent i=new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:waymarknote@gmail.com"));i.putExtra(Intent.EXTRA_SUBJECT,"Waymark Field Notes support");startActivity(i);}catch(Exception e){Toast.makeText(this,"No email app available",Toast.LENGTH_SHORT).show();}}
    private interface Toggle{void set(boolean value);}
}
