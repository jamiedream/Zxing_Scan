/*
 * Copyright (c) 2019.
 * Create by J.Y Yen 26/ 4/ 2019.
 */

package com.followme.scanapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.followme.zxing_android_integration.IntentIntegrator;

import java.lang.ref.WeakReference;


/**
 * Warning: Due to security and performance limitations, use of WEP networks is discouraged.
 *
 * */

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private final int SCAN_CODE = 30;
    private final int WIFI_SCAN_CODE = 31;
    private TextView scannedText;
    private TextView scannedSSID;
    private QRCodeUtil qrCodeUtil;
    private EditText generateText;
    private ImageView qrCode;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.about){
            String[] array = getResources().getStringArray(R.array.license);
            StringBuilder license = new StringBuilder();
            for(int num = 0; num < array.length; num++){
                license.append(array[num] + "\n");
            }
            new ConfirmDialog().showLeftConfirmDialog(getSupportFragmentManager(),
                    null, true, license,null);

        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //transparent status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);

        //permission
        new PermissionUtil().checkPermossion(
                new WeakReference<Activity>(this),
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Build.VERSION_CODES.O_MR1,
                PermissionUtil.PERMISSIONS_REQUEST_WRITE);

        generateText = findViewById(R.id.generate_text);
        qrCode = findViewById(R.id.qr_code_preview);

        qrCodeUtil = new QRCodeUtil();

        generateCode();
        scanGetText();
        generateWifiCode();
        scanToConnectWifi();

    }

    @Override
    protected void onResume() {
        super.onResume();

        generateText.setText("");

        String ssid = getCurrentSsid();
        if(ssid != null){
            ((EditText) findViewById(R.id.ssid_text)).setText(ssid);
        }
    }

    public String getCurrentSsid() {
        String ssid = null;
        ConnectivityManager connManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo.isConnected()) {
            final WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            if (connectionInfo != null && !TextUtils.isEmpty(connectionInfo.getSSID())) {
                ssid = connectionInfo.getSSID();
                ssid = ssid.substring(1, ssid.length() - 1);
            }
        }
        return ssid;
    }

    private void generateCode(){
        Button generateCode = findViewById(R.id.generate_click);
        generateCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(generateText.getText().toString().length() > 0){
                    qrCode.setImageBitmap(
                            qrCodeUtil.textToImageEncode(
                                    new WeakReference<Context>(MainActivity.this),
                                    generateText.getText().toString(),
                                    getCodeDimension()
                            )
                    );
                }
            }
        });
    }

    private void scanGetText(){
        Button click = findViewById(R.id.click);
        scannedText = findViewById(R.id.scanned_text);
        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCheck = new Intent("com.google.zxing.client.android.SCAN");
                if(getPackageManager().queryIntentActivities(intentCheck, PackageManager.MATCH_DEFAULT_ONLY).size() == 0) {
                    //installed scan app if not detected
                    Log.i(TAG, "未安裝");
                    IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                    integrator.initiateScan();
                }else {
                    //open scan app
                    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                    intent.putExtra("SCAN_MODE", "SCAN_MODE");
                    startActivityForResult(intent, SCAN_CODE);
                }
            }
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()){
//                    case MotionEvent.ACTION_DOWN:
//                        Intent intentCheck = new Intent("com.google.zxing.client.android.SCAN");
//                        if(getPackageManager().queryIntentActivities(intentCheck, PackageManager.MATCH_DEFAULT_ONLY).size() == 0) {
//                            //installed scan app if not detected
//                            Log.i(TAG, "未安裝");
//                            IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
//                            integrator.initiateScan();
//                        }else {
//                            //open scan app
//                            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
//                            intent.putExtra("SCAN_MODE", "SCAN_MODE");
//                            startActivityForResult(intent, SCAN_CODE);
//                        }
//                        break;
//                    default:
//                        break;
//                }
//                return false;
//            }
        });
    }

    private void generateWifiCode(){
        Button wifiCode = findViewById(R.id.generate_wifi_code);
        final Spinner spinner = findViewById(R.id.spinner);
        final String[] types = getResources().getStringArray(R.array.security_type);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                findViewById(R.id.pwd_text).setVisibility(types[position].equals(types[0])? View.INVISIBLE: View.VISIBLE );
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });

        wifiCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText ssid = findViewById(R.id.ssid_text);
                EditText pwd = findViewById(R.id.pwd_text);
                String ssidText = ssid.getText().toString();
                String pwdText = pwd.getText().toString();
                CheckBox isHideCheckbox = findViewById(R.id.is_hide_ssid);
                if(ssidText.length() > 0){
                    String qrString = null;
                    switch (spinner.getSelectedItem().toString()) {
                        case "Nano":
                            qrString = qrCodeUtil.getConfig("omit", ssidText, "", isHideCheckbox.isChecked());
                            break;
                        case "WEP":
                            if(pwdText.length() > 0) {
                                qrString = qrCodeUtil.getConfig("WEP", ssidText, pwdText, isHideCheckbox.isChecked());
                            }
                            break;
                        case "WPA/WPA2":
                            if(pwdText.length() > 0) {
                                //WPA and WPA2 different?
                                qrString = qrCodeUtil.getConfig("WPA2", ssidText, pwdText, isHideCheckbox.isChecked());
                            }
                            break;
                        default:
                            break;
                    }

                    if(qrString == null) return;
                    qrCode.setImageBitmap(
                            qrCodeUtil.textToImageEncode(
                                    new WeakReference<Context>(MainActivity.this),
                                    qrString,
                                    getCodeDimension()
                            )
                    );
                }
            }
        });
    }

    private int getCodeDimension(){
        return getResources().getDisplayMetrics().widthPixels / 2;
    }

    private void scanToConnectWifi(){
        scannedSSID = findViewById(R.id.scaned_ssid);
        Button scanToConnectWifi = findViewById(R.id.scan_to_connect_wifi);
        scanToConnectWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCheck = new Intent("com.google.zxing.client.android.SCAN");
                if(getPackageManager().queryIntentActivities(intentCheck, PackageManager.MATCH_DEFAULT_ONLY).size() == 0) {
                    //installed scan app if not detected
                    Log.i(TAG, "未安裝");
                    IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                    integrator.initiateScan();
                }else {
                    //open scan app
                    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                    intent.putExtra("SCAN_MODE", "WIFI_SCAN_MODE");
                    startActivityForResult(intent, WIFI_SCAN_CODE);
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        //get scan result
        if(resultCode == RESULT_OK && intent != null){

            switch (requestCode){
                case SCAN_CODE:
                    scannedText.setText(intent.getStringExtra("SCAN_RESULT") + ":" + intent.getStringExtra("SCAN_RESULT_FORMAT"));
                    break;
                case WIFI_SCAN_CODE:
//                    scannedText.setText(intent.getStringExtra("SCAN_RESULT"));
                    if(intent.getStringExtra("SCAN_RESULT").startsWith("WIFI:")) {
                        String[] params = qrCodeUtil.getWifiConfig(intent.getStringExtra("SCAN_RESULT"));
                        scannedSSID.setText(String.format(getString(R.string.connect_detail), params[0], params[1]));
                        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        qrCodeUtil.connectToWifi(wifiManager, params[0], params[1], params[2], Boolean.parseBoolean(params[3]));
                    }

                    break;
                default:
                    break;
            }
        }
    }

}
