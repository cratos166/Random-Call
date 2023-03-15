package com.nbird.call_random.DATA;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AppData {


    Context context;


    public AppData(Context context) {
        this.context = context;
    }

    public void setMyName(String value){
        final SharedPreferences sharedPreferences = context.getSharedPreferences("MY_DATA", 0);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("NAME", value);
        editor.commit();
    }

    public void setMyDis(String value){
        final SharedPreferences sharedPreferences = context.getSharedPreferences("MY_DATA", 0);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("DIS", value);
        editor.commit();
    }

    public void setMyImage(String value){
        final SharedPreferences sharedPreferences = context.getSharedPreferences("MY_DATA", 0);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("IMAGE", value);
        editor.commit();
    }

    public void setMyUID(String value){
        final SharedPreferences sharedPreferences = context.getSharedPreferences("MY_DATA", 0);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("UID", value);
        editor.commit();
    }

    public void setMyGender(String value){
        final SharedPreferences sharedPreferences = context.getSharedPreferences("MY_DATA", 0);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("GENDER", value);
        editor.commit();
    }

    public void setMyBalance(int value){
        final SharedPreferences sharedPreferences = context.getSharedPreferences("MY_DATA", 0);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("BALANCE", value);
        editor.commit();
    }

    public void setDate(String value){
        final SharedPreferences sharedPreferences = context.getSharedPreferences("MY_DATA", 0);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("DATE", value);
        editor.commit();
    }

    public String getDate(){

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);


        SharedPreferences sharedPreferences = context.getSharedPreferences("MY_DATA", 0);
        String value = sharedPreferences.getString("DATE",formattedDate);
        return value;
    }

    public int getMyBalance(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("MY_DATA", 0);
        int value = sharedPreferences.getInt("BALANCE",100);
        return value;
    }


    public String getMyName(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("MY_DATA", 0);
        String value = sharedPreferences.getString("NAME","");
        return value;
    }

    public String getMyDis(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("MY_DATA", 0);
        String value = sharedPreferences.getString("DIS","");
        return value;
    }

    public String getMyImage(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("MY_DATA", 0);
        String value = sharedPreferences.getString("IMAGE","");
        return value;
    }

    public String getMyUID(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("MY_DATA", 0);
        String value = sharedPreferences.getString("UID","");
        return value;
    }

    public String getMyGender(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("MY_DATA", 0);
        String value = sharedPreferences.getString("GENDER","");
        return value;
    }



    public Boolean isFirstTime(){

            SharedPreferences sharedPreferences = context.getSharedPreferences("MY_DATA", 0);
            Boolean value = sharedPreferences.getBoolean("IS_FIRST_TIME", true);
            return value;

    }

    public void setFirstTime(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("MY_DATA", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("IS_FIRST_TIME", false);
        editor.apply();
    }


    public String getLevel(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("MY_DATA", 0);
        String value = sharedPreferences.getString("LEVEL","");
        return value;
    }

    public void setLevel(String value){
        final SharedPreferences sharedPreferences = context.getSharedPreferences("MY_DATA", 0);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("LEVEL", value);
        editor.commit();
    }

    public String getGenderPref(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("MY_DATA", 0);
        String value = sharedPreferences.getString("PREF_GENDER","");
        return value;
    }

    public void setGenderPref(String value){
        final SharedPreferences sharedPreferences = context.getSharedPreferences("MY_DATA", 0);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("PREF_GENDER", value);
        editor.commit();
    }


}
