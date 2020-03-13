package com.dev.aurora.utils;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class MsgUtils {
    private static Toast toast;
    private static Snackbar snackbar;

    public static void showSnackBar(View showOnView, String text) {
        if (snackbar == null) {
            snackbar = Snackbar.make(showOnView, text, Snackbar.LENGTH_SHORT);
        } else {
            snackbar.setText(text);
        }

        snackbar.show();
    }

    public static void showToast(Context context, String text) {
        if (toast == null) {
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        } else {
            toast.setText(text);
        }

        toast.show();
    }
}
