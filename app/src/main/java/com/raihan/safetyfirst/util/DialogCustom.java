package com.raihan.safetyfirst.util;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.raihan.safetyfirst.R;


public class DialogCustom {

    public static void showCallDialog(Activity activity, String phone) {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(activity).setCancelable(false);
        LayoutInflater inflater = LayoutInflater.from(activity);
        View reg_layout = inflater.inflate(R.layout.call_dialog, null);

        final Button btn_no = reg_layout.findViewById(R.id.btn_no);
        final Button btn_yes = reg_layout.findViewById(R.id.btn_yes);
        final TextView tv_message = reg_layout.findViewById(R.id.tv_message);
        final TextView tv_number = reg_layout.findViewById(R.id.tv_number);

        dialog.setView(reg_layout);
        final AlertDialog alertDialog = dialog.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // tv_message.setText(activity.getString(R.string.alert_message));
        tv_number.setText(phone);
        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();

            }
        });
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call(activity, phone);
                alertDialog.cancel();

            }
        });


        alertDialog.show();

    }

    public static void showErrorMessage(Activity activity, String message, String flag) {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(activity).setCancelable(false);
        LayoutInflater inflater = LayoutInflater.from(activity);
        View reg_layout = inflater.inflate(R.layout.call_dialog, null);

        final Button btn_no = reg_layout.findViewById(R.id.btn_no);
        final Button btn_yes = reg_layout.findViewById(R.id.btn_yes);
        final TextView tv_message = reg_layout.findViewById(R.id.tv_message);
        final ImageView image_icon = reg_layout.findViewById(R.id.image_icon);

        dialog.setView(reg_layout);
        final AlertDialog alertDialog = dialog.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        tv_message.setText(message);
        btn_no.setVisibility(View.GONE);
        if (flag.equals("P")) {
            image_icon.setImageResource(R.drawable.peronal);
        } else if (flag.equals("PO")) {
            image_icon.setImageResource(R.drawable.police);
        } else if (flag.equals("E")) {
            image_icon.setImageResource(R.drawable.emergencycall);
        } else {
            image_icon.setImageResource(R.drawable.police);
        }
        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();

            }
        });
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();

            }
        });


        alertDialog.show();

    }

    public static void call(Activity activity, String phone) {

        //String number = "+8801816028491";
        String number = phone;
        String uri = "tel:" + number.trim();
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse(uri));
        activity.startActivity(intent);
    }


    public static void doClearActivity(Intent intent, Activity activity) {

        intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP); // To clean up all activities
        activity.startActivity(intent);
        activity.finish();
        //activity.finishAffinity();

    }
}