 package com.emalert;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;
/**
 * Created by Sobhini on 2/19/2017.
 */
public class Service_Emp extends Service {

    String user,role;
    Boolean nchk;

    SharedPreferences sh;
    ProgressDialog p;
    RequestQueue rq;

    JsonObjectRequest jor;
    @Override
    public void onCreate() {
        super.onCreate();
        sh = getSharedPreferences("emAlert", Context.MODE_PRIVATE);
        //  p = new ProgressDialog(myService.this);
        //  p.setMessage("Fetching loc...");
        //  p.setCancelable(false);
       user= sh.getString("user",null);
       role= sh.getString("role",null);


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       noti(user,role);
        return Service.START_STICKY;
    }

    private void show_noti() {
        PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, Noti.class), 0);
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("New Notification")
                .setContentText("you have been noti")
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    private void toast(String st)
    {
        Toast.makeText(getApplicationContext(),st,Toast.LENGTH_LONG).show();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    private void noti( String user , String role)
    {
        rq = Volley.newRequestQueue(getApplicationContext());
        String url="http://emalert.hol.es/index.php?user="+user+
                "&role="+role;
        toast(url);
        jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String noti= response.getString("notify");
                            String chk= response.getString("check");

                            if(chk.equals("True") ) {
                                if (noti.equals("yes"))
                                    show_noti();
                            }
                        }
                        catch (JSONException e){
                            toast(e.toString());
                        }
                       // hidepDialog();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley",error.toString());
                        toast(error.toString());
                    }
                });

        rq.add(jor);
    }
}



