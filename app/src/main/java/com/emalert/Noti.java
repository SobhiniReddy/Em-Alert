package com.emalert;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
/**
 * Created by Sobhini on 2/5/2017.
 */
public class Noti extends AppCompatActivity {
    ProgressDialog pd;
    RequestQueue rq;
    JsonObjectRequest jor;
    SharedPreferences sh;
    SharedPreferences.Editor ed;
    String user,role,noti;
    Button show;
    EditText t;
    String msg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noti);
        sh=getSharedPreferences("emAlert", Context.MODE_PRIVATE);
        ed=sh.edit();
        user= sh.getString("user",null);
        role= sh.getString("role",null);

      //  noti=sh.getString("notify",null);
        t= (EditText) findViewById(R.id.editText);
        show = (Button)findViewById(R.id.button);
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                t.setText(msg);
            }
        });
        try{
            if(!sh.getString("user",null).equals(null))
            {

                if (sh.getString("role",null).equals("stud")) {

                    update(user,0);
                }
                else
                {
                    update(user,1);
                }
            }
        }
        catch (Exception e){}
    }

    private void toast(String st)
    {
        Toast.makeText(getApplicationContext(),st,Toast.LENGTH_LONG).show();
    }

    public void update(final String user,int c){
        String url;

        rq= Volley.newRequestQueue(getApplicationContext());
        if (c==0)
            url="http://emalert.hol.es/index.php?user="+user+"&updSt";
        else
            url="http://emalert.hol.es/index.php?user="+user+"&updEm";

        jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String chk = response.getString("check");
                            //String noti= response.getString("notify");
                            if(chk.equals("True")) {
                               // if (noti.equals("no"))
                                    toast("Successfully updated stu " + user + " user");
                               // else if (noti.equals("no"))
                             //       toast("Successfully updated emp " + user + " user");
                            }
                                else
                                    toast("failed to update " + user + " user");

                        }
                        catch (JSONException e){
                            toast(e.toString());
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

        rq.add(jor);
        toast(url);
    }


}
