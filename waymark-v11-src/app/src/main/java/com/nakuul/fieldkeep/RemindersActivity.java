package com.nakuul.fieldkeep;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RemindersActivity extends ComponentActivity {
    private LinearLayout content;

    @Override protected void onCreate(Bundle b){
        super.onCreate(b); Ui.prepareWindow(this,true);
        FrameLayout page=new FrameLayout(this); page.setBackgroundColor(Ui.BG); Ui.applySystemBarInsets(page,4,10);
        ScrollView scroll=new ScrollView(this); scroll.setFillViewport(true);
        content=new LinearLayout(this); content.setOrientation(LinearLayout.VERTICAL); content.setPadding(Ui.dp(this,18),Ui.dp(this,6),Ui.dp(this,18),Ui.dp(this,36)); scroll.addView(content);
        page.addView(scroll,new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(page); rebuild();
    }

    @Override protected void onResume(){ super.onResume(); ReminderUtil.rescheduleAll(this); rebuild(); }

    private void rebuild(){
        if(content==null)return; content.removeAllViews();
        LinearLayout top=new LinearLayout(this);top.setGravity(Gravity.CENTER_VERTICAL);
        TextView back=Ui.iconButton(this,"←");back.setTextSize(30);top.addView(back,new LinearLayout.LayoutParams(Ui.dp(this,52),Ui.dp(this,58)));back.setOnClickListener(v->finish());
        TextView ttl=new TextView(this);ttl.setText("Reminders");ttl.setTextColor(Ui.INK);ttl.setTextSize(24);ttl.setTypeface(Typeface.DEFAULT,Typeface.BOLD);ttl.setGravity(Gravity.CENTER_VERTICAL);top.addView(ttl,new LinearLayout.LayoutParams(0,Ui.dp(this,58),1));content.addView(top);

        LinearLayout controls=Ui.card(this); TextView intro=Ui.body(this,"Set a reminder for any note. You can edit or delete a reminder without deleting the note.");intro.setPadding(0,0,0,Ui.dp(this,10));controls.addView(intro);
        LinearLayout actions=new LinearLayout(this);actions.setOrientation(LinearLayout.HORIZONTAL);
        TextView add=Ui.primaryAction(this,"＋ Add reminder");LinearLayout.LayoutParams ap=new LinearLayout.LayoutParams(0,Ui.dp(this,50),1);ap.rightMargin=Ui.dp(this,6);actions.addView(add,ap);
        TextView settings=Ui.actionChip(this,"⚙  Settings");settings.setGravity(Gravity.CENTER);LinearLayout.LayoutParams sp=new LinearLayout.LayoutParams(0,Ui.dp(this,50),1);sp.leftMargin=Ui.dp(this,6);actions.addView(settings,sp);controls.addView(actions);add.setOnClickListener(v->addReminder());settings.setOnClickListener(v->settings());
        LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);cp.bottomMargin=Ui.dp(this,16);content.addView(controls,cp);

        JSONArray notes=Storage.notes(this); int count=0;
        for(int i=0;i<notes.length();i++){
            JSONObject n=notes.optJSONObject(i); if(n==null||n.optLong("deletedAt",0)>0||n.optBoolean("archived",false)||n.optLong("reminderAt",0)<=0)continue; count++; addReminderCard(n);
        }
        if(count==0){TextView empty=Ui.body(this,"No reminders yet.\n\nTap Add reminder to choose a note and schedule one.");empty.setGravity(Gravity.CENTER);empty.setTextSize(17);empty.setTextColor(Ui.MUTED);content.addView(empty,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,Ui.dp(this,260)));}
    }

    private void addReminderCard(JSONObject n){
        LinearLayout card=Ui.card(this);String title=n.optString("title").trim();if(title.isEmpty())title="Untitled note";
        TextView t=Ui.title(this,title);t.setTextSize(17);t.setPadding(0,0,0,Ui.dp(this,5));card.addView(t);
        long at=n.optLong("reminderAt",0);String when=new SimpleDateFormat("EEE, dd MMM yyyy • h:mm a",Locale.getDefault()).format(new Date(at));TextView w=Ui.body(this,(at<System.currentTimeMillis()?"Overdue • ":"⏰ ")+when);w.setTextColor(at<System.currentTimeMillis()?Ui.DANGER:Ui.ORANGE);w.setTextSize(14);card.addView(w);
        String body=n.optString("body").trim();if(!body.isEmpty()){if(body.length()>120)body=body.substring(0,120)+"…";TextView b=Ui.body(this,body);b.setPadding(0,Ui.dp(this,7),0,0);card.addView(b);}
        LinearLayout buttons=new LinearLayout(this);buttons.setOrientation(LinearLayout.HORIZONTAL);buttons.setPadding(0,Ui.dp(this,10),0,0);
        TextView edit=Ui.actionChip(this,"Edit");edit.setGravity(Gravity.CENTER);TextView open=Ui.actionChip(this,"Open note");open.setGravity(Gravity.CENTER);TextView del=Ui.actionChip(this,"Delete");del.setTextColor(Ui.DANGER);del.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams bp=new LinearLayout.LayoutParams(0,Ui.dp(this,44),1);bp.rightMargin=Ui.dp(this,5);buttons.addView(edit,bp);LinearLayout.LayoutParams bp2=new LinearLayout.LayoutParams(0,Ui.dp(this,44),1);bp2.leftMargin=Ui.dp(this,3);bp2.rightMargin=Ui.dp(this,3);buttons.addView(open,bp2);LinearLayout.LayoutParams bp3=new LinearLayout.LayoutParams(0,Ui.dp(this,44),1);bp3.leftMargin=Ui.dp(this,5);buttons.addView(del,bp3);card.addView(buttons);
        edit.setOnClickListener(v->pickReminder(n));open.setOnClickListener(v->{Intent in=new Intent(this,NoteActivity.class);in.putExtra("note_id",n.optString("id"));startActivity(in);});del.setOnClickListener(v->new AlertDialog.Builder(this).setTitle("Delete reminder?").setMessage("The note will stay in Waymark. Only its reminder will be removed.").setNegativeButton("Cancel",null).setPositiveButton("Delete",(d,x)->{ReminderUtil.clear(this,n);rebuild();}).show());
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);lp.bottomMargin=Ui.dp(this,10);content.addView(card,lp);
    }

    private void ensurePermission(){if(Build.VERSION.SDK_INT>=33&&ContextCompat.checkSelfPermission(this,Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED)requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},701);}

    private void addReminder(){
        ensurePermission();JSONArray notes=Storage.notes(this);ArrayList<JSONObject> list=new ArrayList<>();ArrayList<String> names=new ArrayList<>();
        for(int i=0;i<notes.length();i++){JSONObject n=notes.optJSONObject(i);if(n==null||n.optLong("deletedAt",0)>0||n.optBoolean("archived",false))continue;list.add(n);String t=n.optString("title").trim();names.add(t.isEmpty()?"Untitled note":t);}
        if(list.isEmpty()){new AlertDialog.Builder(this).setTitle("No notes available").setMessage("Create a note first, then set a reminder for it.").setPositiveButton("Create note",(d,w)->startActivity(new Intent(this,NoteActivity.class))).setNegativeButton("Cancel",null).show();return;}
        new AlertDialog.Builder(this).setTitle("Choose note").setItems(names.toArray(new String[0]),(d,w)->pickReminder(list.get(w))).setNegativeButton("Cancel",null).show();
    }

    private void pickReminder(JSONObject n){
        ensurePermission();Calendar base=Calendar.getInstance();long existing=n.optLong("reminderAt",0);if(existing>0)base.setTimeInMillis(existing);
        new DatePickerDialog(this,(dv,y,m,day)->new TimePickerDialog(this,(tv,h,min)->{Calendar c=Calendar.getInstance();c.set(y,m,day,h,min,0);c.set(Calendar.MILLISECOND,0);try{n.put("reminderAt",c.getTimeInMillis());n.put("updatedAt",System.currentTimeMillis());}catch(Exception ignored){}Storage.upsertNote(this,n);ReminderUtil.schedule(this,n);Toast.makeText(this,"Reminder set for "+new SimpleDateFormat("dd MMM, h:mm a",Locale.getDefault()).format(c.getTime()),Toast.LENGTH_LONG).show();rebuild();},base.get(Calendar.HOUR_OF_DAY),base.get(Calendar.MINUTE),false).show(),base.get(Calendar.YEAR),base.get(Calendar.MONTH),base.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void settings(){
        String[] tones={"Default notification","Alarm tone","Silent"};String current=AppPrefs.reminderTone(this);int checked=0;for(int i=0;i<tones.length;i++)if(tones[i].equals(current))checked=i;final int[] chosen={checked};final boolean[] vibrate={AppPrefs.reminderVibrate(this)};
        LinearLayout wrap=new LinearLayout(this);wrap.setOrientation(LinearLayout.VERTICAL);wrap.setPadding(Ui.dp(this,18),0,Ui.dp(this,18),0);RadioGroup group=new RadioGroup(this);for(int i=0;i<tones.length;i++){RadioButton rb=new RadioButton(this);rb.setId(100+i);rb.setText(tones[i]);rb.setTextColor(Ui.INK);group.addView(rb);if(i==checked)rb.setChecked(true);}group.setOnCheckedChangeListener((g,id)->chosen[0]=Math.max(0,id-100));wrap.addView(group);CheckBox vb=new CheckBox(this);vb.setText("Vibrate with reminders");vb.setTextColor(Ui.INK);vb.setChecked(vibrate[0]);vb.setOnCheckedChangeListener((b,on)->vibrate[0]=on);wrap.addView(vb);
        new AlertDialog.Builder(this).setTitle("Reminder settings").setView(wrap).setNegativeButton("Cancel",null).setPositiveButton("Save",(d,w)->{AppPrefs.reminderTone(this,tones[chosen[0]]);AppPrefs.reminderVibrate(this,vibrate[0]);Toast.makeText(this,"Reminder settings saved",Toast.LENGTH_SHORT).show();}).show();
    }
}
