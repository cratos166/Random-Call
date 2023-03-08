package com.nbird.call_random.DATA;

import android.content.Context;
import android.content.SharedPreferences;

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
            SharedPreferences.Editor editor = sharedPreferences.edit();

            Boolean value = sharedPreferences.getBoolean("IS_FIRST_TIME", true);

            if(value){
                editor.putBoolean("IS_FIRST_TIME", false);
                editor.apply();
                return true;
            }else{
                return false;
            }

    }




}
