package com.nakuul.fieldkeep;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

public final class Storage {
    private static final String PREF = "fieldkeep_store";
    private static final String NOTES = "notes";
    private static final String RECORDS = "land_records";
    private static final String LABELS = "labels";
    private static final String LABEL_COLORS = "label_colors";
    public static final long DELETE_AFTER_MS = 30L * 24L * 60L * 60L * 1000L;

    private Storage() {}

    private static SharedPreferences prefs(Context c) {
        return c.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    public static synchronized JSONArray notes(Context c) {
        try { return new JSONArray(prefs(c).getString(NOTES, "[]")); }
        catch (Exception e) { return new JSONArray(); }
    }

    public static synchronized void saveNotes(Context c, JSONArray a) {
        prefs(c).edit().putString(NOTES, a.toString()).apply();
    }

    public static synchronized JSONArray records(Context c) {
        try { return new JSONArray(prefs(c).getString(RECORDS, "[]")); }
        catch (Exception e) { return new JSONArray(); }
    }

    public static synchronized void saveRecords(Context c, JSONArray a) {
        prefs(c).edit().putString(RECORDS, a.toString()).apply();
    }

    public static synchronized JSONArray labels(Context c) {
        try { return new JSONArray(prefs(c).getString(LABELS, "[]")); }
        catch (Exception e) { return new JSONArray(); }
    }

    public static synchronized void saveLabels(Context c, JSONArray a) {
        prefs(c).edit().putString(LABELS, a.toString()).apply();
    }

    public static synchronized int labelColor(Context c,String label){
        if(label==null)return 0xFF1976D2;
        try{JSONObject o=new JSONObject(prefs(c).getString(LABEL_COLORS,"{}"));if(o.has(label))return o.optInt(label,0xFF1976D2);}catch(Exception ignored){}
        int[] colors={0xFF1976D2,0xFFEF6C00,0xFF2E7D32,0xFF7B1FA2,0xFFC2185B,0xFF00838F,0xFF5D4037,0xFF455A64};
        return colors[Math.abs(label.toLowerCase().hashCode())%colors.length];
    }

    public static synchronized void setLabelColor(Context c,String label,int color){
        if(label==null)return;
        try{JSONObject o=new JSONObject(prefs(c).getString(LABEL_COLORS,"{}"));o.put(label,color);prefs(c).edit().putString(LABEL_COLORS,o.toString()).apply();}catch(Exception ignored){}
    }

    public static synchronized boolean addLabel(Context c, String value) {
        String name=value==null?"":value.trim();
        if(name.isEmpty())return false;
        JSONArray a=labels(c);
        for(int i=0;i<a.length();i++) if(name.equalsIgnoreCase(a.optString(i))) return false;
        a.put(name); saveLabels(c,a); return true;
    }

    public static synchronized void removeLabel(Context c,String value){
        JSONArray a=labels(c),out=new JSONArray();
        for(int i=0;i<a.length();i++){String s=a.optString(i);if(!value.equalsIgnoreCase(s))out.put(s);}saveLabels(c,out);
        JSONArray notes=notes(c);boolean changed=false;
        for(int i=0;i<notes.length();i++){JSONObject n=notes.optJSONObject(i);if(n==null)continue;JSONArray ls=n.optJSONArray("labels");if(ls==null)continue;JSONArray nl=new JSONArray();for(int j=0;j<ls.length();j++){String s=ls.optString(j);if(!value.equalsIgnoreCase(s))nl.put(s);else changed=true;}try{n.put("labels",nl);}catch(Exception ignored){}}
        if(changed)saveNotes(c,notes);
        try{JSONObject o=new JSONObject(prefs(c).getString(LABEL_COLORS,"{}"));o.remove(value);prefs(c).edit().putString(LABEL_COLORS,o.toString()).apply();}catch(Exception ignored){}
    }

    public static synchronized JSONObject findRecord(Context c, String id) {
        JSONArray a = records(c);
        for (int i = 0; i < a.length(); i++) {
            JSONObject o = a.optJSONObject(i);
            if (o != null && id.equals(o.optString("id"))) return o;
        }
        return null;
    }

    public static synchronized JSONObject findNote(Context c,String id){
        if(id==null)return null;JSONArray a=notes(c);for(int i=0;i<a.length();i++){JSONObject n=a.optJSONObject(i);if(n!=null&&id.equals(n.optString("id")))return n;}return null;
    }

    public static synchronized void upsertRecord(Context c, JSONObject record) {
        JSONArray a = records(c);
        String id = record.optString("id");
        for (int i = 0; i < a.length(); i++) {
            JSONObject o = a.optJSONObject(i);
            if (o != null && id.equals(o.optString("id"))) {
                try { a.put(i, record); } catch (Exception ignored) {}
                saveRecords(c, a);
                return;
            }
        }
        a.put(record);
        saveRecords(c, a);
    }

    public static synchronized void upsertNote(Context c,JSONObject note){
        JSONArray a=notes(c);String id=note.optString("id");
        for(int i=0;i<a.length();i++){JSONObject n=a.optJSONObject(i);if(n!=null&&id.equals(n.optString("id"))){try{a.put(i,note);}catch(Exception ignored){}saveNotes(c,a);return;}}
        a.put(note);saveNotes(c,a);
    }

    public static synchronized void moveRecordToDeleted(Context c,String id){
        JSONArray a=records(c);for(int i=0;i<a.length();i++){JSONObject r=a.optJSONObject(i);if(r!=null&&id.equals(r.optString("id"))){try{r.put("deletedAt",System.currentTimeMillis());r.put("updatedAt",System.currentTimeMillis());}catch(Exception ignored){}break;}}saveRecords(c,a);
    }

    public static synchronized void restoreDeletedRecord(Context c,String id){
        JSONArray a=records(c);for(int i=0;i<a.length();i++){JSONObject r=a.optJSONObject(i);if(r!=null&&id.equals(r.optString("id"))){try{r.remove("deletedAt");r.put("updatedAt",System.currentTimeMillis());}catch(Exception ignored){}break;}}saveRecords(c,a);
    }

    public static synchronized void permanentlyDeleteRecord(Context c,String id){
        JSONArray a=records(c);for(int i=0;i<a.length();i++){JSONObject r=a.optJSONObject(i);if(r!=null&&id.equals(r.optString("id"))){a.remove(i);break;}}saveRecords(c,a);
    }

    public static synchronized void moveToDeleted(Context c,String id){
        JSONArray a=notes(c);for(int i=0;i<a.length();i++){JSONObject n=a.optJSONObject(i);if(n!=null&&id.equals(n.optString("id"))){try{n.put("deletedAt",System.currentTimeMillis());n.put("archived",false);n.put("updatedAt",System.currentTimeMillis());}catch(Exception ignored){}break;}}saveNotes(c,a);
    }

    public static synchronized void restoreDeleted(Context c,String id){
        JSONArray a=notes(c);for(int i=0;i<a.length();i++){JSONObject n=a.optJSONObject(i);if(n!=null&&id.equals(n.optString("id"))){try{n.remove("deletedAt");n.put("updatedAt",System.currentTimeMillis());}catch(Exception ignored){}break;}}saveNotes(c,a);
    }

    public static synchronized void permanentlyDelete(Context c,String id){
        JSONArray a=notes(c);for(int i=0;i<a.length();i++){JSONObject n=a.optJSONObject(i);if(n!=null&&id.equals(n.optString("id"))){a.remove(i);break;}}saveNotes(c,a);
    }

    public static synchronized int purgeExpiredDeleted(Context c){
        long now=System.currentTimeMillis();int purged=0;
        JSONArray a=notes(c),out=new JSONArray();
        for(int i=0;i<a.length();i++){JSONObject n=a.optJSONObject(i);if(n==null)continue;long deleted=n.optLong("deletedAt",0);if(deleted>0&&now-deleted>=DELETE_AFTER_MS){purged++;continue;}out.put(n);}if(out.length()!=a.length())saveNotes(c,out);
        JSONArray r=records(c),ro=new JSONArray();
        for(int i=0;i<r.length();i++){JSONObject x=r.optJSONObject(i);if(x==null)continue;long deleted=x.optLong("deletedAt",0);if(deleted>0&&now-deleted>=DELETE_AFTER_MS){purged++;continue;}ro.put(x);}if(ro.length()!=r.length())saveRecords(c,ro);
        return purged;
    }

    public static boolean hasLabel(JSONObject note,String label){
        JSONArray a=note==null?null:note.optJSONArray("labels");if(a==null||label==null)return false;for(int i=0;i<a.length();i++)if(label.equalsIgnoreCase(a.optString(i)))return true;return false;
    }
}
