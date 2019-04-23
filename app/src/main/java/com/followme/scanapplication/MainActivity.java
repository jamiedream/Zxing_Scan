package com.followme.scanapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        qrCodeUtil = new QRCodeUtil();

        scanGetText();
        generateWifiCode();
        scanToConnectWifi();
//        scanToConnectedWifi();

        Button generateConnectedWifiCode = findViewById(R.id.generate_connected_wifi_code);
        generateConnectedWifiCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, types);
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
                ImageView qrCode = findViewById(R.id.qr_code_preview);
                String qrString = "";
                if(ssidText.length() > 0){
                    switch (spinner.getSelectedItem().toString()) {
                        case "Nano":
                            qrString = qrCodeUtil.getConfig("omit", ssidText, "", isHideCheckbox.isChecked());
                            break;
                        case "WEP":
                            if(pwdText.length() > 0) {
                                qrString = qrCodeUtil.getConfig("WEP", ssidText, pwdText, isHideCheckbox.isChecked());
                            }else qrString = "";
                            break;
                        case "WPA/WPA2":
                            if(pwdText.length() > 0) {
                                qrString = qrCodeUtil.getConfig("WPA", ssidText, pwdText, isHideCheckbox.isChecked());
                            }else qrString = "";
                            break;
                        default:
                            qrString = "";
                            break;
                    }

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
        if(resultCode == RESULT_OK){

            switch (requestCode){
                case SCAN_CODE:
                    scannedText.setText(intent.getStringExtra("SCAN_RESULT") + ":" + intent.getStringExtra("SCAN_RESULT_FORMAT"));
                    break;
                case WIFI_SCAN_CODE:
//                    scannedText.setText(intent.getStringExtra("SCAN_RESULT"));

                    String[] params = qrCodeUtil.getWifiConfig(intent.getStringExtra("SCAN_RESULT"));
                    scannedSSID.setText("Connecting to SSID...... " + params[0]);
                    WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    qrCodeUtil.connectToWifi(wifiManager, params[0], params[1], params[2], Boolean.parseBoolean(params[3]));

                    break;
                default:
                    break;
            }
        }
    }

}
