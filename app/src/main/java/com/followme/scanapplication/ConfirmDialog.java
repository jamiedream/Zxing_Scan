/*
 * Copyright (c) 2019.
 * Create by J.Y Yen 26/ 4/ 2019.
 */

package com.followme.scanapplication;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

public class ConfirmDialog {

    private String CONFIRM_TAG = ConfirmDialog.class.getSimpleName() + " :confirm";
    public void showLeftConfirmDialog(FragmentManager fragmentManager, SettingParamDialog.OnDialogCancelListener cancelListener,
                                      boolean cancelable, final StringBuilder msg, IEventResultListener<Boolean> listener){

        SettingParamDialog.newInstance(new SettingParamDialog.OnCallDialog(){

            @Override
            public Dialog getDialog(Context context) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.view_dialog_confirm);
                TextView message = dialog.findViewById(R.id.message);
                message.setGravity(Gravity.LEFT);
                ToggleButton toggle = dialog.findViewById(R.id.confirm_toggle);
                message.setText(msg);
                toggle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                return dialog;
            }
        }, cancelable, cancelListener).show(fragmentManager, CONFIRM_TAG);


    }


}
