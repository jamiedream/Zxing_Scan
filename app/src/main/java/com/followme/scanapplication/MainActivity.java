package com.followme.scanapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.followme.zxing_android.android_integration.IntentIntegrator;


public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private final int SCAN_CODE = 30;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView click = findViewById(R.id.click);
        click.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
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
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        //get scan result
        if(requestCode == SCAN_CODE && resultCode == RESULT_OK){

            String contents = intent.getStringExtra("SCAN_RESULT");
            String format = intent.getStringExtra("SCAN_RESULT_FORMAT");

            Log.i(TAG, contents + " : " + format);

        }
    }
}
