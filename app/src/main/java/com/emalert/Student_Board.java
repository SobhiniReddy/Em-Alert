package com.emalert;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Sobhini on 9/19/2016.
 */

public class Student_Board extends AppCompatActivity implements LocationListener {

    Button log, con, req_emp;
    CircleImageView img;
    String msg = "",user,number,tag;
    ProgressDialog p;
    SharedPreferences sh;
    LocationManager lm;
    RequestQueue rq;
    JsonObjectRequest jor;
    int pointer=0;
    String menus[]={"Ragging Scene","Violent Scene","Natural Calamity","Being Kidnapped","Accidental Scene"};
    int imgRes[]={R.drawable.rag,R.drawable.vio,R.drawable.nat,R.drawable.kid,R.drawable.aci};
    TextView label;
    public SensorManager sensorManager;
    public Sensor accelerometer;
    public Vibrator v;
    private float vibrateThreshold = 0;

    private float lastX, lastY, lastZ;
    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;
    AudioManager a;
    //Spinner spinner;
    String grp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student__board);
        img = (CircleImageView) findViewById(R.id.picid);
        label = (TextView) findViewById(R.id.textView4);
        log = (Button) findViewById(R.id.log);
        con = (Button) findViewById(R.id.con);
        req_emp = (Button) findViewById(R.id.Req_emp);

        sh = getSharedPreferences("emAlert", Context.MODE_PRIVATE);
        p = new ProgressDialog(Student_Board.this);
        p.setMessage("Fetching loc...");
        p.setCancelable(false);



//         CircleImageView pic = (de.hdodenhof.circleimageview.CircleImageView)findViewById(R.id.picid);


        fetch();
        grp=sh.getString("cat",null);
        user=sh.getString("user",null);

        Intent ser2 = new Intent(this,whistle.class);
        startService(ser2);
        Calendar cal = Calendar.getInstance();
        Intent i=new Intent(getApplicationContext(), Service_Emp.class);
        PendingIntent pi= PendingIntent.getService(getApplicationContext(), 0, i,0);
        getApplicationContext();
        AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 5*1000, pi);
        startService(i);

        img.setOnTouchListener(new OnSwipeTouchListener(Student_Board.this) {
            public void onSwipeTop() {

            }
            public void onSwipeRight() {

                if(pointer!=0)
                    pointer -=1;
                else
                    pointer = 4;
                label.setText(menus[pointer]);
                img.setImageResource(imgRes[pointer]);
                Toast.makeText(Student_Board.this, "Previous", Toast.LENGTH_SHORT).show();

            }

            public void onSwipeLeft() {
                if(pointer!=4)
                    pointer +=1;
                else
                    pointer =0;

                label.setText(menus[pointer]);
                img.setImageResource(imgRes[pointer]);
                Toast.makeText(Student_Board.this, "Next", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeBottom() {

            }

            public boolean onTouch(View v, MotionEvent event) {
                Toast.makeText(Student_Board.this, "You have clicked "+menus[pointer], Toast.LENGTH_SHORT).show();
                if(menus[pointer].equals("Ragging Scene"))
                {
                    p = new ProgressDialog(Student_Board.this);
                    p.setMessage("Please wait...");
                    p.setCancelable(false);
                    fetch();
                    msg = "Some One is getting Ragged @ " + msg;
                    message(msg,1);
                    //   Log.d("Ragging: ", msg);
                    msg = "";
                }

                else if(menus[pointer].equals("Violent Scene"))
                {
                    p = new ProgressDialog(Student_Board.this);
                    p.setMessage("Please wait...");
                    p.setCancelable(false);
                    fetch();
                    msg = "Violence Activity @ " + msg;
                    message(msg,4);
                    // Log.d("Violence: ", msg);
                    msg = "";
                }

                else if(menus[pointer].equals("Natural Calamity"))
                {
                    p = new ProgressDialog(Student_Board.this);
                    p.setMessage("Please wait...");
                    p.setCancelable(false);
                    fetch();
                    msg = "Natural Calamity @ " + msg;
                    message(msg,5);
                    // Log.d("Natural: ", msg);
                    msg = "";
                }

                else if(menus[pointer].equals("Being Kidnapped"))
                {
                    p = new ProgressDialog(Student_Board.this);
                    p.setMessage("Please wait...");
                    p.setCancelable(false);
                    fetch();
                    msg = "I Got Kidnapped @ " + msg;
                    message(msg,3);
                    // Log.d("Kidnapped: ", msg);
                    msg = "";
                }


                else if(menus[pointer].equals("Accidental Scene"))
                {
                    p = new ProgressDialog(Student_Board.this);
                    p.setMessage("Please wait...");
                    p.setCancelable(false);
                    fetch();
                    msg = "I met with an accident Took Place @ " + msg;
                    message(msg,2);
                    // Log.d("Accident: ", msg);
                    msg = "";
                }

                return gestureDetector.onTouchEvent(event);
            }
        });


        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor ed;
                ed = sh.edit();
                ed.clear();
                ed.commit();
                finish();
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);

            }
        });

        con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),Contacts.class);
                startActivity(i);
            }
        });

        req_emp.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog d = new Dialog(Student_Board.this);
                d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                d.setCancelable(true);
                d.setContentView(R.layout.request);
                final EditText p;
                p = (EditText) d.findViewById(R.id.req_msg);
                final Spinner spinner=(Spinner)d.findViewById(R.id.spinner);
                final Spinner spinner2=(Spinner)d.findViewById(R.id.spinner2);
                Button temp = (Button) d.findViewById(R.id.send);

                ArrayList<String> arrayList = new ArrayList<String>();
                final String asv[]= new String[]{"GrA","GrB","GrC"};
                //mapping
                arrayList.add("Group A");
                arrayList.add("Group B");
                arrayList.add("Group C");
                ((TextView) v).setTextColor(Color.RED);
                ArrayAdapter<String> ad = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_item,arrayList);
                ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(ad);


                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        final String sp1= asv[position];

                     //   Toast.makeText(getApplicationContext(), sp1, Toast.LENGTH_SHORT).show();
                        final ArrayList<String> arrayList_empname = new ArrayList<String>();
                     //   Toast.makeText(getApplicationContext(), sp2, Toast.LENGTH_SHORT).show();
                     //   showpDialog();
                        rq= Volley.newRequestQueue(getApplicationContext());
                        String url="http://emalert.hol.es/index.php?grp="+sp1;
                        url = url.replaceAll(" ", "%20");
                        toast("URL"+url);
                        JsonArrayRequest jar = new JsonArrayRequest(Request.Method.GET, url, null,
                                new Response.Listener<JSONArray>() {
                                    @Override
                                    public void onResponse(JSONArray response) {
                                        ArrayList<String> st = new ArrayList<String>();
                                        try {


                                            Log.d("JsonArray",response.toString());
                                            for(int i = 0; i < response.length(); i++) {
                                                JSONObject jresponse = response.getJSONObject(i);
                                                String username = jresponse.getString("username");
                                                st.add(username);
                                              //  Toast.makeText(getApplicationContext(), username, Toast.LENGTH_LONG).show();
                                              //  Log.d("username", username);
                                            }
                                            ArrayAdapter<String> ad1 = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_item,st);
                                            ad1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            spinner2.setAdapter(ad1);

                                        }catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }


                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.e("Volley",error.toString());
                                        toast(error.toString());
                                    }
                                });

                        rq.add(jar);



                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }});
                spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }});
                    temp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final String sp2= String.valueOf(spinner2.getSelectedItem());
                            rq= Volley.newRequestQueue(getApplicationContext());
                            String url="http://emalert.hol.es/index.php?stu_name="+user+
                                    "&msg="+p.getText().toString()+"&emp_name="+ sp2;
                            toast("URL"+url);
                            jor = new JsonObjectRequest(Request.Method.GET, url, null,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            try {
                                                String chk= response.getString("check");
                                                if(chk.equals("True"))
                                                {
                                                    toast("Your Request Sent....");
                                                    //finish();
                                                }
                                                else
                                                    toast("Failed");
                                            }
                                            catch (JSONException e){
                                                toast(e.toString());
                                            }
                                            hidepDialog();
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
                            alert("Saved !!");
                            d.hide();
                        }
                    });

                d.show();
            }
                });

    }
    private void toast(String st)
    {
        Toast.makeText(getApplicationContext(),st,Toast.LENGTH_LONG).show();
    }




/*    private void calling() {
        for (int i = 1; i < 5; i++) {

            Log.i("Send SMS ", "");
            String number = sh.getString("p" + i, null);

            Intent in = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+number));
            try {
                startActivity(in);
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(getApplicationContext(), "your  Activity is not found", Toast.LENGTH_SHORT).show();
            }
        a = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        a.setMode(AudioManager.MODE_IN_CALL);
        a.setSpeakerphoneOn(true);
        }
    }
*/



    private void message(String st, int t) {
        for (int i = 1; i < 5; i++) {

            switch (t)
            {
                case 1:
                    String number = sh.getString("rag" + i, null);
                    String message = st;
                    try {
                        SmsManager sm = SmsManager.getDefault();
                        sm.sendTextMessage(number, null, message, null, null);
                        Toast.makeText(getApplicationContext(), "SMS Sent... ", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "SMS Sending Failed..", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    alert("Sent: " + sh.getString("rag" + i, null) + " @ " + st);


                    break;

                case  2:
                    String number1 = sh.getString("aci" + i, null);
                    String message1 = st;
                    try {
                        SmsManager sm = SmsManager.getDefault();
                        sm.sendTextMessage(number1, null, message1, null, null);
                        Toast.makeText(getApplicationContext(), "SMS Sent... ", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "SMS Sending Failed..", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    alert("Sent: " + sh.getString("aci" + i, null) + " @ " + st);
                    break;

                case  3:
                    String number2 = sh.getString("kid" + i, null);
                    String message2 = st;
                    try {
                        SmsManager sm = SmsManager.getDefault();
                        sm.sendTextMessage(number2, null, message2, null, null);
                        Toast.makeText(getApplicationContext(), "SMS Sent... ", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "SMS Sending Failed..", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    alert("Sent: " + sh.getString("kid" + i, null) + " @ " + st);
                    break;

                case  4:
                    String number3 = sh.getString("vio" + i, null);
                    String message3 = st;
                    try {
                        SmsManager sm = SmsManager.getDefault();
                        sm.sendTextMessage(number3, null, message3, null, null);
                        Toast.makeText(getApplicationContext(), "SMS Sent... ", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "SMS Sending Failed..", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    alert("Sent: " + sh.getString("vio" + i, null) + " @ " + st);
                    break;

                case  5:
                    String number4 = sh.getString("vio" + i, null);
                    String message4 = st;
                    try {
                        SmsManager sm = SmsManager.getDefault();
                        sm.sendTextMessage(number4, null, message4, null, null);
                        Toast.makeText(getApplicationContext(), "SMS Sent... ", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "SMS Sending Failed..", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    alert("Sent: " + sh.getString("vio" + i, null) + " @ " + st);
                    break;
            }
        }

    }
    private void alert(String st) {
        Toast.makeText(getApplicationContext(), st, Toast.LENGTH_LONG).show();
    }

    private void fetch() {
        showpDialog();
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

}























/*
  @Override
                                    public void onResponse(JSONObject response) {


                                    }
try {
                                            JSONArray   ja = response.getJSONArray("");
                                            ArrayList<String> st = new ArrayList<String>();
                                            for (int i = 0; i < ja.length(); i++) {
                                                JSONArray jsonObject = ja.getJSONArray(i);
                                                st.add(jsonObject.getString(i));
                                             //   Toast.makeText(getApplicationContext(), jsonObject.getString("username"), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                        catch (JSONException e){
                                            toast(e.toString());
                                        }
                                        hidepDialog();*/