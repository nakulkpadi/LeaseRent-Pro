package com.nakuul.fieldkeep;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.ComponentActivity;

import org.json.JSONArray;

public class LabelsActivity extends ComponentActivity {
    private LinearLayout list;
    private EditText input;
    private final int[] palette={0xFF1976D2,0xFFEF6C00,0xFF2E7D32,0xFF7B1FA2,0xFFC2185B,0xFF00838F,0xFF5D4037,0xFF455A64};
    private final String[] paletteNames={"Blue","Orange","Green","Purple","Pink","Teal","Brown","Slate"};

    @Override protected void onCreate(Bundle b){
        super.onCreate(b);Ui.prepareWindow(this,true);
        FrameLayout page=new FrameLayout(this);page.setBackgroundColor(Ui.BG);Ui.applySystemBarInsets(page,4,10);
        LinearLayout outer=new LinearLayout(this);outer.setOrientation(LinearLayout.VERTICAL);page.addView(outer,new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));

        LinearLayout top=new LinearLayout(this);top.setGravity(Gravity.CENTER_VERTICAL);top.setPadding(Ui.dp(this,20),Ui.dp(this,6),Ui.dp(this,20),Ui.dp(this,8));top.setBackgroundColor(Ui.SURFACE);
        TextView back=Ui.iconButton(this,"←");back.setTextSize(30);top.addView(back,new LinearLayout.LayoutParams(Ui.dp(this,54),Ui.dp(this,62)));
        TextView ttl=new TextView(this);ttl.setText("Edit labels");ttl.setTextColor(Ui.INK);ttl.setTextSize(24);ttl.setGravity(Gravity.CENTER_VERTICAL);top.addView(ttl,new LinearLayout.LayoutParams(0,Ui.dp(this,62),1));outer.addView(top);back.setOnClickListener(v->finish());

        LinearLayout create=new LinearLayout(this);create.setGravity(Gravity.CENTER_VERTICAL);create.setPadding(Ui.dp(this,18),Ui.dp(this,7),Ui.dp(this,18),Ui.dp(this,7));create.setBackgroundColor(Ui.SURFACE);
        TextView x=Ui.iconButton(this,"×");x.setTextSize(34);create.addView(x,new LinearLayout.LayoutParams(Ui.dp(this,54),Ui.dp(this,58)));
        input=new EditText(this);input.setHint("Create new label");input.setHintTextColor(Ui.MUTED);input.setTextColor(Ui.INK);input.setTextSize(20);input.setSingleLine(true);input.setBackgroundColor(Color.TRANSPARENT);create.addView(input,new LinearLayout.LayoutParams(0,Ui.dp(this,58),1));
        TextView ok=Ui.iconButton(this,"✓");ok.setTextSize(29);create.addView(ok,new LinearLayout.LayoutParams(Ui.dp(this,56),Ui.dp(this,58)));outer.addView(create,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,Ui.dp(this,74)));
        x.setOnClickListener(v->input.setText(""));ok.setOnClickListener(v->createLabel());input.setOnEditorActionListener((v,a,e)->{createLabel();return true;});

        TextView help=Ui.body(this,"Create labels to group related notes. Tap a colour dot to give each label its own colour.");help.setPadding(Ui.dp(this,22),Ui.dp(this,12),Ui.dp(this,22),Ui.dp(this,4));outer.addView(help);

        ScrollView scroll=new ScrollView(this);list=new LinearLayout(this);list.setOrientation(LinearLayout.VERTICAL);list.setPadding(Ui.dp(this,20),Ui.dp(this,10),Ui.dp(this,20),Ui.dp(this,28));scroll.addView(list);outer.addView(scroll,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0,1));
        setContentView(page);rebuild();page.postDelayed(()->{input.requestFocus();((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(input,InputMethodManager.SHOW_IMPLICIT);},180);
    }

    private void createLabel(){
        String s=input.getText().toString().trim();if(s.isEmpty())return;
        if(Storage.addLabel(this,s)){Storage.setLabelColor(this,s,palette[Math.abs(s.hashCode())%palette.length]);input.setText("");rebuild();Toast.makeText(this,"Label created",Toast.LENGTH_SHORT).show();}
        else Toast.makeText(this,"Label already exists",Toast.LENGTH_SHORT).show();
    }

    private void rebuild(){
        list.removeAllViews();JSONArray labels=Storage.labels(this);
        if(labels.length()==0){TextView empty=Ui.body(this,"Labels you create will appear here and in the dashboard sidebar.");empty.setTextSize(15);empty.setPadding(Ui.dp(this,8),Ui.dp(this,24),Ui.dp(this,8),0);list.addView(empty);return;}
        for(int i=0;i<labels.length();i++){
            String name=labels.optString(i);
            LinearLayout row=new LinearLayout(this);row.setGravity(Gravity.CENTER_VERTICAL);row.setPadding(Ui.dp(this,6),Ui.dp(this,4),Ui.dp(this,4),Ui.dp(this,4));row.setBackground(Ui.roundedStroke(Ui.SURFACE,14,Ui.LINE,this));
            TextView dot=new TextView(this);dot.setText("●");dot.setTextColor(Storage.labelColor(this,name));dot.setTextSize(30);dot.setGravity(Gravity.CENTER);dot.setContentDescription("Change label colour");row.addView(dot,new LinearLayout.LayoutParams(Ui.dp(this,52),Ui.dp(this,58)));dot.setOnClickListener(v->chooseColor(name));
            LinearLayout info=new LinearLayout(this);info.setOrientation(LinearLayout.VERTICAL);info.setGravity(Gravity.CENTER_VERTICAL);
            TextView tx=new TextView(this);tx.setText(name);tx.setTextColor(Ui.INK);tx.setTextSize(18);
            TextView sub=Ui.body(this,countNotes(name)+" note"+(countNotes(name)==1?"":"s"));sub.setTextSize(12);info.addView(tx);info.addView(sub);row.addView(info,new LinearLayout.LayoutParams(0,Ui.dp(this,58),1));
            TextView del=Ui.iconButton(this,"⌫");del.setTextSize(22);del.setTextColor(Ui.DANGER);row.addView(del,new LinearLayout.LayoutParams(Ui.dp(this,52),Ui.dp(this,58)));del.setOnClickListener(v->new AlertDialog.Builder(this).setTitle("Delete label?").setMessage(name+" will be removed from all notes. The notes themselves will not be deleted.").setNegativeButton("Cancel",null).setPositiveButton("Delete",(d,w)->{Storage.removeLabel(this,name);rebuild();}).show());
            LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,Ui.dp(this,72));lp.bottomMargin=Ui.dp(this,8);list.addView(row,lp);
        }
    }

    private int countNotes(String name){int c=0;JSONArray notes=Storage.notes(this);for(int i=0;i<notes.length();i++)if(Storage.hasLabel(notes.optJSONObject(i),name))c++;return c;}

    private void chooseColor(String name){
        int current=Storage.labelColor(this,name);int checked=0;for(int i=0;i<palette.length;i++)if(palette[i]==current){checked=i;break;}
        final int[] chosen={checked};
        new AlertDialog.Builder(this).setTitle("Label colour — "+name).setSingleChoiceItems(paletteNames,checked,(d,w)->chosen[0]=w).setNegativeButton("Cancel",null).setPositiveButton("Apply",(d,w)->{Storage.setLabelColor(this,name,palette[chosen[0]]);rebuild();}).show();
    }
}
