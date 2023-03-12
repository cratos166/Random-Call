package com.nbird.call_random.UNIVERSAL.DIALOG;

import android.app.Dialog;
import android.content.Context;
import android.widget.LinearLayout;


import com.nbird.call_random.R;


public class LoadingDialog {


    Dialog loadingDialog;
    Context context;


    public LoadingDialog(Context context) {
        this.context=context;
    }


    public void showLoadingDialog(){

        loadingDialog=new Dialog(context);
        loadingDialog.setContentView(R.layout.loading_screen);
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);
        try{
            loadingDialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public void dismissLoadingDialog(){
         try{
             loadingDialog.dismiss();
         }catch (Exception e){
            e.printStackTrace();
         }
    }


}
