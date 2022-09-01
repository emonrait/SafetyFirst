package com.raihan.safetyfirst.util;

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

        dialog.setView(reg_layout);
        final AlertDialog alertDialog = dialog.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

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

    static void call(Activity activity, String phone) {

        //String number = "+8801816028491";
        String number = phone;
        String uri = "tel:" + number.trim();
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse(uri));
        activity.startActivity(intent);
    }
}