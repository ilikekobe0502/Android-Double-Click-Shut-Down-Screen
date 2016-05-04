package com.neo.shutdownscreen.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.neo.shutdownscreen.R;

/**
 * Created by neo on 2016/5/4.
 */
public class PublicDialog {

    public static AlertDialog showConfirmDialog(Context context, String message,
                                                DialogInterface.OnClickListener okListener) {
        AlertDialog mDialog = new AlertDialog.Builder(context)
                .setTitle(message)
                .setPositiveButton(R.string.dialog_ok, okListener)
                .show();
        return mDialog;
    }
}
