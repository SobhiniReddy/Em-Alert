package com.emalert;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
/**
 * Created by Sobhini on 1/8/2017.
 */
public class myService extends Service implements SensorEventListener, LocationListener {

    String msg = "";
    ProgressDialog p;
    SharedPreferences sh;
    LocationManager lm;
    int count = 1;
    private boolean init;
    private Sensor mySensor;
    private SensorManager SM;
    private float x1, x2, x3;
    private static final float ERROR = (float) 7.0;
    private static final float SHAKE_THRESHOLD = 15.00f; // m/S**2
    private static final int MIN_TIME_BETWEEN_SHAKES_MILLISECS = 1000;
    private long mLastShakeTime;
    private TextView counter;
    int check;
    AudioManager audioManager;
    TelephonyManager manager;
    StatePhoneReceiver myPhoneStateListener;
    boolean callFromApp=false; // To control the call has been made from the application
    boolean callFromOffHook=false; // To control the change to idle state is from the app call

    @Override
    public void onCreate() {
        super.onCreate();

        myPhoneStateListener = new StatePhoneReceiver(this);
        manager = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE));

        sh = getSharedPreferences("emAlert", Context.MODE_PRIVATE);
        //  p = new ProgressDialog(myService.this);
        //  p.setMessage("Fetching loc...");
        //  p.setCancelable(false);
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        fetch();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Start Detecting", Toast.LENGTH_LONG).show();
        SM = (SensorManager) getSystemService(SENSOR_SERVICE);
        mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        SM.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);


        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();
            if ((curTime - mLastShakeTime) > MIN_TIME_BETWEEN_SHAKES_MILLISECS) {

                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                double acceleration = Math.sqrt(Math.pow(x, 2) +
                        Math.pow(y, 2) +
                        Math.pow(z, 2)) - SensorManager.GRAVITY_EARTH;
                //  Log.d("mySensor", "Acceleration is " + acceleration + "m/s^2");

                if (acceleration > SHAKE_THRESHOLD) {
                    mLastShakeTime = curTime;
                    Toast.makeText(getApplicationContext(), "FALL DETECTED",
                            Toast.LENGTH_LONG).show();
                    new CountDownTimer(30000, 1000) {

                        public void onTick(long millisUntilFinished) {

                        }

                        public void onFinish() {
                            if (check == 0) {
                                  alert(msg);
                                  message("I've met with an accident, plz help me @ " + msg + "");
                                call();

                            }
                        }
                    }.start();


                    AlertDialog ad = new AlertDialog.Builder(myService.this)
                            .setTitle("Are you ok !")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    check = 0;
                                    //p = new ProgressDialog(myService.this);
                                    //p.setMessage("Fetching loc...");
                                    //p.setCancelable(false);
                                    // fetch();
                                    alert(msg);
                                    message("I've met with an accident, plz help me @ " + msg + "");
                                    call();

                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    check = 1;
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .create();
                    ad.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
                    ad.show();
                }
            }
        }
    }

    private void message(String st) {

        for (int i = 1; i < 5; i++) {
            Log.i("Send SMS ", "");
            String number = sh.getString("aci" + i, null);
            String message = st;

            try {
                SmsManager sm = SmsManager.getDefault();
                sm.sendTextMessage(number, null, message, null, null);
                Toast.makeText(myService.this, "SMS Sent... ", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(myService.this, "SMS Sending Failed..", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }


            alert("Sent: " + sh.getString("aci" + i, null) + " @ " + st);

        }
    }

    private void call() {
        //
        Log.i("Call ", "");
       // for (int i = 1; i < 5; i++) {
            String number = sh.getString("aci" + 1, null);
            try {
            /*String uri = "tel:"+number;
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse(uri));*/



                Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                phoneIntent.setData(Uri.parse("tel:" + number));
                phoneIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                manager.listen(myPhoneStateListener,
                        PhoneStateListener.LISTEN_CALL_STATE); // start listening to the phone changes
                callFromApp=true;

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                startActivity(phoneIntent);
                Toast.makeText(myService.this, "Calling... ", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(myService.this, "no" + e.toString(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }



    private void alert(String st) {
        Toast.makeText(myService.this, st, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

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
            hidepDialog();
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

    private void showpDialog() {
        if (!p.isShowing())
            p.show();
    }

    private void hidepDialog() {
        if (p.isShowing())
            p.dismiss();
    }
    // Monitor for changes to the state of the phone
    public class StatePhoneReceiver extends PhoneStateListener {
        Context context;
        public StatePhoneReceiver(Context context) {
            this.context = context;
        }

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);

            switch (state) {

                case TelephonyManager.CALL_STATE_OFFHOOK: //Call is established
                    if (callFromApp) {
                        callFromApp=false;
                        callFromOffHook=true;

                        try {
                            Thread.sleep(500); // Delay 0,5 seconds to handle better turning on loudspeaker
                        } catch (InterruptedException e) {
                        }

                        //Activate loudspeaker
                        AudioManager audioManager = (AudioManager)
                                getSystemService(Context.AUDIO_SERVICE);
                        audioManager.setMode(AudioManager.MODE_IN_CALL);
                        audioManager.setSpeakerphoneOn(true);
                    }
                    break;

                case TelephonyManager.CALL_STATE_IDLE: //Call is finished
                    if (callFromOffHook) {
                        callFromOffHook=false;
                        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                        audioManager.setMode(AudioManager.MODE_NORMAL); //Deactivate loudspeaker
                        manager.listen(myPhoneStateListener, // Remove listener
                                PhoneStateListener.LISTEN_NONE);
                    }
                    break;
            }
        }
    }

}
