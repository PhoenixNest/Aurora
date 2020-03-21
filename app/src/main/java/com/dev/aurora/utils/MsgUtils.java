package com.dev.aurora.utils;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class MsgUtils {
    private static MsgUtils msgUtils;

    private static Toast toast;
    private static Snackbar snackbar;

    public static MsgUtils getInstance() {
        if (msgUtils == null) {
            msgUtils = new MsgUtils();
        }

        return msgUtils;
    }

    public void showSnackBar(View showOnView, String text) {
        snackbar = Snackbar.make(showOnView, text, Snackbar.LENGTH_SHORT);
        snackbar.setText(text);
    }

    public void showToast(Context context, String text) {
        if (toast == null) {
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        } else {
            toast.setText(text);
        }

        toast.show();
    }
}
