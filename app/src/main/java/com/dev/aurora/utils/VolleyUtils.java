package com.dev.aurora.utils;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleyUtils {
    private static RequestQueue INSTANCE;

    public static synchronized RequestQueue getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Volley.newRequestQueue(context.getApplicationContext());
        }

        return INSTANCE;
    }
}
