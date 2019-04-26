/*
 * Copyright (c) 2019.
 * Create by J.Y Yen 26/ 4/ 2019.
 */

package com.followme.scanapplication;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class SettingParamDialog extends DialogFragment {

    private String TAG = SettingParamDialog.class.getSimpleName();

    /**
     * listener for cancel
     */
    private OnDialogCancelListener mCancelListener;

    /**
     * show dialog
     */
    private OnCallDialog mOnCallDialog;

    interface OnDialogCancelListener {
        void onCancel();
    }

    public interface OnCallDialog {
        Dialog getDialog(Context context);
    }

    public static SettingParamDialog newInstance(OnCallDialog callDialog, boolean cancelable) {
        return newInstance(callDialog, cancelable, null);
    }

    public static SettingParamDialog newInstance(OnCallDialog callDialog, boolean cancelable, OnDialogCancelListener cancelListener) {
        SettingParamDialog instance = new SettingParamDialog();
        instance.setCancelable(cancelable);
        instance.mCancelListener = cancelListener;
        instance.mOnCallDialog = callDialog;
        return instance;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if (mOnCallDialog == null) {
            super.onCreate(savedInstanceState);
        }
        return mOnCallDialog.getDialog(getActivity());

    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mCancelListener != null) {
            mCancelListener.onCancel();
        }
    }
}