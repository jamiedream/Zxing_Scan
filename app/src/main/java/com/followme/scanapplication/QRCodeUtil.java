/*
 * Copyright (c) 2019.
 * Create by J.Y Yen 26/ 4/ 2019.
 */

package com.followme.scanapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.followme.zxing_android_integration.IntentIntegrator;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.lang.ref.WeakReference;
import java.util.List;

public class QRCodeUtil {

    private String TAG = QRCodeUtil.class.getSimpleName();

    /**
     * Decode
     *
     * Parameters refer to Intents.java final class SCAN
     * */
    private final String ACTION = "com.google.zxing.client.android.SCAN";
    private final int SCAN_CODE = 30;
    private final String SCAN_RESULT = "SCAN_RESULT";
    private final String SCAN_RESULT_FORMAT = "SCAN_RESULT_FORMAT";

    public void intentToInstalledApp(WeakReference<Activity> activity){
        IntentIntegrator integrator = new IntentIntegrator(activity.get());
        integrator.initiateScan();
    }

    public void openApp(WeakReference<Activity> activity){
        Intent intent = new Intent(ACTION);
        activity.get().startActivityForResult(intent, SCAN_CODE);
    }

    public int getScanCode(){
        return SCAN_CODE;
    }

    public String getScaneResult(Intent intent){
        return intent.getStringExtra(SCAN_RESULT);
    }

    public String getScaneResultFormat(Intent intent){
        return intent.getStringExtra(SCAN_RESULT_FORMAT);
    }

    /**
     * Encode
     * */

    public Bitmap textToImageEncode(WeakReference<Context> context, String value, int dimen){

        int width = dimen;
        int height = dimen;
        BitMatrix bitMatrix = null;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    value,
                    BarcodeFormat.QR_CODE,
                    width,
                    height);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        int[] pixels = new int[width * height];
        for(int numY = 0; numY < height; numY ++){
            int offset = numY * width;
            for(int numX = 0; numX < width; numX ++){
                pixels[offset + numX] = ContextCompat.getColor(context.get(), bitMatrix.get(numX, numY)? R.color.black : R.color.white);
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    /**
     * Wifi config
     *
     * */

    public String getConfig(String type, String ssid, String pwd, boolean isHide){

        if(isHide){
            return String.format("WIFI:T:%1$s;S:%2$s;P:%3$s;;", type, ssid, pwd);
        }else{
            return String.format("WIFI:T:%1$s;S:%2$s;P:%3$s;H:true;;", type, ssid, pwd);
        }

    }

    /**
     * Wifi parameters
     * @param callbackString: 0: ssid, 1:pwd, 2:type, 3:isHide
     * */
    public String[] getWifiConfig(String callbackString){
        String[] parameters = new String[4];

        String[] strings = callbackString.split(";");

//        String ssid = "";
//        String pwd = "";
//        String type = "";
//        boolean isHide = false;

        String SSID = "S:";
        String PWD = "P:";
        String TYPE = "T:";
        String HIDE = "H:";

        for(String s: strings){
            if(s.contains(SSID)){
                parameters[0] = subStringParameter(s, SSID);
//                ssid = subStringParameter(s, SSID);
            }else if(s.contains(PWD)){
                parameters[1] = subStringParameter(s, PWD);
//                pwd = subStringParameter(s, PWD);
            }else if(s.contains(TYPE)){
                parameters[2] = subStringParameter(s, TYPE);
//                type = subStringParameter(s, TYPE);
            }else if(s.contains(HIDE)){
                parameters[3] = subStringParameter(s, HIDE);
//                isHide = Boolean.parseBoolean(subStringParameter(s, HIDE));
            }else if(!s.contains(HIDE)){
                parameters[3] = "false";

            }
        }

        return parameters;
    }

    private String subStringParameter(String string, String para){
        int start;
        int end;
        start = string.indexOf(para) + 2;
        end = string.length();
        return string.substring(start, end);
    }

    /**
     * Connect to specific wifi
     * */
    public void connectToWifi(WifiManager wifiManager, String ssid, String pwd, String type, boolean isHide){

        Log.i(TAG, ssid + ":" + pwd + ":" + type + ":" + isHide);
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = "\"" + ssid + "\"";

        switch (type){
            case "Nano":
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                break;
            case "WEP":
                config.wepKeys[0] = "\"" + pwd + "\"";
                config.wepTxKeyIndex = 0;
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                break;
            case "WPA":
            case "WPA2":
                config.preSharedKey = "\"" + pwd + "\"";
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                config.status = WifiConfiguration.Status.ENABLED;
                break;
            default:
                break;
        }

        config.hiddenSSID = isHide;

        wifiManager.addNetwork(config);

        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for( WifiConfiguration i : list ) {
            if (i.SSID != null && i.SSID.equals("\"" + ssid + "\"")) {
                Log.i(TAG, i.SSID);
                if(!wifiManager.isWifiEnabled()){
                    wifiManager.setWifiEnabled(true);
                }
                wifiManager.disconnect();
                Log.i(TAG, i.networkId + "");
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();
                break;
            }
        }
    }

}
