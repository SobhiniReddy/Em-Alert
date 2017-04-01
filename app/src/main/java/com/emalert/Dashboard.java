package com.emalert;
/**
 * Created by Sobhini on 10/13/2016.
 */
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class Dashboard extends AppCompatActivity {

    TextView usr,role;
    Button bnoty,bcall,bck,bl;
    String rol,cat_val;
    RequestQueue rq;
    JsonObjectRequest jor;
    ProgressDialog p;
    SharedPreferences sh;
    AlertDialog.Builder a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        usr = (TextView)findViewById(R.id.greet);
        role = (TextView)findViewById(R.id.role);



        bnoty = (Button)findViewById(R.id.bnotify);
        bcall = (Button)findViewById(R.id.bcall);
        bck = (Button)findViewById(R.id.bcheck);
        bl = (Button)findViewById(R.id.blogout);

        sh = getSharedPreferences("emAlert", Context.MODE_PRIVATE);
        usr.setText("Howdy "+sh.getString("user",null)+",");
        cat_val=sh.getString("cat",null);
        p = new ProgressDialog(Dashboard.this);
        p.setMessage("Please wait...");
        p.setCancelable(false);
        fetch();
        Calendar cal = Calendar.getInstance();
        Intent i=new Intent(getApplicationContext(), Service_Emp.class);
        PendingIntent pi= PendingIntent.getService(getApplicationContext(), 0, i,0);
        getApplicationContext();
        AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 5*1000, pi);
        startService(i);


        bnoty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showManual();
            }
        });

        bcall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAuto();
            }
        });

        bl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor ed;
                ed=sh.edit();
                ed.clear();
                ed.commit();
                finish();
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);

            }
        });
        bck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),Emp_Activity.class);
                startActivity(i);
            }
        });
    }

    private void toast(String st)
    {
        Toast.makeText(getApplicationContext(),st,Toast.LENGTH_LONG).show();
    }

    private void fetch()
    {
        showpDialog();
        rq= Volley.newRequestQueue(getApplicationContext());
        String url="http://emalert.hol.es/index.php?user="+sh.getString("user",null);
        jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            rol= response.getString("role");
                            if(rol.equals("stud"))
                                role.setText("Role: Student");
                            else
                                role.setText("Role: Employee");
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
    }
    private void showpDialog() {
        if (!p.isShowing())
            p.show();
    }

    private void hidepDialog() {
        if (p.isShowing())
            p.dismiss();
    }

    private void showManual() {
        a= new AlertDialog.Builder(this);
        final String as[],asv[];

        as = new String[]{"1st year","2nd year","3rd year","4th year"};
        asv=new String[]{"1yr","2yr","3yr","4yr"};

        a.setTitle("Notification For:")
                .setItems(as, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"You have clicked "+as[which],Toast.LENGTH_LONG).show();
                    //    noti(asv[which],0);
                        final String value=asv[which];
                        switch(as[which]){
                            default:
                                AlertDialog.Builder alert2 = new AlertDialog.Builder(Dashboard.this);
                                alert2.setTitle("Type ur message");
                                final EditText input = new EditText(Dashboard.this);
                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.MATCH_PARENT);
                                input.setLayoutParams(lp);
                                alert2.setView(input);
                                alert2.setPositiveButton("Send",new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                       // Toast.makeText(getApplicationContext(),input.getText().toString(),Toast.LENGTH_LONG).show();
                                       noti(value,0,input.getText().toString());
                                        dialog.dismiss();
                                    }
                                });
                                alert2.setNeutralButton("Close", null);

                                alert2.show();
                                return;


                        }
                    }
                });
        a.setNegativeButton("Cancel", null);
        a.show();
        AlertDialog ab = a.create();
        ab.show();
    }

    public void noti(final String yr,int c,String msg){
        showpDialog();
        String url;
        rq= Volley.newRequestQueue(getApplicationContext());
        if (c==0){
            url="http://emalert.hol.es/index.php?year="+yr+"&type"+"&msg="+msg;
            url = url.replaceAll(" ", "%20");
        }
        else{
            url="http://emalert.hol.es/index.php?grp="+yr+"&type"+"&msg="+msg;
            url = url.replaceAll(" ", "%20");
        }

        jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String chk = response.getString("check");
                            if(chk.equals("True"))
                                toast("Successfully notified "+yr+" year");
                                //noti walla service employee
                                //Intent ser1 = new Intent(this,myService.class);
                                //startService(ser1);
                            else
                                toast("failed to notified "+yr+" year");
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

    }


    private void showAuto() {
        a= new AlertDialog.Builder(this);
        final String as[],asv[];

        as = new String[]{"Group A","Group B","Group C"};
        asv= new String[]{"GrA","GrB","GrC"};

        a.setTitle("Notification For:")
                .setItems(as, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"You have clicked "+as[which],Toast.LENGTH_LONG).show();
                       // noti(asv[which],1);
                        final String value=asv[which];
                        switch(as[which]){
                            default:
                                AlertDialog.Builder alert2 = new AlertDialog.Builder(Dashboard.this);
                                alert2.setTitle("Type ur message");
                                final EditText input = new EditText(Dashboard.this);
                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.MATCH_PARENT);
                                input.setLayoutParams(lp);
                                alert2.setView(input);
                                alert2.setPositiveButton("Send",new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // Toast.makeText(getApplicationContext(),input.getText().toString(),Toast.LENGTH_LONG).show();
                                        noti(value,1,input.getText().toString());
                                        dialog.dismiss();
                                    }
                                });
                                alert2.setNeutralButton("Close", null);
                                alert2.show();
                                return;


                        }
                    }
                });
        a.setNegativeButton("Cancel", null);
        a.show();
        AlertDialog ab = a.create();
        ab.show();
    }
}
