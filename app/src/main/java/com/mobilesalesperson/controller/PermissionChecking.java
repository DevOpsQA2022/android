package com.mobilesalesperson.controller;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;

import com.tbruyelle.rxpermissions2.RxPermissions;

public class PermissionChecking extends AppBaseActivity {
    private ToastMessage toastMsg;
    RxPermissions rxPermissions ;

    private Button btnLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_permission_layout);
        btnLogin = (Button) findViewById(R.id.btn_Login);
        toastMsg = new ToastMessage();
        rxPermissions = new RxPermissions(this); // where this is an Activity instance
       // permissioncheck();

    }
    private void permissioncheck() {
        rxPermissions
                .requestEachCombined(Manifest.permission.CAMERA,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(permission -> { // will emit 2 Permission objects
                    if (permission.granted) {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        Context context = getApplicationContext();
                        // Check whether has the write settings permission or not.
                        boolean settingsCanWrite = false;
                            settingsCanWrite = Settings.System.canWrite(context);
                            if(!settingsCanWrite) {
                                // If do not have write settings permission then open the Can modify system settings panel.
                                toastMsg.showToast(PermissionChecking.this,
                                        "Please select Allow modify system setting");
                                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                                intent.setData(Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                                startActivity(intent);
                            }else {
                                // If has permission then show an alert dialog with message.
                                Intent intent = new Intent(PermissionChecking.this, Login.class);
                                this.startActivity(intent);
                            }
                        }
                        else{
                            Intent intent = new Intent(PermissionChecking.this, Login.class);
                            this.startActivity(intent);
                        }

                        // `permission.name` is granted !
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        // Denied permission without ask never again
                        toastMsg.showToast(PermissionChecking.this,
                                "Give us a Permission");
                        permissioncheck();
                    } else {
                        toastMsg.showToast(PermissionChecking.this,
                                "Give us a Permission in setting");
                        startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID)));
                        // Denied permission with ask never again
                        // Need to go to the settings
                    }
                });
    }



    @Override
    protected void onStart() {
        super.onStart();
        permissioncheck();
    }
}
