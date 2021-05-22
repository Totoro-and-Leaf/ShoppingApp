package com.fyp.shoping;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.fyp.shoping.R;

public class Dialog {

    Activity activity;
    AlertDialog alertDialog;

    Dialog(Activity activity){
        this.activity = activity;
    }

    public void startLoadingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater layoutInflater = activity.getLayoutInflater();
        builder.setView(layoutInflater.inflate(R.layout.dialog, null));
        builder.setCancelable(false);

        alertDialog = builder.create();
        alertDialog.show();
    }

    public void dismiss(){
        alertDialog.dismiss();
    }



}
