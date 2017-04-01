package com.emalert;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.SensorEventListener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Locale;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

/**
 * Created by Sobhini on 26-03-2017.
 */


public class whistle extends Service implements  LocationListener {
    @Nullable

    String msg = "";
    SharedPreferences sh;
    Handler hand;
    LocationManager lm;

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public void onCreate() {
        hand = new Handler();
        sh = getSharedPreferences("emAlert", Context.MODE_PRIVATE);
        fetch();
        super.onCreate();
    }

    private void runOnUiThread(Runnable runnable)
    {
        hand.post(runnable);
    }

    @Override
    public void onStart(Intent intent, int startid) {



        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050,1024,0);
        PitchDetectionHandler pdh = new PitchDetectionHandler() {
            @Override
            public void handlePitch(PitchDetectionResult result, AudioEvent e)
            {
                final float pitchInHz = result.getPitch();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {

                        if(pitchInHz >= 1318.51 &&  pitchInHz <	1567.98)
                        {
                            Toast.makeText(getApplicationContext(), "Play Hela", Toast.LENGTH_LONG).show();
                            alert(msg);
                            message("I've met with an raging, plz help me @ " + msg + "");
                            /*try {
                                Thread.sleep(1500);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }*/

                        }


                    }
                });
            }
        };
        AudioProcessor p = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, pdh);
        dispatcher.addAudioProcessor(p);
        new Thread(dispatcher,"Audio Dispatcher").start();
    }

    @Override
    public void onDestroy() {


        super.onDestroy();
    }

    private void alert(String st) {
        Toast.makeText(whistle.this, st, Toast.LENGTH_LONG).show();
    }

    private void message(String st) {

        for (int i = 1; i < 5; i++) {
            Log.i("Send SMS ", "");
            String number = sh.getString("rag" + i, null);
            String message = st;
            if (!number.equals("null"))
            {
                try {
                    SmsManager sm = SmsManager.getDefault();
                    sm.sendTextMessage(number, null, message, null, null);
                    Toast.makeText(whistle.this, "SMS Sent... ", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(whistle.this, "SMS Sending Failed..", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                alert("Sent: " + sh.getString("rag" + i, null) + " @ " + st);
            }



        }
    }

    private void fetch() {
        //showpDialog();
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //Permission Check..
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
    }
    @Override
    public void onLocationChanged(Location location) {
        try {

        } catch (Exception e) {
        }
        msg = "maps.google.com/?q=" + location.getLatitude() + "," + location.getLongitude();
        //alert("I've met with an accident, plz help me @ " + msg + "");
        //toast("Lat: "+location.getLatitude()+ " Lon: "+location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(getBaseContext(), "GPS turned on!! ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
        Toast.makeText(getBaseContext(), "Turn the GPS On ", Toast.LENGTH_SHORT).show();
    }






}


