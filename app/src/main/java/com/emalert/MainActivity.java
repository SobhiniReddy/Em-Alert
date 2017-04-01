package com.emalert;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    Button bl,br;
    EditText user,pass;
    ProgressDialog pd;
    RequestQueue rq;
    JsonObjectRequest jor;
    SharedPreferences sh;
    SharedPreferences.Editor ed;
    AnimationDrawable animationDrawable;
    RelativeLayout relativeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        relativeLayout = (RelativeLayout)findViewById(R.id.r);
        animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(5000);
        animationDrawable.setExitFadeDuration(2000);


        bl=(Button)findViewById(R.id.login);
        br=(Button)findViewById(R.id.register);
        user=(EditText)findViewById(R.id.username);
        pass=(EditText)findViewById(R.id.password);
        RelativeLayout rl = (RelativeLayout)findViewById(R.id.r);
      //  rl.setBackgroundColor(Color.RED);
        //        rl.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        sh=getSharedPreferences("emAlert", Context.MODE_PRIVATE);
        ed=sh.edit();
       //Accelerometer sevice...
        Intent ser = new Intent(this,myService.class);
        startService(ser);
        //noti walla service employee
        //gesture walla service

        //whistle walla service

            try{
            if(!sh.getString("user",null).equals(null))
            {

                if (sh.getString("role",null).equals("stud")) {
                    Intent i = new Intent(MainActivity.this, Student_Board.class);
                    startActivity(i);
                    finishAffinity();
                }
                else
                {
                    Intent i1 = new Intent(MainActivity.this,Dashboard.class);
                    startActivity(i1);
                    finishAffinity();
                }
            }
        }

        catch (Exception e){}
        bl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pd = new ProgressDialog(MainActivity.this);
                pd.setMessage("Please wait...");
                pd.setCancelable(false);
                check();
            }
        });



        br.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,Register.class);
                startActivity(i);
                finishAffinity();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        animationDrawable.start();
    }
    private void toast(String st)
    {
        Toast.makeText(getApplicationContext(),st,Toast.LENGTH_LONG).show();
    }
    private void check()
    {
        showpDialog();
        rq = Volley.newRequestQueue(getApplicationContext());
        String url="http://emalert.hol.es/index.php?user="+user.getText().toString()+
                "&pass="+pass.getText().toString();
        url = url.replaceAll(" ", "%20");
        jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String chk= response.getString("check");
                            String role= response.getString("role");

                         if(chk.equals("True")) {
                                toast("Successfull Login");
                                ed.putString("user", user.getText().toString());
                                ed.putString("role", role);
                                ed.commit();

                                if (role.equals("stud")) {
                                    Intent i = new Intent(MainActivity.this, Student_Board.class);
                                    startActivity(i);
                                    finishAffinity();
                                }
                                else
                                {
                                    Intent i1 = new Intent(MainActivity.this,Dashboard.class);
                                    startActivity(i1);
                                    finishAffinity();
                                }


                            }
                            else
                            
                                toast("Failed");
                        }
                        catch (JSONException e){
                            toast("Wrong Credentials");
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
        if (!pd.isShowing())
            pd.show();
    }

    private void hidepDialog() {
        if (pd.isShowing())
            pd.dismiss();
    }


}
