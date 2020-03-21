package com.dev.aurora.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.dev.aurora.R;

import java.util.ArrayList;
import java.util.List;

public class PermissionsUtils {
    private static PermissionsUtils permissionsUtils;

    private final static int _requestCode = 100;
    public static boolean showSystemSetting = true;

    private IPermissionResult iPermissionResult;

    private AlertDialog permissionDialog;

    public interface IPermissionResult {
        void permissionPassed();

        void permissionDenied();
    }

    public synchronized static PermissionsUtils getInstance() {
        if (permissionsUtils == null) {
            permissionsUtils = new PermissionsUtils();
        }

        return permissionsUtils;
    }

    public void checkPermission(Activity context, @NonNull String[] permission, IPermissionResult permissionResult) {
        iPermissionResult = permissionResult;

        List<String> permissionList = new ArrayList<>();
        for (String string : permission) {
            if (ContextCompat.checkSelfPermission(context, string) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(string);
            }
        }

        if (permissionList.size() > 0) {
            ActivityCompat.requestPermissions(context, permission, _requestCode);
        } else {
            permissionResult.permissionPassed();
        }
    }

    public void onRequestPermissionResult(Activity context, int requestCode, int[] passResults) {
        boolean hasPermissionDismiss = false;

        if (_requestCode == requestCode) {
            for (int passResult : passResults) {
                if (passResult == -1) {
                    hasPermissionDismiss = true;
                    break;
                }
            }

            if (hasPermissionDismiss) {
                if (showSystemSetting) {
                    showPermissionSettingDialog(context);
                } else {
                    iPermissionResult.permissionDenied();
                }
            } else {
                iPermissionResult.permissionPassed();
            }
        }
    }

    private void showPermissionSettingDialog(@NonNull Activity context) {
        String packageName = context.getPackageName();
        if (permissionDialog == null) {
            permissionDialog = new AlertDialog.Builder(context)
                    .setMessage(R.string.permissionDialog_Msg)
                    .setPositiveButton(R.string.permissionDialog_Positive, (dialog, which) -> {
                        dismissPermissionDialog();
                        Uri uri = Uri.parse("package:" + packageName);
                        context.startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri));
                        context.finish();
                    })
                    .setNegativeButton(R.string.permissionDialog_Negative, (dialog, which) -> {
                        dismissPermissionDialog();
                        iPermissionResult.permissionDenied();
                    })
                    .create();
        }

        permissionDialog.show();
    }

    private void dismissPermissionDialog() {
        if (permissionDialog != null) {
            permissionDialog.cancel();
            permissionDialog = null;
        }
    }

}
