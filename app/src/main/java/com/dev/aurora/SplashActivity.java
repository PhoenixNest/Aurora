package com.dev.aurora;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.dev.aurora.utils.ConstUtils;
import com.dev.aurora.utils.PermissionsUtils;

public class SplashActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ConstraintLayout conSplashInfo;
    private TextView tvAppName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        PermissionsUtils.showSystemSetting = true;

        conSplashInfo = findViewById(R.id.cvSplashInfo);
        tvAppName = findViewById(R.id.appName);
        Button button = findViewById(R.id.btnPermission);

        sharedPreferences = getSharedPreferences(ConstUtils.app_sp_name, MODE_PRIVATE);
        boolean hasPermission = sharedPreferences.getBoolean(ConstUtils.permission_name, false);

        if (hasPermission) {
            tvAppName.setVisibility(View.VISIBLE);
            conSplashInfo.setVisibility(View.INVISIBLE);

            new Handler().postDelayed(() -> {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                overridePendingTransition(R.anim.nav_default_enter_anim, R.anim.nav_default_exit_anim);
                finish();
            }, 2000);
        } else {
            tvAppName.setVisibility(View.INVISIBLE);
            conSplashInfo.setVisibility(View.VISIBLE);
            Toast.makeText(this, R.string.permissionCheck, Toast.LENGTH_SHORT).show();
        }

        button.setOnClickListener(v ->
                PermissionsUtils.getInstance().checkPermission(SplashActivity.this, ConstUtils.permissions, permissionResult));

    }

    PermissionsUtils.IPermissionResult permissionResult = new PermissionsUtils.IPermissionResult() {
        @Override
        public void permissionPassed() {
            System.out.println(ConstUtils.splashActivityName + ": permissionPassed");

            editor = sharedPreferences.edit();
            editor.putBoolean(ConstUtils.permission_name, true);
            editor.apply();

            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            overridePendingTransition(R.anim.nav_default_enter_anim, R.anim.nav_default_exit_anim);
            finish();
        }

        @Override
        public void permissionDenied() {
            System.out.println(ConstUtils.splashActivityName + ": permissionDenied");
            tvAppName.setVisibility(View.INVISIBLE);
            conSplashInfo.setVisibility(View.VISIBLE);

            editor = sharedPreferences.edit();
            editor.putBoolean(ConstUtils.permission_name, false);
            editor.apply();
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionsUtils.getInstance().onRequestPermissionResult(this, requestCode, grantResults);
    }

}
